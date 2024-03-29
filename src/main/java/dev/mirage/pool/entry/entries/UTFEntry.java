package dev.mirage.pool.entry.entries;

import dev.mirage.pool.entry.PoolEntry;
import dev.mirage.pool.entry.constants.EntryConstants;
import dev.mirage.util.UnsignedStreamWrapper;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UTFEntry implements PoolEntry {
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();
    @Getter
    private String value;

    public UTFEntry(String value) {
        this.value = value;
    }

    @Override
    public byte[] writeEntry() {
        try (UnsignedStreamWrapper wrapper = new UnsignedStreamWrapper(stream)) {
            wrapper.putUnsignedByte(EntryConstants.TAG_UTF8);
            wrapper.putUnsignedShort(value.getBytes(StandardCharsets.UTF_8).length);
            wrapper.write(value.getBytes(StandardCharsets.UTF_8));

            System.out.println(value);
            for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
                System.out.printf("%02x ", b);
            }
            System.out.println("-");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }
}
