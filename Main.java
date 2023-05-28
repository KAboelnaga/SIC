package com.company;

import java.io.*;

public class Main {

    public static void printProgram(PassOne p) throws FileNotFoundException {
        File f = new File("out.txt");
        PrintWriter output = new PrintWriter(f);
        for (int j = 0; j < p.entireCode.size(); j++) {
            LineOfCode i = p.entireCode.get(j);
            // System.out.println( i.getAddress() + "\t" + i.getLabel() + "\t" + i.getOperation() + "\t" + i.getOperand() +"\t"+ i.getObjectCode() + "\t"+"\n");
            output.print(i.getAddress() + "\t" + i.getLabel() + "\t" + i.getOperation() + "\t" + i.getOperand() + "\t" + i.getObjectCode() + "\t" + "\n");
        }
        output.close();
    }

    public static void printSymTable(PassOne p) throws FileNotFoundException {
        File f = new File("symbTable.txt");
        PrintWriter output = new PrintWriter(f);
        output.println(p.symbolTable);
        output.close();
    }

    public static void WriteHTEFile(PassOne p) throws IOException {
        File f = new File("HTE.txt");
        FileWriter fw = new FileWriter(f);
        String progname = p.entireCode.get(0).getLabel();
        while (progname.length() < 6) {
            progname = progname + " ";
        }
        String st = p.entireCode.get(0).getOperand();
        String en = p.entireCode.get(p.entireCode.size() - 1).getAddress();
        int start = Integer.parseInt(st, 16);
        int end = Integer.parseInt(en, 16);
        int length = end - start;
        String programLength = Integer.toHexString(length);
        while (programLength.length() < 6) {
            programLength = "0" + programLength;
        }
        while (st.length() < 6) {
            st = "0" + st;
        }
        fw.write("H" + progname + st + programLength);
        fw.write(System.lineSeparator());
        String objectcode = "";
        String len = "";
        String add = "";
        for (int i = 1; i < p.entireCode.size(); i++) {
            if (p.entireCode.get(i).getOperation().equalsIgnoreCase("BASE"))
                continue;
            if (add.equals("") && !p.entireCode.get(i).getObjectCode().equals("")) {
                add = p.entireCode.get(i).getAddress();
            }
            while (add.length() < 6) {
                add = "0" + add;
            }
            if (objectcode.length() + p.entireCode.get(i).getObjectCode().length() <= 60
                    && p.entireCode.get(i).getObjectCode().length() > 0) {
                objectcode = objectcode + p.entireCode.get(i).getObjectCode();
            } else {
                if (objectcode.length() > 0) {
                    len = Integer.toHexString(objectcode.length() / 2);
                    if (len.length() < 2) {
                        len = "0" + len;
                    }
                    fw.write("T" + add + len + objectcode);
                    fw.write(System.lineSeparator());

                }
                add = "";
                objectcode = p.entireCode.get(i).getObjectCode();
                if (!objectcode.equals("")) {
                    add = p.entireCode.get(i).getAddress();
                }
            }

        }
        if (objectcode.length() > 0) {
            len = Integer.toHexString(objectcode.length() / 2);
            if (len.length() < 2) {
                len = "0" + len;
            }
            fw.write("T" + add + len + objectcode);
            fw.write(System.lineSeparator());

        }


        for (int i = 0; i < p.mrecords.size(); i++) {
            fw.write(p.mrecords.get(i));
            fw.write(System.lineSeparator());
        }


        fw.write("E" + st);
        fw.close();

    }

    public static void main(String[] args) throws Exception {
        PassOne p = new PassOne();
        p.readInstructionsFromInstructionSet();
        p.readProgramFromFile();
        p.passOne();
        p.pass2();
        printSymTable(p);
        printProgram(p);
        WriteHTEFile(p);
    }
}
