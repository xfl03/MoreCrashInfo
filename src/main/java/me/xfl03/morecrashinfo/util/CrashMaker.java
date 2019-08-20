package me.xfl03.morecrashinfo.util;

public class CrashMaker {
    public static void makeCrash() {
        throw new RuntimeException(new RuntimeException(new RuntimeException("Crashed for test.")));
    }

    public static void doNothing() {

    }
}
