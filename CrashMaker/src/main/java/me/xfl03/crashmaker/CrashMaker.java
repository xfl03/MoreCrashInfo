package me.xfl03.crashmaker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("crashmaker")
public class CrashMaker {
    private static final Logger LOGGER = LogManager.getLogger();

    public CrashMaker() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.debug("MoreCrashInfo: PreInit Begin.");

        LOGGER.debug("MoreCrashInfo: PreInit End.");
    }

//    @SubscribeEvent
//    public void onServerStarting(FMLServerStartingEvent event) {
//        // Uncomment to cause java.lang.RuntimeException
//        //CrashMaker.makeCrash();
//
//        // Uncomment in crashtransformers.js can cause special crash
//        CrashMaker.doNothing();
//    }
    public static void makeCrash() {
        throw new RuntimeException(new RuntimeException(new RuntimeException("Crashed for test.")));
    }

    public static void doNothing() {

    }
}
