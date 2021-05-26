import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

public class cifa {
    //存储单词符号\种别编码\助记符
    public String[] signals_1;
    public String[] signals_2;
    public Map<String,String> classMap;
    public Map<String,String> mnemonicCodeMap;

    //科学计数法转换
    public String convertNumber(String numberStr) {
        BigDecimal resultNumber = BigDecimal.valueOf(Double.parseDouble(numberStr));
        String r = resultNumber.setScale(10, BigDecimal.ROUND_HALF_UP).toString();
        return r.replaceAll("(0)+$", "");
    }

    //字符串分辨
    public boolean doAna(String seg){

        for (String s:signals_2) {
            if (seg.contains(s)){
                String[] sep = seg.split(s);
                int counter = 0;
                for (String item:sep) {
                    doAna(item);
                    counter++;
                    if (counter==1){
                        System.out.println("(" + mnemonicCodeMap.get(classMap.get(s)) + ", )");
                    }
                }
                return true;
            }
        }

        String strToken = "";
        int i = 0;
        char ch = seg.charAt(i);
        //标识符和关键字的判断
        while (i < seg.length()) {
            //判断该字符是否为字母
            if (Character.isLetter(ch)) {
                //该循环用于分离第一个字符为字母后续若干字母或者数字的字符串
                while (Character.isLetter(ch) || Character.isDigit(ch)) {
                    strToken += Character.toString(ch);
                    //判断该行是否还有字符
                    if ((++i) < seg.length()) {
                        ch = seg.charAt(i);
                    } else {
                        //该行没有下一个字符的话终止循环进行下一行的分析
                        i = seg.length();
                        break;
                    }
                }
                System.out.println("(" + mnemonicCodeMap.get(classMap.get("标识符")) + "," + strToken + ")");
            }
            //常数的判断
            else if (Character.isDigit(ch)) {//判断该字符是否为数字
                //判断下面是否还有数字或小数点或字母
                if (++i < seg.length()) {
                    strToken += Character.toString(ch);
                    ch = seg.charAt(i);
                }
                else {
                    strToken += Character.toString(ch);
                    System.out.println("(" + mnemonicCodeMap.get(classMap.get("整型常数")) + "," + strToken + ")");
                    i = seg.length();
                    break;
                }
                boolean haveDot = false;
                boolean haveEe = false;
                int digits_count = 1;
                boolean doBreak = false;

                //判断后续字符，确定为科学计数法或者浮点数。
                while (true){
                    //若下一位为点
                    if (ch == '.') {
                        haveDot = true;
                    }
                    else if (ch == 'E' || ch == 'e') {
                        haveEe = true;
                    }
                    else if (Character.isDigit(ch)){
                        digits_count++;
                    }else {
                        if(haveEe){
                            System.out.println("(" + mnemonicCodeMap.get(classMap.get("实型常数")) + "," + convertNumber(strToken) + ")");
                        }else if (haveDot){
                            System.out.println("(" + mnemonicCodeMap.get(classMap.get("实型常数")) + "," + strToken + ")");
                        }else {
                            System.out.println("(" + mnemonicCodeMap.get(classMap.get("整型常数")) + "," + strToken + ")");
                        }
                        break;
                    }
                    //判断下面是否还有数字或小数点或字母
                    if (++i < seg.length()) {
                        strToken += Character.toString(ch);
                        ch = seg.charAt(i);
                    }
                    else {
                        strToken += Character.toString(ch);
                        if(haveEe){
                            System.out.println("(" + mnemonicCodeMap.get(classMap.get("实型常数")) + "," + convertNumber(strToken) + ")");
                        }else if (haveDot){
                            System.out.println("(" + mnemonicCodeMap.get(classMap.get("实型常数")) + "," + strToken + ")");
                        }else {
                            System.out.println("(" + mnemonicCodeMap.get(classMap.get("整型常数")) + "," + strToken + ")");
                        }
                        i = seg.length();
                        doBreak = true;
                        break;
                    }
                }
                if (doBreak) break;

            }//运算符和界符的判断
            else if (classMap.containsKey(Character.toString(ch))) {
                strToken = Character.toString(ch);
                System.out.println("(" + mnemonicCodeMap.get(classMap.get(strToken)) + ", )");
                strToken = "";
                ch = ' ';
                //判断该行是否结束
                if (++i < seg.length()) {
                    ch = seg.charAt(i);
                } else {
                    //该行结束的话终止循环进行下一行的分析
                    i = seg.length();
                    break;
                }
            }
            //不合法输入
            else {
                System.out.println("(" + "不合法符号" + ", " + ch + ")");
                strToken = "";
                ch = ' ';
                //判断该行是否结束
                if (++i < seg.length()) {
                    ch = seg.charAt(i);
                } else {
                    //该行结束的话终止循环进行下一行的分析
                    i = seg.length();
                    break;
                }
            }
        }
        return true;
    }

    //构造
    cifa() {
        signals_1 = new String[]{"+","-","*","/","<","=",">"};
        signals_2 = new String[]{"<=",":=","<>",">="};

        classMap = new HashMap<String,String>();
        classMap.put("begin","1");
        classMap.put("end","2");
        classMap.put("repeat","3");
        classMap.put("until","4");
        classMap.put("if","5");
        classMap.put("else","6");
        classMap.put("标识符","7");
        classMap.put("整型常数","8");
        classMap.put("实型常数","9");
        classMap.put("<","10");
        classMap.put("<=","11");
        classMap.put("=","12");
        classMap.put("<>","13");
        classMap.put(">","14");
        classMap.put(">=","15");
        classMap.put(":=","16");
        classMap.put("+","17");
        classMap.put("-","18");
        classMap.put("*","19");
        classMap.put("/","20");

        mnemonicCodeMap = new HashMap<String,String>();
        mnemonicCodeMap.put("1","BEGIN");
        mnemonicCodeMap.put("2","END");
        mnemonicCodeMap.put("3","REPEAT");
        mnemonicCodeMap.put("4","UNTIL");
        mnemonicCodeMap.put("5","IF");
        mnemonicCodeMap.put("6","ELSE");
        mnemonicCodeMap.put("7","ID");
        mnemonicCodeMap.put("8","INT");
        mnemonicCodeMap.put("9","REAL");
        mnemonicCodeMap.put("10","LT");
        mnemonicCodeMap.put("11","LE");
        mnemonicCodeMap.put("12","EQ");
        mnemonicCodeMap.put("13","NE");
        mnemonicCodeMap.put("14","GT");
        mnemonicCodeMap.put("15","GE");
        mnemonicCodeMap.put("16","IS");
        mnemonicCodeMap.put("17","PL");
        mnemonicCodeMap.put("18","MI");
        mnemonicCodeMap.put("19","MU");
        mnemonicCodeMap.put("20","DI");

    }

    public static void main(String[] args) throws Exception {
        int i =0,j=0;
        String strToken ="";//存储从strTest分离出来的关键字、标识符、常数、运算符、界符
        String strTest ="";//存储从文件里面读来的一行代码
        char ch ;//用来存储从strTest中分离出来的单个字符
        cifa worldAnalyse = new cifa();
        File file = new File("input.txt");//创建要访问的文件
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        while ((strTest = bufferedReader.readLine()) != null)//读取文件的一行
        {
            System.out.println("第"+(++j)+"行分析结果：");
            strToken="";
            ch=' ';
            String[] l = strTest.split(" ");
            //标识符和关键字的判断
            for (String seg:l){
                if (worldAnalyse.classMap.containsKey(seg)){
                    System.out.println("("+worldAnalyse.mnemonicCodeMap.get(worldAnalyse.classMap.get(seg))+","+seg+")");
                }else {
                    worldAnalyse.doAna(seg);
                }
            }
        }
        //关闭文件读取
        bufferedReader.close();
    }
}
