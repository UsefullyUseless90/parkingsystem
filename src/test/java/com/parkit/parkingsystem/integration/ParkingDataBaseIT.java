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
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        int next = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket(vehicleFakeRegNumber);
        Assertions.assertNotNull(ticket);

        ParkingSpot parkingSpot = ticket.getParkingSpot();
        Assertions.assertNotNull(parkingSpot);
        Assertions.assertFalse(parkingSpot.isAvailable());
        Assertions.assertEquals(next, parkingSpot.getId());
    }

    @Test
    public void testParkingLotExit() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(vehicleFakeRegNumber);
        Assertions.assertNotNull(ticket);
        parkingService.processExitingVehicle();
        ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));
        ticket.setOutTime(LocalDateTime.now(ZoneId.systemDefault()));
        Assertions.assertNotNull(ticket.getOutTime());
        Assertions.assertEquals(0, ticket.getPrice());
    }
    @Test
    public void testParkingABike() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(vehicleFakeRegNumber);

        Assertions.assertNotNull(ticket);
        Assertions.assertEquals(4, parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE));
    }

    @Test
    public void testParkingLotExitBike() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();

        Ticket ticket = ticketDAO.getTicket(vehicleFakeRegNumber);
        ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));
        ticket.setOutTime(LocalDateTime.now(ZoneId.systemDefault()));
        Assertions.assertNotNull(ticket.getPrice());
        Assertions.assertNotNull(ticket.getOutTime());

    }
    @Test
    public void FreeFareDataBaseTest(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();
        Ticket ticket = ticketDAO.getTicket(vehicleFakeRegNumber);
        ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(29));
        ticket.setOutTime(LocalDateTime.now(ZoneId.systemDefault()));
        Assertions.assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

}