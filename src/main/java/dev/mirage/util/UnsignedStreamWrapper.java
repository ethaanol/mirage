package dev.mirage.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UnsignedStreamWrapper extends DataOutputStream {
    public UnsignedStreamWrapper(OutputStream stream) {
        super(stream);
    }

    public void putUnsignedByte(short value) throws IOException {
        value = (short) (value & 0xff);
        writeByte(value);
    }

    public void putUnsignedShort(int value) throws IOException {
        value = value & 0xffff;
        writeShort(value);
    }
}
