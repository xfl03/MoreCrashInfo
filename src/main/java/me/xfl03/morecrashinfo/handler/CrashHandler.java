package me.xfl03.morecrashinfo.handler;

import cpw.mods.modlauncher.log.TransformingThrowablePatternConverter;
import me.xfl03.morecrashinfo.MoreCrashInfo;
import me.xfl03.morecrashinfo.crash.*;
import me.xfl03.morecrashinfo.handler.exception.*;
import me.xfl03.morecrashinfo.util.ReflectionHelper;
import me.xfl03.morecrashinfo.util.VersionUtil;

import java.util.*;
import java.util.function.Function;

public class CrashHandler {
    private static final Map<Class<?>, Function<Throwable, ExceptionHandler>> handlers = new HashMap<>();

    private static void initHandlers() {
        registerHandler(VerifyError.class, VerifyErrorHandler::new);
    }

    static {
        initHandlers();
        CallableManager.initCallables();
    }

    private static ExceptionHandler handler;

    public static void registerHandler(Class<?> exception, Function<Throwable, ExceptionHandler> handler) {
        handlers.put(exception, handler);
    }

    // 1.16.5- net.minecraftforge.fml.CrashReportExtender.addCrashReportHeader
    // 1.17.1 net.minecraftforge.fmllegacy.CrashReportExtender.addCrashReportHeader
    public static void addCrashReportHeader(StringBuilder stringbuilder, net.minecraft.CrashReport crashReport) {
        MoreCrashInfo.logger.debug("CrashHandler.addCrashReportHeader");
        ReflectionHelper.printCallables();
        Throwable cause = crashReport.getException();//1.16.5- net.minecraft.crash.CrashReport.func_71505_b
        handler = Optional.ofNullable(handlers.get(cause.getClass()))
                .orElse(ExceptionHandler::new).apply(cause);
        handler.handleHeader(stringbuilder);
    }

    // 1.16.5- net.minecraftforge.fml.CrashReportExtender.generateEnhancedStackTrace
    // 1.17.1 net.minecraftforge.fmllegacy.CrashReportExtender.generateEnhancedStackTrace
    public static String generateEnhancedStackTrace(final Throwable throwable) {
        MoreCrashInfo.logger.debug("CrashHandler.generateEnhancedStackTrace");
        ReflectionHelper.printCallables();
        StringBuilder stringbuilder = new StringBuilder();
        handler.handleException(stringbuilder);
        stringbuilder.append(TransformingThrowablePatternConverter.generateEnhancedStackTrace(throwable));
        //In 1.13-1.14, add callables message here
        if (VersionUtil.getMinecraftMajorVersion() <= 14) {
            stringbuilder.append(CallableManager.getCallableMessages());
        }
        return stringbuilder.toString();
    }
}
