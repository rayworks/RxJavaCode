package org.rpr.code.extension;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import twitter4j.Status;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class PublishRefTest {
    Observable<Status> observable = Observable.create(subscriber -> {
        System.out.println("Establishing connection");

        // ...

        TwitterStream twitterStream = TwitterStreamFactory.getSingleton();
        subscriber.add(Subscriptions.create(() -> {
            System.out.println("Disconnecting");
            twitterStream.shutdown();
        }));

        // authentication required when subscribed.
        //twitterStream.sample();
    });

    @Test
    public void testNormal() {
        subscribeAndUnsubscribe(observable);
    }

    private void subscribeAndUnsubscribe(Observable<Status> observable) {
        Subscription sub1 = observable.subscribe();
        System.out.println("Subscribed 1");

        Subscription sub2 = observable.subscribe();
        System.out.println("Subscribed 2");

        sub1.unsubscribe();
        System.out.println("Unsubscribed 1");
        sub2.unsubscribe();
        System.out.println("Unsubscribed 2");
    }

    @Test
    public void testPublishRef() {
        Observable<Status> lazy = observable.publish().refCount();
        // ...
        System.out.println("Before subscribers");

        subscribeAndUnsubscribe(lazy);
    }
}
