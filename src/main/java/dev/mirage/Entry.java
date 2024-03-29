package dev.mirage;

import dev.mirage.models.ClassModel;
import dev.mirage.models.InstructionModel;
import dev.mirage.models.LabelModel;
import dev.mirage.models.MethodModel;
import dev.mirage.models.constants.ClassConstants;
import dev.mirage.models.constants.MethodConstants;
import dev.mirage.models.constants.Opcodes;
import dev.mirage.writers.IndividualModelWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Entry {
    public static void main(String[] args) throws IOException {
        IndividualModelWriter writer = new IndividualModelWriter(new ClassModel(
                "Testing",
                "java/lang/Object",
                ClassConstants.PUBLIC,
                ClassConstants.JVM8
        ));
        MethodModel method = new MethodModel(
                "test",
                "()V",
                MethodConstants.PUBLIC | MethodConstants.STATIC
        );

        LabelModel lbl = new LabelModel();
        method.attach(new InstructionModel(Opcodes.GOTO, lbl));

        // 0xBA - new label opcode
        method.attach(new InstructionModel((byte) 0xBA, lbl));
        method.attach(new InstructionModel(Opcodes.LDC, 18));

        writer.attachModel(method);

        writer.initiateModel();
        byte[] bytes = writer.finalizeModel();

        Files.write(Paths.get("Testing.class"), bytes);
    }
}
