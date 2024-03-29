package dev.mirage.pool.entry.entries;

import dev.mirage.pool.entry.PoolEntry;
import dev.mirage.pool.entry.constants.EntryConstants;
import dev.mirage.util.UnsignedStreamWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FieldRefEntry implements PoolEntry {
    private int ownerPointer, nameAndTypePointer;

    public FieldRefEntry(int ownerPointer, int nameAndTypePointer) {
        this.ownerPointer = ownerPointer;
        this.nameAndTypePointer = nameAndTypePointer;
    }

    @Override
    public byte[] writeEntry() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (UnsignedStreamWrapper wrapper = new UnsignedStreamWrapper(stream)) {
            wrapper.putUnsignedByte(EntryConstants.TAG_FIELD_REF);
            wrapper.putUnsignedShort(ownerPointer);
            wrapper.putUnsignedShort(nameAndTypePointer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    public int getOwnerPointer() {
        return ownerPointer;
    }

    public int getNameAndTypePointer() {
        return nameAndTypePointer;
    }
}
