package ume.Assert;

public class Assert {
    private Assert() {
    }

    public static void assertTrue(String m, boolean condition) {
        if (!condition) throw new RuntimeException(m);
    }
}
