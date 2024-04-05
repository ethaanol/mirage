package dev.mirage;

import dev.mirage.models.*;
import dev.mirage.models.constants.ClassConstants;
import dev.mirage.models.constants.FieldConstants;
import dev.mirage.models.constants.MethodConstants;
import dev.mirage.models.constants.Opcodes;
import dev.mirage.readers.GlobalModelReader;
import dev.mirage.writers.IndividualModelWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entry {
    public static void main(String[] args) throws IOException {
        ClassModel klass = new ClassModel(
                "Funny",
                "java/lang/Object",
                ClassConstants.PUBLIC,
                ClassConstants.JVM8
        );

        MethodModel main = new MethodModel(
                "main",
                "([Ljava/lang/String;)V",
                MethodConstants.PUBLIC | MethodConstants.STATIC
        );

        main.attach(new InstructionModel[]{
                new InstructionModel(
                        Opcodes.GETSTATIC,
                        "java/lang/System",
                        "out",
                        "Ljava/io/PrintStream;"
                ),
                new InstructionModel(Opcodes.LDC, "before goto invoke"),
                new InstructionModel(
                        Opcodes.INVOKEVIRTUAL,
                        "java/io/PrintStream",
                        "println",
                        "(Ljava/lang/String;)V"
                )
        });

        // if the method with JMP_S isnt used the relative position can be completely random.
        // if used: 152 to jump to the method in front, -312 to jump to the method behind.
        main.attach(new InstructionModel[]{
                new InstructionModel(Opcodes.JMP_S, (short) (152 * 2), true)
        });

        main.attach(new InstructionModel[]{
                new InstructionModel(
                        Opcodes.GETSTATIC,
                        "java/lang/System",
                        "out",
                        "Ljava/io/PrintStream;"
                ),
                new InstructionModel(Opcodes.LDC, "after goto invoke"),
                new InstructionModel(
                        Opcodes.INVOKEVIRTUAL,
                        "java/io/PrintStream",
                        "println",
                        "(Ljava/lang/String;)V"
                )
        });


        MethodModel model = new MethodModel(
                "test1",
                "()V",
                MethodConstants.PUBLIC | MethodConstants.STATIC
        );

        model.attach(new InstructionModel[]{
                new InstructionModel(
                        Opcodes.GETSTATIC,
                        "java/lang/System",
                        "out",
                        "Ljava/io/PrintStream;"
                ),
                new InstructionModel(Opcodes.LDC, "test1"),
                new InstructionModel(
                        Opcodes.INVOKEVIRTUAL,
                        "java/io/PrintStream",
                        "println",
                        "(Ljava/lang/String;)V"
                )
        });

        MethodModel model1 = new MethodModel(
                "test2",
                 "()V",
                MethodConstants.PUBLIC | MethodConstants.STATIC
        );

        model1.attach(new InstructionModel[]{
                new InstructionModel(
                        Opcodes.GETSTATIC,
                        "java/lang/System",
                        "out",
                        "Ljava/io/PrintStream;"
                ),
                new InstructionModel(Opcodes.LDC, "test2"),
                new InstructionModel(
                        Opcodes.INVOKEVIRTUAL,
                        "java/io/PrintStream",
                        "println",
                        "(Ljava/lang/String;)V"
                )
        });

        model1.attach(new InstructionModel[]{
                new InstructionModel(Opcodes.NEW, "java/lang/Exception"),
                new InstructionModel(Opcodes.DUP),
                new InstructionModel(Opcodes.LDC, "woah"),
                new InstructionModel(
                        Opcodes.INVOKESPECIAL,
                        "java/lang/Exception",
                        "<init>",
                        "(Ljava/lang/String;)V"
                ),
                new InstructionModel(Opcodes.ATHROW)
        });

        klass.getMethods().add(main);
        klass.getMethods().add(model);
        klass.getMethods().add(model1);

        IndividualModelWriter writer = new IndividualModelWriter(klass);
        writer.initiateModel();
        byte[] bytes = writer.finalizeModel();

        Files.write(Paths.get("Funny.class"), bytes);
    }

    public static void buildReadTest() throws IOException {
        GlobalModelReader reader = new GlobalModelReader(Files.readAllBytes(Paths.get("Testing.class")));

        ClassModel klass = reader.accept();

        klass.getFields().add(new FieldModel("test", "I", 32, FieldConstants.PUBLIC));

        IndividualModelWriter writer = new IndividualModelWriter(klass);

        writer.initiateModel();
        byte[] bytes = writer.finalizeModel();

        Files.write(Paths.get("PassThrough.class"), bytes);
    }

    public static void buildTest() throws IOException {
        ClassModel klass = new ClassModel(
                "Testing",
                "java/lang/Object",
                ClassConstants.PUBLIC,
                ClassConstants.JVM8
        );

        LabelModel labelA = new LabelModel();
        InstructionModel[] insns = {
                new InstructionModel(Opcodes.IFNE, labelA),
                new InstructionModel(Opcodes.LABEL, labelA),
                new InstructionModel(Opcodes.LDC, 32)
        };

        MethodModel model = new MethodModel(
                "main",
                "([Ljava/lang/String;)V",
                MethodConstants.PUBLIC | MethodConstants.STATIC
        );
        model.attach(insns);

        klass.getMethods().add(model);

        IndividualModelWriter writer = new IndividualModelWriter(klass);

        writer.initiateModel();
        byte[] bytes = writer.finalizeModel();

        Files.write(Paths.get("Testing.class"), bytes);
    }
}
