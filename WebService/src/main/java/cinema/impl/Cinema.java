package cinema.impl;

import cinema.*;
import javax.jws.WebService;
import java.util.*;

@WebService(name = "WebService", portName = "ICinema_HttpSoap11_Port", targetNamespace = "http://www.iit.bme.hu/soi/hw/SeatReservation", endpointInterface = "cinema.ICinema", wsdlLocation = "WEB-INF/wsdl/SeatReservation.wsdl")
public class Cinema implements ICinema {
    private static final String[] rowCodes = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static boolean isInitialized;
    private static ArrayOfSeat arrayOfSeats;
    private static HashMap<SeatWrapper, SeatStatus> seatStatusMap;
    private static HashMap<String, List<SeatWrapper>> lockedSeats;

    @Override
    public void init(int rows, int columns) throws ICinemaInitCinemaException {
        if(rows < 1 || 26 < rows || columns < 1 || 100 < columns) {
            throw new ICinemaInitCinemaException("Invalid row and/or column value(s).", new CinemaException());
        } else {
            Cinema.arrayOfSeats = new ArrayOfSeat();
            Cinema.seatStatusMap = new HashMap<>();
            Cinema.lockedSeats = new HashMap<>();
            for(int r = 1; r <= rows; r++) {
                for(int c = 1; c <= columns; c++) {
                    SeatWrapper seatWrapper = new SeatWrapper();
                    seatWrapper.setRow(rowCodes[r - 1]);
                    seatWrapper.setColumn(Integer.toString(c));
                    Cinema.arrayOfSeats.getSeat().add(seatWrapper);
                    Cinema.seatStatusMap.put(seatWrapper, SeatStatus.FREE);
                }
            }
            Cinema.isInitialized = true;
        }
    }

    @Override
    public ArrayOfSeat getAllSeats() {
        if(!Cinema.isInitialized) {
            return new ArrayOfSeat();
        }
        return Cinema.arrayOfSeats;
    }

    @Override
    public SeatStatus getSeatStatus(Seat seat) throws ICinemaGetSeatStatusCinemaException {
        if(!Cinema.isInitialized) {
            throw new ICinemaGetSeatStatusCinemaException("Cinema is not yet initialized.", new CinemaException());
        }
        SeatWrapper seatWrapper = new SeatWrapper(seat);
        SeatStatus seatStatus = Cinema.seatStatusMap.get(seatWrapper);
        if(seatStatus == null) {
            throw new ICinemaGetSeatStatusCinemaException("Seat does not exist.", new CinemaException());
        }
        return seatStatus;
    }

    @Override
    public String lock(Seat seat, int count) throws ICinemaLockCinemaException {
        if(!Cinema.isInitialized) {
            throw new ICinemaLockCinemaException("Cinema is not yet initialized.", new CinemaException());
        }
        SeatWrapper seatWrapper = new SeatWrapper(seat);
        int seatIndex = getAllSeats().getSeat().indexOf(seatWrapper);
        if(seatIndex == -1) {
            throw new ICinemaLockCinemaException("Seat to be locked does not exist.", new CinemaException());
        } else if(count <= 0) {
            throw new ICinemaLockCinemaException("Only a positive number of seats can be locked.", new CinemaException());
        }
        LinkedList<SeatWrapper> lockableSeats = new LinkedList<>();
        ListIterator<Seat> listIterator = getAllSeats().getSeat().listIterator(seatIndex);
        SeatWrapper currentSeat = seatWrapper;
        while(listIterator.hasNext() && lockableSeats.size() != count) {
            try {
                currentSeat = (SeatWrapper) listIterator.next();
                if(getSeatStatus(currentSeat) != SeatStatus.FREE) {
                    throw new ICinemaLockCinemaException("One seat is not free.", new CinemaException());
                } else if(!currentSeat.getRow().equals(seat.getRow())) {
                    break;
                } else {
                    lockableSeats.add(currentSeat);
                }
            } catch(ICinemaGetSeatStatusCinemaException e) {
                throw new ICinemaLockCinemaException(e.getMessage(), new CinemaException());
            }
        }
        if(lockableSeats.size() != count) {
            throw new ICinemaLockCinemaException("There aren't enough lockable seats in the row.", new CinemaException());
        }
        for(SeatWrapper sw : lockableSeats) {
            Cinema.seatStatusMap.put(sw, SeatStatus.LOCKED);
        }
        String lockId = seat.getRow() + "-" + seat.getColumn() + "-" + currentSeat.getColumn(); // A-1-14
        Cinema.lockedSeats.put(lockId, lockableSeats);
        return lockId;
    }

    @Override
    public void unlock(String lockId) throws ICinemaUnlockCinemaException {
        if(!Cinema.isInitialized) {
            throw new ICinemaUnlockCinemaException("Cinema is not yet initialized.", new CinemaException());
        }
        List<SeatWrapper> lockedSeats = Cinema.lockedSeats.get(lockId);
        if(lockedSeats == null || lockedSeats.isEmpty()) {
            throw new ICinemaUnlockCinemaException("Wrong lockid.", new CinemaException());
        }
        SeatStatus lockStatus = Cinema.seatStatusMap.get(lockedSeats.get(0));
        if(lockStatus != SeatStatus.LOCKED) {
            throw new ICinemaUnlockCinemaException("Only locked seats can be unlocked.", new CinemaException());
        }
        Cinema.lockedSeats.remove(lockId);
        for(SeatWrapper sw : lockedSeats) {
            Cinema.seatStatusMap.put(sw, SeatStatus.FREE);
        }
    }

    @Override
    public void reserve(String lockId) throws ICinemaReserveCinemaException {
        if(!Cinema.isInitialized) {
            throw new ICinemaReserveCinemaException("Cinema is not yet initialized.", new CinemaException());
        }
        List<SeatWrapper> lockedSeats = Cinema.lockedSeats.get(lockId);
        if(lockedSeats == null || lockedSeats.isEmpty()) {
            throw new ICinemaReserveCinemaException("Wrong lockid.", new CinemaException());
        }
        SeatStatus lockStatus = Cinema.seatStatusMap.get(lockedSeats.get(0));
        if(lockStatus != SeatStatus.LOCKED) {
            throw new ICinemaReserveCinemaException("Only locked seats can be reserved.", new CinemaException());
        }
        for(SeatWrapper sw : lockedSeats) {
            Cinema.seatStatusMap.put(sw, SeatStatus.RESERVED);
        }
    }

    @Override
    public void buy(String lockId) throws ICinemaBuyCinemaException {
        if(!Cinema.isInitialized) {
            throw new ICinemaBuyCinemaException("Cinema is not yet initialized.", new CinemaException());
        }
        List<SeatWrapper> lockedSeats = Cinema.lockedSeats.get(lockId);
        if(lockedSeats == null || lockedSeats.isEmpty()) {
            throw new ICinemaBuyCinemaException("Wrong lockid.", new CinemaException());
        }
        SeatStatus lockStatus = Cinema.seatStatusMap.get(lockedSeats.get(0));
        if(lockStatus != SeatStatus.LOCKED && lockStatus != SeatStatus.RESERVED) {
            throw new ICinemaBuyCinemaException("Only locked or reserved seats can be bought.", new CinemaException());
        }
        for(SeatWrapper sw : lockedSeats) {
            Cinema.seatStatusMap.put(sw, SeatStatus.SOLD);
        }
    }

    private static class SeatWrapper extends Seat {
        public SeatWrapper() {}

        public SeatWrapper(Seat s) {
            row = s.getRow();
            column = s.getColumn();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || !(obj instanceof SeatWrapper)) {
                return false;
            }
            SeatWrapper sw = (SeatWrapper) obj;
            return row.equals(sw.row) && column.equals(sw.column);
        }

        @Override
        public int hashCode() {
            return (row + column).hashCode();
        }
    }
}
