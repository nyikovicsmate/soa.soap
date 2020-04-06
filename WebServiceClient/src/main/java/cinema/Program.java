package cinema;

import javax.xml.ws.BindingProvider;
import java.util.List;

public class Program {
     public static void main(String[] args) {
        if(args.length != 4) {
            System.out.println("Wrong method call.");
            return;
        }
        String url = args[0];
        String row = args[1];
        String column = args[2];
        String task = args[3];
        // Create the proxy factory:
        CinemaService cinemaService = new CinemaService();
        // Create the hello proxy object:
        ICinema cinema = cinemaService.getICinemaHttpSoap11Port();
        // Cast it to a BindingProvider:
        BindingProvider bp = (BindingProvider) cinema;
        // Set the URL of the service:
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        // Call the service:
        Seat seat = new Seat();
        seat.setRow(row);
        seat.setColumn(column);
        switch(task) {
            case "Init":
                try {
                    cinema.init(Integer.parseInt(row), Integer.parseInt(column));
                    System.out.println("Initialized cinema " + row + " rows " + column + " columns.");
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "GetAllSeats":
                try {
                    ArrayOfSeat arrayOfSeat = null;
                    arrayOfSeat = cinema.getAllSeats();
                    List<Seat> seats = arrayOfSeat.getSeat();
                    System.out.println("Cinema seats:");
                    for (Seat s : seats) {
                        System.out.println(s.getRow() + "-" + s.getColumn());
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "GetSeatStatus":
                try {
                    SeatStatus seatStatus = null;
                    seatStatus = cinema.getSeatStatus(seat);
                    System.out.println(seatStatus.value());
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Lock":
                try {
                    String lockId = cinema.lock(seat, 1);
                    System.out.println(lockId);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Unlock":
                try {
                    String lockId = seat.getLockid();
                    cinema.unlock(lockId);
                    System.out.println(lockId);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Reserve":
                try {
                    String lockId = cinema.lock(seat, 1);
                    cinema.reserve(lockId);
                    System.out.println(lockId);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Buy":
                try {
                    String lockId = seat.getLockid();
                    cinema.buy(lockId);
                    System.out.println(lockId);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
