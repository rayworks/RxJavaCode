package org.rpr.code.operator;

import org.junit.Test;
import rx.Observable;

import java.time.DayOfWeek;
import java.util.concurrent.TimeUnit;

import static org.rpr.code.operator.FilterAndTransformationTest.Sound.DAH;
import static org.rpr.code.operator.FilterAndTransformationTest.Sound.DI;
import static rx.Observable.empty;
import static rx.Observable.just;
import static rx.Observable.timer;

public class FilterAndTransformationTest {
    @Test
    public void filterAndMap() {
        just(8, 9, 10)
                .doOnNext(i -> System.out.println("A: " + i))
                .filter(i -> i % 3 > 0)
                .doOnNext(i -> System.out.println("B: " + i))
                .map(i -> "#" + i * 10)
                .doOnNext(s -> System.out.println("C: " + s))
                .filter(s -> s.length() < 4)
                .subscribe(s -> System.out.println("D: " + s));
    }

    @Test
    public void morseCode() {
        just('S', 'p', 'r', 't', 'a')
                .map(Character::toLowerCase)
                .flatMap(this::toMorseCode)
                .subscribe(System.out::println)
        ;
    }

    enum Sound {DI, DAH}

    public Observable<Sound> toMorseCode(char ch) {
        switch (ch) {
            case 'a':
                return just(DI, DAH);
            case 'b':
                return just(DAH, DI, DI, DI);
            case 'c':
                return just(DAH, DI, DAH, DI);

            // ...
            case 'p':
                return just(DI, DAH, DAH, DI);
            case 'r':
                return just(DI, DAH, DI);
            case 's':
                return just(DI, DI, DI);
            case 't':
                return just(DAH);

            // ...
            default:
                return empty();
        }
    }

    @Test
    public void delayEvents() {
        Observable.just("Lorem", "ipsum", "dolor", "sit", "amet",
                "consectetur", "adipiscing", "elit")
                //.delay(word -> timer(word.length(), TimeUnit.SECONDS))
                .flatMap(word -> timer(word.length(), TimeUnit.SECONDS).map(x -> word))
                .subscribe(System.out::println);

        sleepInSeconds(15);
    }

    public static void sleepInSeconds(int i) {
        try {
            TimeUnit.SECONDS.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void flatmapOrder() {
        just(10L, 1L)
                .flatMap(x -> just(x).delay(x, TimeUnit.SECONDS))
                .subscribe(System.out::println);

        sleepInSeconds(12);
    }

    Observable<String> loadRecordsFor(DayOfWeek dow) {
        switch (dow) {
            case SUNDAY:
                return Observable.interval(90, TimeUnit.MILLISECONDS)
                        .take(5)
                        .map(i -> "Sun-" + i);
            case MONDAY:
                return Observable.interval(65, TimeUnit.MILLISECONDS)
                        .take(5)
                        .map(i -> "Mon-" + i);
        }

        throw new IllegalArgumentException("unsupported " + dow);
    }

    @Test
    public void dayOfWeekRange() {
        Observable.just(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
                .concatMap(this::loadRecordsFor)
                //.flatMap(this::loadRecordsFor)
                .subscribe(System.out::println)
        ;

        sleepInSeconds(3);
    }

    @Test
    public void chessBoard() {
        Observable<Integer> oneToEight = Observable.range(1, 8);
        Observable<String> ranks = oneToEight.map(Object::toString);
        Observable<String> files = oneToEight
                .map(x -> 'a' + x - 1)
                .map(ascii -> (char) ascii.intValue())
                .map(ch -> Character.toString(ch));

        Observable<String> squares = files
                .flatMap(file -> ranks.map(rank -> file + rank));

        squares.subscribe(System.out::println);
    }
}
