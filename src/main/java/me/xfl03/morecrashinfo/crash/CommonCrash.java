package me.xfl03.morecrashinfo.crash;

import net.minecraftforge.fml.ISystemReportExtender;

public abstract class CommonCrash implements ISystemReportExtender {
    abstract String innerCall() throws Exception;

    public Object call() throws Exception {
        return get();
    }

    public String get() {
        try {
            System.out.println("Getting " + getLabel());
            return innerCall();
        } catch (Exception e) {
            System.out.println("Error getting crash detail");
            e.printStackTrace();
            return e.toString();
        }
    }
}
