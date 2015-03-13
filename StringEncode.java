public class StringEncode{

    private static void printStringInfo(String a){
        int u_length = a.length();
        int p_length = a.codePointCount(0, u_length);
        System.out.println("code unit length: " + u_length);
        System.out.println("code point lenght: " + p_length);
        for(int i=0; i<u_length; i++){
            char c = a.charAt(i);
            System.out.println("code unit at " + i + " = 0x" + Integer.toHexString((int)c));
        }
        for(int i=0; i<p_length; i++){
            int index = a.offsetByCodePoints(0, i);
            int cp = a.codePointAt(index);
            System.out.println("code point at " + i + " = 0x" + Integer.toHexString(cp) + " index=" + index);
        }
        for(int i=0; i<u_length; ){
            int cp = a.codePointAt(i);
            System.out.println("i = " + i + " code point = 0x" + Integer.toHexString(cp));
            if(Character.isSupplementaryCodePoint(cp)) i+=2;
            else i++;
        }

    }

    public static void main(String[] args){
        String a = "12";
        //U+24B62=D852-DF62
        //U+10437=D801-DC37
        String b = "𤭢𐐷";
        printStringInfo(a);
        printStringInfo(b);
    }
}
