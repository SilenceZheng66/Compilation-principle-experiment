import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class yufa {

    private static ArrayList<String> stack = new ArrayList<>(); // 当前栈
    private static ArrayList<Integer> reader = new ArrayList<>(); // 待读队列
    private static Production[] productions = new Production[43]; // 产生式数组
    private static HashMap<Integer, String> map_i2s; // 种别码Map，种别码为键，单词为值
    private static HashMap<String, Integer> map_s2i; // 种别码Map，单词为键，种别码为值
    private static HashMap<String, Integer> mnemonicCodeMap; //种别码Map，单词为键，种别码为值，用于转化词法分析的结果。

    public static boolean gramAnalyse(){
        int stackTop = 1;
        int readerTop = 0;
        int index = 0; // 当前步骤数
        initMap(); // 初始化种别码Map
        initProductions(); // 产生式初始化
        stack.add(0, String.valueOf(map_s2i.get("$"))); // 在stack底部加上$
        stack.add(stackTop, "S'"); // 将S'压入栈
        String filepath = "cifaOutput.txt";
        StringBuffer outputBuffer = new StringBuffer(); // 输出到文件的StringBuffer

        // 通过词法分析器的输出结果，初始化reader
        try {
            readToReader(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        reader.add(map_s2i.get("$")); // 在reader末尾加上$
        while (stackTop >= 0) {
            System.out.printf("%-6s", "第" + ++index + "步：");
            System.out.printf("%-10s", "当前栈：");
            outputBuffer.append("第" + index + "步：    当前栈：");
            StringBuffer sb = new StringBuffer(); // 引入StringBuffer仅为控制在控制台的输出格式对齐
            for (int i = 0; i <= stackTop; i++) {
                String str = null;
                try {
                    str = map_i2s.get(Integer.valueOf(stack.get(i)));
                    if (str != null) {
                        sb.append(str + " ");
                        outputBuffer.append(str + " ");
                    }
                } catch (NumberFormatException e) {
                    sb.append(stack.get(i) + " ");
                    outputBuffer.append(stack.get(i) + " ");
                }
            }
            System.out.printf("%-30s", sb.toString());
            System.out.print("待读队列：");
            outputBuffer.append("             待读队列：");
            sb = new StringBuffer();
            for (int i = 0; i < reader.size(); i++) {
                sb.append(map_i2s.get(reader.get(i)) + " ");
                outputBuffer.append(map_i2s.get(reader.get(i)) + " ");
            }
            System.out.printf("%-55s", sb.toString());

            if (match(stackTop, readerTop)) {
                stackTop--;
                System.out.print("\n");
                outputBuffer.append("\n");
            } else {
                int i = ll1_table(stackTop, readerTop);
                System.out.println("stacktop="+stackTop+"|i="+i+"|stacktopItem="+stack.get(stackTop)+"|readertopItem="+reader.get(readerTop));
                stackTop += stackPush(stackTop, productions[i]); // 压栈
                System.out.printf("%-30s", "下一步所用产生式：" + productions[i].prod);
                System.out.println();
                outputBuffer.append("         下一步所用产生式：" + productions[i].prod + "\n");
            }
        }
        if (stackTop == -1) {
            System.out.println("语法分析成功");
            outputBuffer.append("Accept");
        }

        System.out.print("语法分析结果文件的保存路径：yufaOutput.txt");
        String outputPath = "yufaOutput.txt";
        // 将StringBuffer的内容输出到文件
        File outputFile = new File(outputPath);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        PrintWriter writer = null;
        try {
            outputFile.createNewFile();
            writer = new PrintWriter(new FileOutputStream(outputFile));
            writer.write(outputBuffer.toString());
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int stackTop = 1;
        int readerTop = 0;
        int index = 0; // 当前步骤数
        initMap(); // 初始化种别码Map
        initProductions(); // 产生式初始化
        stack.add(0, String.valueOf(map_s2i.get("$"))); // 在stack底部加上$
        stack.add(stackTop, "S'"); // 将S'压入栈
        System.out.print("请输入词法分析结果的文件路径：");
        Scanner scanner = new Scanner(System.in);
        String filepath = scanner.next();
        StringBuffer outputBuffer = new StringBuffer(); // 输出到文件的StringBuffer

        // 通过词法分析器的输出结果，初始化reader
        try {
            readToReader(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        reader.add(map_s2i.get("$")); // 在reader末尾加上$
        while (stackTop >= 0) {
            System.out.printf("%-6s", "第" + ++index + "步：");
            System.out.printf("%-10s", "当前栈：");
            outputBuffer.append("第" + index + "步：    当前栈：");
            StringBuffer sb = new StringBuffer(); // 引入StringBuffer仅为控制在控制台的输出格式对齐
            for (int i = 0; i <= stackTop; i++) {
                String str = null;
                try {
                    str = map_i2s.get(Integer.valueOf(stack.get(i)));
                    if (str != null) {
                        sb.append(str + " ");
                        outputBuffer.append(str + " ");
                    }
                } catch (NumberFormatException e) {
                    sb.append(stack.get(i) + " ");
                    outputBuffer.append(stack.get(i) + " ");
                }
            }
            System.out.printf("%-30s", sb.toString());
            System.out.print("待读队列：");
            outputBuffer.append("             待读队列：");
            sb = new StringBuffer();
            for (int i = 0; i < reader.size(); i++) {
                sb.append(map_i2s.get(reader.get(i)) + " ");
                outputBuffer.append(map_i2s.get(reader.get(i)) + " ");
            }
            System.out.printf("%-55s", sb.toString());

            if (match(stackTop, readerTop)) {
                stackTop--;
                System.out.print("\n");
                outputBuffer.append("\n");
            } else {
                int i = ll1_table(stackTop, readerTop);
                System.out.println("stacktop="+stackTop+"|i="+i+"|stacktopItem="+stack.get(stackTop)+"|readertopItem="+reader.get(readerTop));
                stackTop += stackPush(stackTop, productions[i]); // 压栈
                System.out.printf("%-30s", "下一步所用产生式：" + productions[i].prod);
                System.out.println();
                outputBuffer.append("         下一步所用产生式：" + productions[i].prod + "\n");
            }
        }
        if (stackTop == -1) {
            System.out.println("语法分析成功");
            outputBuffer.append("Accept");
        }

        System.out.print("请输入语法分析结果文件的保存路径：");
        String outputPath = scanner.next();
        // 将StringBuffer的内容输出到文件
        File outputFile = new File(outputPath);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        PrintWriter writer = null;
        try {
            outputFile.createNewFile();
            writer = new PrintWriter(new FileOutputStream(outputFile));
            writer.write(outputBuffer.toString());
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void readToReader(String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        line = br.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            int pos1 = line.indexOf("(");
            int pos2 = line.indexOf(",");
            reader.add(mnemonicCodeMap.get(line.substring(pos1+1, pos2)));
            line = br.readLine(); // 读取下一行
        }
        br.close();
        is.close();
    }

    private static int stackPush(int stackTop, Production production) {
        int len = production.r_str.length;
        stack.remove(stackTop);
        if ("ε".equals(production.r_str[0])) {
        } else {
            for (int i = len - 1; i >= 0; i--) {
                stack.add(production.r_str[i]);
            }
            return len - 1;
        }
        return -1;
    }

    // 利用LL(1)预测分析表进行分析
    private static int ll1_table(int stackTop, int readerTop) {
        if ("S".equals(stack.get(stackTop))) {
            if ("int".equals(map_i2s.get(reader.get(readerTop)))) {
                return 0;
            } else if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 1;
            } else if ("if".equals(map_i2s.get(reader.get(readerTop)))) {
                return 2;
            } else if ("}".equals(map_i2s.get(reader.get(readerTop)))) {
                return 4;
            } else if ("$".equals(map_i2s.get(reader.get(readerTop)))) {
                return 4;
            } else {
                return -1;
            }
        }
        else if ("B".equals(stack.get(stackTop))) {
            if ("(".equals(map_i2s.get(reader.get(readerTop)))) {
                return 5;
            } else if ("=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 6;
            } else {
                return -1;
            }
        }
        else if ("L".equals(stack.get(stackTop))) {
            if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 7;
            } else {
                return -1;
            }
        }
        else if ("L'".equals(stack.get(stackTop))) {
            if (";".equals(map_i2s.get(reader.get(readerTop)))) {
                return 9;
            } else if (")".equals(map_i2s.get(reader.get(readerTop)))) {
                return 9;
            } else if ("}".equals(map_i2s.get(reader.get(readerTop)))) {
                return 9;
            } else if ("$".equals(map_i2s.get(reader.get(readerTop)))) {
                return 9;
            } else if (",".equals(map_i2s.get(reader.get(readerTop)))) {
                return 8;
            } else {
                return -1;
            }
        }
        else if ("Q".equals(stack.get(stackTop))) {
            if ("}".equals(map_i2s.get(reader.get(readerTop)))) {
                return 11;
            } else if ("$".equals(map_i2s.get(reader.get(readerTop)))) {
                return 11;
            } else if ("else".equals(map_i2s.get(reader.get(readerTop)))) {
                return 10;
            } else {
                return -1;
            }
        }
        else if ("X".equals(stack.get(stackTop))) {
            if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            } else if ("int".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            }  else if ("real".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            } else if ("+".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            } else if ("-".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            } else if ("(".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            } else {
                return -1;
            }
        }
        else if ("E".equals(stack.get(stackTop))) {
            if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 15;
            } else if ("int".equals(map_i2s.get(reader.get(readerTop)))) {
                return 15;
            } else if ("real".equals(map_i2s.get(reader.get(readerTop)))) {
                return 15;
            } else if ("(".equals(map_i2s.get(reader.get(readerTop)))) {
                return 15;
            } else if ("+".equals(map_i2s.get(reader.get(readerTop)))) {
                return 13;
            } else if ("-".equals(map_i2s.get(reader.get(readerTop)))) {
                return 14;
            } else {
                return -1;
            }
        }
        else if ("E'".equals(stack.get(stackTop))) {
            if ("+".equals(map_i2s.get(reader.get(readerTop)))) {
                return 16;
            } else if ("-".equals(map_i2s.get(reader.get(readerTop)))) {
                return 16;
            } else if (">".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if (">=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if ("<".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if ("<=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if ("==".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if ("!=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if (";".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if (")".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else {
                return -1;
            }
        }
        else if ("M".equals(stack.get(stackTop))) {
            if ("+".equals(map_i2s.get(reader.get(readerTop)))) {
                return 18;
            } else if ("-".equals(map_i2s.get(reader.get(readerTop)))) {
                return 19;
            } else {
                return -1;
            }
        }
        else if ("T".equals(stack.get(stackTop))) {
            if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 20;
            } else if ("int".equals(map_i2s.get(reader.get(readerTop)))) {
                return 20;
            }  else if ("real".equals(map_i2s.get(reader.get(readerTop)))) {
                return 20;
            }else if ("(".equals(map_i2s.get(reader.get(readerTop)))) {
                return 20;
            }
        }
        else if ("T'".equals(stack.get(stackTop))) {
            if ("+".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("-".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("*".equals(map_i2s.get(reader.get(readerTop)))) {
                return 21;
            } else if ("/".equals(map_i2s.get(reader.get(readerTop)))) {
                return 21;
            } else if (">".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if (">=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("<".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("<=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("==".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("!=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if (";".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if (")".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else {
                return -1;
            }
        }
        else if ("N".equals(stack.get(stackTop))) {
            if ("*".equals(map_i2s.get(reader.get(readerTop)))) {
                return 23;
            } else if ("/".equals(map_i2s.get(reader.get(readerTop)))) {
                return 24;
            } else {
                return -1;
            }
        }
        else if ("F".equals(stack.get(stackTop))) {
            if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 25;
            } else if ("int".equals(map_i2s.get(reader.get(readerTop)))) {
                return 26;
            }  else if ("real".equals(map_i2s.get(reader.get(readerTop)))) {
                return 42;
            }else if ("(".equals(map_i2s.get(reader.get(readerTop)))) {
                return 27;
            } else {
                return -1;
            }
        }
        else if ("R".equals(stack.get(stackTop))) {
            if (">".equals(map_i2s.get(reader.get(readerTop)))) {
                return 28;
            } else if (">=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 29;
            } else if ("<".equals(map_i2s.get(reader.get(readerTop)))) {
                return 30;
            } else if ("<=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 31;
            } else if ("==".equals(map_i2s.get(reader.get(readerTop)))) {
                return 32;
            } else if ("!=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 33;
            } else {
                return -1;
            }
        }
        else if ("S'".equals(stack.get(stackTop))) {
            if ("int".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("if".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("$".equals(map_i2s.get(reader.get(readerTop)))) {
                return 35;
            } else {
                return -1;
            }
        }
        else if ("A".equals(stack.get(stackTop))) {
            if ("int".equals(map_i2s.get(reader.get(readerTop)))) {
                return 38;
            }
            else {
                return -1;
            }
        }
        else {
            System.out.println("语法错误");
        }
        return -1;
    }

    private static boolean match(int stackTop, int readerTop) {
        try {
            int stackTopVal = Integer.parseInt(stack.get(stackTop)); // 未抛出异常说明是终结符
            if (stackTopVal == reader.get(0)) {
                stack.remove(stackTop);
                reader.remove(readerTop);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            // 抛出异常说明是非终结符
            return false;
        }
    }

    // S’:程序，S:语句，Q:else语句，L:标识符表，E:表达式，X:条件表达式，R:比较运算符，id:标识符，int:常量, real:实型常量
    private static void initProductions() {
        productions[0] = new Production("S",
                new String[]{"A", "L", String.valueOf(map_s2i.get(";"))},
                "S --> A L;");
        productions[1] = new Production("S",
                new String[]{String.valueOf(map_s2i.get("id")), "B"},
                "S --> id B");
        productions[2] = new Production("S",
                new String[]{String.valueOf(map_s2i.get("if")), String.valueOf(map_s2i.get("(")), "X", String.valueOf(map_s2i.get(")")), String.valueOf(map_s2i.get("{")), "S", String.valueOf(map_s2i.get("}")), "Q"},
                "S --> if(X){S}Q");
        productions[4] = new Production("S",
                new String[]{"ε"},
                "S --> ε");
        productions[5] = new Production("B",
                new String[]{String.valueOf(map_s2i.get("(")), "L", String.valueOf(map_s2i.get(")")), String.valueOf(map_s2i.get(";"))},
                "B --> (L);");
        productions[6] = new Production("B",
                new String[]{String.valueOf(map_s2i.get("=")), "E", String.valueOf(map_s2i.get(";"))},
                "B --> =E;");
        productions[7] = new Production("L",
                new String[]{String.valueOf(map_s2i.get("id")), "L'"},
                "L --> id L'");
        productions[8] = new Production("L'",
                new String[]{String.valueOf(map_s2i.get(",")), String.valueOf(map_s2i.get("id")), "L'"},
                "L' --> ,id L'");
        productions[9] = new Production("L'",
                new String[]{"ε"},
                "L' --> ε");
        productions[10] = new Production("Q",
                new String[]{String.valueOf(map_s2i.get("else")), String.valueOf(map_s2i.get("{")), "S", String.valueOf(map_s2i.get("}"))},
                "Q --> else{S}");
        productions[11] = new Production("Q",
                new String[]{"ε"},
                "Q --> ε");
        productions[12] = new Production("X",
                new String[]{"E", "R", "E"},
                "X --> ERE");
        productions[13] = new Production("E",
                new String[]{String.valueOf(map_s2i.get("+")), "T", "E'"},
                "E --> +TE'");
        productions[14] = new Production("E",
                new String[]{String.valueOf(map_s2i.get("-")), "T", "E'"},
                "E --> -TE'");
        productions[15] = new Production("E",
                new String[]{"T", "E'"},
                "E --> TE'");
        productions[16] = new Production("E'",
                new String[]{"M", "E'"},
                "E' --> ME'");
        productions[17] = new Production("E'",
                new String[]{"ε"},
                "E' --> ε");
        productions[18] = new Production("M",
                new String[]{String.valueOf(map_s2i.get("+")), "T"},
                "M --> +T");
        productions[19] = new Production("M",
                new String[]{String.valueOf(map_s2i.get("-")), "T"},
                "M --> -T");
        productions[20] = new Production("T",
                new String[]{"F", "T'"},
                "T --> FT'");
        productions[21] = new Production("T'",
                new String[]{"N", "T'"},
                "T' --> NT'");
        productions[22] = new Production("T'",
                new String[]{"ε"},
                "T' --> ε");
        productions[23] = new Production("N",
                new String[]{String.valueOf(map_s2i.get("*")), "F"},
                "N --> *F");
        productions[24] = new Production("N",
                new String[]{String.valueOf(map_s2i.get("/")), "F"},
                "N --> /F");
        productions[25] = new Production("F",
                new String[]{String.valueOf(map_s2i.get("id"))},
                "F --> id");
        productions[26] = new Production("F",
                new String[]{String.valueOf(map_s2i.get("int"))},
                "F --> int");
        productions[27] = new Production("F",
                new String[]{String.valueOf(map_s2i.get("(")), "E", String.valueOf(map_s2i.get(")"))},
                "F --> (E)");
        productions[28] = new Production("R",
                new String[]{String.valueOf(map_s2i.get(">"))},
                "R --> >");
        productions[29] = new Production("R",
                new String[]{String.valueOf(map_s2i.get(">="))},
                "R --> >=");
        productions[30] = new Production("R",
                new String[]{String.valueOf(map_s2i.get("<"))},
                "R --> <");
        productions[31] = new Production("R",
                new String[]{String.valueOf(map_s2i.get("<="))},
                "R --> <=");
        productions[32] = new Production("R",
                new String[]{String.valueOf(map_s2i.get("=="))},
                "R --> ==");
        productions[33] = new Production("R",
                new String[]{String.valueOf(map_s2i.get("!="))},
                "R --> !=");
        productions[34] = new Production("S'",
                new String[]{"S", "S'"},
                "S' --> SS'");
        productions[35] = new Production("S'",
                new String[]{"ε"},
                "S' --> ε");
        productions[38] = new Production("A",
                new String[]{String.valueOf(map_s2i.get("int"))},
                "A --> int");
        productions[42] = new Production("F",
                new String[]{String.valueOf(map_s2i.get("real"))},
                "F --> real");
    }

    private static void initMap() {
        map_s2i = new HashMap<>();
        map_s2i.put("begin", 1);
        map_s2i.put("end", 2);
        map_s2i.put("repeat", 3);
        map_s2i.put("until", 4);
        map_s2i.put("if", 5);
        map_s2i.put("else", 6);
        map_s2i.put("id", 7);
        map_s2i.put("int", 8);
        map_s2i.put("real", 9);
        map_s2i.put("<", 10);
        map_s2i.put("<=", 11);
        map_s2i.put("=", 12);
        map_s2i.put("<>", 13);
        map_s2i.put(">", 14);
        map_s2i.put(">=", 15);
        map_s2i.put(":=", 16);
        map_s2i.put("+", 17);
        map_s2i.put("-", 18);
        map_s2i.put("*", 19);
        map_s2i.put("/", 20);
        map_s2i.put("(", 21);
        map_s2i.put(")", 22);
        map_s2i.put("{", 23);
        map_s2i.put("}", 24);
        map_s2i.put(";", 25);
        map_s2i.put("$", 26);

        map_i2s = new HashMap<>();
        map_i2s.put(1,"begin");
        map_i2s.put(2,"end");
        map_i2s.put(3,"repeat");
        map_i2s.put(4,"until");
        map_i2s.put(5,"if");
        map_i2s.put(6,"else");
        map_i2s.put(7,"id");
        map_i2s.put(8,"int");
        map_i2s.put(9,"real");
        map_i2s.put(10,"<");
        map_i2s.put(11,"<=");
        map_i2s.put(12,"=");
        map_i2s.put(13,"<>");
        map_i2s.put(14,">");
        map_i2s.put(15,">=");
        map_i2s.put(16,":=");
        map_i2s.put(17,"+");
        map_i2s.put(18,"-");
        map_i2s.put(19,"*");
        map_i2s.put(20,"/");
        map_i2s.put(21,"(");
        map_i2s.put(22,")");
        map_i2s.put(23,"{");
        map_i2s.put(24,"}");
        map_i2s.put(25,";");
        map_i2s.put(26,"$");

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

    /**
     * 产生式类
     */
    private static class Production {
        String l_str;
        String[] r_str;
        String prod;
        public Production(String l_str, String[] r_str, String prod) {
            this.l_str = l_str;
            this.r_str = r_str;
            this.prod = prod;
        }
    }
}
