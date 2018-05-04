package org.rpr.code.applying;

public final class Logger {
    public static void log(Object label) {
        System.out.println(
                System.currentTimeMillis() /*- start*/ + "\t| " +
                        Thread.currentThread().getName() + "\t| " +
                        label
        );
    }
}
