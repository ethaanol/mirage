package dev.mirage.models;

import java.util.ArrayList;
import java.util.List;

public class MethodModel {
    private String name, desc;
    private int access;

    private List<InstructionModel> instructions = new ArrayList<>();
    private List<LabelModel> labels = new ArrayList<>();

    public MethodModel(String name, String desc, int access) {
        this.name = name;
        this.desc = desc;
        this.access = access;
    }

    public List<LabelModel> getLabels() {
        return labels;
    }

    public void attach(InstructionModel model) {
        instructions.add(model);
    }

    public void attach(List<InstructionModel> models) {
        instructions.addAll(models);
    }

    public void attach(LabelModel label) {
        labels.add(label);
    }

    public List<InstructionModel> getInstructions() {
        return instructions;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getAccess() {
        return access;
    }
}
