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
        buildReadTest();
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
        List<InstructionModel> insns = Arrays.asList(
                new InstructionModel(Opcodes.GOTO, labelA),
                new InstructionModel(Opcodes.LABEL, labelA),
                new InstructionModel(Opcodes.LDC, 32)
        );

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
