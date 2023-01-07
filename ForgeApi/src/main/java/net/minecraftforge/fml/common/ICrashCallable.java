package net.minecraftforge.fml.common;

// 1.16.5-
public interface ICrashCallable {
    String getLabel();
    String call() throws Exception;
}
