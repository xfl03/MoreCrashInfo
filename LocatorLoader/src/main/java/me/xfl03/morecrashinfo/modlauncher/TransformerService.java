package me.xfl03.morecrashinfo.modlauncher;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TransformerService implements ITransformationService {
    public static Logger logger;

    @Override
    public @NotNull String name() {
        return "MoreCrashInfoTransformerService";
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {
        logger = LogManager.getLogger("MoreCrashInfoTransformerService");
        logger.debug("We are now at MoreCrashInfo TransformationService");
    }

    public static Path corePath;

    private Path gameDir;

    @Override
    public void initialize(IEnvironment environment) {
        Optional<Path> gamedir = environment.getProperty(IEnvironment.Keys.GAMEDIR.get());
        if (gamedir.isPresent()) {
            gameDir = gamedir.get();
            visitModDir(gameDir.resolve("mods"));
            logger.debug("Mod path {}", modPath);
            if (modPath == null) {
                return;
            }
            logger.debug("Minecraft version {} major {}",
                    VersionUtil.getMinecraftVersion(), VersionUtil.getMinecraftMajorVersion());
            if (VersionUtil.getMinecraftMajorVersion() >= 17) {
                logger.debug("Minecraft Version is 1.17-1.19, don't need extra Locator.");
                return;
            }
            Path mapped = gameDir.resolve(
                    String.format("tmp/MoreCrashInfo-Locator-%s.jar", VersionUtil.getMinecraftVersion()));
            JarRemapper remapper = new JarRemapper();
            remapper.processJar(modPath, mapped);
            try {
                ClasspathHelper.add(Launcher.class.getClassLoader(), mapped);
            } catch (Throwable e) {
                logger.warn("Error while appendToClassPath");
                logger.warn(e);
            }
        }
    }

    @Override
    public @NotNull List<ITransformer> transformers() {
//        return Collections.emptyList();
        return Collections.singletonList(new CrashReportExtenderTransformer());
    }

    public void beginScanning(IEnvironment environment) {
    }

    private Path modPath;

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
        if (!Files.isRegularFile(path) || !path.toString().endsWith(".jar")) {
            return;
        }
        try (ZipFile zip = new ZipFile(new File(path.toUri()))) {
            ZipEntry ze = zip.getEntry("MoreCrashInfo-Locator.jar");
            if (ze != null) {
                modPath = gameDir.resolve("tmp/MoreCrashInfo-Locator.jar");
                unzip(zip, ze, modPath);
            }
            ze = zip.getEntry("MoreCrashInfo-Core.jar");
            if (ze != null) {
                corePath = gameDir.resolve("tmp/MoreCrashInfo-Core.jar");
                unzip(zip, ze, corePath);
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
}
