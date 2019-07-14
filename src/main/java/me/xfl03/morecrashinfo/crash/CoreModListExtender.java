package me.xfl03.morecrashinfo.crash;

import me.xfl03.morecrashinfo.util.CoreModHelper;
import me.xfl03.morecrashinfo.util.PrintHelper;
import net.minecraftforge.coremod.CoreMod;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.forgespi.coremod.ICoreModFile;

import java.util.ArrayList;
import java.util.List;

public class CoreModListExtender implements ICrashCallable {
    @Override
    public String getLabel() {
        return "Forge CoreMods";
    }

    @Override
    public String call() throws Exception {
        //Get coremod list
        List<CoreMod> list = CoreModHelper.getCoreModList();
        if (list == null || list.isEmpty()) {
            return "Not found";
        }

        //Extend coremod list
        List<List<String>> datas = new ArrayList<>();
        datas.add(PrintHelper.createLine("ID", "Status", "Source"));
        for (CoreMod it : list) {
            ICoreModFile f = it.getFile();
            datas.add(PrintHelper.createLine(
                    f.getOwnerId(),
                    it.hasError() ? "Error" : "Loaded",
                    getSource(f)
            ));
        }
        return PrintHelper.printLine("\n\t\t", datas);
    }

    private String getSource(ICoreModFile file) {
        if (file.getPath() == null) return "Not Found";
        return file.getPath().getFileName().toString();
    }
}
