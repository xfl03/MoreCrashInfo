package me.xfl03.morecrashinfo.handler;

import cpw.mods.modlauncher.log.TransformingThrowablePatternConverter;
import me.xfl03.morecrashinfo.crash.CoreModList;
import me.xfl03.morecrashinfo.crash.ModList;
import me.xfl03.morecrashinfo.handler.exception.*;

import java.util.*;
import java.util.function.Function;

public class CrashHandler {
    private static final Map<Class<?>, Function<Throwable, ExceptionHandler>> handlers = new HashMap<>();

    static {
        CrashHandler.registerHandler(VerifyError.class, VerifyErrorHandler::new);

        net.minecraftforge.fml.CrashReportCallables.registerCrashCallable(new ModList());// 1.16.5- net.minecraftforge.fml.CrashReportExtender
        net.minecraftforge.fml.CrashReportCallables.registerCrashCallable(new CoreModList());// 1.16.5- net.minecraftforge.fml.CrashReportExtender
    }

    private static ExceptionHandler handler;

    public static void registerHandler(Class<?> exception, Function<Throwable, ExceptionHandler> handler) {
        handlers.put(exception, handler);
    }

    // 1.16.5- net.minecraftforge.fml.CrashReportExtender.addCrashReportHeader
    // 1.17.1 net.minecraftforge.fmllegacy.CrashReportExtender.addCrashReportHeader
    public static void addCrashReportHeader(StringBuilder stringbuilder, net.minecraft.CrashReport crashReport) {
        Throwable cause = crashReport.getException();//1.16.5- net.minecraft.crash.CrashReport.func_71505_b
        handler = Optional.ofNullable(handlers.get(cause.getClass()))
                .orElse(ExceptionHandler::new).apply(cause);
        handler.handleHeader(stringbuilder);
    }

    // 1.16.5- net.minecraftforge.fml.CrashReportExtender.generateEnhancedStackTrace
    // 1.17.1 net.minecraftforge.fmllegacy.CrashReportExtender.generateEnhancedStackTrace
    public static String generateEnhancedStackTrace(final Throwable throwable) {
        StringBuilder stringbuilder = new StringBuilder();
        handler.handleException(stringbuilder);
        stringbuilder.append(TransformingThrowablePatternConverter.generateEnhancedStackTrace(throwable));
        return stringbuilder.toString();
    }
}
