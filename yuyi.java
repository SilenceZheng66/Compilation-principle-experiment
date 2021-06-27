import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class yuyi {
    static class siyuanshi{
        private final String operator;
        private final String param1;
        private final String param2;
        private final String result;

        public siyuanshi(String operator, String param1, String param2, String result){
            this.operator = operator;
            this.param1 = param1;
            this.param2 = param2;
            this.result = result;
        }

        @Override
        public String toString() {
            return ("("+operator+","+param1+","+param2+","+result+")");
        }
    }

    public int i = 0;
    public int order = 0;
    static ArrayList<Integer> codeStack;
    static ArrayList<String> stack;
    static ArrayList<siyuanshi> results;
    private static HashMap<String, Integer> mnemonicCodeMap; //种别码Map，单词为键，种别码为值，用于转化词法分析的结果。

    private static void initMap() {
        mnemonicCodeMap = new HashMap<>();
        mnemonicCodeMap.put("BEGIN",1);
        mnemonicCodeMap.put("END",2);
        mnemonicCodeMap.put("REPEAT",3);
        mnemonicCodeMap.put("UNTIL",4);
        mnemonicCodeMap.put("IF",5);
        mnemonicCodeMap.put("ELSE",6);
        mnemonicCodeMap.put("ID",7);
        mnemonicCodeMap.put("INT",8);
        mnemonicCodeMap.put("REAL",9);
        mnemonicCodeMap.put("LT",10);
        mnemonicCodeMap.put("LE",11);
        mnemonicCodeMap.put("EQ",12);
        mnemonicCodeMap.put("NE",13);
        mnemonicCodeMap.put("GT",14);
        mnemonicCodeMap.put("GE",15);
        mnemonicCodeMap.put("IS",16);
        mnemonicCodeMap.put("PL",17);
        mnemonicCodeMap.put("MI",18);
        mnemonicCodeMap.put("MU",19);
        mnemonicCodeMap.put("DI",20);
        mnemonicCodeMap.put("OP",21);// Opening and closing parentheses
        mnemonicCodeMap.put("CP",22);
        mnemonicCodeMap.put("OB",23);// Opening and closing braces
        mnemonicCodeMap.put("CB",24);
        mnemonicCodeMap.put("SC",25);// Semicolon
    }

    public static void readToReader(String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        line = br.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            int pos1 = line.indexOf("(");
            int pos2 = line.indexOf(",");
            int pos3 = line.indexOf(")");
            switch (mnemonicCodeMap.get(line.substring(pos1+1, pos2))){
                case 7:
                case 8:
                case 9:
                    stack.add(line.substring(pos2+1,pos3));
                    codeStack.add(mnemonicCodeMap.get(line.substring(pos1+1, pos2)));
                    break;
                case 17:
                    stack.add("+");
                    codeStack.add(mnemonicCodeMap.get(line.substring(pos1+1, pos2)));
                    break;
                case 18:
                    stack.add("-");
                    codeStack.add(mnemonicCodeMap.get(line.substring(pos1+1, pos2)));
                    break;
                case 19:
                    stack.add("*");
                    codeStack.add(mnemonicCodeMap.get(line.substring(pos1+1, pos2)));
                    break;
                case 20:
                    stack.add("/");
                    codeStack.add(mnemonicCodeMap.get(line.substring(pos1+1, pos2)));
                    break;
                case 21:
                    stack.add("(");
                    codeStack.add(mnemonicCodeMap.get(line.substring(pos1+1, pos2)));
                    break;
                case 22:
                    stack.add(")");
                    codeStack.add(mnemonicCodeMap.get(line.substring(pos1+1, pos2)));
                    break;
            }
            line = br.readLine(); // 读取下一行
        }
        br.close();
        is.close();
    }

    private void m(){  // PM程序
        if (stack.get(this.i).equals("+")){
            this.i++;
            String ret1 = this.e();
            results.add(new siyuanshi("+","0",ret1,"out"));
            this.order++;
        }
        else if (stack.get(this.i).equals("-")){
            this.i++;
            String ret2 = this.e();
            results.add(new siyuanshi("-","0",ret2,"out"));
            this.order++;
        }
        else {
            String ret3 = this.e();
            results.add(new siyuanshi("=",ret3,"0","out"));
        }
    }

    private String e(){// PE程序
        //System.out.println("e");
        String ret1 = this.t();
        String[] temp = this.e1();
        String ret2 = temp[0], ret3 = temp[1];
        if (!ret2.equals("&")){
            this.order++;
            String r = "T"+String.valueOf(this.order);
            results.add(new siyuanshi(ret2,ret1,ret3,r));
            return r;
        }else {
            return ret1;
        }
    }

    private String[] e1(){ //PE1程序
        //System.out.println("e1");
        if (stack.get(this.i).equals("+")){
            this.i++;
            String ret1 = this.t();
            String[] temp = this.e1();
            String ret2 = temp[0], ret3 = temp[1];
            if (ret2.equals("&")){
                return new String[]{"+",ret1};
            }else {
                this.order++;
                String r = "T"+String.valueOf(this.order);
                results.add(new siyuanshi(ret2,ret1,ret3,r));
                return new String[]{"+",r};
            }
        }
        else if (stack.get(this.i).equals("-")){
            this.i++;
            String ret1 = this.t();
            String[] temp = this.e1();
            String ret2 = temp[0], ret3 = temp[1];
            if (ret2.equals("&")){
                return new String[]{"-",ret1};
            }else {
                this.order++;
                String r = "T"+String.valueOf(this.order);
                results.add(new siyuanshi(ret2,ret1,ret3,r));
                return new String[]{"-",r};
            }
        }
        else {
            return new String[]{"&","&"};
        }
    }

    private String t(){//  PT程序
        //System.out.println("t");
        String ret1 = this.f();
        String[] temp = this.t1();
        String ret2 = temp[0], ret3 = temp[1];
        if (!ret2.equals("&")){
            this.order++;
            String r = "T"+String.valueOf(this.order);
            results.add(new siyuanshi(ret2,ret1,ret3,r));
            return r;
        }else {
            return ret1;
        }
    }

    private String[] t1(){// PT1程序
        //System.out.println("t1");
        if (stack.get(this.i).equals("*")){
            this.i++;
            String ret1 = this.f();
            String[] temp = this.t1();
            String ret2 = temp[0], ret3 = temp[1];
            if (ret2.equals("&")){
                return new String[]{"*",ret1};
            }else {
                this.order++;
                String r = "T"+String.valueOf(this.order);
                results.add(new siyuanshi(ret2,ret1,ret3,r));
                return new String[]{"*",r};
            }
        }
        else if (stack.get(this.i).equals("/")){
            this.i++;
            String ret1 = this.f();
            String[] temp = this.t1();
            String ret2 = temp[0], ret3 = temp[1];
            if (ret2.equals("&")){
                return new String[]{"/",ret1};
            }else {
                this.order++;
                String r = "T"+String.valueOf(this.order);
                results.add(new siyuanshi(ret2,ret1,ret3,r));
                return new String[]{"/",r};
            }
        }
        else {
            return new String[]{"&","&"};
        }
    }

    private String f(){// PF程序
        //System.out.println("f");
        if (stack.get(this.i).equals("(")){
            this.i++;
            String ret1 = this.e();
            this.i++;
            return ret1;
        }
        else if (codeStack.get(this.i)==7){ //id
            int temp = this.i;
            this.i++;
            return stack.get(temp);
        }
        else if (codeStack.get(this.i)==8||codeStack.get(this.i)==9){ // 数字
            int temp = this.i;
            this.i++;
            return stack.get(temp);
        }
        return "null";
    }

    public void meanAnalyse() throws IOException {
        codeStack = new ArrayList<>();
        stack = new ArrayList<>();
        results = new ArrayList<>();
        initMap();
        String filepath = "cifaOutput.txt";
        readToReader(filepath);
        stack.add("#");
        System.out.println(stack);
        //start analyze
        this.m();
        for (siyuanshi s:results) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws IOException {
        yuyi y = new yuyi();
        y.meanAnalyse();
    }
}
