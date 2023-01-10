package me.xfl03.morecrashinfo.crash;

import me.xfl03.morecrashinfo.util.PrintHelper;
import me.xfl03.morecrashinfo.util.Reflection;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.service.MixinService;

import java.util.ArrayList;
import java.util.List;

public class MixinList extends CommonCallable {
    @Override
    String innerCall() throws Exception {
        //Avoid mixin not found
        try {
            Class.forName("org.spongepowered.asm.mixin.Mixins");
        } catch (Exception e) {
            return "Mixin class not found";
        }
        List<?> configs;
        try {
            configs = (List<?>) Reflection.clazz(MixinService.class)
                    .get("getService()")
                    .get("transformationHandler")
                    .get("transformer")
                    .get("processor")
                    .get("configs")
                    .get();
        } catch (Exception e) {
            return "Error getting mixin config: " + e;
        }

        List<List<String>> datas = new ArrayList<>();
        datas.add(PrintHelper.createLine("Name", "Mixin Package", "Priority", "Required", "Targets"));
        for (Object config : configs) {
            if (config instanceof IMixinConfig) {
                IMixinConfig cfg = (IMixinConfig) config;
                datas.add(PrintHelper.createLine(
                        cfg.getName(),
                        cfg.getMixinPackage(),
                        Integer.toString(cfg.getPriority()),
                        Boolean.toString(cfg.isRequired()),
                        Integer.toString(cfg.getTargets().size())
                ));
            } else {
                System.err.println(config + " is not instance of IMixinConfig");
            }
        }

        return PrintHelper.printLine("\n\t\t", datas);
//        return Mixins.getConfigs().stream().map(Config::getName).collect(Collectors.joining(", "));
    }

    @Override
    public String getLabel() {
        return "Mixin Configs";
    }
}
