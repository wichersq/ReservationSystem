import java.util.*;

/**
 * One object of AirplaneSeats represents all seats in an airplane.
 */
public class AirplaneSeats {

    private static final int NUM_ROWS_OF_FIRST_CLASS = 2;
    private static final int NUM_ROWS_OF_ECO_CLASS = 20;
    private static final int INDEX_OF_ECO_START = NUM_ROWS_OF_FIRST_CLASS;
    private static final int INDEX_OF_FIRST_START = 0;
    private static final int ROW_FIRST_START = 1;
    private static final int ROW_ECO_START = 10;


    private ArrayList<SeatRow> airplaneSeats = new ArrayList<>();
    private int emptyFirstSeats = 0;
    private int emptyEcoSeats = 0;


    /**
     * Constructor for the class.
     */
    public AirplaneSeats() {
        allocateSeatRow(false);
        allocateSeatRow(true);
    }

    /**
     * Allocates all seat rows depending on the service class.
     * @param isEconomy if it is for economy class or first class.
     */
    private void allocateSeatRow(boolean isEconomy) {
        int rowNumStart;
        int totalRow;
        if (isEconomy) {
            rowNumStart = ROW_ECO_START;
            totalRow = NUM_ROWS_OF_ECO_CLASS;
            emptyEcoSeats = SeatRow.ECONOMY_SEATS_MAP.length * NUM_ROWS_OF_ECO_CLASS;

        } else {
            rowNumStart = ROW_FIRST_START;
            totalRow = NUM_ROWS_OF_FIRST_CLASS;
            emptyFirstSeats =  SeatRow.FIRST_CLASS_SEAT_MAP.length * NUM_ROWS_OF_FIRST_CLASS;
        }
        SeatRow row;
        for (int i = 0; i < totalRow; i++) {
            row = new SeatRow(isEconomy, i + rowNumStart);
            airplaneSeats.add(row);
        }
    }

    /**
     * Converts the row number to the index of airplaneSeats array list.
     *
     * @param row number of a SeatRow.
     * @return an index of the SeatRow in the airplaneSeats list.
     */
    private int convertRowToIndex(int row) {
        if (row <= NUM_ROWS_OF_FIRST_CLASS) {
            return row - ROW_FIRST_START;
        }
        return (row - ROW_ECO_START) + INDEX_OF_ECO_START;
    }

    /**
     * Gets total vacant seats in first class.
     *
     * @return the number of vacant seats.
     */
    public int getEmptyFirstSeats() {
        return emptyFirstSeats;
    }

    /**
     * Gets total vacant seats in economy class.
     *
     * @return the number of vacant seats.
     */
    public int getEmptyEcoSeats() {
        return emptyEcoSeats;
    }

    /**
     * Gets a row with avalible seats for the seat type.
     *
     * @param isEconomy To get a row in economy or not.
     * @param seatType The type of seat to get.
     * @return The avalible row.
     */
    public SeatRow getAvailableSeatRow(boolean isEconomy, String seatType) {
        int end = NUM_ROWS_OF_FIRST_CLASS;
        int start = INDEX_OF_FIRST_START;

        if (isEconomy) {
            start = INDEX_OF_ECO_START;
            end = airplaneSeats.size();
        }
        for (int i = start; i < end; i++) {
            SeatRow row = airplaneSeats.get(i);
            if (row.isTheSeatAvailable(seatType)) {
                return row;
            }
        }
        return null;
    }

    /**
     * Checks if there are no available seats.
     * @return True if there are no seats left.
     */   
    public boolean isFull() {
        return (emptyEcoSeats + emptyFirstSeats == 0);
    }

    /**
     * Finds out the sufficient row to reserve a passenger base on his/her seat preference.
     *
     * @param pas the passenger to make reservation.
     * @return true if successfully reserve a passenger.
     */
    public boolean individualReservation(Passenger pas, SeatRow row) {
        if (row.addIndividualToSeat(pas)) {
            changeTotalVacantSeats(-1, pas.isEconomy());
            return true;
        }
        return false;
    }

    /**
     * Update the total vacant seats for either of Economy or First class.
     *
     * @param num       the number of seats to increase or decrease.
     * @param isEconomy to change economy class or first class.
     */
    public void changeTotalVacantSeats(int num, boolean isEconomy) {
        if (isEconomy) {
            emptyEcoSeats += num;
        } else {
            emptyFirstSeats += num;
        }
    }

    /**
     * Finds seatRow that contains the passenger and removes them.
     *
     * @param pas the passenger to remove.
     * @return true if successfully removed.
     */
    public boolean removeIndividual(Passenger pas) {
        Seat seatToCancel = pas.getSeat();
        SeatRow row = airplaneSeats.get(convertRowToIndex(seatToCancel.getRow()));

        if (row.removePasFromSeat(seatToCancel.getCol())) {
            changeTotalVacantSeats(1, pas.isEconomy());
            return true;
        }
        return false;
    }


    /**
     * Makes group reservation.
     *
     * @param pasGroup a group of passenger needs to make reservation.
     * @return true if successfully reserves.
     */
    public boolean addGroup(GroupOfPassenger pasGroup) {

        int end = NUM_ROWS_OF_FIRST_CLASS;
        int start = INDEX_OF_FIRST_START;

        if (pasGroup.isEconomy()) {
            start = INDEX_OF_ECO_START;
            end = airplaneSeats.size();
        }

        ArrayList<Passenger> passengers = pasGroup.getPassengerGroup();

        int numOfPas = passengers.size();

        ArrayList<Integer> maxAdjSeatPerRow = new ArrayList<Integer>();

        SeatRow row;

        for (int i = start; i < end; i++) {
            row = airplaneSeats.get(i);
            maxAdjSeatPerRow.add(row.findMaxAdjacentSeats());
        }

        int indexOfPas = 0;

        while (numOfPas > 0) {

            int indexOfMax = 0;
            int maxNumOfSeat = 0;

            for (int index = 0; index < maxAdjSeatPerRow.size(); index++) {

                int numOfSeat = maxAdjSeatPerRow.get(index);

                if (numOfSeat >= numOfPas) {
                    maxNumOfSeat = numOfPas;
                    indexOfMax = index;
                    break;
                }

                if (numOfSeat > maxNumOfSeat) {
                    maxNumOfSeat = numOfSeat;
                    indexOfMax = index;

                }

            }
            SeatRow maxRow = airplaneSeats.get(indexOfMax + start);

            ArrayList<Passenger> partialPasList = new ArrayList<Passenger>();
            int indexOfPassBefore = indexOfPas;
            while (maxNumOfSeat > 0) {
                Passenger pas = passengers.get(indexOfPas);
                partialPasList.add(pas);
                indexOfPas++;
                maxNumOfSeat--;
            }

            if (maxRow.groupReservation(partialPasList)) {
                numOfPas -= partialPasList.size();
                maxAdjSeatPerRow.set(indexOfMax, maxRow.findMaxAdjacentSeats());
            }
            else
                indexOfPas = indexOfPassBefore;
        }

        if (numOfPas == 0) {
            changeTotalVacantSeats(-passengers.size(), pasGroup.isEconomy());
            return true;
        }
        return false;
    }


    /**
     * Removes a group of passenger from reservation.
     *
     * @param group the cancelling group.
     * @return true if successfully removed.
     */
    public boolean removeGroup(GroupOfPassenger group) {

        int count = 0;
        for (Passenger pas : group.getPassengerGroup()) {

            if (removeIndividual(pas)) {
                count++;
            }
        }
        return (count == group.size());
    }

    /**
     * Searches all empty seats from the first or economy class.
     *
     * @param isEconomy if it is economy or first class.
     * @return a map with key as the seat row number and value as a list of the empty seat columns.
     */
    public TreeMap<Integer, ArrayList<Integer>> getAllVacantSeats(boolean isEconomy) {
        int start = INDEX_OF_ECO_START;
        int end = airplaneSeats.size();

        if (!isEconomy) {
            start = INDEX_OF_FIRST_START;
            end = NUM_ROWS_OF_FIRST_CLASS;
        }
        TreeMap<Integer, ArrayList<Integer>> seatList = new TreeMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> vacantRows;
        for (int i = start; i < end; i++) {
            SeatRow row = airplaneSeats.get(i);
            vacantRows = row.findEmptySpot();
            if (vacantRows == null) {
                continue;
            }
            seatList.put(row.getRowNumber(), vacantRows);
        }
        return seatList;
    }

    /**
     * Gets the list of passengers.
     *
     * @param isEconomy if it is economy or first class.
     * @return a list of reserved passengers.
     */
    public ArrayList<Passenger> getAllReservedPas(boolean isEconomy) {
        int start = INDEX_OF_ECO_START;
        int end = airplaneSeats.size();

        if (!isEconomy) {
            start = INDEX_OF_FIRST_START;
            end = NUM_ROWS_OF_FIRST_CLASS;
        }
        ArrayList<Passenger> pasList = new ArrayList<>();

        for (int i = start; i < end; i++) {
            SeatRow row = airplaneSeats.get(i);
            ArrayList<Passenger> tempList = row.getPasList();
            if (tempList != null) {
                pasList.addAll(tempList);
            }
        }
        return pasList;
    }

    /**
     * Determines a seat row on the airplane by the row number.
     * @param rowNum a row number.
     * @return a seat row.
     */
    public SeatRow getSeatRow(int rowNum) {
        int index;
        index = convertRowToIndex(rowNum);
        return airplaneSeats.get(index);
    }
}
