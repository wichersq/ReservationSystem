
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * One object of SeatRow class represents a row of seat in the airplane.
 */
public class SeatRow {
    private final static int NUM_SEATS_DEFAULT = 2;
    private final static int NUM_CEN_SEAT_FIRST_CLASS = 0;
    private String[] seatMap;
    private Hashtable<String, ArrayList<Integer>> indexOfSeatType;
    private int totalSeatsInRow;
    private int numOfCenterSeat;
    private int numOfAisleSeat;
    private int numOfWindowSeat;
    private int totalVacantSeats;
    private static final String WIN_SEAT = "W";
    private static final String CEN_SEAT = "C";
    private static final String AIS_SEAT = "A";
    public static final String[] ECONOMY_SEATS_MAP = {"W", "C", "A", "A", "C", "W"};
    public static final String[] FIRST_CLASS_SEAT_MAP = {"W", "A", "A", "W"};
    private int rowNumber = -1;
    private Passenger[] seatRow;


    /**
     * Constructor of the class
     *
     * @param isEconomy if the row is first or economy Class
     * @param rowNum    row number
     */
    public SeatRow(boolean isEconomy, int rowNum) {
        if (isEconomy) {
            numOfCenterSeat = NUM_SEATS_DEFAULT;
            seatMap = ECONOMY_SEATS_MAP;
        } else {
            numOfCenterSeat = NUM_CEN_SEAT_FIRST_CLASS;
            seatMap = FIRST_CLASS_SEAT_MAP;
        }
        numOfAisleSeat = NUM_SEATS_DEFAULT;
        numOfWindowSeat = NUM_SEATS_DEFAULT;
        createSeatMap();
        rowNumber = rowNum;

        totalSeatsInRow = numOfAisleSeat + numOfWindowSeat + numOfCenterSeat;
        totalVacantSeats = totalSeatsInRow;
        seatRow = new Passenger[totalVacantSeats];
    }


    /**
     * Creates indexes of array corresponding to the location of different seat types in a seat row
     *
     * @return a hash table with key as a seat type and indexes as value
     */
    private void createSeatMap() {
        indexOfSeatType = new Hashtable<String, ArrayList<Integer>>();
        for (int i = 0; i < seatMap.length; i++) {
            String key = seatMap[i];
            if (!indexOfSeatType.containsKey(key)) {
                indexOfSeatType.put(seatMap[i], new ArrayList<>());
            }
            indexOfSeatType.get(key).add(i);
        }
    }


    /**
     * Find maximum number of empty adjacent seats in row
     *
     * @return a number of the seats
     */
    public int findMaxAdjacentSeats() {

        if (totalVacantSeats <= 0) return totalVacantSeats;
        int maxAdjacent = 0;
        int vacantSeats = 0;

        for (Passenger k : seatRow) {

            if (k == null)
                vacantSeats++;

            else
                vacantSeats = 0;

            if (vacantSeats > maxAdjacent)
                maxAdjacent = vacantSeats;
        }
        return maxAdjacent;
    }


    /**
     * Adds passengers to adjacent seats
     *
     * @param passengers list of passengers to add
     * @return true if successfully adds all passengers otherwise false
     */

    public boolean groupReservation(ArrayList<Passenger> passengers) {
        int totalSeatBeforeAdd = totalVacantSeats;
        int[] seatColList = getAdjacentSeatsList(passengers.size());
        for (int i = 0; i < passengers.size(); i++) {
            addToSeat(passengers.get(i), seatColList[i]);
        }
        return (totalSeatBeforeAdd - totalVacantSeats == passengers.size());
    }

    /**
     * Gets indexes of empty adjacent seats
     *
     * @param numOfSeat maximum number of the seats found in row
     * @return an array of indexes of the seat
     */

    private int[] getAdjacentSeatsList(int numOfSeat) {
        int[] seatPosList = new int[numOfSeat];
        int index = 0;
        int i = 0;

        while (index < numOfSeat) {
            if (seatRow[i] == null) {
                seatPosList[index] = i;
                index++;

            } else {
                index = 0;
            }
            i++;
        }

        return seatPosList;
    }

    /**
     * Accessor of rowNumber variable
     *
     * @return the row number in the airplane
     */
    public int getRowNumber() {
        return rowNumber;
    }

    /**
     * Finds out the passenger seat preference and
     * chooses the sufficient seat in row to add the passenger
     *
     * @param pas the passenger to add
     * @return true if successfully add the passenger.
     */
    public boolean addIndividualToSeat(Passenger pas) {

        String seatType = pas.getSeatPref();

        ArrayList<Integer> columnOfSeat;
        int numOfVacantSeat = 0;

        switch (seatType) {
            case WIN_SEAT:
                numOfVacantSeat = numOfWindowSeat;
                break;

            case CEN_SEAT:
                numOfVacantSeat = numOfCenterSeat;
                break;

            case AIS_SEAT:
                numOfVacantSeat = numOfAisleSeat;
                break;
        }
        if (numOfVacantSeat > 0) {
            columnOfSeat = indexOfSeatType.get(seatType);
            for (int col : columnOfSeat) {

                if (seatRow[col] == null) {
                    addToSeat(pas, col);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds a passenger to seat and updates the number of available seats
     *
     * @param pas the passenger to add
     * @param col the index to add the passenger
     */
    public void addToSeat(Passenger pas, int col) {
        seatRow[col] = pas;
        pas.setSeat(new Seat(rowNumber, col));
        updateAvailableSeat(-1, col);
        totalVacantSeats--;
    }

    /**
     * Checks if the row still has empty seats base of seat type
     *
     * @param seatType type of seat either window, center or ailse
     * @return true if there is still empty seat in the row
     */
    public boolean isTheSeatAvailable(String seatType) {
        switch (seatType) {
            case WIN_SEAT:
                return (numOfWindowSeat > 0);
            case CEN_SEAT:
                return (numOfCenterSeat > 0);
            case AIS_SEAT:
                return (numOfAisleSeat > 0);
        }
        return false;
    }

    /**
     * Uses the position seat to determine what type of seat and
     * updates the number of vacant seats when adding or removing a passenger
     *
     * @param num     number of a seat (negative when taking and positive when returning)
     * @param seatCol a position of the seat in row.
     */
    private void updateAvailableSeat(int num, int seatCol) {
        String seatType;
        seatType = seatMap[seatCol];
        switch (seatType) {
            case WIN_SEAT:
                numOfWindowSeat += num;
                break;
            case CEN_SEAT:
                numOfCenterSeat += num;
                break;
            case AIS_SEAT:
                numOfAisleSeat += num;
                break;
        }
    }

    /**
     * Removes a passenger
     *
     * @param seatCol the position
     * @return true if successfully remove a passenger
     */
    public boolean removePasFromSeat(int seatCol) {
        if (seatRow[seatCol] != null) {
            seatRow[seatCol] = null;
            totalVacantSeats++;
            updateAvailableSeat(1, seatCol);

            return true;
        }
        return false;
    }

    /**
     * Finds all empty seats in the row
     *
     * @return number of empty seats
     */
    public ArrayList<Integer> findEmptySpot() {
        ArrayList<Integer> vacantSeats = new ArrayList<>();
        if (totalVacantSeats > 0) {
            for (int i = 0; i < seatRow.length; i++) {
                if (seatRow[i] == null) {
                    vacantSeats.add(i);
                }
            }
            return vacantSeats;
        }
        return null;
    }

    /**
     * Gets all the passengers being reserved in the row
     *
     * @return list of passengers
     */
    public ArrayList<Passenger> getPasList() {
        ArrayList<Passenger> pasList = new ArrayList<>();
        if (totalVacantSeats < totalSeatsInRow) {
            for (Passenger pas : seatRow) {
                if (pas != null) {
                    pasList.add(pas);
                }
            }
        }
        return pasList;
    }
}
