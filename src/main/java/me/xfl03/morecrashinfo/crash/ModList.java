package me.xfl03.morecrashinfo.crash;

import me.xfl03.morecrashinfo.MoreCrashInfo;
import me.xfl03.morecrashinfo.util.ModHelper;
import me.xfl03.morecrashinfo.util.PrintHelper;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.ArrayList;
import java.util.List;

public class ModList extends CommonCrash {
    @Override
    public String getLabel() {
        return "Forge Mods";
    }

    public String innerCall() throws Exception {
        //Extend mod list text
        List<List<String>> datas = new ArrayList<>();
        datas.add(PrintHelper.createLine("ID", "Name", "Version", "Source", "Status"));
        net.minecraftforge.fml.ModList modList = net.minecraftforge.fml.ModList.get();
        if (modList == null) {
            return "ModList not found.";
        }
        List<?> mods = modList.getMods();
        for (Object itt : mods) {
            if (itt instanceof IModInfo) {
                IModInfo it = (IModInfo) itt;
                datas.add(PrintHelper.createLine(
                        it.getModId(),
                        it.getDisplayName(),
                        it.getVersion().toString(),
                        ModHelper.getSource(it),
                        ModHelper.getStatus(it.getModId())
                ));
            } else {
                MoreCrashInfo.logger.warn("{} is not instance of IModInfo", itt);
            }
        }
        return PrintHelper.printLine("\n\t\t", datas);
    }
}
