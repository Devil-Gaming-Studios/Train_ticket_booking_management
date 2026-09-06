package com.example.demo;

import com.example.demo.entities.Train;
import com.example.demo.entities.User;
import com.example.demo.services.UserBookingService;
import com.example.demo.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class App {
    public static void main(String args[])
    {
//        List<Integer> l = Arrays.asList(1,2,3,4,5,6,7,8,9);
//        List<Integer> l1 = l.stream().filter(isEven()).collect(Collectors.toList());
//        l.stream().map(e -> e*2).collect(Collectors.toList());
        System.out.println("Running Train Booking");
        Scanner scanner = new Scanner(System.in);

        int option = 0;
        UserBookingService userBookingService;
        try{
            userBookingService = new UserBookingService();
        }catch(IOException e)
        {
            e.printStackTrace();
            return;
        }

        Train trainSelectedForBooking = new Train();
        while(option!=7) {
            System.out.println("Choose option");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.println("Enter the username to signup");
                    String nameToSignUp = scanner.next();
                    System.out.println("Enter the password to signup");
                    String passwordToSignUp = scanner.next();
                    User userToSignup = new User(nameToSignUp, passwordToSignUp, UserServiceUtil.hashPassword(passwordToSignUp), new ArrayList<>(), UUID.randomUUID().toString());
                    userBookingService.signUp(userToSignup);
                    break;
                case 2:
                    System.out.println("Enter the username to Login");
                    String nameToLogin = scanner.next();
                    System.out.println("Enter the password to login");
                    String passwordToLogin = scanner.next();
                    System.out.println("DEBUG password='" + passwordToLogin + "' length=" + passwordToLogin.length());
                    User userToLogin = new User(nameToLogin, passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin), new ArrayList<>(), UUID.randomUUID().toString());
                    try {
                        userBookingService = new UserBookingService(userToLogin);
                        if (userBookingService.loginUser()) {
                            System.out.println("Login successful");
                        } else {
                            System.out.println("Login failed");
                        }
                    } catch (IOException ex) {
                        System.err.println("Can't load user booking service");
                        return;
                    }
                    break;
                case 3:
                    System.out.println("Fetching your bookings");
                    userBookingService.fetchBooking();
                    break;
                case 4:
                    System.out.println("Type your source station");
                    String source = scanner.next();
                    System.out.println("Type your destination station");
                    String dest = scanner.next();
                    List<Train> trains;
                    try {
                        trains = userBookingService.getTrain(source, dest);
                    }catch(IOException e)
                    {
                        System.err.println("can't load the train data");
                        return;
                    }
                    int index = 1;
                    for (Train t : trains) {
                        System.out.println(index + " Train id : " + t.getTrainId());
                        for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                            System.out.println("station " + entry.getKey() + " time: " + entry.getValue());
                        }
                        index++;
                    }

                    System.out.println("Select a train by typing 1,2,3...");
                    int train_index = scanner.nextInt();

                    if (train_index >= 1 && train_index <= trains.size()) {
                        trainSelectedForBooking = trains.get(train_index - 1);
                        System.out.println("Selected train: " + trainSelectedForBooking.getTrainId());
                    }
                    else
                        System.err.println("train not found");

                    break;
                case 5:
                    System.out.println("Select a seat out of these seats");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);

                    if(seats != null)
                    for (List<Integer> row : seats) {
                        for (Integer val : row) {
                            System.out.print(val + " ");
                        }
                        System.out.println();
                    }
                    else
                    {
                        System.err.println("seats werent found");
                        continue;
                    }
                    System.out.println("Select the seat by typing the row and column");
                    System.out.println("Enter the row");
                    int row = scanner.nextInt();
                    System.out.println("Enter the column");
                    int col = scanner.nextInt();
                    System.out.println("Booking your seat....");
                    Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col,userBookingService);
                    if (booked.equals(Boolean.TRUE)) {
                        System.out.println("Booked! Enjoy your journey");
                    } else {
                        System.out.println("Can't book this seat");
                    }
                    break;
                case 6:
                    System.out.println("Select a seat out of these seats");
                    seats = userBookingService.fetchSeats(trainSelectedForBooking);

                    if(seats != null)
                        for (List<Integer> rows : seats) {
                            for (Integer val : rows) {
                                System.out.print(val + " ");
                            }
                            System.out.println();
                        }
                    else
                    {
                        System.err.println("seats werent found");
                        continue;
                    }
                    System.out.println("Select the seat by typing the row and column");
                    System.out.println("Enter the row");
                    row = scanner.nextInt();
                    System.out.println("Enter the column");
                    col = scanner.nextInt();
                    System.out.println("Booking your seat....");
                    Boolean canceled = userBookingService.cancelTrainSeat(trainSelectedForBooking, row, col,userBookingService);
                    if (canceled.equals(Boolean.TRUE)) {
                        System.out.println("canceled seat");
                    } else {
                        System.out.println("Can't cancel this seat");
                    }
                    break;
                default:
                    break;
            }
        }
    }
    //public static Predicate<Integer> isEven()
//    {
//        return i -> i % 2 == 0;
//    }



}
