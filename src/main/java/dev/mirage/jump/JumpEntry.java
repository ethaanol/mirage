package dev.mirage.jump;

import dev.mirage.models.LabelModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JumpEntry {
    private final int jumpPos, targetPos;
    private final LabelModel target;
}
