package me.xfl03.morecrashinfo;

import me.xfl03.morecrashinfo.crash.CoreModListExtender;
import me.xfl03.morecrashinfo.crash.ModListExtender;
import net.minecraftforge.fml.CrashReportExtender;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("morecrashinfo")
public class MoreCrashInfo
{
    private static final Logger LOGGER = LogManager.getLogger();

    public MoreCrashInfo() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.debug("MoreCrashInfo: PreInit Begin.");

        CrashReportExtender.registerCrashCallable(new ModListExtender());
        CrashReportExtender.registerCrashCallable(new CoreModListExtender());

        LOGGER.debug("MoreCrashInfo: PreInit End.");
    }
}
