package me.xfl03.morecrashinfo.modlauncher;

import cpw.mods.modlauncher.Launcher;

import java.lang.reflect.Field;

public class VersionUtil {
    public static String getMinecraftVersion() {
        try {
            String[] args = (String[]) getField(getField(Launcher.INSTANCE, "argumentHandler"), "args");
//            TransformerService.logger.info(args);
            for (int i = 0; i < args.length - 1; ++i) {
                if (args[i].equalsIgnoreCase("--fml.mcversion")) {
                    return args[i + 1];
                }
            }
        } catch (Exception e) {
            TransformerService.logger.warn("Error getting minecraft version");
            TransformerService.logger.warn(e);
        }
        TransformerService.logger.warn("Minecraft version not found");
        return "1.19.3";
    }

    public static int getMinecraftMajorVersion() {
        String[] s = getMinecraftVersion().split("\\.");
        return Integer.parseInt(s[1]);
    }

    private static Object getField(Object instance, String field) throws Exception {
        Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(instance);
    }
}
