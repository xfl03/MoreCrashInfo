package me.xfl03.morecrashinfo.fml;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModLocator extends AbstractJarFileModLocator {
    private final Logger logger = LogManager.getLogger("MoreCrashInfoModLocator");
    private Path modPath = null;

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
                logger.info("MoreCrashInfo mod found at {}", path);
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

    @Override
    public Stream<Path> scanCandidates() {
        Path modFolder = FMLPaths.MODSDIR.get();
        logger.warn("Mod folder {}", modFolder);
        visitModDir(modFolder);
        logger.warn("Mod path {}", modPath);
        return modPath == null ? Stream.empty() : Stream.of(modPath);
    }

    @Override
    public String name() {
        return "MoreCrashInfoModLocator";
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }
}
