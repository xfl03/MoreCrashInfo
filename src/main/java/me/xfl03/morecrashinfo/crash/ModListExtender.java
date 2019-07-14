package me.xfl03.morecrashinfo.crash;

import me.xfl03.morecrashinfo.util.PrintHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.ArrayList;
import java.util.List;

public class ModListExtender implements ICrashCallable {
    @Override
    public String getLabel() {
        return "Forge Mods";
    }

    @Override
    public String call() throws Exception {
        //Extend mod list text
        List<List<String>> datas = new ArrayList<>();
        datas.add(PrintHelper.createLine("ID", "Name", "Version", "Source"));
        List<ModInfo> mods = ModList.get().getMods();
        for (ModInfo it : mods) {
            datas.add(PrintHelper.createLine(
                    it.getModId(),
                    it.getDisplayName(),
                    it.getVersion().toString(),
                    getSource(it)
            ));
        }
        return PrintHelper.printLine("\n\t\t", datas);
    }

    private String getSource(ModInfo it) {
        if (it.getOwningFile() == null) return "Not Found";
        if (it.getOwningFile().getFile() == null) return "Not Found";
        return it.getOwningFile().getFile().getFileName();
    }
}
