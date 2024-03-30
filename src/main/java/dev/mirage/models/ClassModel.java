package dev.mirage.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ClassModel {
    private String name;
    private String superClass;
    private int access, version;

    private List<FieldModel> fields = new ArrayList<>();
    private List<MethodModel> methods = new ArrayList<>();

    private ByteArrayOutputStream stream = new ByteArrayOutputStream();

    public ClassModel(String name, String superClass, int access, int version) {
        this.name = name;
        this.superClass = superClass;
        this.access = access;
        this.version = version;
    }

    public ClassModel() {}

    public List<FieldModel> getFields() {
        return fields;
    }

    public List<MethodModel> getMethods() {
        return methods;
    }

    public void setFields(List<FieldModel> fields) {
        this.fields = fields;
    }

    public void setMethods(List<MethodModel> methods) {
        this.methods = methods;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ByteArrayOutputStream getStream() {
        return stream;
    }

    public void setStream(ByteArrayOutputStream stream) {
        this.stream = stream;
    }

    public String getName() {
        return name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public int getAccess() {
        return access;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public void setAccess(int access) {
        this.access = access;
    }
}
