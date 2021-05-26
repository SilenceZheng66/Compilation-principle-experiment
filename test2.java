import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * @author FengTianHao
 * @version 1.0
 * @since 2019/4/13 9:46
 */
public class test2 {
    //存储单词符号\种别编码\助记符
    public String[] signals_1;
    public String[] signals_2;
    public Map<String,String> classMap;
    public Map<String,String> mnemonicCodeMap;

    //构造
    test2() {
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
        test2 worldAnalyse = new test2();
        File file = new File("input.txt");//创建要访问的文件
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        while ((strTest = bufferedReader.readLine()) != null)//读取文件的一行
        {
            System.out.println("第"+(++j)+"行分析结果：");
            strToken="";
            ch=' ';
            //找到该行中第一个不是空的字符的位置
            for (i = 0; i < strTest.length(); i++) {
                if (strTest.charAt(i) != ' ') {
                    ch = strTest.charAt(i);
                    break;
                }
            }
            //标识符和关键字的判断
            while (i < strTest.length()) {
                //判断该字符是否为字母
                if (Character.isLetter(ch)) {
                    //该循环用于分离第一个字符为字母后续若干字母或者数字的字符串
                    while (Character.isLetter(ch) || Character.isDigit(ch)) {
                        strToken += Character.toString(ch);
                        //判断该行是否还有字符
                        if ((++i) < strTest.length()) {
                            ch = strTest.charAt(i);
                        } else {
                            //该行没有下一个字符的话终止循环进行下一行的分析
                            i = strTest.length();
                            break;
                        }
                    }
                    //判断为关键字
                    if (worldAnalyse.classMap.containsKey(strToken)) {
                        System.out.println("("+worldAnalyse.mnemonicCodeMap.get(worldAnalyse.classMap.get(strToken))+","+strToken+")");
                    }
                    else//判断为标识符
                    {
                        System.out.println("("+worldAnalyse.mnemonicCodeMap.get(worldAnalyse.classMap.get("标识符"))+","+strToken+")");
                    }
                    ch = ' ';
                    strToken = "";
                }
                //常数的判断
                else if (Character.isDigit(ch)) {//判断该字符是否为数字
                    //判断下面是否还有数字或小数点或字母
                    if (++i < strTest.length()) {
                        strToken += Character.toString(ch);
                        ch = strTest.charAt(i);
                    }else {
                        strToken += Character.toString(ch);
                        System.out.println("("+worldAnalyse.mnemonicCodeMap.get(worldAnalyse.classMap.get("整型常数"))+","+strToken+")");
                        strToken = "";
                        ch = ' ';
                        i = strTest.length();
                    }

                    //判断下一位是小数点还是E 分情况
                    if(ch==' '){

                    }else {
                        if (ch=='.'){
                            String head = "";
                            head+=strToken;
                            head+=ch;
                            while (Character.isDigit(ch)) {//连续读取若干数字
                                strToken += Character.toString(ch);
                                //判断该行是否结束
                                if (++i < strTest.length()) {
                                    ch = strTest.charAt(i);
                                } else {
                                    //该行结束的话终止循环进行下一行的分析
                                    i = strTest.length();
                                    break;
                                }
                            }


                        }
                        else if (ch=='E' || ch=='e'){


                        }else {
                            while (Character.isDigit(ch)) {//连续读取若干数字
                                strToken += Character.toString(ch);
                                //判断该行是否结束
                                if (++i < strTest.length()) {
                                    ch = strTest.charAt(i);
                                } else {
                                    //该行结束的话终止循环进行下一行的分析
                                    i = strTest.length();
                                    break;
                                }
                            }
                            System.out.println("("+worldAnalyse.mnemonicCodeMap.get(worldAnalyse.classMap.get("整型常数"))+","+strToken+")");
                            strToken = "";
                            ch = ' ';
                        }
                    }
                }
                //运算符和界符的判断
                else if (worldAnalyse.classMap.containsKey(Character.toString(ch))) {
                    strToken=Character.toString(ch);
                    System.out.println("("+worldAnalyse.classMap.get(strToken)+","+strToken+")");
                    strToken = "";
                    ch = ' ';
                    //判断该行是否结束
                    if (++i < strTest.length()) {
                        ch = strTest.charAt(i);
                    } else {
                        //该行结束的话终止循环进行下一行的分析
                        i = strTest.length();
                        break;
                    }
                }
                //不合法输入
                else {
                    System.out.println("("+"不合法符号"+","+ch+")");
                    strToken="";
                    ch=' ';
                    //判断该行是否结束
                    if (++i < strTest.length()) {
                        ch = strTest.charAt(i);
                    } else {
                        //该行结束的话终止循环进行下一行的分析
                        i = strTest.length();
                        break;
                    }
                }
                //找到下一个不为空的字符的位置
                for (; i < strTest.length(); i++) {
                    if (strTest.charAt(i) != ' ') {
                        ch = strTest.charAt(i);
                        break;
                    }
                }
            }
        }
        //关闭文件读取
        bufferedReader.close();
    }
}
