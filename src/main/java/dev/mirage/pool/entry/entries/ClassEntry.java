package dev.mirage.pool.entry.entries;

import dev.mirage.pool.entry.PoolEntry;
import dev.mirage.pool.entry.constants.EntryConstants;
import dev.mirage.util.UnsignedStreamWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ClassEntry implements PoolEntry {
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();
    private int utfPointer;

    public ClassEntry(int utfPointer) {
        this.utfPointer = utfPointer;
    }

    @Override
    public byte[] writeEntry() {
        try (UnsignedStreamWrapper wrapper = new UnsignedStreamWrapper(stream)) {
            wrapper.putUnsignedByte(EntryConstants.TAG_CLASS);
            wrapper.putUnsignedShort((short) utfPointer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    public int getUtfPointer() {
        return utfPointer;
    }
}
