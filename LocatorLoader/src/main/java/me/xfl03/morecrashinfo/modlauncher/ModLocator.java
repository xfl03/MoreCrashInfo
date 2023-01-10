package me.xfl03.morecrashinfo.modlauncher;

import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModLocator;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public class ModLocator extends AbstractJarFileModLocator {
    @Override
    public Stream<Path> scanCandidates() {
        TransformerService.logger.info("Core path {}", TransformerService.corePath);
        return Stream.of(TransformerService.corePath);
    }

    @Override
    public String name() {
        return "MoreCrashInfoModLocator 1.17-1.19";
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }
}
