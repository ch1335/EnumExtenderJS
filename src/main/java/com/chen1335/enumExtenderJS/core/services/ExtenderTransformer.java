package com.chen1335.enumExtenderJS.core.services;

import com.chen1335.enumExtenderJS.core.JSEnumExtender;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TargetType;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import java.util.Set;

public class ExtenderTransformer implements ITransformer<ClassNode> {
    private final Set<Target<ClassNode>> targets;


    public ExtenderTransformer(Set<Target<ClassNode>> targets) {
        this.targets = targets;
    }

    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {

        JSEnumExtender.process(input);
        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target<ClassNode>> targets() {
        return targets;
    }

    @Override
    public @NotNull TargetType<ClassNode> getTargetType() {
        return TargetType.CLASS;
    }
}
