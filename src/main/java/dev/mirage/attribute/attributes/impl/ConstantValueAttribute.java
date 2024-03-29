package dev.mirage.attribute.attributes.impl;

import dev.mirage.attribute.attributes.Attribute;
import dev.mirage.models.FieldModel;
import dev.mirage.pool.ConstantPool;

public class ConstantValueAttribute implements Attribute {
    private FieldModel model;
    private ConstantPool pool;

    public ConstantValueAttribute(FieldModel model, ConstantPool pool) {
        this.model = model;
        this.pool = pool;
    }

    @Override
    public byte[] writeAttribute() {
        if (model.getValue() != null) {
            Object value = model.getValue();
            int valueIndex = 0;

            if (value instanceof Integer)
                valueIndex = pool.requestNcon((int) value);
            else if (value instanceof Boolean)
                valueIndex = pool.requestNcon((boolean) value ? 1 : 0);
            else if (value instanceof String)
                valueIndex = pool.requestString((String) value);
            else throw new IllegalArgumentException("mirage : unknown value passed to field");

            return new byte[]{(byte) ((valueIndex >> 8) & 0xff), (byte) (valueIndex & 0xff)};
        }
        return new byte[]{};
    }

    public FieldModel getModel() {
        return model;
    }
}
