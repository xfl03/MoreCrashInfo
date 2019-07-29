package me.xfl03.morecrashinfo.util;

import net.minecraftforge.coremod.CoreMod;
import net.minecraftforge.coremod.CoreModEngine;
import net.minecraftforge.coremod.CoreModProvider;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.CoreModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.coremod.ICoreModFile;
import net.minecraftforge.forgespi.coremod.ICoreModProvider;

import java.util.List;

public class ModHelper {
    public static List<CoreMod> getCoreModList() throws Exception {
        //Get and convert coremod provider
        ICoreModProvider provider = FMLLoader.getCoreModProvider();
        CoreModProvider p = (CoreModProvider) provider;

        //Reflect to get coremod list
        CoreModEngine engine = (CoreModEngine) ReflectionHelper.getField(p, "engine");
        List<CoreMod> list = (List<CoreMod>) ReflectionHelper.getField(engine, "coreMods");
        return list;
    }

    public static String getSource(ModInfo it) {
        if (it.getOwningFile() == null || it.getOwningFile().getFile() == null) return "Not Found";
        return it.getOwningFile().getFile().getFileName();
    }

    public static String getSource(ICoreModFile file) {
        if (file.getPath() == null) return "Not Found";
        return file.getPath().getFileName().toString();
    }

    public static String getName(ICoreModFile file) {
        String name = "Not Found";
        try {
            CoreModFile f = (CoreModFile) file;
            name = (String) ReflectionHelper.getField(f, "name");
        } catch (Exception ignored) {
        }
        return name;
    }
}
