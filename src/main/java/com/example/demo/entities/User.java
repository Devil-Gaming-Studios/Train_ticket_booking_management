package com.example.demo.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String name;
    private String password;

    @JsonProperty("hashed_password")
    private String hashedPassword;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("ticketsBooked")
    private List<Ticket> ticketsBooked;


    public String getName()
    {
        return name;
    }
    public User(String name, String password, String hashedPassword, List<Ticket> ticketsBooked, String userId){
        this.name = name;
        this.password = password;
        this.hashedPassword = hashedPassword;
        this.ticketsBooked = ticketsBooked;
        this.userId = userId;
    }

    public User(){}

    public String getPassword(){
        return password;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public List<Ticket> getTicketsBooked() {
        return ticketsBooked;
    }

    public void printTickets(){
        if (ticketsBooked == null || ticketsBooked.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        for(int i = 0; i<ticketsBooked.size(); i++){
            System.out.println(ticketsBooked.get(i).getTicketInfo());
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setTicketsBooked(List<Ticket> ticketsBooked) {
        this.ticketsBooked = ticketsBooked;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
