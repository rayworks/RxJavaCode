package org.rpr.code.applying;

import org.junit.Test;
import rx.Observable;

import static org.rpr.code.applying.CustomSchedulers.schedulerA;
import static org.rpr.code.applying.CustomSchedulers.schedulerB;
import static org.rpr.code.applying.CustomSchedulers.schedulerC;
import static org.rpr.code.applying.Logger.log;
import static org.rpr.code.applying.ObservableSrc.simple;
import static org.rpr.code.applying.ObservableSrc.store;
import static org.rpr.code.operator.FilterAndTransformationTest.sleepInSeconds;

public class SchedulerTest {
    @Test
    public void specifiedSubscribedScheduler() {
        log("Starting");
        final Observable<String> obs = simple();

        log("Created");
        obs.subscribeOn(schedulerA)
                .subscribe(x -> log("Got " + x),
                        Throwable::printStackTrace,
                        () -> log("Completed")
                );
        log("Exiting");

        sleepInSeconds(2);
    }


    @Test
    public void specifiedSchedulers() {
        log("Starting");
        Observable<String> obs = Observable.create(
                subscriber -> {
                    log("Subscribed");
                    subscriber.onNext("A");
                    subscriber.onNext("B");
                    subscriber.onNext("C");
                    subscriber.onNext("D");
                    subscriber.onCompleted();
                }
        );
        log("Created");

        obs.subscribeOn(schedulerA)
                .flatMap(record -> store(record).subscribeOn(schedulerB))
                .observeOn(schedulerC)
                .subscribe(x -> log("Got " + x),
                        Throwable::printStackTrace,
                        () -> log("Completed")
                );
        log("Exiting");

        sleepInSeconds(3);
    }
}
