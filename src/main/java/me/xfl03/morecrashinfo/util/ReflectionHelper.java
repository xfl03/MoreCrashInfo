package me.xfl03.morecrashinfo.util;

import me.xfl03.morecrashinfo.MoreCrashInfo;
import net.minecraftforge.fml.CrashReportCallables;
import net.minecraftforge.fml.ISystemReportExtender;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionHelper {
    public static Object getField(Object o, String name) throws Exception {
        Class c = o.getClass();
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(o);
    }

    public static List<ISystemReportExtender> getCallables() {
        try {
            Field field = CrashReportCallables.class.getDeclaredField("crashCallables");// 1.16.5- net.minecraftforge.fml.CrashReportExtender
            field.setAccessible(true);
            return (List<ISystemReportExtender>) field.get(null);
        } catch (Exception e) {
            MoreCrashInfo.logger.warn("Error get callable");
            MoreCrashInfo.logger.warn(e);
            return Collections.emptyList();
        }
    }

    public static void printCallables() {
        List<ISystemReportExtender> callables = getCallables();
        MoreCrashInfo.logger.info("Registered {} callables", callables.size());
        MoreCrashInfo.logger.info(callables.stream()
                .map(ISystemReportExtender::getLabel).collect(Collectors.joining(", ")));
    }

    public static void testCallables() {
        List<ISystemReportExtender> callables = getCallables();
        for (ISystemReportExtender callable : callables) {
            try {
                Method method = ISystemReportExtender.class.getMethod("call");
                MoreCrashInfo.logger.info(method.invoke(callable));
            } catch (Exception e) {
                MoreCrashInfo.logger.warn("Error call callable {}", callable.getLabel());
                e.printStackTrace();
            }
        }
    }
}
