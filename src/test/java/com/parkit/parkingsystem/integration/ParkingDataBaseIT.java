package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

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
    public void testParkingACar() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);//Create new object with parameters assigned

        int next = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);// Looking for an available parking spot
        parkingService.processIncomingVehicle();//This treats the vehicle that comes to park

        Ticket ticket = ticketDAO.getTicket(vehicleFakeRegNumber);// Create a new object assigning a method's class and a parameter
        Assertions.assertNotNull(ticket);// Verify that there's actually a ticket

        ParkingSpot parkingSpot = ticket.getParkingSpot();//Assigning to the object a class's method
        Assertions.assertNotNull(parkingSpot);//Make sure that the parking spot exists
        Assertions.assertFalse(parkingSpot.isAvailable());//Make sure that the parking spot is free
        Assertions.assertEquals(next, parkingSpot.getId());//Make sure that the parking spot located
    }

    @Test
    public void testParkingLotExit() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);//Create new object with parameters assigned

        parkingService.processIncomingVehicle();//This treats the vehicle that comes to park
        Ticket ticket = ticketDAO.getTicket(vehicleFakeRegNumber);// Create a new object assigning a method's class and a parameter
        Assertions.assertNotNull(ticket);// Verify that there's actually a ticket
        parkingService.processExitingVehicle();//This treats the vehicle that comes to park
        ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));//Minus 1 hour means that we get 1 hour back
        ticket.setOutTime(LocalDateTime.now(ZoneId.systemDefault()));//Takes the current hour so it counts difference of when it entered and when it gets out
        Assertions.assertNotNull(ticket.getOutTime());//Verify that there's a ticket with an out time generated in DB
        Assertions.assertEquals(0, ticket.getPrice());//Making sure that the correct price is applied
    }

    @Test
    public void testParkingABike() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();//This treats the vehicle that comes to park
        Ticket ticket = ticketDAO.getTicket(vehicleFakeRegNumber);// Create a new object assigning a class's method  and a parameter

        Assertions.assertNotNull(ticket);// Verify that there's actually a ticket
        Assertions.assertEquals(4, parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE));//Making sure that the correct price is applied
    }

    @Test
    public void testParkingLotExitBike() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);//Create new object with parameters assigned
        parkingService.processIncomingVehicle();//This treats the vehicle that comes to park
        parkingService.processExitingVehicle();//This treats the same vehicle that comes out

        Ticket ticket = ticketDAO.getTicket(vehicleFakeRegNumber);// Create a new object assigning a method's class and a parameter
        ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));//Minus 1 hour means that we get 1 hour back
        ticket.setOutTime(LocalDateTime.now(ZoneId.systemDefault()));//Takes the current hour so it counts difference of when it entered and when it gets out
        Assertions.assertNotNull(ticket.getPrice());//Verify that there's a ticket with a price generated in DB
        Assertions.assertNotNull(ticket.getOutTime());//Verify that there's a ticket with an out time generated in DB

    }

    @Test
    public void FreeFareDataBaseTest() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);//Create new object with parameters assigned
        parkingService.processIncomingVehicle();//This treats the vehicle that comes to park
        parkingService.processExitingVehicle();//This treats the same vehicle that comes out
        Ticket ticket = ticketDAO.getTicket(vehicleFakeRegNumber);// Create a new object assigning a method's class and a parameter
        ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(29));//Minus 29 minutes means that we get 29 minutes back
        ticket.setOutTime(LocalDateTime.now(ZoneId.systemDefault()));//Takes the current hour so it counts difference of when it entered and when it gets out
        Assertions.assertEquals(0 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice());//Making sure that the correct price is applied
    }

}
