package com.company;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PassOne {

    ArrayList<LineOfCode> entireCode = new ArrayList<>();
    HashMap<String, Instruction> instructionSet = new HashMap<>();
    HashMap<String, String> symbolTable = new HashMap<>();
    ArrayList<String> mrecords = new ArrayList<>();
    ArrayList<Literal> LiteralTable = new ArrayList<Literal>();

    public void readInstructionsFromInstructionSet() {
        File f = new File("instructionSet.txt");
        try {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                String currentInstructionLine = s.nextLine();
                String[] splittedInstruction = currentInstructionLine.split(" ");
                Instruction currentInstruction = new Instruction();
                currentInstruction.setOperation(splittedInstruction[0]);
                currentInstruction.setFormat(splittedInstruction[1]);
                currentInstruction.setOpcode(splittedInstruction[2]);
                instructionSet.put(splittedInstruction[0], currentInstruction);
            }

        } catch (Exception e) {
            System.out.println("Error in reading file");
        }

    }

    public void readProgramFromFile() {
        File f = new File("Program.txt");
        try {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                String currentLineOfCodeFromFile = s.nextLine();
                currentLineOfCodeFromFile = fixLineSpacing(currentLineOfCodeFromFile);
                String[] separatedLineOfCode = currentLineOfCodeFromFile.split(" ");
                LineOfCode currentLineOfCode = new LineOfCode();

                String operation = "";
                if (separatedLineOfCode.length == 3) {
                    currentLineOfCode.setLabel(separatedLineOfCode[0]);
                    currentLineOfCode.setOperation(separatedLineOfCode[1]);
                    currentLineOfCode.setOperand(separatedLineOfCode[2]);
                    operation = separatedLineOfCode[1];
                } else if (separatedLineOfCode.length == 2) {
                    currentLineOfCode.setLabel("\t");
                    currentLineOfCode.setOperation(separatedLineOfCode[0]);
                    currentLineOfCode.setOperand(separatedLineOfCode[1]);
                    operation = separatedLineOfCode[0];
                } else if (separatedLineOfCode.length == 1) {
                    currentLineOfCode.setLabel("\t");
                    currentLineOfCode.setOperation(separatedLineOfCode[0]);
                    currentLineOfCode.setOperand("\t");
                    operation = separatedLineOfCode[0];
                }
                if (operation.startsWith("$"))
                    currentLineOfCode.setFormat("6");
                else if (operation.startsWith("&"))
                    currentLineOfCode.setFormat("5");
                else if (operation.startsWith("+"))
                    currentLineOfCode.setFormat("4");
                else {
                    Instruction instruction = instructionSet.get(operation);
                    if (instruction != null) {
                        currentLineOfCode.setFormat(instruction.getFormat());
                    } else {
                        currentLineOfCode.setFormat("");
                    }
                }
                entireCode.add(currentLineOfCode);

            }


        } catch (Exception e) {
            System.out.println("can't read from file");
        }
    }

    public void passOne() {
        String address = entireCode.get(0).getOperand();
        entireCode.get(0).setAddress(address);
        int increment = 0;
        for (int i = 1; i < entireCode.size(); i++) {
            LineOfCode currentLineOfCode = entireCode.get(i);
            address = convertToHexa(address, increment);
            currentLineOfCode.setAddress(address);
            if (!currentLineOfCode.getLabel().trim().equals("")) //Fill Symbol Table
            {
                symbolTable.put(currentLineOfCode.getLabel(), address);
            }
            String currentOperand = currentLineOfCode.getOperand();
            if (currentOperand.startsWith("=")) // gets Literals and adds them to symbol table
            {
                boolean checkForLiteralInLiteralTable = false;
                for (int j = 0; j < LiteralTable.size(); j++) {
                    if (LiteralTable.get(j).getName().equals(currentOperand)) {
                        checkForLiteralInLiteralTable = true;
                        break;
                    }
                }
                if (!checkForLiteralInLiteralTable) {
                    Literal newLiteral = new Literal();
                    newLiteral.setName(currentOperand);
                    newLiteral.setAddress("");
                    if (currentOperand.startsWith("=X")) {

                        newLiteral.setLength((currentOperand.length() - 4) / 2); //removes =x hex 2 values each' '
                        newLiteral.setValue(currentOperand.substring(3, currentOperand.length() - 1));
                    } else {
                        newLiteral.setLength((currentOperand.length() - 4));
                        String asciiCode = "";
                        for (int j = 3; j < currentOperand.length() - 1; j++) {
                            int asciiCodeIntegerForm = currentOperand.charAt(j);
                            asciiCode += Integer.toHexString(asciiCodeIntegerForm);
                        }
                        newLiteral.setValue(asciiCode);
                    }
                    LiteralTable.add(newLiteral);
                }
            }
            if (entireCode.get(i).getOperation().equalsIgnoreCase("LTORG")) {
                increment = 0;
                for (int j = 0; j < LiteralTable.size(); j++) {
                    if (LiteralTable.get(j).getAddress().equals("")) {
                        address = convertToHexa(address, increment);
                        address = addZeros(address, 4);
                        LiteralTable.get(j).setAddress(address);
                        LineOfCode insertToLTORG = new LineOfCode();
                        insertToLTORG.setLabel("*");
                        insertToLTORG.setOperation(LiteralTable.get(j).getName());
                        insertToLTORG.setOperand("");
                        insertToLTORG.setObjectCode(LiteralTable.get(j).getValue());
                        insertToLTORG.setAddress(address);
                        entireCode.add(i + 1, insertToLTORG);
                        increment = LiteralTable.get(j).getLength();
                        i++;
                    }
                }
            }
            if (currentLineOfCode.getOperation().startsWith("+") || currentLineOfCode.getOperation().startsWith("$")) {
                increment = 4;
            } else if (currentLineOfCode.getOperation().startsWith("&")) {
                increment = 3;
            } else if (currentLineOfCode.getOperation().equalsIgnoreCase("RESW")) {
                int a = Integer.parseInt(currentLineOfCode.getOperand());
                increment = a * 3;
            } else if (currentLineOfCode.getOperation().equalsIgnoreCase("RESB")) {
                int a = Integer.parseInt(currentLineOfCode.getOperand());
                increment = a;
            } else if (currentLineOfCode.getOperation().equalsIgnoreCase("BYTE")) {
                if (currentLineOfCode.getOperand().startsWith("X") || currentLineOfCode.getOperand().startsWith("x")) {
                    int lengthOfHexaDecimalLiteral = (currentLineOfCode.getOperand().length() - 3) / 2;
                    increment = lengthOfHexaDecimalLiteral;
                }
                if (currentLineOfCode.getOperand().startsWith("C") || currentLineOfCode.getOperand().startsWith("c")) {
                    int lengthOfCharacterLiteral = (currentLineOfCode.getOperand().length() - 3);
                    increment = lengthOfCharacterLiteral;
                }
            } else if (currentLineOfCode.getOperation().equalsIgnoreCase("WORD")) {

                increment = 3;
            } else if (entireCode.get(i).getOperation().equalsIgnoreCase("BASE") || entireCode.get(i).getOperation().equalsIgnoreCase("END")) {
                increment = 0;
            } else {
                String op = currentLineOfCode.getOperation();
                Instruction o = instructionSet.get(op);
                try {
                    increment = Integer.parseInt(o.getFormat());
                } catch (Exception e) {

                }
            }
        }
        increment = 0;
        for (int j = 0; j < LiteralTable.size(); j++) {
            if (LiteralTable.get(j).getAddress().equals("")) {
                address = convertToHexa(address, increment);
                address = addZeros(address, 4);
                LiteralTable.get(j).setAddress(address);
                LineOfCode ins = new LineOfCode();
                ins.setLabel("*");
                ins.setOperation(LiteralTable.get(j).getName());
                ins.setOperand("");
                ins.setObjectCode(LiteralTable.get(j).getValue());
                ins.setAddress(address);
                entireCode.add(ins);
                increment = LiteralTable.get(j).getLength();
            }
        }

    }


    public String getValueofB() {
        for (int i = 0; i < entireCode.size(); i++) {
            if (entireCode.get(i).getOperation().equalsIgnoreCase("LDB")) {
                String operand = entireCode.get(i).getOperand();
                if (operand.startsWith("#"))
                    operand = operand.substring(1);

                return symbolTable.get(operand);
            }
        }
        return "";
    }

    public String convertToHexa(String hexaNumber, int value) {
        int decimalNumber = Integer.parseInt(hexaNumber, 16);
        decimalNumber = decimalNumber + value;
        String newHexNumber = Integer.toHexString(decimalNumber);
        newHexNumber = newHexNumber.toUpperCase();
        return newHexNumber;
    }

    public String fixLineSpacing(String line) {
        String x = line.trim();

        while (x.contains("  ")) {
            x = x.replace("  ", " ");
        }
        return x;
    }

    public void pass2() {

        for (int i = 1; i < entireCode.size(); i++) {
            LineOfCode currentLineOfCode = entireCode.get(i);
            String hexaValue = "";
            String operand = "";
            if (currentLineOfCode.getLabel().equalsIgnoreCase("*")) {
                continue;
            }
            if (currentLineOfCode.getOperation().equalsIgnoreCase("WORD")) {
                int value = Integer.parseInt(currentLineOfCode.getOperand());
                hexaValue = Integer.toHexString(value);
                hexaValue = hexaValue.toUpperCase();
                hexaValue = addZeros(hexaValue, 6);
            } else if (currentLineOfCode.getOperation().equalsIgnoreCase("BYTE")) {
                operand = currentLineOfCode.getOperand();
                if (operand.startsWith("X")) {
                    hexaValue = operand.substring(2, operand.length() - 1);
                } else {
                    operand = operand.substring(2, operand.length() - 1);
                    for (int k = 0; k < operand.length(); k++) {
                        int x = operand.charAt(k);
                        hexaValue += Integer.toHexString(x);
                    }
                }
            } else if (currentLineOfCode.getFormat().equals("1")) {
                hexaValue = instructionSet.get(currentLineOfCode.getOperation()).getOpcode();
            } else if (currentLineOfCode.getFormat().equals("2")) {
                hexaValue = instructionSet.get(currentLineOfCode.getOperation()).getOpcode();
                String reg = currentLineOfCode.getOperand();
                if (reg.contains(",")) {
                    String twoRegisters[] = reg.split(",");
                    hexaValue = hexaValue + getRegisterNumber(twoRegisters[0]);
                    hexaValue = hexaValue + getRegisterNumber(twoRegisters[1]);
                } else {
                    hexaValue = hexaValue + getRegisterNumber(reg);
                }
            } else if (currentLineOfCode.getFormat().equals("3")) {
                hexaValue = format3(currentLineOfCode);
            } else if (currentLineOfCode.getFormat().equals("4")) {
                int x = Integer.parseInt(currentLineOfCode.getAddress(), 16);
                x++;
                String add = Integer.toHexString(x);
                add = addZeros(add, 6);
                mrecords.add("M" + add + "05");
                hexaValue = format4(currentLineOfCode);
            } else if (currentLineOfCode.getFormat().equals("5")) {
                hexaValue = format5(currentLineOfCode);

            } else if (currentLineOfCode.getFormat().equals("6")) {
                hexaValue = format6(currentLineOfCode);
            }
            currentLineOfCode.setObjectCode(hexaValue);
        }
    }

    public String addZeros(String hexaNumber, int num) {
        for (int i = 0; num > hexaNumber.length(); i++) {
            hexaNumber = "0" + hexaNumber;

        }
        return hexaNumber;
    }

    private String format3(LineOfCode currentLineOfCode) {
        String operation = currentLineOfCode.getOperation();
        String opcode = instructionSet.get(operation).getOpcode();
        String binary = removezeros(opcode);
        String operand = currentLineOfCode.getOperand();
        String hexa = "";

        if (currentLineOfCode.getOperand().startsWith("@")) {
            operand = operand.substring(1);
            binary = binary + "10";
        } else if (currentLineOfCode.getOperand().startsWith("#")) {
            operand = operand.substring(1);
            binary = binary + "01";
        } else {
            binary = binary + "11";
        }
        if (operand.endsWith(",X")) {
            binary += "1";
        } else {
            binary += "0";
        }
        int disp, ta, pc;
        String TA = "";
        if (operand.startsWith("=")) {
            for (int j = 0; j < LiteralTable.size(); j++) {
                if (LiteralTable.get(j).getName().equals(operand)) {
                    TA = LiteralTable.get(j).getAddress();
                    break;
                }
            }

        } else {
            TA = symbolTable.get(operand);
        }
        String PC = currentLineOfCode.getAddress();
        ta = Integer.parseInt(TA, 16);
        pc = Integer.parseInt(PC, 16) + Integer.parseInt(currentLineOfCode.getFormat());
        disp = ta - pc;
        String DISP;
        if (disp >= -2048 && disp <= 2047) {
            binary += "010";
            int m = Integer.parseInt(binary, 2);
            hexa = Integer.toHexString(m);
            hexa = addZeros(hexa, 3);
            DISP = Integer.toHexString(disp);
            DISP = addZeros(DISP, 3);
        } else {
            String baseReg = getValueofB();
            int b = Integer.parseInt(baseReg);
            disp = ta - b;
            binary += "100";
            int m = Integer.parseInt(binary, 2);
            hexa = Integer.toHexString(m);
            hexa = addZeros(hexa, 3);
            DISP = Integer.toHexString(disp);
            DISP = addZeros(DISP, 3);
        }
        hexa = hexa + DISP;
        return hexa;
    }

    private String format5(LineOfCode is) {
        String operation = is.getOperation().substring(1);//remove +
        String opcode = instructionSet.get(operation).getOpcode();
        String binary = removezeros(opcode);
        String operand = is.getOperand();
        String hexa = "";


        int disp, ta, pc;
        // System.out.println(operand);
        String TA = symbolTable.get(operand);
        String PC = is.getAddress();
        ta = Integer.parseInt(TA, 16);
        pc = Integer.parseInt(PC, 16) + Integer.parseInt(is.getFormat());
        disp = ta - pc;
        String DISP;
        if (disp >= -2048 && disp <= 2047) {
            if (disp % 2 == 0) {
                binary += "1";
            } else {
                binary += "0";
            }
            if (disp > 0) {
                binary += "0";
            } else {
                binary += "1";
            }
            if (operand.endsWith(",X")) {
                binary += "1";
            } else {
                binary += "0";
            }
            binary += "01";
            if (disp == 0) {
                binary += "1";
            } else {
                binary += "0";
            }
            int m = Integer.parseInt(binary, 2);
            hexa = Integer.toHexString(m);
            hexa = addZeros(hexa, 3);
            DISP = Integer.toHexString(disp);
            DISP = addZeros(DISP, 3);
        } else {
            String baseReg = getValueofB();
            int b = Integer.parseInt(baseReg);
            disp = ta - b;

            if (disp % 2 == 0) {
                binary += "1";
            } else {
                binary += "0";
            }
            if (disp > 0) {
                binary += "0";
            } else {
                binary += "1";
            }
            if (operand.endsWith(",X")) {
                binary += "1";
            } else {
                binary += "0";
            }
            binary += "10";
            if (disp == 0) {
                binary += "1";
            } else {
                binary += "0";
            }
            int m = Integer.parseInt(binary, 2);
            hexa = Integer.toHexString(m);
            hexa = addZeros(hexa, 3);
            DISP = Integer.toHexString(disp);
            DISP = addZeros(DISP, 3);
        }
        hexa = hexa + DISP;
        return hexa;
    }

    public int Subtract(String TA, String PC) {
        int ta = Integer.parseInt(TA, 16);
        int pc = Integer.parseInt(PC, 16);
        return ta - pc;
    }

    private String format4(LineOfCode is) {
        String operation = is.getOperation().substring(1);//remove +
        String opcode = instructionSet.get(operation).getOpcode();
        String binary = removezeros(opcode);
        String operand = is.getOperand();
        String hexa = "";
        if (is.getOperand().startsWith("@")) {
            operand = operand.substring(1);
            binary = binary + "10";
        } else if (is.getOperand().startsWith("#")) {
            operand = operand.substring(1);
            binary = binary + "01";
        } else {
            binary = binary + "11";
        }

        if (operand.endsWith(",X")) {
            binary += "1001";
        } else {
            binary += "0001";
        }
        // System.out.println(binary);
        int m = Integer.parseInt(binary, 2);
        hexa = Integer.toHexString(m);
        hexa = addZeros(hexa, 3);
        String address = symbolTable.get(operand);
        address = addZeros(address, 5);
        hexa = hexa + address;
        return hexa;
    }

    private String format6(LineOfCode is) {
        String operation = is.getOperation().substring(1);//remove +
        String opcode = instructionSet.get(operation).getOpcode();
        String binary = removezeros(opcode);
        String operand = is.getOperand();
        String hexa = "";
        if (is.getOperand().startsWith("@")) {
            operand = operation.substring(1);
            binary = binary + "10";
        } else if (is.getOperand().startsWith("#")) {
            operand = operation.substring(1);
            binary = binary + "01";
        } else {
            binary = binary + "11";

        }

        if (operand.endsWith(",X")) {
            binary += "1";
        } else {
            binary += "0";
        }
        String address = symbolTable.get(operand);
        int add = Integer.parseInt(address, 16);
        if (add % 2 == 0) {
            binary += "0";
        } else {
            binary += "1";
        }
        if (add == 0) {
            binary += "0";
        } else {
            binary += "1";
        }
        String base = getValueofB();
        int b = Integer.parseInt(base, 16);
        if (add == b) {
            binary += "0";
        } else {
            binary += "1";
        }

        //  System.out.println(binary);
        int m = Integer.parseInt(binary, 2);
        hexa = Integer.toHexString(m);
        hexa = addZeros(hexa, 3);
        address = addZeros(address, 5);
        hexa = hexa + address;
        return hexa;
    }

    public boolean isDirective(String oper) {
        if (oper.equalsIgnoreCase("RESW") || oper.equalsIgnoreCase("RESB") || oper.equalsIgnoreCase("START") || oper.equalsIgnoreCase("END"))
            return true;
        else
            return false;
    }

    public String getRegisterNumber(String name) {
        if (name.equalsIgnoreCase("A"))
            return "0";
        else if (name.equalsIgnoreCase("X"))
            return "1";
        else if (name.equalsIgnoreCase("L"))
            return "2";
        else if (name.equalsIgnoreCase("B"))
            return "3";
        else if (name.equalsIgnoreCase("S"))
            return "4";
        else if (name.equalsIgnoreCase("T"))
            return "5";
        else if (name.equalsIgnoreCase("F"))
            return "6";
        return "0";
    }

    public String removezeros(String opcode) {
        int x = Integer.parseInt(opcode, 16);
        String binary = Integer.toBinaryString(x);
        binary = addZeros(binary, 8);
        binary = binary.substring(0, binary.length() - 2);
        return binary;
    }

}
