package org.rpr.code.extension;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;
import twitter4j.*;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

// DON'T DO THIS,  very brittle and error prone
public class LazyTwitterObservable {
    private final Set<Subscriber<? super Status>> subscribers = new
            CopyOnWriteArraySet<>();

    private final TwitterStream twitterStream;

    public LazyTwitterObservable() {
        this.twitterStream = new TwitterStreamFactory().getSingleton();
        this.twitterStream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                subscribers.forEach(s -> s.onNext(status));
            }

            @Override
            public void onException(Exception ex) {
                subscribers.forEach(s -> s.onError(ex));
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {

            }

            @Override
            public void onStallWarning(StallWarning warning) {

            }


        });
    }

    private final Observable<Status> observable = Observable.create(
            subscriber -> {
                register(subscriber);
                subscriber.add(Subscriptions.create(() -> {
                    this.deregister(subscriber);
                }));
            });

    Observable<Status> observable() {
        return observable;
    }

    private synchronized void register(Subscriber<? super Status> subscriber) {
        if (subscribers.isEmpty()) {
            subscribers.add(subscriber);
            twitterStream.sample();
        } else {
            subscribers.add(subscriber);
        }
    }

    private synchronized void deregister(Subscriber<? super Status> subscriber) {
        subscribers.remove(subscriber);
        if (subscribers.isEmpty()) {
            twitterStream.shutdown();
        }
    }
}
