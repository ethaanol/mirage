package dev.mirage.jump;

import dev.mirage.models.LabelModel;

public class JumpEntry {
    private final int jumpPos, targetPos;
    private final LabelModel target;

    public JumpEntry(int jumpPos, int targetPos, LabelModel target) {
        this.jumpPos = jumpPos;
        this.targetPos = targetPos;
        this.target = target;
    }

    public int getJumpPos() {
        return jumpPos;
    }

    public int getTargetPos() {
        return targetPos;
    }

    public LabelModel getTarget() {
        return target;
    }
}
