package me.xfl03.morecrashinfo;

import me.xfl03.morecrashinfo.crash.CoreModList;
import me.xfl03.morecrashinfo.crash.ModList;
import me.xfl03.morecrashinfo.handler.CrashHandler;
import me.xfl03.morecrashinfo.handler.exception.VerifyErrorHandler;
import me.xfl03.morecrashinfo.util.CrashMaker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.CrashReportExtender;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("morecrashinfo")
public class MoreCrashInfo
{
    private static final Logger LOGGER = LogManager.getLogger();

    public MoreCrashInfo() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.debug("MoreCrashInfo: PreInit Begin.");

        CrashReportExtender.registerCrashCallable(new ModList());
        CrashReportExtender.registerCrashCallable(new CoreModList());

        CrashHandler.registerHandler(VerifyError.class, VerifyErrorHandler::new);

        LOGGER.debug("MoreCrashInfo: PreInit End.");
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // Uncomment to cause java.lang.RuntimeException
        //CrashMaker.makeCrash();

        // Uncomment in crashtransformers.js can cause special crash
        CrashMaker.doNothing();
    }
}
