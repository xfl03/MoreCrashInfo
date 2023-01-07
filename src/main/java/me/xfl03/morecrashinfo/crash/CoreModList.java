package me.xfl03.morecrashinfo.crash;

import me.xfl03.morecrashinfo.util.ModHelper;
import me.xfl03.morecrashinfo.util.PrintHelper;
import net.minecraftforge.coremod.CoreMod;
import net.minecraftforge.fml.ISystemReportExtender;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.forgespi.coremod.ICoreModFile;

import java.util.ArrayList;
import java.util.List;

public class CoreModList implements ICrashCallable, ISystemReportExtender {
    @Override
    public String getLabel() {
        return "Forge CoreMods";
    }

    @Override
    public String call() throws Exception {
        //Get coremod list
        List<CoreMod> list = ModHelper.getCoreModList();
        if (list == null || list.isEmpty()) {
            return "Not found";
        }

        //Extend coremod list
        List<List<String>> datas = new ArrayList<>();
        datas.add(PrintHelper.createLine("ID", "Name", "Source", "Status"));
        for (CoreMod it : list) {
            ICoreModFile f = it.getFile();
            datas.add(PrintHelper.createLine(
                    f.getOwnerId(),
                    ModHelper.getName(f),
                    ModHelper.getSource(f),
                    it.hasError() ? "Error" : "Loaded"
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
