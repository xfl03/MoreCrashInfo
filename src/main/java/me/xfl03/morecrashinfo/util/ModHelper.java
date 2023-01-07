package me.xfl03.morecrashinfo.util;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import net.minecraftforge.coremod.CoreMod;
import net.minecraftforge.coremod.CoreModEngine;
import net.minecraftforge.coremod.CoreModProvider;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.CoreModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.coremod.ICoreModFile;
import net.minecraftforge.forgespi.coremod.ICoreModProvider;
import net.minecraftforge.forgespi.language.IModInfo;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ModHelper {
    public static List<CoreMod> getCoreModList() throws Exception {
        //Get and convert coremod provider
        ICoreModProvider provider = FMLLoader.getCoreModProvider();
        CoreModProvider p = (CoreModProvider) provider;

        //Reflect to get coremod list
        CoreModEngine engine = (CoreModEngine) ReflectionHelper.getField(p, "engine");
        return (List<CoreMod>) ReflectionHelper.getField(engine, "coreMods");
    }

    public static String getSource(IModInfo it) {
        if (it instanceof ModInfo) return getSource((ModInfo) it);
        return "Not Found";
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

    public static Optional<? extends ModContainer> getModContainer(String modId) {
        return ModList.get().getModContainerById(modId);
    }

    public static String getStatus(String modId) {
        return getModContainer(modId)
                .map(ModContainer::getCurrentState).map(Objects::toString).orElse("NONE");
    }

    public static String getAuditLine(String className) {
        String name = className.replace('/', '.');
        return Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.AUDITTRAIL.get())
                .map(it -> it.getAuditString(name)).orElse("");
    }

    public static List<? extends ModContainer> getTransformers(String className) {
        return Arrays.stream(
                getAuditLine(className).split(","))
                .filter(it -> it.startsWith("xf:"))
                .map(it -> it.split(":"))
                .filter(it -> it.length > 2)
                .map(it -> it[1].equals("fml") ? it[2] : it[1])
                .map(ModHelper::getModContainer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static Optional<? extends ModContainer> getModByClass(String className) {
        String path = className.replace('.', '/') + ".class";
        URL url = ModHelper.class.getClassLoader().getResource(path);
        return url == null ? Optional.empty() : getModContainer(url.getHost());
    }
}
