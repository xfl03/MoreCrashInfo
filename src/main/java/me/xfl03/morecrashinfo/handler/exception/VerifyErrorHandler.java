package me.xfl03.morecrashinfo.handler.exception;

import me.xfl03.morecrashinfo.handler.ExceptionHandler;
import me.xfl03.morecrashinfo.util.ModHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VerifyErrorHandler extends ExceptionHandler {
    private final String className;
    private final List<? extends ModContainer> transformers;
    private final Optional<? extends ModContainer> owner;

    public VerifyErrorHandler(Throwable cause) {
        super(cause);
        className = getClass(cause.getMessage());
        transformers = ModHelper.getTransformers(className);
        owner = ModHelper.getModByClass(className);
    }

    @Override
    public void handleHeader(StringBuilder sb) {
        sb.append("Possible Reason:\n\tBytecode in class '").append(className).append("' failed to verify. ")
                .append(getReason()).append("\n\n");
    }

    private String getReason() {
        if (!transformers.isEmpty()) {

            return String.format(
                    "\n\tCoreMod '%s' modified that class, which may cause crash.\n" +
                            "Possible Solution:\n\tPlease remove or update %s'%s' and try again.",
                    transformers.stream().map(it -> it.getModInfo().getDisplayName())
                            .collect(Collectors.joining(",")),
                    transformers.size() > 1 ? "one of " : "",
                    transformers.stream().map(it -> it.getModInfo().getDisplayName() +
                            "(" + ModHelper.getSource(it.getModInfo()) + ")")
                            .collect(Collectors.joining(","))
            );
        } else if (owner.isPresent()) {
            IModInfo info = owner.get().getModInfo();
            return String.format(
                    "\n\tMod '%s' might has been broken.\n" +
                            "Possible Solution:\n\tPlease remove or update '%s' and try again.",
                    info.getDisplayName(),
                    info.getDisplayName() + "(" + ModHelper.getSource(info) + ")"
            );
        }
        return "";
    }

    @Override
    public void handleException(StringBuilder sb) {
        sb.append("Error Info:\n\tClass: ").append(className)
                .append("\n\tOwner: ").append(owner.map(it -> it.getModInfo().getDisplayName()).orElse("Unknown"))
                .append("\n\tAudit: ").append(ModHelper.getAuditLine(className)).append("\n\n");
    }

    private String getClass(String message) {
        String[] t = message.split("Location:");
        if (t.length < 2) return null;
        t = t[1].split("\\.");
        return t[0].trim().replace('/', '.');
    }
}
