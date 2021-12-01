package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static String vehicleFakeRegNumber = "ABCDEF";
    private static ParkingService parkingService;
    private static ParkingType parkingType;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;


    @Test
    public void processIncomingVehicleTest() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1);//This mock the function of selecting the type of vehicle this case the number one (CAR type)
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleFakeRegNumber);//This mock the function of putting the reg number to register the vehicle in DB so it can be assigned a ticket
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);//Creates a new object assigning parameters to it (parking spot number, vehicle type, availability)
            Ticket ticket = new Ticket();//Creates a new object
            ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));// Minus 1 Hour means that we get 1 Hour back from now
            ticket.setParkingSpot(parkingSpot);// Assigning parking spot to a ticket
            ticket.setVehicleRegNumber(vehicleFakeRegNumber);// Assigning the reg number previously assigned for the test
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true); //Mock of ParkingSpotDAO updating the infos (parking spot number and availability) in DB
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);//Creates a new object with parameters assigned
            ParkingType car = ParkingType.CAR; //Creates a new object with a type assigned
            when(parkingSpotDAO.getNextAvailableSlot(car)).thenReturn(2);//Mock of ParkingSpotDAO and showing the number of the parking spot
        } catch (Exception e) {
            e.printStackTrace();// This show what's wrong
            throw new RuntimeException("Failed to set up test mock objects");//Exception thrown when there's a fail in mocking objects
        }
        parkingService.processIncomingVehicle();//Finally calling the tested method
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));//Mock of a verification of DB updating parking spot availability
    }

    @Test
    public void getVehicleTypeTestCar() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1);//This mock the function of selecting the type of vehicle this case the number one (CAR type)
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);//Creates a new object assigning parameters to it (parking spot number, vehicle type, availability)
            Ticket ticket = new Ticket();//Creates a new object
            ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));// Minus 1 Hour means that we get 1 Hour back from now
            ticket.setParkingSpot(parkingSpot);// Assigning parking spot to a ticket
            ticket.setVehicleRegNumber(vehicleFakeRegNumber);// Assigning the reg number previously assigned for the test
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);//Creates a new object with parameters assigned
        } catch (Exception e) {
            e.printStackTrace();// This show what's wrong
            throw new RuntimeException("Failed to set up test mock objects");//Exception thrown when there's a fail in mocking objects
        }
        Assertions.assertEquals(ParkingType.CAR, parkingService.getVehicleType());// Verify that vehicle type is actually a car
    }

    @Test
    // Verify how process a bike when it's entering the parking lot
    public void getVehicleTypeTestBike() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(2);//This mock the function of selecting the type of vehicle this case the number two (BIKE type)
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);//Creates a new object assigning parameters to it (parking spot number, vehicle type, availability)
            Ticket ticket = new Ticket();//Creates a new object
            ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));// Minus 1 Hour means that we get 1 Hour back from now
            ticket.setParkingSpot(parkingSpot);// Assigning parking spot to a ticket
            ticket.setVehicleRegNumber(vehicleFakeRegNumber);// Assigning the reg number previously assigned for the test
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);//Creates a new object with parameters assigned
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        Assertions.assertEquals(ParkingType.BIKE, parkingService.getVehicleType());// Verify that vehicle type is actually a bike
    }

    @Test
    //Verify how reacts the system when it's facing an unknown type
    public void getVehicleTypeTestUnknown() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(3);//This mock the function of selecting the type of vehicle this case the number three (UNKNOWN type)
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);//Creates a new object assigning parameters to it (parking spot number, vehicle type, availability)
            Ticket ticket = new Ticket();//Creates a new object
            ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));// Minus 1 Hour means that we get 1 Hour back from now
            ticket.setParkingSpot(parkingSpot);// Assigning parking spot to a ticket
            ticket.setVehicleRegNumber(vehicleFakeRegNumber);// Assigning the reg number previously assigned for the test
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);//Creates a new object with parameters assigned
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        Assertions.assertThrows(IllegalArgumentException.class, () -> parkingService.getVehicleType());
    }


    @Test
    //Verify how process while a vehicle exiting the parking works
    public void processExitingVehicleTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleFakeRegNumber);//This mock the function of putting the reg number to register the vehicle in DB so it can be assigned a ticket
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);//Creates a new object assigning parameters to it (parking spot number, vehicle type, availability)
            Ticket ticket = new Ticket();//Creates a new object
            ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));// Minus 1 Hour means that we get 1 Hour back from now
            ticket.setParkingSpot(parkingSpot);// Assigning parking spot to a ticket
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);//Mock of ticketDAO updating the infos in DB
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);//Mock of ticketDAO updating the infos (ticket and availability) in DB

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);//Mock of ParkingSpotDAO updating the infos (parking spot number and availability) in DB

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);//Creates a new object with parameters assigned
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        parkingService.processExitingVehicle();//Finally calling the tested method
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));//Verify that the parking space is updated in DB
    }
    @Test
    //Verify how process while a bike exiting the parking works
    public void processExitingBikeTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleFakeRegNumber);//This mock the function of putting the reg number to register the vehicle in DB so it can be assigned a ticket
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);//Creates a new object assigning parameters to it (parking spot number, vehicle type, availability)
            Ticket ticket = new Ticket();//Creates a new object
            ticket.setInTime(LocalDateTime.now(ZoneId.systemDefault()).minusHours(1));// Minus 1 Hour means that we get 1 Hour back from now
            ticket.setParkingSpot(parkingSpot);// Assigning parking spot to a ticket
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);//Mock of ticketDAO updating the infos in DB
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);//Mock of ticketDAO updating the infos (ticket and availability) in DB

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);//Mock of ParkingSpotDAO updating the infos (parking spot number and availability) in DB

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);//Creates a new object with parameters assigned
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        parkingService.processExitingVehicle();//Finally calling the tested method
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));//Verify that the parking space is updated in DB
    }

}