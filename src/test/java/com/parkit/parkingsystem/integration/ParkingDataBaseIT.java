package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static com.parkit.parkingsystem.constants.Fare.CAR_RATE_PER_HOUR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private static double ParkingTime = 60; // Expressed in minutes
    private static final String vehicleFakeRegNumber = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleFakeRegNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void testParkingACar() {
        Date inTime = new Date((long) (System.currentTimeMillis() - ParkingTime * 60 * 1000));
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle(inTime);
        Ticket ticketTest = ticketDAO.getTicket(vehicleFakeRegNumber);
        assertNotNull(ticketTest);
        assertEquals(vehicleFakeRegNumber, ticketTest.getVehicleRegNumber());
        assertFalse(ticketTest.getParkingSpot().isAvailable());
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
    }


    @Test
    public void testParkingLotExit() {
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        Ticket ticketTest = TicketDAO.getTicket(vehicleFakeRegNumber);
        assertNotNull(ticketTest);
        checkCalculateFareFromDB(ticketTest);
        assertTrue(ticketTest.getPaid());
        assertTrue(ticketTest.getParkingSpot().isAvailable());
        //TODO: check that the fare generated and out time are populated correctly in the database
    }

    public void checkCalculateFareFromDB(Ticket ticketTest) {
        switch (ticketTest.getParkingSpot().getParkingType()) {
            case CAR: {
                if (ticketDAO.howManyTimesYouVeBeenParked(vehicleFakeRegNumber, 5)) {
                    checkCalculateRegularCarUsersFarFromDB(ticketTest);
                } else {
                    checkCalculateCarFareFromDB(ticketTest);
                }
                break;
            }
            case BIKE: {
            }
            break;
        }
    }


    private void checkCalculateRegularCarUsersFarFromDB(Ticket ticketTest) {
        double price;
        if (ParkingTime <= 30) {
            price = 0;
        } else if (ParkingTime < 60) {
            price = (ParkingTime * 0.025) * 0.95;
        } else {
            price = ((double) (ParkingTime / 60) * CAR_RATE_PER_HOUR) * 0.95;
        }
        assertEquals(price, ticketTest.getPrice());
    }

    public void checkCalculateCarFareFromDB(Ticket ticketTest) {
        double price;
        if (ParkingTime <= 30) {
            price = 0;
        } else if (ParkingTime < 60) {
            price = ParkingTime * 0.025;
        } else {
            price = (double) (ParkingTime / 60) * CAR_RATE_PER_HOUR;
        }
        assertEquals(price, ticketTest.getPrice());
    }

}