package dev.mirage.pool.entry.entries;

import dev.mirage.pool.entry.PoolEntry;
import dev.mirage.pool.entry.constants.EntryConstants;
import dev.mirage.util.UnsignedStreamWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class IntegerEntry implements PoolEntry {
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();
    private int value;

    public IntegerEntry(int value) {
        this.value = value;
    }

    @Override
    public byte[] writeEntry() {
        try (UnsignedStreamWrapper wrapper = new UnsignedStreamWrapper(stream)) {
            wrapper.putUnsignedByte(EntryConstants.TAG_INTEGER);
            wrapper.writeInt(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    public int getValue() {
        return value;
    }
}
