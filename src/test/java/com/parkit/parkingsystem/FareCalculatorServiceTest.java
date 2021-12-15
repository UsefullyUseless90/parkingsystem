package com.parkit.parkingsystem;


import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;
    private TicketDAO ticketDao = new TicketDAO();
    private static final String vehicleFakeRegNumber = "ABCDEF";
    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();

    }
    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }



    @Test
    // This verify if the car fare is correctly applied for an hour parking
    public void calculateFareCar() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(1);// Minus 1 Hour means that we get 1 Hour back from now
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it makes difference of when it entered and when it gets out

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);// Creating Object and assigning parameters spot number, type, availability
        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        assertEquals(ticket.getPrice(), (Fare.CAR_RATE_PER_HOUR));//Making sure that the correct price is applied

    }

    @Test
    // This verify if the bike fare is correctly applied for an hour parking
    public void calculateFareBike() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(1);// Minus 1 Hour means that we get 1 Hour back from now
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);// Creating Object and assigning parameters spot number, type, availability
        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);//Making sure that the correct price is applied
    }

    @Test
    //Verify how the system works when an unknown is shown
    public void calculateFareUnknownType() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(1);// Minus 1 Hour means that we get 1 Hour back from now
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out

        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);// Creating Object and assigning parameters spot number, type, availability

        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot

        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDao));// Verify that if there is no vehicle type the FareCalculator class will throw an exception

    }
    @Test
    //Verify how the system works when the outTime is before the inTime
    public void calculateFareCarWithFutureInTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(1);// Minus 1 Hour means that we get 1 Hour back from now

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);// Creating Object and assigning parameters spot number, type, availability

        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDao));
    }

    @Test
    //Verify how the system works when the outTime is before the inTime
    public void calculateFareBikeWithFutureInTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(1);// Minus 1 Hour means that we get 1 Hour back from now

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);// Creating Object and assigning parameters spot number, type, availability

        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDao));
    }

    @Test
    //Verify how the fare is applied for 3/4th hour of bike parking
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(45);// Minus 45 minutes means that we get 25 minutes back
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);// Creating Object and assigning parameters spot number, type, availability

        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());//Making sure that the correct price is applied
    }

    @Test
    // This verify if as previously tested the duration but with  the car fare applied
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(45);// Minus 45 minutes means that we get 25 minutes back
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);// Creating Object and assigning parameters spot number, type, availability

        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());//Making sure that the correct price is applied
    }

    @Test
    // This verify how the fare is counted when a car is parked more than 24h
    public void calculateFareCarWithMoreThanADayParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(25);// Minus 25 minutes means that we get 25 minutes back
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);// Creating Object and assigning parameters spot number, type, availability
        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        assertEquals((25 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());//Making sure that the correct price is applied
    }
    @Test
    // This verify how the fare is counted when a bike is parked more than 24h
    public void calculateFareBikeWithMoreThanADayParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(25);// Minus 25 minutes means that we get 25 minutes back
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);// Creating Object and assigning parameters spot number, type, availability
        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        assertEquals((25 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());//Making sure that the correct price is applied
    }

    @Test
    public void calculateFareCarForThirtyMinutesParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(30);// Minus 30 minutes means that we get 30 minutes back
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);// Creating Object and assigning parameters spot number, type, availability

        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        assertEquals((0.5 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());//Making sure that the correct price is applied
    }

    @Test
    public void calculateFareBikeForThirtyMinutesParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(30);// Minus 30 minutes means that we get 30 minutes back
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out

        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);// Creating Object and assigning parameters spot number, type, availability

        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        assertEquals((0.5 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());//Making sure that the correct price is applied
    }

    @Test
    // This verify if the 30 minutes and less are not counted and that the free fare is applied for a bike
    public void calculateFareCarParkingTimeLessThanThirtyMinutes() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(29);// Minus 29 minutes means that we get 29 minutes back
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);// Creating Object and assigning parameters spot number, type, availability
        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        assertEquals((0), ticket.getPrice());//Making sure that the correct price is applied

    }

    @Test
    // This verify if the 30 minutes and less are not counted and that free fare is applied for a bike
    public void calculateFareBikeParkingTimeLessThanThirtyMinutes() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(12);// Minus 12 minutes means that we get 12 minutes back
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);// Creating Object and assigning parameters spot number, type, availability
        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        assertEquals(0, ticket.getPrice());//Making sure that the correct price is applied

    }

    @Test
    // This test verify if the discount is applied to car users
    public void calculateForRegularUsersCarFare() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(60);// Minus 60 minutes means that we get 60 minutes back
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);// Creating Object and assigning parameters spot number, type, availability
        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setVehicleRegNumber(vehicleFakeRegNumber);//Gets the reg number of a vehicle in that case the one that we chose
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        ticket.setDiscountPrice(true);//Validate the fact that the user is a regular
        ticket.getPrice();// Sorting the price

        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        double discount = (Fare.CAR_RATE_PER_HOUR * 5) / 100;//Calculating the 5% discount in case of regular user
        assertEquals(ticket.isDiscountPrice(), true);// Making sure that the user is a regular
        assertEquals((Fare.CAR_RATE_PER_HOUR - discount), ticket.getPrice());//Making sure that the correct price with the discount is applied

    }

    @Test
    // This test verify if the discount is applied to bike users
    public void calculateForRegularUsersBikeFare() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(60);// Minus 60 minutes means that we get 60 minutes back
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());//Takes the current hour so it counts difference of when it entered and when it gets out
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);// Creating Object and assigning parameters spot number, type, availability

        ticket.setInTime(inTime);//Setting the time of entering
        ticket.setOutTime(outTime);//Setting the time of exiting
        ticket.setVehicleRegNumber(vehicleFakeRegNumber);//Gets the reg number of a vehicle in that case the one that we chose
        ticket.setParkingSpot(parkingSpot);// Assigning a parking spot
        ticket.setDiscountPrice(true);//Validate the fact that the user is a regular
        ticket.getPrice();// Sorting the price

        fareCalculatorService.calculateFare(ticket, ticketDao);//Calculating price regarding the stay duration
        double discount = (Fare.BIKE_RATE_PER_HOUR * 5) / 100;//Calculating the 5% discount in case of regular user
        assertEquals(ticket.isDiscountPrice(), true);// Making sure that the user is a regular
        assertEquals((Fare.BIKE_RATE_PER_HOUR - discount), ticket.getPrice());//Making sure that the correct price is applied
    }
    
}