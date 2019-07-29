package me.xfl03.morecrashinfo.crash;

import me.xfl03.morecrashinfo.util.ModHelper;
import me.xfl03.morecrashinfo.util.PrintHelper;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.ArrayList;
import java.util.List;

public class ModList implements ICrashCallable {
    @Override
    public String getLabel() {
        return "Forge Mods";
    }

    @Override
    public String call() throws Exception {
        //Extend mod list text
        List<List<String>> datas = new ArrayList<>();
        datas.add(PrintHelper.createLine("ID", "Name", "Version", "Source"));
        List<ModInfo> mods = net.minecraftforge.fml.ModList.get().getMods();
        for (ModInfo it : mods) {
            datas.add(PrintHelper.createLine(
                    it.getModId(),
                    it.getDisplayName(),
                    it.getVersion().toString(),
                    ModHelper.getSource(it)
            ));
        }
        return PrintHelper.printLine("\n\t\t", datas);
    }

}
