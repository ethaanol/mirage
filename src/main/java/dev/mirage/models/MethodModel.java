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

    public MethodModel() {}

    public List<LabelModel> getLabels() {
        return labels;
    }

    public void attach(InstructionModel model) {
        instructions.add(model);
    }

    public void attach(List<InstructionModel> models) {
        instructions.addAll(models);
    }

    public void remove(InstructionModel model) {
        instructions.remove(model.getId());
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public void setInstructions(List<InstructionModel> instructions) {
        this.instructions = instructions;
    }

    public void setLabels(List<LabelModel> labels) {
        this.labels = labels;
    }
}
