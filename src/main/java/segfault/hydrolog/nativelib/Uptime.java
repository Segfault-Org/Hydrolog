package segfault.hydrolog.nativelib;

public class Uptime {
    static {
        System.loadLibrary("uptime");
    }

    public static native long uptime();
}
