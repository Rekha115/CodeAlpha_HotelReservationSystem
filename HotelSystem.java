import java.io.*;
import java.util.*;

public class HotelSystem {

    // ----- ROOM CLASS -----
    static class Room {
        private int roomNumber;
        private String category;
        private boolean isAvailable;

        public Room(int roomNumber, String category, boolean isAvailable) {
            this.roomNumber = roomNumber;
            this.category = category;
            this.isAvailable = isAvailable;
        }

        public int getRoomNumber() { return roomNumber; }
        public String getCategory() { return category; }
        public boolean isAvailable() { return isAvailable; }
        public void setAvailable(boolean available) { isAvailable = available; }

        public String toString() {
            return roomNumber + "," + category + "," + isAvailable;
        }

        public static Room fromString(String data) {
            String[] parts = data.split(",");
            return new Room(Integer.parseInt(parts[0]), parts[1], Boolean.parseBoolean(parts[2]));
        }
    }

    // ----- BOOKING CLASS -----
    static class Booking {
        private String bookingId;
        private String userName;
        private int roomNumber;
        private String category;
        private int price;

        public Booking(String userName, int roomNumber, String category, int price) {
            this.bookingId = UUID.randomUUID().toString().substring(0, 8);
            this.userName = userName;
            this.roomNumber = roomNumber;
            this.category = category;
            this.price = price;
        }

        public String getBookingId() { return bookingId; }
        public String getUserName() { return userName; }
        public int getRoomNumber() { return roomNumber; }
        public String getCategory() { return category; }
        public int getPrice() { return price; }

        public String toString() {
            return bookingId + "," + userName + "," + roomNumber + "," + category + "," + price;
        }

        public static Booking fromString(String data) {
            String[] parts = data.split(",");
            Booking booking = new Booking(parts[1], Integer.parseInt(parts[2]), parts[3], Integer.parseInt(parts[4]));
            booking.bookingId = parts[0];
            return booking;
        }
    }

    // ----- HOTEL CLASS -----
    static class Hotel {
        private List<Room> rooms;
        private List<Booking> bookings;
        private final String ROOM_FILE = "rooms.txt";
        private final String BOOKING_FILE = "bookings.txt";

        public Hotel() {
            rooms = loadRooms();
            bookings = loadBookings();
        }

        private List<Room> loadRooms() {
            List<Room> roomList = new ArrayList<>();
            File file = new File(ROOM_FILE);
            if (!file.exists()) {
                for (int i = 1; i <= 10; i++) {
                    String cat = i <= 4 ? "Standard" : i <= 7 ? "Deluxe" : "Suite";
                    roomList.add(new Room(i, cat, true));
                }
                saveRooms(roomList);
            } else {
                try (Scanner sc = new Scanner(file)) {
                    while (sc.hasNextLine()) {
                        roomList.add(Room.fromString(sc.nextLine()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return roomList;
        }

        private void saveRooms(List<Room> roomList) {
            try (PrintWriter pw = new PrintWriter(ROOM_FILE)) {
                for (Room r : roomList) {
                    pw.println(r.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private List<Booking> loadBookings() {
            List<Booking> list = new ArrayList<>();
            File file = new File(BOOKING_FILE);
            if (file.exists()) {
                try (Scanner sc = new Scanner(file)) {
                    while (sc.hasNextLine()) {
                        list.add(Booking.fromString(sc.nextLine()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return list;
        }

        private void saveBookings() {
            try (PrintWriter pw = new PrintWriter(BOOKING_FILE)) {
                for (Booking b : bookings) {
                    pw.println(b.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void viewAvailableRooms(String category) {
            boolean found = false;
            for (Room r : rooms) {
                if (r.getCategory().equalsIgnoreCase(category) && r.isAvailable()) {
                    System.out.println("🛏️ Room: " + r.getRoomNumber());
                    found = true;
                }
            }
            if (!found) {
                System.out.println("❌ No available rooms in " + category);
            }
        }

        public void bookRoom(String userName, String category) {
            int price = category.equalsIgnoreCase("Standard") ? 3000 :
                        category.equalsIgnoreCase("Deluxe") ? 5000 : 8000;

            for (Room r : rooms) {
                if (r.getCategory().equalsIgnoreCase(category) && r.isAvailable()) {
                    r.setAvailable(false);
                    Booking booking = new Booking(userName, r.getRoomNumber(), category, price);
                    bookings.add(booking);
                    saveRooms(rooms);
                    saveBookings();
                    System.out.println("✅ Booking successful! Booking ID: " + booking.getBookingId());
                    System.out.println("💳 Payment simulated: ₹" + price);
                    return;
                }
            }
            System.out.println("❌ No rooms available in that category.");
        }

        public void cancelBooking(String bookingId) {
            for (Booking b : bookings) {
                if (b.getBookingId().equals(bookingId)) {
                    bookings.remove(b);
                    for (Room r : rooms) {
                        if (r.getRoomNumber() == b.getRoomNumber()) {
                            r.setAvailable(true);
                            break;
                        }
                    }
                    saveRooms(rooms);
                    saveBookings();
                    System.out.println("✅ Booking canceled.");
                    return;
                }
            }
            System.out.println("❌ Booking ID not found.");
        }

        public void viewBookings() {
            if (bookings.isEmpty()) {
                System.out.println("📭 No bookings found.");
            } else {
                for (Booking b : bookings) {
                    System.out.println("📌 ID: " + b.getBookingId() + ", Name: " + b.getUserName() +
                            ", Room: " + b.getRoomNumber() + ", Category: " + b.getCategory() +
                            ", ₹" + b.getPrice());
                }
            }
        }
    }

    // ----- MAIN METHOD -----
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel();

        while (true) {
            System.out.println("\n--- Hotel Booking System ---");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View All Bookings");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter category (Standard/Deluxe/Suite): ");
                    String cat = sc.nextLine();
                    hotel.viewAvailableRooms(cat);
                    break;
                case 2:
                    System.out.print("Enter your name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter room category (Standard/Deluxe/Suite): ");
                    String category = sc.nextLine();
                    hotel.bookRoom(name, category);
                    break;
                case 3:
                    System.out.print("Enter Booking ID to cancel: ");
                    String id = sc.nextLine();
                    hotel.cancelBooking(id);
                    break;
                case 4:
                    hotel.viewBookings();
                    break;
                case 5:
                    System.out.println("👋 Exiting. Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }
}

