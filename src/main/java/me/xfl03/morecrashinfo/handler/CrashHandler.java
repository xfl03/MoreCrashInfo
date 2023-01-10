package me.xfl03.morecrashinfo.handler;

import cpw.mods.modlauncher.log.TransformingThrowablePatternConverter;
import me.xfl03.morecrashinfo.MoreCrashInfo;
import me.xfl03.morecrashinfo.crash.CoreModList;
import me.xfl03.morecrashinfo.crash.ModList;
import me.xfl03.morecrashinfo.handler.exception.*;
import me.xfl03.morecrashinfo.util.ReflectionHelper;
import net.minecraftforge.fml.ISystemReportExtender;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CrashHandler {
    private static final Map<Class<?>, Function<Throwable, ExceptionHandler>> handlers = new HashMap<>();

    static {
        try {
            MoreCrashInfo.logger.info("CrashHandler.static");
            CrashHandler.registerHandler(VerifyError.class, VerifyErrorHandler::new);

            List<ISystemReportExtender> callables = ReflectionHelper.getCallables();
            callables.add(new ModList());
            callables.add(new CoreModList());
            MoreCrashInfo.logger.debug("Callable list size {}", callables.size());
            MoreCrashInfo.logger.debug("Callable list member {}", callables.stream()
                    .map(ISystemReportExtender::getLabel).collect(Collectors.joining(", ")));

            ReflectionHelper.printCallables();
//            ReflectionHelper.testCallables();
        } catch (Exception e) {
            MoreCrashInfo.logger.warn("Error register callable");
            MoreCrashInfo.logger.warn(e);
        }
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
        return stringbuilder.toString();
    }
}
