package dev.mirage.models;

public class FieldModel {
    private String name, desc;
    private Object value;
    private int access;

    public FieldModel(String name, String desc, Object value, int access) {
        this.name = name;
        this.desc = desc;
        this.value = value;
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Object getValue() {
        return value;
    }

    public int getAccess() {
        return access;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setAccess(int access) {
        this.access = access;
    }
}
