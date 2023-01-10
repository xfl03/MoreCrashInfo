package me.xfl03.morecrashinfo.crash;

import me.xfl03.morecrashinfo.MoreCrashInfo;
import me.xfl03.morecrashinfo.util.ReflectionHelper;
import me.xfl03.morecrashinfo.util.VersionUtil;
import net.minecraftforge.fml.ISystemReportExtender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallableManager {
    private static final List<CommonCallable> callables = new ArrayList<>();

    public static void initCallables() {
        try {
            MoreCrashInfo.logger.debug("CrashHandler.initCallables");
            int majorVersion = VersionUtil.getMinecraftMajorVersion();

            //From 1.16, Forge formatted mod list
            if (majorVersion <= 15) {
                callables.add(new ModList());
            }
            callables.add(new CoreModList());
            //Mixin added in 1.15.2-31.2.44 and 1.16.1-32.0.72
            if (majorVersion >= 16 || VersionUtil.getMinecraftVersion().equals("1.15.2")) {
                callables.add(new MixinList());
            }
            MoreCrashInfo.logger.debug("Callable list size {}", callables.size());
            MoreCrashInfo.logger.debug("Callable list member {}", callables.stream()
                    .map(ISystemReportExtender::getLabel).collect(Collectors.joining(", ")));
            MoreCrashInfo.logger.info(getCallableMessages());

            //1.13-1.14, we added these message behind stacktrace
            if (majorVersion >= 15) {
                //register callables to Forge
                List<ISystemReportExtender> callables0 = ReflectionHelper.getCallables();
                callables0.addAll(callables);
                ReflectionHelper.printCallables();
//            ReflectionHelper.testCallables();
            }
        } catch (Exception e) {
            MoreCrashInfo.logger.warn("Error register callable");
            MoreCrashInfo.logger.warn(e);
        }
    }

    public static String getCallableMessages() {
        StringBuilder sb = new StringBuilder("\n");
        for (CommonCallable callable : callables) {
            sb.append("\t");
            sb.append(callable.getLabel());
            sb.append(":");
            sb.append(callable.get());
            sb.append("\n");
        }
        return sb.toString();
    }
}
