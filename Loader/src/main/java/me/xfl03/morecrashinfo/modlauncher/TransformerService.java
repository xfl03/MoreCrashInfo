package me.xfl03.morecrashinfo.modlauncher;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TransformerService implements ITransformationService {
    public static Logger logger;

    @Override
    public @NotNull String name() {
        return "MoreCrashInfoTransformerService";
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {
        logger = LogManager.getLogger("MoreCrashInfoTransformerService");
    }

    @Override
    public void initialize(IEnvironment environment) {
    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        return Arrays.asList(new CustomCrashExtenderTransformer(), new CrashReportExtenderTransformer());
    }
}
