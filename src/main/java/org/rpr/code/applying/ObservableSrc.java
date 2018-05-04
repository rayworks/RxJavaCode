package org.rpr.code.applying;

import rx.Observable;

import java.util.UUID;

import static org.rpr.code.applying.Logger.log;

public final class ObservableSrc {
    public static Observable<String> simple() {
        return Observable.create(subscriber -> {
            log("Subscribed");
            subscriber.onNext("A");
            subscriber.onNext("B");
            subscriber.onCompleted();
        });
    }

    public static Observable<UUID> store(String s) {
        return Observable.create(subscriber -> {
            log("Storing " + s);
            // hard work
            subscriber.onNext(UUID.randomUUID());
            subscriber.onCompleted();
        });
    }
}
