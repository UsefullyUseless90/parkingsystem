package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class ParkingSpotTicketTest {

    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private LocalDateTime getTime;
    private Ticket ticket;
    private ParkingSpot parkingSpot;
    private static String FakeVehicleRegNumber ="ABCDEF";

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void ticketSetTest() {
        ticket.setId(12);
        ticket.setVehicleRegNumber(FakeVehicleRegNumber);
        parkingSpot = new ParkingSpot(10, ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(10);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        Assertions.assertEquals(12, ticket.getId());
        Assertions.assertSame(parkingSpot, ticket.getParkingSpot());
        Assertions.assertEquals(FakeVehicleRegNumber, ticket.getVehicleRegNumber());
        Assertions.assertEquals(10, ticket.getPrice());
    }

    @Test
    public void ticketSetNullTimeTest() {
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        Assertions.assertNull(ticket.getInTime());
        Assertions.assertNull(ticket.getOutTime());
    }

    @Test
    public void ParkingSpotSetTest() {
        parkingSpot = new ParkingSpot(0, null, true);
        parkingSpot.setAvailable(false);
        parkingSpot.setId(10);
        parkingSpot.setParkingType(ParkingType.CAR);
        Assertions.assertFalse(parkingSpot.isAvailable());
        Assertions.assertEquals(10, parkingSpot.getId());
        Assertions.assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
    }

}

