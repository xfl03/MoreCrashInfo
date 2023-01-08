package me.xfl03.morecrashinfo.modlauncher;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.LinkedHashSet;
import java.util.Set;

public class CrashReportExtenderTransformer implements ITransformer<MethodNode> {
    private static MethodNode redirectStaticMethod(MethodNode method, String className) {
        Type[] args = Type.getArgumentTypes(method.desc);

        //Find last useful instruction opcode, which can be used in return
        int returnOp = -1;
        for (AbstractInsnNode insnNode : method.instructions) {
            if (insnNode.getOpcode() != -1) {
                returnOp = insnNode.getOpcode();
            }
        }

        method.instructions.clear();
//        method.maxStack = Math.max(argNum, returnOp == Opcodes.RETURN ? 0 : 1);

        for (int i = 0; i < args.length; ++i) {
//            TransformerService.logger.info("ALOAD {}", i);
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, i));
        }
//        TransformerService.logger.info("INVOKESTATIC {} {} {}", className, method.name, method.desc);
        method.instructions.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC, className, method.name, method.desc, false));
//        TransformerService.logger.info("{}", returnOp);
        method.instructions.add(new InsnNode(returnOp));

        return method;
    }

    @Override
    public @NotNull MethodNode transform(MethodNode input, ITransformerVotingContext context) {
        TransformerService.logger.info("Transforming {} {}", input.name, input.desc);
        return redirectStaticMethod(input, "me/xfl03/morecrashinfo/handler/CrashHandler");
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        Set<Target> targets = new LinkedHashSet<>();
        //Minecraft 1.16-
        targets.add(Target.targetMethod(
                "net/minecraftforge/fml/CrashReportExtender",
                "addCrashReportHeader",
                "(Ljava/lang/StringBuilder;Lnet/minecraft/crash/CrashReport;)V"
        ));
        targets.add(Target.targetMethod(
                "net/minecraftforge/fml/CrashReportExtender",
                "generateEnhancedStackTrace",
                "(Ljava/lang/Throwable;)Ljava/lang/String;"
        ));
        //Minecraft 1.17
        targets.add(Target.targetMethod(
                "net/minecraftforge/fmllegacy/CrashReportExtender",
                "addCrashReportHeader",
                "(Ljava/lang/StringBuilder;Lnet/minecraft/CrashReport;)V"
        ));
        targets.add(Target.targetMethod(
                "net/minecraftforge/fmllegacy/CrashReportExtender",
                "generateEnhancedStackTrace",
                "(Ljava/lang/Throwable;)Ljava/lang/String;"
        ));
        //Minecraft 1.18+
        targets.add(Target.targetMethod(
                "net/minecraftforge/logging/CrashReportExtender",
                "addCrashReportHeader",
                "(Ljava/lang/StringBuilder;Lnet/minecraft/CrashReport;)V"
        ));
        targets.add(Target.targetMethod(
                "net/minecraftforge/logging/CrashReportExtender",
                "generateEnhancedStackTrace",
                "(Ljava/lang/Throwable;)Ljava/lang/String;"
        ));
        return targets;
    }
}
