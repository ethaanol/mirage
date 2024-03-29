package dev.mirage.pool.entry.entries;

import dev.mirage.pool.entry.PoolEntry;
import dev.mirage.pool.entry.constants.EntryConstants;
import dev.mirage.util.UnsignedStreamWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@AllArgsConstructor
public class NameAndTypeEntry implements PoolEntry {
    @Getter
    @Setter
    private int namePointer, descPointer;

    @Override
    public byte[] writeEntry() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (UnsignedStreamWrapper wrapper = new UnsignedStreamWrapper(stream)) {
            wrapper.putUnsignedByte(EntryConstants.TAG_NAME_AND_TYPE);
            wrapper.putUnsignedShort(namePointer);
            wrapper.putUnsignedShort(descPointer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }
}
