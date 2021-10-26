package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Timer;
import java.util.TimerTask;


public class FareCalculatorService {


    private void firstThirtyMinutesAreFreeCounter(int minutes) {
        TimerTask task = new TimerTask() {
            public void run() {
            }
        };
            Timer timer = new Timer();
            int delay = minutes * 30;
            timer.schedule(task, delay);
    }

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration = (outHour - inHour) /1000 /3600;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case KNOWNCAR: {
                ticket.getVehicleRegNumber(); // If reg number is know in DB//
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR - (Fare.CAR_RATE_PER_HOUR * 0.05 )); // 5% applied//
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            case KNOWNBIKE: {
                ticket.getVehicleRegNumber(); // If reg number is know in DB//
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR - (Fare.BIKE_RATE_PER_HOUR * 0.05)); // 5% applied//
            }

            default: throw new IllegalArgumentException("Unknown Parking Type");
        }

    }

}