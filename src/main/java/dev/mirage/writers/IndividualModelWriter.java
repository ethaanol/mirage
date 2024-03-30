package dev.mirage.writers;

import dev.mirage.attribute.Attributes;
import dev.mirage.attribute.attributes.impl.CodeAttribute;
import dev.mirage.attribute.attributes.impl.ConstantValueAttribute;
import dev.mirage.models.*;
import dev.mirage.models.constants.Opcodes;
import dev.mirage.pool.ConstantPool;
import dev.mirage.pool.entry.entries.ClassEntry;
import dev.mirage.util.UnsignedStreamWrapper;
import dev.mirage.writers.constants.WriterConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndividualModelWriter {
    private ClassModel target;
    private ConstantPool pool = new ConstantPool();

    public IndividualModelWriter(ClassModel target) {
        this.target = target;
    }

    public void initiateModel() {
        try (UnsignedStreamWrapper wrapper = new UnsignedStreamWrapper(target.getStream())) {
            wrapper.writeInt(WriterConstants.MAGIC);
            wrapper.writeInt(target.getVersion());

            pool.addEntry(null);

            int nameIndex = pool.requestUTF(target.getName());
            int classIndex = pool.addEntry(new ClassEntry(nameIndex));
            int superNameIndex = pool.requestUTF(target.getSuperClass());
            int superClassIndex = pool.addEntry(new ClassEntry(superNameIndex));

            mergeFieldModels(target.getFields());
            mergeMethodModels(target.getMethods());

            byte[] fieldBytes = new byte[]{};
            byte[] methodBytes = new byte[]{};

            if (!target.getFields().isEmpty())
                fieldBytes = assembleFields(target.getFields());
            if (!target.getMethods().isEmpty())
                methodBytes = assembleMethods(target.getMethods());

            wrapper.putUnsignedShort(pool.getPoolCount());
            wrapper.write(pool.writePool());

            wrapper.putUnsignedShort(target.getAccess());
            wrapper.putUnsignedShort(classIndex);
            wrapper.putUnsignedShort(superClassIndex);
            wrapper.write(new byte[]{0x00, 0x00});

            if (!target.getFields().isEmpty()) wrapper.write(fieldBytes);
            else wrapper.putUnsignedShort(0);
            if (!target.getMethods().isEmpty()) wrapper.write(methodBytes);
            else wrapper.putUnsignedShort(0);

            wrapper.write(new byte[]{0x00, 0x00});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mergeFieldModels(List<FieldModel> models) {
        for (FieldModel model : models) {
            pool.requestUTF(model.getName());
            pool.requestUTF(model.getDesc());

            if  (model.getValue() != null) {
                pool.requestUTF("ConstantValue");
                Object value = model.getValue();

                if (value instanceof Integer) {
                    pool.requestNcon((int) value);
                } else if (value instanceof Boolean) {
                    pool.requestNcon((boolean) value ? 1 : 0);
                } else if (value instanceof String) {
                    pool.requestString((String) value);
                } else
                    throw new IllegalArgumentException("mirage : failed to merge field model with name `" + model.getName() + "`.");
            }
        }
    }

    public void mergeMethodModels(List<MethodModel> models) {
        for (MethodModel model : models) {
            pool.requestUTF(model.getName());
            pool.requestUTF(model.getDesc());

            if (!model.getInstructions().isEmpty()) {
                pool.requestUTF("Code");
            }
        }
    }

    public byte[] assembleFields(List<FieldModel> models) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (UnsignedStreamWrapper wrapper = new UnsignedStreamWrapper(stream)) {
            wrapper.putUnsignedShort(models.size());

            for (FieldModel model : models) {
                Attributes attributes = new Attributes(pool);

                wrapper.putUnsignedShort(model.getAccess());
                wrapper.putUnsignedShort(pool.requestUTF(model.getName()));
                wrapper.putUnsignedShort(pool.requestUTF(model.getDesc()));

                attributes.getAttributes().add(new ConstantValueAttribute(model, pool));

                wrapper.putUnsignedShort(attributes.getAttributes().size());
                wrapper.write(attributes.writeAttributes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    public byte[] assembleMethods(List<MethodModel> models) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (UnsignedStreamWrapper wrapper = new UnsignedStreamWrapper(stream)) {
            wrapper.putUnsignedShort(models.size());

            for (MethodModel model : models) {
                Attributes attributes = new Attributes(pool);

                wrapper.putUnsignedShort(model.getAccess());
                wrapper.putUnsignedShort(pool.requestUTF(model.getName()));
                wrapper.putUnsignedShort(pool.requestUTF(model.getDesc()));

                attributes.getAttributes().add(new CodeAttribute(model, pool));

                wrapper.putUnsignedShort(attributes.getAttributes().size());
                wrapper.write(attributes.writeAttributes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    public byte[] finalizeModel() {
        return target.getStream().toByteArray();
    }
}
