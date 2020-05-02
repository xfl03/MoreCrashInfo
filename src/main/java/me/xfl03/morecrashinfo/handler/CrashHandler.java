package me.xfl03.morecrashinfo.handler;

import cpw.mods.modlauncher.log.TransformingThrowablePatternConverter;
import me.xfl03.morecrashinfo.crash.CoreModList;
import me.xfl03.morecrashinfo.crash.ModList;
import me.xfl03.morecrashinfo.handler.exception.*;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.CrashReportExtender;

import java.util.*;
import java.util.function.Function;

public class CrashHandler {
    private static Map<Class, Function<Throwable, ExceptionHandler>> handlers = new HashMap<>();

    static {
        CrashHandler.registerHandler(VerifyError.class, VerifyErrorHandler::new);

        CrashReportExtender.registerCrashCallable(new ModList());
        CrashReportExtender.registerCrashCallable(new CoreModList());
    }

    private static ExceptionHandler handler;

    public static void registerHandler(Class exception, Function<Throwable, ExceptionHandler> handler) {
        handlers.put(exception, handler);
    }

    // net.minecraftforge.fml.CrashReportExtender.addCrashReportHeader
    public static void addCrashReportHeader(StringBuilder stringbuilder, CrashReport crashReport) {
        Throwable cause = crashReport.getCrashCause();
        handler = Optional.ofNullable(handlers.get(cause.getClass()))
                .orElse(ExceptionHandler::new).apply(cause);
        handler.handleHeader(stringbuilder);
    }

    // net.minecraftforge.fml.CrashReportExtender.generateEnhancedStackTrace
    public static String generateEnhancedStackTrace(final Throwable throwable) {
        StringBuilder stringbuilder = new StringBuilder();
        handler.handleException(stringbuilder);
        stringbuilder.append(TransformingThrowablePatternConverter.generateEnhancedStackTrace(throwable));
        return stringbuilder.toString();
    }
}
