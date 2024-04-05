package dev.mirage.readers;

import dev.mirage.models.*;
import dev.mirage.models.constants.Opcodes;
import dev.mirage.pool.ConstantPool;
import dev.mirage.pool.entry.PoolEntry;
import dev.mirage.pool.entry.constants.EntryConstants;
import dev.mirage.pool.entry.entries.*;
import dev.mirage.readers.exceptions.InvalidClassMagicException;
import dev.mirage.readers.exceptions.InvalidConstantPoolException;
import org.w3c.dom.stylesheets.LinkStyle;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pulse
 * Reads bytes and returns a {@link ClassModel}.
 */
public class GlobalModelReader {
    private final byte[] classBytes;
    private final List<Integer> classIndexes = new ArrayList<>();

    public GlobalModelReader(byte[] classBytes) {
        this.classBytes = classBytes;
    }

    public ClassModel accept() {
        ClassModel model = new ClassModel();

        try (DataInputStream stream = new DataInputStream(new ByteArrayInputStream(classBytes))) {
            if (!Validator.validateMagic(classBytes)) throwErr(1);
            stream.readInt(); // magic
            stream.readUnsignedShort(); // minor

            ConstantPool pool = new ConstantPool();
            pool.addEntry(null);

            model.setVersion(stream.readUnsignedShort());
            int cpSize = stream.readUnsignedShort(); // pool count

            // populate pool
            readPool(stream, pool, model, cpSize);

            model.setAccess(stream.readUnsignedShort());

            ClassEntry thisClassEntry = (ClassEntry) pool.getEntries().get(stream.readUnsignedShort());
            ClassEntry superClassEntry = (ClassEntry) pool.getEntries().get(stream.readUnsignedShort());

            UTFEntry utfThisClass = (UTFEntry) pool.getEntries().get(thisClassEntry.getUtfPointer());
            UTFEntry utfSuperClass = (UTFEntry) pool.getEntries().get(superClassEntry.getUtfPointer());

            model.setName(utfThisClass.getValue());
            model.setSuperClass(utfSuperClass.getValue());

            stream.readUnsignedShort();

            List<FieldModel> fields = readFields(stream, pool);
            List<MethodModel> methods = readMethods(stream, pool);

            model.setFields(fields);
            model.setMethods(methods);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    private List<MethodModel> readMethods(DataInputStream stream, ConstantPool pool) throws IOException {
        List<MethodModel> methods = new ArrayList<>();

        int count = stream.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            MethodModel method = new MethodModel();

            int access = stream.readUnsignedShort();
            UTFEntry methodNameUtf = (UTFEntry) pool.getEntries().get(stream.readUnsignedShort());
            UTFEntry methodDescUtf = (UTFEntry) pool.getEntries().get(stream.readUnsignedShort());

            method.setAccess(access);
            method.setName(methodNameUtf.getValue());
            method.setDesc(methodDescUtf.getValue());

            int attribCount = stream.readUnsignedShort();
            for (int j = 0; j < attribCount; j++) {
                UTFEntry attribNameUtf = (UTFEntry) pool.getEntries().get(stream.readUnsignedShort());
                int attribLength = stream.readInt();

                switch (attribNameUtf.getValue()) {
                    case "Code": {
                        stream.readInt();

                        List<InstructionModel> insns = new ArrayList<>();

                        int codeLength = stream.readInt();
                        int bytesRead = 0;
                        for (int l = 0; l < codeLength; l++) {
                            byte opcode = (byte) stream.readUnsignedByte();
                            bytesRead++;

                            switch (opcode) {
                                case Opcodes.INVOKESTATIC, Opcodes.INVOKEVIRTUAL, Opcodes.INVOKESPECIAL: {
                                    MethodRefEntry entry = (MethodRefEntry) pool.getEntries().get(stream.readUnsignedShort());
                                    bytesRead += 2;

                                    ClassEntry ownerEntry = (ClassEntry) pool.getEntries().get(entry.getOwnerPointer());
                                    NameAndTypeEntry nameAndTypeEntry = (NameAndTypeEntry) pool.getEntries().get(entry.getNameAndTypePointer());

                                    UTFEntry ownerUtf = (UTFEntry) pool.getEntries().get(ownerEntry.getUtfPointer());
                                    UTFEntry nameUtf = (UTFEntry) pool.getEntries().get(nameAndTypeEntry.getNamePointer());
                                    UTFEntry descUtf = (UTFEntry) pool.getEntries().get(nameAndTypeEntry.getDescPointer());

                                    String owner = ownerUtf.getValue();
                                    String name = nameUtf.getValue();
                                    String desc = descUtf.getValue();

                                    insns.add(new InstructionModel(opcode, owner, name, desc));
                                    break;
                                }
                                case Opcodes.GETSTATIC, Opcodes.PUTSTATIC: {
                                    handleFieldInstruction(stream, pool, opcode, bytesRead, insns);
                                    break;
                                }
                                case Opcodes.BIPUSH: {
                                    insns.add(new InstructionModel(Opcodes.BIPUSH, stream.readUnsignedByte()));
                                    bytesRead++;
                                    break;
                                }
                                case Opcodes.LDC: {
                                    handleLDC(stream, pool, opcode, bytesRead, insns);
                                    break;
                                }
                                case Opcodes.GOTO, Opcodes.IFEQ, Opcodes.IFNE,
                                        Opcodes.IFGE, Opcodes.IFLE, Opcodes.IFGT,
                                        Opcodes.IFLT, Opcodes.IF_ICMPEQ, Opcodes.IF_ICMPNE,
                                        Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE: {
                                    // goto only gives us the relative position to the instruction
                                    // but, we need the absolute.
                                    int absolute = (bytesRead - 1) + stream.readUnsignedShort();

                                    LabelModel label = new LabelModel();
                                    label.setPosition(absolute);

                                    insns.add(new InstructionModel(opcode, label));
                                    method.getLabels().add(label);
                                    break;
                                }
                                case Opcodes.RETURN, Opcodes.IRETURN: {
                                    insns.add(new InstructionModel(opcode));
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                        method.setInstructions(insns);
                        methods.add(method);
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        return methods;
    }

    private void handleFieldInstruction(DataInputStream stream, ConstantPool pool, byte opcode, int counter, List<InstructionModel> insns) throws IOException {
        switch (opcode) {
            case Opcodes.GETSTATIC, Opcodes.PUTSTATIC: {
                FieldRefEntry entry = (FieldRefEntry) pool.getEntries().get(stream.readUnsignedShort());
                counter += 2;

                ClassEntry ownerEntry = (ClassEntry) pool.getEntries().get(entry.getOwnerPointer());
                NameAndTypeEntry nameAndTypeEntry = (NameAndTypeEntry) pool.getEntries().get(entry.getNameAndTypePointer());

                UTFEntry ownerUtf = (UTFEntry) pool.getEntries().get(ownerEntry.getUtfPointer());
                UTFEntry nameUtf = (UTFEntry) pool.getEntries().get(nameAndTypeEntry.getNamePointer());
                UTFEntry descUtf = (UTFEntry) pool.getEntries().get(nameAndTypeEntry.getDescPointer());

                String owner = ownerUtf.getValue();
                String name = nameUtf.getValue();
                String desc = descUtf.getValue();

                insns.add(new InstructionModel(opcode, owner, name, desc));
                break;
            }
            default:
                break;
        }
    }

    private void handleLDC(DataInputStream stream, ConstantPool pool, byte opcode, int counter, List<InstructionModel> insns) throws IOException {
        PoolEntry entry = pool.getEntries().get(opcode == Opcodes.LDC ? stream.readUnsignedByte() : stream.readUnsignedShort());

        if (opcode == Opcodes.LDC) counter++;
        else if (opcode == Opcodes.LDC_WIDE) counter += 2;

        if (entry instanceof IntegerEntry) {
            insns.add(new InstructionModel(opcode, ((IntegerEntry) entry).getValue()));
        } else if (entry instanceof StringEntry) {
            UTFEntry utfEntry = (UTFEntry) pool.getEntries().get(((StringEntry) entry).getUtfPointer());
            insns.add(new InstructionModel(opcode, utfEntry.getValue()));
        }
    }

    private List<FieldModel> readFields(DataInputStream stream, ConstantPool pool) throws IOException {
        List<FieldModel> fields = new ArrayList<>();

        int count = stream.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            FieldModel field = new FieldModel();

            int access = stream.readUnsignedShort();
            UTFEntry fieldNameUtf = (UTFEntry) pool.getEntries().get(stream.readUnsignedShort());
            UTFEntry fieldDescUtf = (UTFEntry) pool.getEntries().get(stream.readUnsignedShort());

            field.setName(fieldNameUtf.getValue());
            field.setDesc(fieldDescUtf.getValue());
            field.setAccess(access);

            int attribCount = stream.readUnsignedShort();
            for (int j = 0; j < attribCount; j++) {
                UTFEntry attribNameUtf = (UTFEntry) pool.getEntries().get(stream.readUnsignedShort());
                int attribLength = stream.readInt();

                for (int k = 0; k < attribLength; k++) {
                    switch (attribNameUtf.getValue()) {
                        case "ConstantValue": {
                            int index = stream.readUnsignedShort();
                            PoolEntry entry = pool.getEntries().get(index);

                            if (entry instanceof IntegerEntry) {
                                field.setValue(((IntegerEntry) entry).getValue());
                            } else if (entry instanceof StringEntry) {
                                UTFEntry utfEntry = (UTFEntry) pool.getEntries().get(((StringEntry) entry).getUtfPointer());
                                field.setValue(utfEntry.getValue());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
            }

            fields.add(field);
        }
        return fields;
    }

    private void readPool(DataInputStream stream, ConstantPool pool, ClassModel model, int cpSize) throws IOException {
        for (int i = 1; i < cpSize; i++) {
            byte tag = (byte) stream.readUnsignedByte();
            switch (tag) {
                case EntryConstants.TAG_UTF8: {
                    int length = stream.readUnsignedShort();
                    byte[] readBytes = new byte[length];

                    for (int j = 0; j < length; j++) {
                        readBytes[j] = (byte) stream.readUnsignedByte();
                    }
                    pool.addEntry(new UTFEntry(new String(readBytes)));
                    break;
                }
                case EntryConstants.TAG_NAME_AND_TYPE: {
                    int nameIndex = stream.readUnsignedShort();
                    int descIndex = stream.readUnsignedShort();

                    pool.addEntry(new NameAndTypeEntry(nameIndex, descIndex));
                    break;
                }
                case EntryConstants.TAG_METHOD_REF, EntryConstants.TAG_FIELD_REF: {
                    int classIndex = stream.readUnsignedShort();
                    int nameAndTypeIndex = stream.readUnsignedShort();

                    pool.addEntry(tag == EntryConstants.TAG_METHOD_REF
                            ? new MethodRefEntry(classIndex, nameAndTypeIndex)
                            : new FieldRefEntry(classIndex, nameAndTypeIndex)
                    );
                    break;
                }
                case EntryConstants.TAG_CLASS: {
                    int index = stream.readUnsignedShort();
                    pool.addEntry(new ClassEntry(index));
                    break;
                }
                case EntryConstants.TAG_INTEGER: {
                    pool.addEntry(new IntegerEntry(stream.readInt()));
                    break;
                }
                case EntryConstants.TAG_STRING: {
                    pool.addEntry(new StringEntry(stream.readUnsignedShort()));
                    break;
                }
            }
        }
    }

    private void throwErr(int code) {
        if (code == 1) throw new InvalidClassMagicException("Invalid class file magic.");
        if (code == 2) throw new InvalidConstantPoolException("Invalid constant pool.");
    }

    public byte[] getClassBytes() {
        return classBytes;
    }
}
