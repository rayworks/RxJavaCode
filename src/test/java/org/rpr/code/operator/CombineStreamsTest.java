package org.rpr.code.operator;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import rx.Observable;
import rx.internal.operators.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.rpr.code.operator.CustomOperator.toStringOfOdd;
import static org.rpr.code.operator.FilterAndTransformationTest.sleepInSeconds;

public class CombineStreamsTest {

    Observable<String> speak(String quote, long millisPreChar) {
        String[] tokens = quote.replaceAll("[:,]", "").split(" ");

        Observable<String> words = Observable.from(tokens);
        Observable<Long> absoluteDelay = words
                .map(String::length)
                .map(len -> len * millisPreChar)
                .scan((total, current) -> total + current);

        return words.zipWith(absoluteDelay.startWith(0L), Pair::of)
                .flatMap(pair -> Observable.just(pair.getLeft())
                        .delay(pair.getRight(), TimeUnit.MILLISECONDS));
    }

    @Test
    public void speakQuote() {
        speak("Though this be madness", 100)
                .subscribe(System.out::println);

        sleepInSeconds(2);
    }

    Observable<String> alice = speak("To be, or not to be: that is the question",
            110);

    Observable<String> bob = speak("Though this be madness, yet there is method in't",
            90);

    Observable<String> jane = speak("There are more things in Heaven and Earth, "
                    + "Horatio, than are dreamt of in your philosophy",
            100);

    @Test
    public void speakQuoteHamlet() {
        Observable.
                /*merge*/ concat(
                        alice.map(w -> "Alice: " + w),
                        bob.map(w -> "Bob: " + w),
                        jane.map(w -> "Jane: " + w)
                )
                .subscribe(System.out::println);

        sleepInSeconds(20);
    }

    @Test
    public void quoteSwitchOnNext() {
        Random rnd = new Random();
        Observable<Observable<String>> quotes = Observable.just(
                alice.map(w -> "Alice: " + w),
                bob.map(w -> "Bob: " + w),
                jane.map(w -> "Jane: " + w)
        ).flatMap(innerObs -> Observable.just(innerObs)
                .delay(rnd.nextInt(5), TimeUnit.SECONDS));

        Observable.switchOnNext(quotes)
                .subscribe(System.out::println);

        sleepInSeconds(20);
    }

    static <T> Observable.Transformer<T, T> odd() {
        Observable<Boolean> trueFalse = Observable.just(true, false).repeat();
        return upstream -> upstream.zipWith(trueFalse, Pair::of)
                .filter(Pair::getRight)
                .map(Pair::getLeft);
    }

    @Test
    public void compose() {
        Observable<Character> alphabet = Observable.range(0, 'Z' - 'A' + 1)
                .map(c -> (char) ('A' + c));
        alphabet.compose(odd()).forEach(System.out::println);
    }

    @Test
    public void lift() {
        Observable.range(1, 1000)
                .filter(x -> x % 3 == 0)
                .lift(OperatorDistinct.<Integer>instance())
                .lift(new OperatorScan<>((Integer a, Integer x) -> a + x))
                .lift(new OperatorTakeLast<Integer>(1))
                .lift(OperatorSingle.instance())
                .lift(new OperatorMap<>(Integer::toHexString))
                .subscribe(System.out::println);
    }

    @Test
    public void customOperator() {
        Observable.range(1, 4)
                .repeat()
                .lift(toStringOfOdd())
                .take(3)
                .subscribe(
                        System.out::println,
                        Throwable::printStackTrace,
                        () -> System.out.println("Completed")
                );
    }
}
