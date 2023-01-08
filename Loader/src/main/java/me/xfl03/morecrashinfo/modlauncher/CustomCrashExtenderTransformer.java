package me.xfl03.morecrashinfo.modlauncher;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import java.util.LinkedHashSet;
import java.util.Set;

public class CustomCrashExtenderTransformer implements ITransformer<ClassNode> {
    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        TransformerService.logger.info("Transforming class {}", input.name);
        input.interfaces.removeIf(it -> it.equals("net/minecraftforge/fml/common/ICrashCallable"));
        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        Set<Target> targets = new LinkedHashSet<>();
        targets.add(Target.targetClass("me/xfl03/morecrashinfo/crash/CustomCrashExtender"));
        return targets;
    }
}
