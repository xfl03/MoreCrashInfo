package me.xfl03.morecrashinfo.modlauncher;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.ModuleLayerHandler;
import cpw.mods.modlauncher.TransformationServiceDecorator;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.util.ServiceLoaderUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        EnumMap<IModuleLayerManager.Layer, ?> completedLayers = getCompletedLayers(moduleLayerHandler);
        boolean alreadyBuilt = completedLayers.containsKey(layer);

        if (alreadyBuilt) {
            completedLayers.remove(layer);
        }

        addToLayer(moduleLayerHandler, layer, jar);

        if (alreadyBuilt) {
            Object layerInfo = moduleLayerHandler.buildLayer(layer);

            try {
                Field layerField = Class.forName("cpw.mods.modlauncher.ModuleLayerHandler$LayerInfo").getDeclaredField("layer");
                layerField.setAccessible(true);
                Object moduleLayer = layerField.get(layerInfo);
                Stream<ITransformationService> serviceStream = ServiceLoaderUtils.streamServiceLoader(() -> ServiceLoader.load((ModuleLayer) moduleLayer, ITransformationService.class), sce -> TransformerService.logger.warn("Encountered serious error loading transformation service, expect problems", sce));

                Map<String, TransformationServiceDecorator> decoratorMap = serviceStream.collect(Collectors.toMap(ITransformationService::name, ModuleHelper::newService));

                for (TransformationServiceDecorator decorator : decoratorMap.values()) {

                    Field serviceField = TransformationServiceDecorator.class.getDeclaredField("service");
                    serviceField.setAccessible(true);
                    ITransformationService service = (ITransformationService) serviceField.get(decorator);

                    if (service.name().equals("jcplugin")) {
                        try {
                            Method initMethod = TransformationServiceDecorator.class.getDeclaredMethod("onInitialize", IEnvironment.class);
                            initMethod.setAccessible(true);
                            initMethod.invoke(decorator, Launcher.INSTANCE.environment());
                        } catch (InvocationTargetException e) {
                            TransformerService.logger.warn("JCPlugin compatibility hook failed");
                            TransformerService.logger.warn(e);
                        }
                        break;
                    }
                }

            } catch (NullPointerException | NoSuchFieldException | NoSuchMethodException | ClassNotFoundException |
                     java.lang.IllegalAccessException e) {
                TransformerService.logger.warn("Failed to re-scan Modlauncher services");
                TransformerService.logger.warn(e);
            }
        }
        TransformerService.logger.info(
                "Added {} to {} with {}", jar, layer, alreadyBuilt ? "Rebuilt" : "Not Build");
    }

    private static EnumMap<IModuleLayerManager.Layer, ?> getCompletedLayers(ModuleLayerHandler handler) {
        try {
            Field field = ModuleLayerHandler.class.getDeclaredField("completedLayers");
            field.setAccessible(true);
            return (EnumMap<IModuleLayerManager.Layer, ?>) field.get(handler);
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

    private static TransformationServiceDecorator newService(ITransformationService a) {
        try {
            Constructor<?> c = TransformationServiceDecorator.class.getDeclaredConstructor(ITransformationService.class);
            c.setAccessible(true);
            return (TransformationServiceDecorator) c.newInstance(new Object[]{a});
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
