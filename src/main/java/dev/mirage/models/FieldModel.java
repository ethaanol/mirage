package dev.mirage.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldModel {
    private String name, desc;
    private Object value;
    private int access;
}