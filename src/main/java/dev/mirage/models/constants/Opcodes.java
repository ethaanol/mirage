package dev.mirage.models.constants;

import java.lang.invoke.MethodHandles;

public class Opcodes {
    public static final byte LDC = (byte) 0x12;
    public static final byte LDC_WIDE = (byte) 0x13;
    public static final byte RETURN = (byte) 0xb1;
    public static final byte IRETURN = (byte) 0xac;
    public static final byte BIPUSH = (byte) 0x10;
    public static final byte SIPUSH = (byte) 0x11;
    public static final byte INVOKESTATIC = (byte) 0xb8;
    public static final byte INVOKESPECIAL = (byte) 0xb7;
    public static final byte INVOKEVIRTUAL = (byte) 0xb6;
    public static final byte GETSTATIC = (byte) 0xb2;
    public static final byte PUTSTATIC = (byte) 0xb3;
    public static final byte JMP_S = (byte) 0xCA;

    public static final byte NEW = (byte) 0xbb;
    public static final byte DUP = (byte) 0x59;
    public static final byte ATHROW = (byte) 0xbf;

    // jumps
    public static final byte GOTO = (byte) 0xa7;
    public static final byte IFEQ = (byte) 0x99;
    public static final byte IFNE = (byte) 0x9a;
    public static final byte IFGE = (byte) 0x9c;
    public static final byte IFLT = (byte) 0x9b;
    public static final byte IFGT = (byte) 0x9d;
    public static final byte IFLE = (byte) 0x9e;
    public static final byte IF_ICMPEQ = (byte) 0x9f;
    public static final byte IF_ICMPNE = (byte) 0xa0;
    public static final byte IF_ACMPEQ = (byte) 0xa5;
    public static final byte IF_ACMPNE = (byte) 0xa6;

    public static final byte LABEL = (byte) 0xBA;
}
