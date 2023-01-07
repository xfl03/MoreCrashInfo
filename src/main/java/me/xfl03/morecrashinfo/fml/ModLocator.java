package me.xfl03.morecrashinfo.fml;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

public class ModLocator extends AbstractJarFileModLocator {
    private final Logger logger = LogManager.getLogger("MoreCrashInfoModLocator");
    private Path modPath = null;

    private void visitModFile(Path path) {
//        logger.info("Try to Visit {} {} {}", path, Files.isRegularFile(path), path.toString().endsWith(".jar"));
        if (!Files.isRegularFile(path) || !path.toString().endsWith(".jar")) {
            return;
        }
        try (ZipFile zip = new ZipFile(new File(path.toUri()))) {
//            logger.info("Visiting {}", path);
            if (zip.getEntry("me/xfl03/morecrashinfo/MoreCrashInfo.class") != null) {
                logger.info("MoreCrashInfo mod found at {}", path);
                modPath = path;
            }
        } catch (Exception e) {
            logger.warn("Error while loading {}", path);
        }
    }

    private void visitModDir(Path path) {
        try (Stream<Path> paths = Files.list(path)) {
            paths.forEach(this::visitModFile);
        } catch (Exception e) {
            logger.warn("Error while visiting mod dir");
        }
    }

    @Override
    public Stream<Path> scanCandidates() {
        Path modFolder = FMLPaths.MODSDIR.get();
        logger.warn("Mod folder {}", modFolder);
        visitModDir(modFolder);
        logger.warn("Mod path {}", modPath);
        return Stream.ofNullable(modPath);
    }

    @Override
    public String name() {
        return "MoreCrashInfoModLocator";
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }
}
