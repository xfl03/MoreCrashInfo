package me.xfl03.morecrashinfo.modlauncher;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.ModuleLayerHandler;
import cpw.mods.modlauncher.api.IModuleLayerManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.EnumMap;

public class ModuleHelper {
    public static void addModule(IModuleLayerManager.Layer layer, Path jar) {
        if (!Launcher.INSTANCE.findLayerManager().isPresent()) {
            throw new IllegalStateException("LayerManager not found.");
        }
        IModuleLayerManager handler = Launcher.INSTANCE.findLayerManager().get();
        if (!(handler instanceof ModuleLayerHandler)) {
            throw new IllegalArgumentException("IModuleLayerManager not supported: " + handler);
        }
        ModuleLayerHandler moduleLayerHandler = (ModuleLayerHandler) handler;
        removeCompletedLayers(moduleLayerHandler, layer);
        addToLayer(moduleLayerHandler, layer, jar);
        moduleLayerHandler.buildLayer(layer);
        TransformerService.logger.info("Added {} to {}", jar, layer);
    }

    private static void removeCompletedLayers(ModuleLayerHandler handler, IModuleLayerManager.Layer layer) {
        try {
            Field field = ModuleLayerHandler.class.getDeclaredField("completedLayers");
            field.setAccessible(true);
            EnumMap<IModuleLayerManager.Layer, ?> completedLayer =
                    (EnumMap<IModuleLayerManager.Layer, ?>) field.get(handler);
            completedLayer.remove(layer);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void addToLayer(ModuleLayerHandler handler, IModuleLayerManager.Layer layer, Path jar) {
        try {
            Method method = ModuleLayerHandler.class.getDeclaredMethod(
                    "addToLayer", IModuleLayerManager.Layer.class, SecureJar.class);
            method.setAccessible(true);
            method.invoke(handler, layer, SecureJar.from(jar));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
