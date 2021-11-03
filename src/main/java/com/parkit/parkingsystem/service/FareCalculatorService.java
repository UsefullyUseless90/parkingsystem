package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;


public class FareCalculatorService{
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void calculateFare(Ticket ticket){

        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        double duration = (outHour - inHour) /1000 /3600;

        if (duration < 0.5) {
            duration = 0;

        }
        switch (ticket.getParkingSpot().getParkingType()) {

            case CAR: {
                double fare = duration * Fare.CAR_RATE_PER_HOUR;
                TicketDAO TA = new TicketDAO();
                if (TA.howManyTimesYouVeBeenParked(ticket.getVehicleRegNumber(), 5)>5){
                    fare = 0.95 * fare;
                }
                ticket.setPrice(fare);
                break;
            }

            case BIKE: {
                double fare = duration * Fare.BIKE_RATE_PER_HOUR;
                TicketDAO TA = new TicketDAO();
                if (TA.howManyTimesYouVeBeenParked(ticket.getVehicleRegNumber(), 5)>5){
                    fare = 0.95 * fare;
                }
                ticket.setPrice(fare);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
    }
}
}

