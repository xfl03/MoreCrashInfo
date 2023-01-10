package me.xfl03.morecrashinfo.fml;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModLocator extends AbstractJarFileModLocator {
    public static final Logger logger = LogManager.getLogger("MoreCrashInfoModLocator");
    private Path modPath = null;
    private final JarRemapper remapper = new JarRemapper();

    private void unzip(ZipFile zip, ZipEntry entry, Path target) {
        try {
            Path dir = target.getParent();
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (Exception e) {
            logger.warn("Error mkdir {}", target);
            logger.warn(e);
        }

        try (InputStream in = zip.getInputStream(entry)) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            logger.warn("Error unzip {}", target);
            logger.warn(e);
        }
    }

    private void visitModFile(Path path) {
//        logger.info("Try to Visit {} {} {}", path, Files.isRegularFile(path), path.toString().endsWith(".jar"));
        if (!Files.isRegularFile(path) || !path.toString().endsWith(".jar")) {
            return;
        }
        try (ZipFile zip = new ZipFile(new File(path.toUri()))) {
//            logger.info("Visiting {}", path);
            ZipEntry ze = zip.getEntry("MoreCrashInfo-Core.jar");
            if (ze != null) {
//                logger.info("MoreCrashInfo mod found at {}", path);
                modPath = FMLPaths.GAMEDIR.get().resolve("tmp/MoreCrashInfo-Core.jar");
                unzip(zip, ze, modPath);
            }
        } catch (Exception e) {
            logger.warn("Error while loading {}", path);
            logger.warn(e);
        }
    }

    private void visitModDir(Path path) {
        try (Stream<Path> paths = Files.list(path)) {
            paths.forEach(this::visitModFile);
        } catch (Exception e) {
            logger.warn("Error while visiting mod dir");
            logger.warn(e);
        }
    }

    //1.13-1.16
    public List<IModFile> scanModsLegacy() {
        List<Path> candidates = scanCandidates().collect(Collectors.toList());
        if (candidates.size() == 0) {
            logger.warn("Mod not found");
            return Collections.emptyList();
        }
        List<IModFile> modFiles = new ArrayList<>();

        try {
            //1.13-1.14
            Constructor<?> constructor0 = null;
            for (Constructor<?> constructor : ModFile.class.getConstructors()) {
                if (constructor.getParameterCount() == 2) {
                    logger.debug("Found constructor method");
                    constructor0 = constructor;
                    break;
                }
            }
            if (constructor0 != null) {
                for (Path p : candidates) {
                    modFiles.add((ModFile) constructor0.newInstance(p, this));
                }
            }

            //1.15-1.16
            Method method0 = null;
            for (Method method : ModFile.class.getMethods()) {
                if (method.getName().equals("newFMLInstance")) {
                    logger.debug("Found newFMLInstance method");
                    method0 = method;
                    break;
                }
            }

            if (method0 != null) {
                for (Path p : candidates) {
                    modFiles.add((ModFile) method0.invoke(null, p, this));
                }
            }
        } catch (Exception e) {
            logger.warn("Error build mod");
            logger.warn(e);
        }

        if (modFiles.size() == 0) {
            logger.warn("Mod not built.");
            return modFiles;
        }

        try {
            Class<?> clazz = Class.forName("net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator");
            Method method0 = null;
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals("createFileSystem")) {
                    logger.debug("Found createFileSystem method");
                    method0 = method;
                    break;
                }
            }

            Field field = clazz.getDeclaredField("modJars");
            Map<ModFile, FileSystem> modJars = (Map<ModFile, FileSystem>) field.get(this);
            if (method0 != null) {
                for (IModFile modFile : modFiles) {
                    ModFile mf = (ModFile) modFile;
                    if (!modJars.containsKey(mf)) {
                        FileSystem fs = (FileSystem) method0.invoke(this, modFile);
                        modJars.put(mf, fs);
                        logger.debug("{} {} {} {}", mf, fs, isValid(mf), mf.identifyMods());
                    }
                }
                return modFiles;
            }

        } catch (Exception e) {
            logger.warn("Error build mod filesystem");
            logger.warn(e);
        }

        logger.warn("Mod filesystem not built");
        return modFiles;
    }

    //1.17+
    @Override
    public Stream<Path> scanCandidates() {
        logger.info("We are now at MoreCrashInfo ModLocator");
        Path gameFolder = FMLPaths.GAMEDIR.get();
        logger.debug("Game folder {}", gameFolder);
        Path modFolder = FMLPaths.MODSDIR.get();
        logger.debug("Mod folder {}", modFolder);
        visitModDir(modFolder);
        logger.info("Core path {}", modPath);
        if (modPath == null) {
            return Stream.empty();
        }
        logger.info("Minecraft version {} major {}",
                VersionUtil.getMinecraftVersion(), VersionUtil.getMinecraftMajorVersion());
        Path mapped = gameFolder.resolve(
                String.format("tmp/MoreCrashInfo-Core-%s.jar", VersionUtil.getMinecraftVersion()));
        remapper.processJar(modPath, mapped);
        return Stream.of(mapped);
    }

    @Override
    public String name() {
        return "MoreCrashInfoModLocator 1.13-1.16";
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }
}
