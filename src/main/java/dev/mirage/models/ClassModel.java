package dev.mirage.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;

@Getter
@Setter
@AllArgsConstructor
public class ClassModel {
    private String name;
    private String superClass;
    private int access, version;

    private ByteArrayOutputStream stream = new ByteArrayOutputStream();

    public ClassModel(String name, String superClass, int access, int version) {
        this.name = name;
        this.superClass = superClass;
        this.access = access;
        this.version = version;
    }
}
