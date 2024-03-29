package dev.mirage.models;

public class InstructionModel {
    private byte opcode;
    private Object value;
    private LabelModel label;

    // for invocation
    private String owner, name, desc;

    private LabelModel target;

    public InstructionModel(byte opcode) {
        this.opcode = opcode;
    }

    public InstructionModel(byte opcode, Object value) {
        this.opcode = opcode;
        this.value = value;
    }

    public InstructionModel(byte opcode, String owner, String name, String desc) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    public InstructionModel(byte opcode, LabelModel target) {
        this.opcode = opcode;
        this.target = target;
    }

    public LabelModel getTarget() {
        return target;
    }

    public String getOwner() {
        return owner;
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

    public byte getOpcode() {
        return opcode;
    }
}
