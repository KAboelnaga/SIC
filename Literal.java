package com.company;

public class Literal {
    private String name;
    private String value;
    private int length;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Literal{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", length=" + length +
                ", address='" + address + '\'' +
                '}';
    }
}
