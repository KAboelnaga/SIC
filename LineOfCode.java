package com.company;

public class LineOfCode {
    private String address;
    private String label;
    private String operation;
    private String operand;
    private String format;
    private String objectCode;

    public LineOfCode() {
        objectCode = "";
    }


    public LineOfCode(String address, String label, String operation, String operand, String format) {
        this.address = address;
        this.label = label;
        this.operation = operation;
        this.operand = operand;
        this.format = format;
        objectCode = "";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    @Override
    public String toString() {
        return "LineOfCode{" +
                "address='" + address + '\'' +
                ", label='" + label + '\'' +
                ", operation='" + operation + '\'' +
                ", operand='" + operand + '\'' +
                ", format='" + format + '\'' +
                ", objectCode='" + objectCode + '\'' +
                '}';
    }
}
