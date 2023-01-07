package me.xfl03.morecrashinfo.crash;

import me.xfl03.morecrashinfo.util.ModHelper;
import me.xfl03.morecrashinfo.util.PrintHelper;
import net.minecraftforge.fml.ISystemReportExtender;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.ArrayList;
import java.util.List;

public class ModList implements ICrashCallable, ISystemReportExtender {
    @Override
    public String getLabel() {
        return "Forge Mods";
    }

    @Override
    public String call() throws Exception {
        //Extend mod list text
        List<List<String>> datas = new ArrayList<>();
        datas.add(PrintHelper.createLine("ID", "Name", "Version", "Source", "Status"));
        List<IModInfo> mods = net.minecraftforge.fml.ModList.get().getMods();
        for (IModInfo it : mods) {
            datas.add(PrintHelper.createLine(
                    it.getModId(),
                    it.getDisplayName(),
                    it.getVersion().toString(),
                    ModHelper.getSource(it),
                    ModHelper.getStatus(it.getModId())
            ));
        }
        return PrintHelper.printLine("\n\t\t", datas);
    }

    @Override
    public String get() {
        try {
            return call();
        } catch (Exception e) {
            return e.toString();
        }
    }
}
