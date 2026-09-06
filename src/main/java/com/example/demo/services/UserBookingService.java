package com.example.demo.services;

import com.example.demo.entities.Ticket;
import com.example.demo.entities.Train;
import com.example.demo.entities.User;
import com.example.demo.util.UserServiceUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.aot.hint.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserBookingService {
    private User user;

    private List<User> userList;
    private ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String USERS_PATH = "src/main/java/com/example/demo/localDb/users.json";
    private static final String TRAINS_PATH = "src/main/java/com/example/demo/localDb/trains.json";
    private static List<Train> all_trains;
    private String source;
    private String destination;
    public UserBookingService(User user) throws IOException {
        this.user = user;
        userList = loadUsers();
        all_trains = loadTrains();
    }

    public UserBookingService() throws IOException {
        userList = loadUsers();
        all_trains = loadTrains();
    }

    public List<User> loadUsers() throws IOException {
        File users = new File(USERS_PATH);
        return OBJECT_MAPPER.readValue(users, new TypeReference<List<User>>() {
        });
    }

    public List<Train> loadTrains() throws IOException {
        File trains = new File(TRAINS_PATH);
        return OBJECT_MAPPER.readValue(trains, new TypeReference<List<Train>>() {
        });
    }

    public Boolean loginUser() {
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            boolean nameMatch = user1.getName().equals(user.getName());
            boolean passMatch = UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
            System.out.println("Comparing: '" + user1.getName() + "' vs '" + user.getName() + "' nameMatch=" + nameMatch + " passMatch=" + passMatch + " hashed password = "+ UserServiceUtil.hashPassword(user.getPassword()));
            System.out.println("storedHash=" + user1.getHashedPassword());
            return nameMatch && passMatch;
        }).findFirst();

        if (foundUser.isPresent()) {
            this.user = foundUser.get();   // ← replace with the real, persisted user
            return true;
        }
        return false;
    }

    public Boolean signUp(User user1) {
        try {
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        OBJECT_MAPPER.writeValue(usersFile, userList);
    }

    //json --> object(user) --> deserialize
    //object --> json --> serialize
    public void fetchBooking() {
        user.printTickets();
    }

    public Boolean cancelBooking(String ticketId) {
        List<Ticket> listTicket = user.getTicketsBooked();
        Optional<Ticket> ticket1 = listTicket.stream().filter(ticket -> {
            return ticket.getTicketId().equals(ticketId);
        }).findFirst();
        if (ticket1.isPresent()) {
            listTicket.remove(ticket1.get());
            user.setTicketsBooked(listTicket);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public List<Train> getTrains() {
        return all_trains;
    }

    public List<Train> getTrain(String source, String destination) throws IOException {
        //System.out.println(all_trains.get(0).getStations());
        return all_trains.stream().filter(train -> {
            return train_filter(train, source, destination);
        }).toList();
    }

    private boolean train_filter(Train train, String source, String destination) {
        List<String> stations = train.getStations();
        boolean first = false;

        for (int i = 0; i < stations.size(); i++) {
            if(stations.get(i).equalsIgnoreCase(source))
            {
                first = true;
            }
            else if(first && stations.get(i).equalsIgnoreCase(destination))
            {
                this.source = source;
                this.destination = destination;
                return true;
            }
        }
        return false;
    }

    public List<List<Integer>> fetchSeats(Train train) {
        System.out.println(user.getTicketsBooked().size());
        for(int i = user.getTicketsBooked().size() -1 ; i > -1 ; i--) {
            if (user.getTicketsBooked().get(i).getTrain().getTrainId().equals(train.getTrainId()))
                return user.getTicketsBooked().get(i).getTrain().getSeats();
        }
        return train.getSeats();
    }


    public boolean bookTrainSeat(Train train, int row, int col,UserBookingService userBookingService) {

        List<List<Integer>> seats = userBookingService.fetchSeats(train);

        if (seats == null || seats.isEmpty()) {
            System.out.println("No seats available for this train.");
            return false;
        }
        if(row < 0 || row >= seats.size() || col < 0 || col >= seats.get(row).size())
        {
            return false;
        }

        if(seats.get(row).get(col) == 1)
        {
            return false;
        }

        seats.get(row).set(col,1);
        train.setSeats(seats);
        List<Ticket> list_tickets = user.getTicketsBooked();
        if(list_tickets == null)
            list_tickets = new ArrayList<Ticket>();

        Ticket booked_ticket = new Ticket(UUID.randomUUID().toString(), user.getUserId(),source,destination,"122",train);
        list_tickets.add(booked_ticket);
        user.setTicketsBooked(list_tickets);
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(user.getUserId())) {
                userList.set(i, user);
                break;
            }
        }
        try {
            saveUserListToFile();
        }
        catch(Exception e)
        {
            System.err.println("ticket was not saved on the disk");
        }
        return true;
    }

    public boolean cancelTrainSeat(Train train, int row, int col,UserBookingService userBookingService) {

        List<List<Integer>> seats = userBookingService.fetchSeats(train);

        if (seats == null || seats.isEmpty()) {
            System.out.println("No seats available for this train.");
            return false;
        }
        if(row < 0 || row >= seats.size() || col < 0 || col >= seats.get(row).size())
        {
            return false;
        }

        if(seats.get(row).get(col) == 0)
        {
            return false;
        }

        seats.get(row).set(col,0);
        train.setSeats(seats);
        List<Ticket> list_tickets = user.getTicketsBooked();
        if(list_tickets == null)
            list_tickets = new ArrayList<Ticket>();

        Ticket booked_ticket = new Ticket(UUID.randomUUID().toString(), user.getUserId(),source,destination,"122",train);
        list_tickets.add(booked_ticket);
        user.setTicketsBooked(list_tickets);
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(user.getUserId())) {
                userList.set(i, user);
                break;
            }
        }
        try {
            saveUserListToFile();
        }
        catch(Exception e)
        {
            System.err.println("ticket was not saved on the disk");
        }
        return true;
    }
}