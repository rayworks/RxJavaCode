package org.rpr.code.applying;

import rx.Observable;

import java.math.BigDecimal;

import static org.rpr.code.applying.Logger.log;

public class RxGroceries {
    private BigDecimal priceForProduct = new BigDecimal(1.0);// fake price for all

    Observable<BigDecimal> purchase(String productName, int quantity) {
        return Observable.fromCallable(
                () -> doPurchase(productName, quantity)
        );
    }

    BigDecimal doPurchase(String productName, int quantity) {
        log("Purchasing " + quantity + " " + productName);
        // real logic here
        log("Done " + quantity + " " + productName);

        return priceForProduct.multiply(BigDecimal.valueOf(quantity));
    }
}
