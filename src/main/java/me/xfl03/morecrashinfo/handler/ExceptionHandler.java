package me.xfl03.morecrashinfo.handler;

public class ExceptionHandler {
    protected Throwable cause;

    public ExceptionHandler(Throwable cause) {
        this.cause = cause;
    }

    public void handleHeader(StringBuilder sb) {
    }

    public void handleException(StringBuilder sb) {
    }
}
