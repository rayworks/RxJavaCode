package org.rpr.code.applying;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.Executors.newFixedThreadPool;

@SuppressWarnings("unused")
public final class CustomSchedulers {
    private static ThreadFactory threadFactory(String pattern) {
        return new ThreadFactoryBuilder().setNameFormat(pattern).build();
    }

    static ExecutorService poolA = newFixedThreadPool(10, threadFactory("Sched-A-%d"));
    public static Scheduler schedulerA = Schedulers.from(poolA);

    static ExecutorService poolB = newFixedThreadPool(10, threadFactory("Sched-B-%d"));
    public static Scheduler schedulerB = Schedulers.from(poolB);

    static ExecutorService poolC = newFixedThreadPool(10, threadFactory("Sched-C-%d"));
    public static Scheduler schedulerC = Schedulers.from(poolC);
}
