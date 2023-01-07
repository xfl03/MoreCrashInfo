package net.minecraftforge.fml.common;

public interface ICrashCallable {
    String getLabel();
    String call() throws Exception;
}
