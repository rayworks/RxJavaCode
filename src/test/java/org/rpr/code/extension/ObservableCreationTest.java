package org.rpr.code.extension;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ObservableCreationTest {
    private static void log(Object msg) {
        System.out.println(Thread.currentThread().getName() + ": " + msg);
    }

    // ...

    @Test
    public void testCreatedByRange() {
        log("Before");
        Observable.range(5, 3).subscribe(i -> {
            log(i);
        });
        log("After");
    }

    @Test
    public void normalCreation() {
        Observable<Integer> ints = Observable.create(subscriber -> {
            log("Create");
            subscriber.onNext(5);
            subscriber.onNext(6);
            subscriber.onNext(7);
            subscriber.onCompleted();
            log("Complete");
        });

        log("Starting");
        ints.subscribe(i -> log("Element: " + i));
        log("Exit");
    }

    @Test
    public void multipleSubscribers() {
        Observable<Integer> ints = Observable.create(subscriber -> {
            log("Create");
            subscriber.onNext(42);
            subscriber.onCompleted();
        });

        log("Starting");
        ints.subscribe(i -> log("Element A: " + i));
        ints.subscribe(i -> log("Element B: " + i));
        log("Exit");
    }

    @Test
    public void multipleSubscribersWithCache() {
        Observable<Integer> ints = Observable.<Integer>create(subscriber -> {
            log("Create");
            subscriber.onNext(42);
            subscriber.onCompleted();
        }).cache();

        log("Starting");
        ints.subscribe(i -> log("Element A: " + i));
        ints.subscribe(i -> log("Element B: " + i));
        log("Exit");
    }

    static void sleep(int timeout, TimeUnit unit) {
        try {
            unit.sleep(timeout);
        } catch (InterruptedException ignored) {
            // intentionally ignored
            System.err.println(ignored.getMessage());
        }
    }

    static <T> Observable<T> delayed(T x) {
        return Observable.create(subscriber -> {
            Runnable r = () -> {
                sleep(10, SECONDS);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(x);
                    subscriber.onCompleted();
                }
            };

            Thread thread = new Thread(r);
            thread.start();

            // a call back to interrupt the worker thread when Observable unsubscribed.
            subscriber.add(Subscriptions.create(thread::interrupt));
        });
    }

    @Test
    public void testNormalDelay() {
        System.out.println("invoke now");
        delayed("delayed result").subscribe(
                s -> System.out.println(s),
                Throwable::printStackTrace,
                () -> {
                    System.out.println("Done");
                });

        // waiting for the async result
        sleep(12, SECONDS);
    }

    @Test
    public void testDelayInterrupted() {
        System.out.println("invoke now");
        Subscription subscribe = delayed("delayed result").subscribe(
                s -> System.out.println(s),
                Throwable::printStackTrace,
                () -> {
                    System.out.println("Done");
                });

        // waiting for the async result
        sleep(3, SECONDS);
        subscribe.unsubscribe();
    }
}
