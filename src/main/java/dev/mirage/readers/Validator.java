package dev.mirage.readers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Validator {
    public static boolean validateMagic(byte[] bytes) throws IOException {
        return new DataInputStream(new ByteArrayInputStream(bytes))
                .readInt() == 0xCAFEBABE;
    }
}
