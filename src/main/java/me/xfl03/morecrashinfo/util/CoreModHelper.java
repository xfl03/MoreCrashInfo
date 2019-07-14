package me.xfl03.morecrashinfo.util;

import net.minecraftforge.coremod.CoreMod;
import net.minecraftforge.coremod.CoreModEngine;
import net.minecraftforge.coremod.CoreModProvider;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.coremod.ICoreModProvider;

import java.util.List;

public class CoreModHelper {
    public static List<CoreMod> getCoreModList() throws Exception {
        //Get and convert coremod provider
        ICoreModProvider provider = FMLLoader.getCoreModProvider();
        CoreModProvider p = (CoreModProvider) provider;

        //Reflect to get coremod list
        CoreModEngine engine = (CoreModEngine) ReflectionHelper.getField(p, "engine");
        List<CoreMod> list = (List<CoreMod>) ReflectionHelper.getField(engine, "coreMods");
        return list;
    }
}
