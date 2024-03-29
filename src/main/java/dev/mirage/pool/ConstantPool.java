package dev.mirage.pool;

import dev.mirage.pool.entry.PoolEntry;
import dev.mirage.pool.entry.entries.IntegerEntry;
import dev.mirage.pool.entry.entries.StringEntry;
import dev.mirage.pool.entry.entries.UTFEntry;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ConstantPool {
    @Setter
    @Getter
    private List<PoolEntry> entries = new LinkedList<>();

    public byte[] writePool() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        entries.forEach(entry -> {
            if (entry == null) return;

            try {
                stream.write(entry.writeEntry());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return stream.toByteArray();
    }

    public int getPoolCount() {
        return entries.size();
    }

    public int addEntry(PoolEntry entry) {
        entries.add(entry);
        return entries.size() - 1;
    }

    public int requestUTF(String str) {
        int entryCount = entries.size();

        for (int i = 0; i < entryCount; i++) {
            PoolEntry entry = (PoolEntry) entries.get(i);
            if (!(entry instanceof UTFEntry)) continue;

            if (((UTFEntry) entry).getValue().equals(str)) return i;
        }

        entries.add(new UTFEntry(str));
        return entryCount;
    }

    public int requestString(String str) {
        int entryCount = entries.size();

        int candidate = requestUTF(str);

        for (int i = 1; i < entryCount; i++) {
            PoolEntry entry = entries.get(i);
            if (!(entry instanceof StringEntry)) continue;

            if (((StringEntry) entry).getUtfPointer() == candidate) return i;
        }

        entries.add(new StringEntry(candidate));
        return entryCount;
    }

    public int requestNcon(int value) {
        int entryCount = entries.size();

        for (int i = 0; i < entryCount; i++) {
            PoolEntry entry = entries.get(i);

            if (!(entry instanceof IntegerEntry)) continue;

            if (((IntegerEntry) entry).getValue() == value) return i;
        }
        entries.add(new IntegerEntry(value));
        return entryCount;
    }
}
