public class StringTest
{
//javac 1.6.0_45
//java version "1.6.0_45"
//Java(TM) SE Runtime Environment (build 1.6.0_45-b06)
//Java HotSpot(TM) Server VM (build 20.45-b01, mixed mode)
//1627 -> 348 -> 334 -> 250 -> 152
  public static void main(String[] args)
  {
    if(args.length != 1)
    {
      return;
    }

    String str = ""; //D186783E36B721651E8AF96AB1C4000B
    long nBegin = System.currentTimeMillis();
    for(int i=0; i<1024*1024; i++)
    {
      str = filter5(args[0]);
    }
    long nEnd = System.currentTimeMillis();

    System.out.println(nEnd-nBegin);
    System.out.println(str);
  }

  private static String filter1(String strOld)
  {
    String strNew = new String();
    for(int i=0; i<strOld.length(); i++)
    {
      if('0'<=strOld.charAt(i) && strOld.charAt(i)<='9')
      {
        strNew += strOld.charAt(i);
      }
    }
    return strNew;
  }

  private static String filter2(String strOld)
  {
    StringBuffer strNew = new StringBuffer();
    for(int i=0; i<strOld.length(); i++)
    {
      if('0'<=strOld.charAt(i) && strOld.charAt(i)<='9')
      {
        strNew.append(strOld.charAt(i));
      }
    }
    return strNew.toString();
  }

  private static String filter3(String strOld)
  {
    StringBuffer strNew = new StringBuffer();
    int nLen = strOld.length();
    for(int i=0; i<nLen; i++)
    {
      char ch = strOld.charAt(i);
      if('0'<=ch && ch<='9')
      {
        strNew.append(ch);
      }
    }
    return strNew.toString();
  }

  private static String filter4(String strOld)
  {
    int nLen = strOld.length();
    StringBuffer strNew = new StringBuffer(nLen);
    for(int i=0; i<nLen; i++)
    {
      char ch = strOld.charAt(i);
      if('0'<=ch && ch<='9')
      {
        strNew.append(ch);
      }
    }
    return strNew.toString();
  }

  private static String filter5(String strOld)
  {
    int nLen = strOld.length();
    char[] chArray = new char[nLen];
    int nPos = 0;
    for(int i=0; i<nLen; i++)
    {
      char ch = strOld.charAt(i);
      if('0'<=ch && ch<='9')
      {
        chArray[nPos] = ch;
        nPos++;
      }
    }
    return new String(chArray, 0, nPos);
  }
};
