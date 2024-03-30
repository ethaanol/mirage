package dev.mirage.models;

public class InstructionModel {
    private byte opcode;
    private Object value;
    private LabelModel label;
    private int id;
    private short distance;

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

    public InstructionModel(byte opcode, short dist, boolean t) {
        this.opcode = opcode;
        this.distance = dist;
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

    public void setTarget(LabelModel target) {
        this.target = target;
    }

    public void setOpcode(byte opcode) {
        this.opcode = opcode;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public short getDistance() {
        return distance;
    }
}
