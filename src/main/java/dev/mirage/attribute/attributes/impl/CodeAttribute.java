package dev.mirage.attribute.attributes.impl;

import dev.mirage.attribute.attributes.Attribute;
import dev.mirage.jump.JumpEntry;
import dev.mirage.models.InstructionModel;
import dev.mirage.models.LabelModel;
import dev.mirage.models.MethodModel;
import dev.mirage.models.constants.Opcodes;
import dev.mirage.pool.ConstantPool;
import dev.mirage.pool.entry.entries.*;
import dev.mirage.util.UnsignedStreamWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodeAttribute implements Attribute {
    private MethodModel model;
    private ConstantPool pool;
    private List<JumpEntry> jumps = new ArrayList<>();

    public CodeAttribute(MethodModel model, ConstantPool pool) {
        this.model = model;
        this.pool = pool;
    }

    @Override
    public byte[] writeAttribute() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (UnsignedStreamWrapper wrapper = new UnsignedStreamWrapper(stream)) {
            wrapper.putUnsignedShort(256); // maxs
            wrapper.putUnsignedShort(256);

            ByteArrayOutputStream codeByteStream = new ByteArrayOutputStream();
            try (UnsignedStreamWrapper codeWrapper = new UnsignedStreamWrapper(codeByteStream)) {
                if (!model.getInstructions().isEmpty()) {
                    for (int i = 0; i < model.getInstructions().size(); i++) {
                        model.getInstructions().get(i).setId(i);
                    }
                    for (InstructionModel insn : model.getInstructions()) {
                        // this is retarded but apparently java 8 doesnt support merged cases :angry:
                        switch (insn.getOpcode()) {
                            case Opcodes.BIPUSH: {
                                codeWrapper.putUnsignedByte(insn.getOpcode());
                                handlePush(insn, codeWrapper);
                                break;
                            }
                            case Opcodes.SIPUSH: {
                                codeWrapper.putUnsignedByte(insn.getOpcode());
                                handlePush(insn, codeWrapper);
                                break;
                            }
                            case Opcodes.LDC: {
                                codeWrapper.putUnsignedByte(insn.getOpcode());
                                handleLDC(insn, codeWrapper);
                                break;
                            }
                            case Opcodes.LDC_WIDE: {
                                codeWrapper.putUnsignedByte(insn.getOpcode());
                                handleLDC(insn, codeWrapper);
                                break;
                            }
                            case Opcodes.INVOKESTATIC: {
                                codeWrapper.putUnsignedByte(insn.getOpcode());

                                int nameIndex = pool.requestUTF(insn.getName());
                                int descIndex = pool.requestUTF(insn.getDesc());
                                int ownerNameIndex = pool.requestUTF(insn.getOwner());
                                int ownerIndex = pool.addEntry(new ClassEntry(ownerNameIndex));

                                int nameAndTypeIndex = pool.addEntry(new NameAndTypeEntry(nameIndex, descIndex));
                                int methodRefIndex = pool.addEntry(new MethodRefEntry(ownerIndex, nameAndTypeIndex));

                                codeWrapper.putUnsignedShort(methodRefIndex);
                                break;
                            }
                            case Opcodes.INVOKEVIRTUAL: {
                                codeWrapper.putUnsignedByte(insn.getOpcode());

                                int nameIndex = pool.requestUTF(insn.getName());
                                int descIndex = pool.requestUTF(insn.getDesc());
                                int ownerNameIndex = pool.requestUTF(insn.getOwner());
                                int ownerIndex = pool.addEntry(new ClassEntry(ownerNameIndex));

                                int nameAndTypeIndex = pool.addEntry(new NameAndTypeEntry(nameIndex, descIndex));
                                int methodRefIndex = pool.addEntry(new MethodRefEntry(ownerIndex, nameAndTypeIndex));

                                codeWrapper.putUnsignedShort(methodRefIndex);
                                break;
                            }
                            case Opcodes.GETSTATIC: {
                                codeWrapper.putUnsignedByte(insn.getOpcode());

                                int nameIndex = pool.requestUTF(insn.getName());
                                int descIndex = pool.requestUTF(insn.getDesc());
                                int ownerNameIndex = pool.requestUTF(insn.getOwner());
                                int ownerIndex = pool.addEntry(new ClassEntry(ownerNameIndex));

                                int nameAndTypeIndex = pool.addEntry(new NameAndTypeEntry(nameIndex, descIndex));
                                int fieldRefIndex = pool.addEntry(new FieldRefEntry(ownerIndex, nameAndTypeIndex));

                                codeWrapper.putUnsignedShort(fieldRefIndex);
                                break;
                            }
                            case Opcodes.GOTO: {
                                codeWrapper.putUnsignedByte(insn.getOpcode());
                                codeWrapper.putUnsignedShort(0);

                                jumps.add(new JumpEntry(codeWrapper.size() - 3, codeWrapper.size() - 2, insn.getTarget()));
                                break;
                            }
                            case (byte) 0xBA: {
                                insn.getTarget().setPosition(codeWrapper.size());
                                break;
                            }
                            case Opcodes.IRETURN: {
                                codeWrapper.putUnsignedByte(insn.getOpcode());
                                break;
                            }
                            case Opcodes.RETURN: {
                                codeWrapper.putUnsignedByte(insn.getOpcode());
                                break;
                            }
                            default:
                                throw new RuntimeException("mirage : unknown opcode (" + String.format("%02x", insn.getOpcode()) + ")");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] bytes = codeByteStream.toByteArray();

            jumps.forEach(jmp -> {
                int relative = -(jmp.getJumpPos() - jmp.getTarget().getPosition());

                byte jmp1 = (byte) ((relative >> 8) & 0xff);
                byte jmp2 = (byte) (relative & 0xff);

                bytes[jmp.getTargetPos()] = jmp1;
                bytes[jmp.getTargetPos() + 1] = jmp2;
            });

            wrapper.writeInt(bytes.length);
            wrapper.write(bytes);

            wrapper.writeInt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    public void handlePush(InstructionModel model, UnsignedStreamWrapper wrapper) throws IOException {
        if (model.getValue() instanceof Integer) {
            int value = (int) model.getValue();
            if (value > 255) throw new IllegalArgumentException("mirage : value for bipush is higher than 255.");
            wrapper.putUnsignedByte((byte) (value & 0xFF));
        } else throw new IllegalArgumentException("mirage : value for bipush isn't of type integer.");
    }

    public void handleLDC(InstructionModel model, UnsignedStreamWrapper wrapper) throws IOException {
        switch (model.getOpcode()) {
            case Opcodes.LDC_WIDE: {
                if (model.getValue() instanceof String) {
                    String value = (String) model.getValue();
                    wrapper.putUnsignedShort((short) pool.requestString(value));
                    break;
                } else if (model.getValue() instanceof Integer) {
                    int value = (int) model.getValue();
                    wrapper.putUnsignedShort((short) pool.requestNcon(value));
                    break;
                } else throw new IllegalArgumentException("mirage : value for ldc isn't of type `java/lang/String`.");
            }
            case Opcodes.LDC: {
                if (model.getValue() instanceof String) {
                    String value = (String) model.getValue();
                    int strIndex = pool.addEntry(new StringEntry(pool.requestUTF(value)));
                    wrapper.putUnsignedByte((short) strIndex);
                    break;
                } else if (model.getValue() instanceof Integer) {
                    int value = (int) model.getValue();
                    wrapper.putUnsignedByte((short) pool.requestNcon(value));
                    break;
                } else throw new IllegalArgumentException("bleak : value for ldc isn't of type `java/lang/String`.");
            }
        }
    }

    public MethodModel getModel() {
        return model;
    }
}
