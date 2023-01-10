package me.xfl03.morecrashinfo.util;

import me.xfl03.morecrashinfo.MoreCrashInfo;
import net.minecraftforge.fml.loading.FMLLoader;

import java.lang.reflect.Field;

public class VersionUtil {
    public static String getMinecraftVersion() {
        Class<FMLLoader> fmlLoaderClass = FMLLoader.class;
        try {
            //1.13-1.16
            Field mcVersionField = fmlLoaderClass.getDeclaredField("mcVersion");
            mcVersionField.setAccessible(true);
            return (String) mcVersionField.get(null);
        } catch (NoSuchFieldException e) {
            //1.16-1.19
            return FMLLoader.versionInfo().mcVersion();
        } catch (IllegalAccessException e) {
            MoreCrashInfo.logger.warn("Error getting mcVersion");
            MoreCrashInfo.logger.warn(e);
            return "1.16.5";
        }
    }

    public static int getMinecraftMajorVersion() {
        String[] s = getMinecraftVersion().split("\\.");
        return Integer.parseInt(s[1]);
    }
}
