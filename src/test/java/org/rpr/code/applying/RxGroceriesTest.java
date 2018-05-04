package org.rpr.code.applying;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import java.math.BigDecimal;

import static org.rpr.code.applying.CustomSchedulers.schedulerA;
import static org.rpr.code.operator.FilterAndTransformationTest.sleepInSeconds;

public class RxGroceriesTest {

    private RxGroceries store;

    @Before
    public void setup() {
        store = new RxGroceries();
    }

    @Test
    public void purchaseSerially() {
        Observable<BigDecimal> totalPrice = Observable
                .just("bread", "butter", "milk", "tomato", "cheese")
                .subscribeOn(schedulerA)
                //.map(prod -> store.doPurchase(prod, 1))
                .flatMap(prod -> store.purchase(prod, 1))
                .reduce(BigDecimal::add)
                .single();

        totalPrice.subscribe(System.out::println);

        sleepInSeconds(2);
    }

    @Test
    public void purchaseWithGroupBy() {
        Observable<BigDecimal> totalPrice = Observable.just(
                "bread", "butter", "egg", "milk", "tomato",
                "cheese", "tomato", "egg", "egg")
                .groupBy(prod -> prod)
                .flatMap(grouped ->
                        grouped.count()
                                .map(quantity -> {
                                    String productName = grouped.getKey();
                                    return Pair.of(productName, quantity);
                                }))
                .flatMap(order -> store
                        .purchase(order.getKey(), order.getValue())
                        .subscribeOn(schedulerA))
                .reduce(BigDecimal::add)
                .single();

        totalPrice.subscribe(System.out::println);

        sleepInSeconds(3);
    }
}
