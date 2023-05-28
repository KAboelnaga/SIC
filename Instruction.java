package com.company;

public class Instruction {
    private String opcode;
    private String operation;
    private String Format;

    public Instruction(String opcode) {
        this.opcode = opcode;
    }

    public Instruction() {
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "opcode='" + opcode + '\'' +
                ", operation='" + operation + '\'' +
                ", Format='" + Format + '\'' +
                '}';
    }
}
