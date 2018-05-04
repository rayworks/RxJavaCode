package org.rpr.code.applying;

import rx.Observable;

public class FlightManager {
    Flight lookupFlight(String flightNo) {
        return new Flight(flightNo); // fake
    }

    Passenger findPassenger(long id) {
        return new Passenger(id);
    }

    Ticket bookTicket(Flight flight, Passenger passenger) {
        return new Ticket();
    }

    SmtpResponse sendEmail(Ticket ticket) {
        return new SmtpResponse();
    }

    Observable<Flight> rxLookupFlight(String flightNo) {
        return Observable.defer(() ->
                Observable.just(lookupFlight(flightNo)));
    }

    Observable<Passenger> rxFindPassenger(long id) {
        return Observable.defer(() ->
                Observable.just(findPassenger(id)));
    }

    public void notifyFlightPassenger() {
        Observable<Flight> flight = rxLookupFlight("LOT 783");
        Observable<Passenger> passenger = rxFindPassenger(42);
        Observable<Ticket> ticket =
                flight.zipWith(passenger, (f, p) -> bookTicket(f, p));
        ticket.subscribe(this::sendEmail);
    }
}
