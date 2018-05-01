package org.rpr.code.extension;

import rx.Observable;
import twitter4j.Status;
import twitter4j.TwitterFactory;

import java.math.BigInteger;
import java.util.Iterator;

public class NaturalNumbersIterator implements Iterator<BigInteger> {

    private BigInteger current = BigInteger.ZERO;

    @Override
    public boolean hasNext() {
        Observable<Integer> ints = Observable.<Integer>create(subscriber -> {
        }).cache();
        return true;
    }

    @Override
    public BigInteger next() {
        current = current.add(BigInteger.ONE);
        return current;
    }
}
