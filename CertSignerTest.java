import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PushbackReader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import sun.misc.BASE64Encoder;


public class CertSignerTest
{  public static void main(String[] args)
   {  String ksname = null; // the keystore name
      String alias = null; // the private key alias
      String inname = null; // the input file name
      String outname = null; // the output file name
      for (int i = 0; i < args.length; i += 2)
      {  if (args[i].equals("-keystore"))
            ksname = args[i + 1];
         else if (args[i].equals("-alias"))
            alias = args[i + 1];
         else if (args[i].equals("-infile"))
            inname = args[i + 1];
         else if (args[i].equals("-outfile"))
            outname = args[i + 1];
         else usage();
      }

      if (ksname == null || alias == null ||
         inname == null || outname == null) usage();


      try
      {  PushbackReader console = new PushbackReader(new
            InputStreamReader(System.in));
         KeyStore store = KeyStore.getInstance("JKS");
         InputStream in = new FileInputStream(ksname);
         char[] password
            = readPassword(console, "Keystore password");
         store.load(in, password);
         Arrays.fill(password, ' ');
         in.close();

         char[] keyPassword
            = readPassword(console, "Key password for " + alias);
         PrivateKey issuerPrivateKey
            = (PrivateKey)store.getKey(alias, keyPassword);
         Arrays.fill(keyPassword, ' ');

         OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("private.key"), "UTF-8");
         BufferedWriter bfout = new BufferedWriter(out);
         //String b64 = new BASE64Encoder.encode(issuerPrivateKey.getEncoded());
         String b64 = javax.xml.bind.DatatypeConverter.printBase64Binary(issuerPrivateKey.getEncoded());
         bfout.write("-----BEGIN PRIVATE KEY-----");
         bfout.newLine();
         bfout.write(b64);
         bfout.newLine();
         bfout.write("-----END PRIVATE KEY-----");
         bfout.close();
         out.close();

         if (issuerPrivateKey == null)
            error("No such private key");

         X509Certificate certificate = (X509Certificate) store.getCertificate(alias);
         out = new OutputStreamWriter(new FileOutputStream("public.cert"), "UTF-8");
         bfout = new BufferedWriter(out);
         //b64 = new BASE64Encoder.encode(certificate.getEncoded());
         b64 = javax.xml.bind.DatatypeConverter.printBase64Binary(certificate.getEncoded());
         bfout.write("-----BEGIN CERTIFICATE-----");
         bfout.newLine();
         bfout.write(b64);
         bfout.newLine();
         bfout.write("-----END CERTIFICATE-----");
         bfout.close();
         out.close();
         
         PublicKey pubkey = certificate.getPublicKey();
         out = new OutputStreamWriter(new FileOutputStream("public.key"), "UTF-8");
         bfout = new BufferedWriter(out);
         //b64 = new BASE64Encoder.encode(pubkey.getEncoded());
         b64 = javax.xml.bind.DatatypeConverter.printBase64Binary(pubkey.getEncoded());
         bfout.write("-----BEGIN PUBLIC KEY-----");
         bfout.newLine();
         bfout.write(b64);
         bfout.newLine();
         bfout.write("-----END PUBLIC KEY-----");
         bfout.close();
         out.close();
         
         File infile = new File(inname);
         in = new FileInputStream(infile);
         int length = (int) infile.length();
         byte[] message = new byte[length];
         in.read(message, 0, length);
         System.out.println("infile length = " + length);
         System.out.println("infile message = " + message.toString());
         in.close();
         
         Signature signalg = Signature.getInstance("SHA256WithRSA");
         signalg.initSign(issuerPrivateKey);
         signalg.update(message);
         byte[] signature = signalg.sign();
         int signlength = signature.length;
         System.out.println("signature length = " + signlength);
         System.out.println("signature message = " + signature.toString());

         String hex = ByteArrayToString(signature);
         System.out.println("hex string = " + hex);

         OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outname), "UTF-8");
         BufferedWriter bfwriter = new BufferedWriter(writer);
         bfwriter.write(new String(message, "UTF-8"));
         bfwriter.write(hex);
         bfwriter.close();
         writer.close();
      }
      catch (Exception exception)
      {  System.out.println(exception);
          exception.printStackTrace();
      }
   }

   public static char[] readPassword(PushbackReader in,
      String prompt) throws IOException
   {  System.out.print(prompt + ": ");
      System.out.flush();
      final int MAX_PASSWORD_LENGTH = 100;
      int length = 0;
      char[] buffer = new char[MAX_PASSWORD_LENGTH];

      while (true)
      {  int ch = in.read();
         if (ch == '\r' || ch == '\n' || ch == -1
            || length == MAX_PASSWORD_LENGTH)
         {  if (ch == '\r') // handle DOS "\r\n" line ends
            {  ch = in.read();
               if (ch != '\n' && ch != -1) in.unread(ch);
            }
            char[] password = new char[length];
            System.arraycopy(buffer, 0, password, 0, length);
            Arrays.fill(buffer, ' ');
            return password;
         }
         else
         {  buffer[length] = (char)ch;
            length++;
         }
      }
   }

   public static void error(String message)
   {  System.out.println(message);
      System.exit(1);
   }

   public static void usage()
   {  System.out.println("Usage: java CertSignerHex"
         + " -keystore keyStore -alias issuerKeyAlias"
         + " -infile inputFile -outfile outputFile");
      System.exit(1);
   }

   final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
   public static String ByteArrayToString(byte[] bytes)
   {
       char[] hexChars = new char[bytes.length * 2];
       int v;
       for ( int j = 0; j < bytes.length; j++ ) {
           v = bytes[j] & 0xFF;
           hexChars[j * 2] = hexArray[v >>> 4];
           hexChars[j * 2 + 1] = hexArray[v & 0x0F];
       }
       return new String(hexChars);
   }
}