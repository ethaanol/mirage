package dev.mirage.attribute;

import dev.mirage.attribute.attributes.Attribute;
import dev.mirage.attribute.attributes.impl.CodeAttribute;
import dev.mirage.attribute.attributes.impl.ConstantValueAttribute;
import dev.mirage.pool.ConstantPool;
import dev.mirage.util.UnsignedStreamWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Attributes {
    private final List<Attribute> attributes = new ArrayList<>();
    private ConstantPool pool;

    public Attributes(ConstantPool pool) {
        this.pool = pool;
    }

    public byte[] writeAttributes() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        attributes.forEach(attribute -> {
            try (UnsignedStreamWrapper wrapper = new UnsignedStreamWrapper(stream)) {
                byte[] attrib = attribute.writeAttribute();

                if (attribute instanceof ConstantValueAttribute) {
                    wrapper.putUnsignedShort(pool.requestUTF("ConstantValue"));
                    wrapper.writeInt(attrib.length);
                    wrapper.write(attrib);
                } else if (attribute instanceof CodeAttribute) {
                    wrapper.putUnsignedShort(pool.requestUTF("Code"));
                    wrapper.writeInt(attrib.length);
                    wrapper.write(attrib);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return stream.toByteArray();
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
