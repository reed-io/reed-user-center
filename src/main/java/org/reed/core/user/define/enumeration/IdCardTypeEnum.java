package org.reed.core.user.define.enumeration;

public enum IdCardTypeEnum {
    ID_CARD(0, "身份证"),
    OTHER(1, "其他");

    private int value;
    private String name;

    private IdCardTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

