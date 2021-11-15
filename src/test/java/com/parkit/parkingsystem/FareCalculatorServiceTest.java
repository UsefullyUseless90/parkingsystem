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
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(1);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, ticketDao);
        assertEquals(ticket.getPrice(), (Fare.CAR_RATE_PER_HOUR));

    }

    @Test
    // This verify if the bike fare is correctly applied for an hour parking
    public void calculateFareBike() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(1);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, ticketDao);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnknownType() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(1);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());

        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // we want to be sure if there is no vehicule type the FareCalculator class will
        // throwing an exception
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDao));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(1);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDao));
    }

    @Test
    // 45 minutes parking time should give 3/4th parking fare
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, ticketDao);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    // 45 minutes parking time should give 3/4th parking fare
    // This verify if for the same duration with bike fare
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, ticketDao);
        assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    // This verify how the fare is counted when a car is parked mor than 24h
    public void calculateFareCarWithMoreThanADayParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusHours(25);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, ticketDao);
        assertEquals((25 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }
    @Test
    public void calculateFareCarForThirtyMinutesParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(30);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, ticketDao);
        assertEquals((0.5 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareBikeForThirtyMinutesParkingTime() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(30);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());

        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, ticketDao);
        assertEquals((0.5 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    // This verify if the 30 minutes and less are not counted and that free fare is applied for a bike
    public void calculateFareCarParkingTimeLessThanThirtyMinutes() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(29);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, ticketDao);
        assertEquals((0), ticket.getPrice());

    }

    @Test
    // This verify if the 30 minutes and less are not counted and that free fare is applied for a bike
    public void calculateFareBikeParkingTimeLessThanThirtyMinutes() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(12);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, ticketDao);
        assertEquals(0, ticket.getPrice());

    }

    @Test
    // This test verify if the discount is applied to car users
    public void calculateForRegularUsersCarFare() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setVehicleRegNumber(vehicleFakeRegNumber);
        ticket.setParkingSpot(parkingSpot);
        ticket.setDiscountPrice(true);
        ticket.getPrice();

        fareCalculatorService.calculateFare(ticket, ticketDao);
        double discount = (Fare.CAR_RATE_PER_HOUR * 5) / 100;
        assertEquals(ticket.isDiscountPrice(), true);
        assertEquals((Fare.CAR_RATE_PER_HOUR - discount), ticket.getPrice());

    }

    @Test
    // This test verify if the discount is applied to bike users
    public void calculateForRegularUsersBikeFare() {
        LocalDateTime inTime = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now(ZoneId.systemDefault());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setVehicleRegNumber(vehicleFakeRegNumber);
        ticket.setParkingSpot(parkingSpot);
        ticket.setDiscountPrice(true);
        ticket.getPrice();

        fareCalculatorService.calculateFare(ticket, ticketDao);
        double discount = (Fare.BIKE_RATE_PER_HOUR * 5) / 100;
        assertEquals(ticket.isDiscountPrice(), true);
        assertEquals((Fare.BIKE_RATE_PER_HOUR - discount), ticket.getPrice());
    }
}