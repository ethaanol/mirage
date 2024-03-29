package dev.mirage.models.constants;

public class Opcodes {
    public static final byte LDC = (byte) 0x12;
    public static final byte LDC_WIDE = (byte) 0x13;
    public static final byte RETURN = (byte) 0xb1;
    public static final byte IRETURN = (byte) 0xac;
    public static final byte BIPUSH = (byte) 0x10;
    public static final byte SIPUSH = (byte) 0x11;
    public static final byte INVOKESTATIC = (byte) 0xb8;
    public static final byte INVOKEVIRTUAL = (byte) 0xb6;
    public static final byte GETSTATIC = (byte) 0xb2;
    public static final byte GOTO = (byte) 0xa7;
}
