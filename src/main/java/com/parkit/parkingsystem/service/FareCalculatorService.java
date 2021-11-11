package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, TicketDAO ticketDao) {

        if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        LocalDateTime inHour = ticket.getInTime();
        LocalDateTime outHour = ticket.getOutTime();

        // TODO: Some tests are failing here. Need to check if this logic is correct
        long duration = ChronoUnit.SECONDS.between(inHour, outHour);

        // duration set to zero if less than 30 minutes
        if (duration < 30 * 60) {
            duration = 0;
        }

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR) / 3600);
                applyDiscount(ticket.getPrice(), ticket);
                break;
            }
            case BIKE: {
                ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR) / 3600);
                applyDiscount(ticket.getPrice(), ticket);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }

    public void applyDiscount(double price, Ticket ticket) {
        if (ticket.isDiscountPrice()) {
            double discount = (price * 5) / 100;
            price = price - discount;
        }
        ticket.setPrice(price);
    }

}