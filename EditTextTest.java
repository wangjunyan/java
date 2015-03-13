public class EditTextTest{
    public static void main(String[] args){
        System.out.println("EditTextTest");
        int len = 20;
        int sep = 4;
        char c = '5';
        String r = "+";
        StringBuffer sb = new StringBuffer(len);
        String s = "";
        for(int i = 0; i < len; i++){
            sb.append(c);
            s = sb.toString();
            System.out.println("--------------------");
            System.out.println("in:  " + s);
            String rs = formatString(s, 4, r);
            System.out.println("out: " + rs);
            formatString(rs, 4, r);
        }

        String hex = args[0];
        System.out.println("------------------");
        System.out.println("isHex = " + hex.matches("[0-9a-fA-F]*"));
    }

    private static String formatString(String s, int sep, String r){
        String t = s.replace(r, "");
        int len = t.length();
        int newLen = len + len/sep - (len%sep==0 ? 1 : 0);
        String rs = "";
        StringBuffer sb = new StringBuffer(newLen);
        System.out.println("old lenght: " + len);
        System.out.println("new length: " + newLen);
        for(int i = 0; i < len; i++){
            sb.append(t.charAt(i));
            if(((i+1)%sep==0) && (i!=(len-1))){
                sb.append(r);
            }
        }
        rs = sb.toString();
        System.out.println("equal: " + rs.equals(s));
        return rs;
    }
}
