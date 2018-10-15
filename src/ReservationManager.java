import java.io.*;
import java.util.*;

/**
 * Methods for managing the airplane reservations.
 */
public class ReservationManager {
    private final static int CONVERT_NUM_TO_CHAR = 65;
    private static final String WIN_SEAT = "W";
    private static final String CEN_SEAT = "C";
    private static final String AIS_SEAT = "A";
    private final static String FIRST_CLASS = "First Class";
    private final static String ECONOMY_CLASS = "Economy Class";
    private Hashtable<String, Passenger> individualReservedList;
    private Hashtable<String, GroupOfPassenger> groupReservedList;
    private AirplaneSeats airplane;

    /**
     * Initializes variables.
     */
    public ReservationManager() {
        airplane = new AirplaneSeats();
        individualReservedList = new Hashtable<String, Passenger>();
        groupReservedList = new Hashtable<String, GroupOfPassenger>();
    }


    /**
     * Gets total vacant seats.
     * @param isEconomy True to get vacant for economy.
     * @return total vacant seats.
     */
    public int getVacantSeats(boolean isEconomy) {
        return isEconomy ? airplane.getEmptyEcoSeats() : airplane.getEmptyFirstSeats();
    }


    /**
     * Creates a single reservation.
     * @param name Passanger's name.
     * @param isEconomy True if sitting in economy.
     * @param seatPref Type of seat they want.
     * @return True if successful.
     */
    public boolean makeIndividualReservation(String name, boolean isEconomy, String seatPref) {
        System.out.println("SeatPref " + seatPref);
        SeatRow row = airplane.getAvailableSeatRow(isEconomy, seatPref);
        if (row != null) {
            Passenger pas = new Passenger(name, isEconomy, seatPref);
            if (airplane.individualReservation(pas, row)) {
                Seat assignedSeat = pas.getSeat();
                popReservationNotification(assignedSeat.getRow(), assignedSeat.getCol(), pas.getName());
                individualReservedList.put(pas.getName(), pas);
                return true;
            }
        }
        System.out.println("No more seats for this type. Please choose another seat preference");
        return false;
    }

    /**
     * Checks duplicate name
     * @param isGrouped true if is a group name, false if it is a individual passenger name.
     * @param name  name of passenger or group
     * @return true if name is in the system
     */
    public boolean isNameDuplicated(boolean isGrouped, String name) {
        if (isGrouped) {
            return (groupReservedList.containsKey(name));
        }

        return (individualReservedList.containsKey(name));
    }

    /**
     * Tells the user the reservation is successful.
     * @param row The seat row.
     * @param col The seat column.
     * @param name The person's name.
     */
    private void popReservationNotification(int row, int col, String name) {
        System.out.printf("Seat %d%s is successfully reserved for %s.\n",
                row, convertSeatColToString(col), name);
    }

    /**
     * Tells the user the reservation was canceled.
     * @param p The passenger.
     */
    private void popCancellationNotification(Passenger p) {
        Seat s = p.getSeat();
        int row = s.getRow();
        int col = s.getCol();
        System.out.printf("Successfully canceled seat %d%s for %s.\n",
                 row, convertSeatColToString(col),p.getName());
    }


    /**
     * Creates a group reservation.
     * @param names Names of passengers in the group.
     * @param gName Name of the group.
     * @param isEconomy True if the reservation is economy class.
     * @return True if successful.
     */
    public boolean makeGroupReservation(String[] names, String gName, boolean isEconomy) {
        GroupOfPassenger group = new GroupOfPassenger(names, gName, isEconomy);
        if (airplane.addGroup(group)) {
            for (Passenger k : group.getPassengerGroup()) {
                Seat s = k.getSeat();
                popReservationNotification(s.getRow(), s.getCol(), k.getName());
            }
            groupReservedList.put(group.getGroupName(), group);
            ArrayList<String> key =  new ArrayList<>(groupReservedList.keySet());
            for (String k : key) {
            }

           return true;
        }
        return false;

    }


    /**
     * Cancels a reservation.
     * @param name Name for the person or group.
     * @param isGroup True for group.
     * @return True if successful.
     * @throws Exception If the name is invalid.
     */
    public boolean cancelReservation(String name, boolean isGroup) throws Exception {
        if (isGroup && groupReservedList.containsKey(name)) {
            GroupOfPassenger group = groupReservedList.get(name);
            if (airplane.removeGroup(group)) {
                for (Passenger k : group.getPassengerGroup()) {
                    popCancellationNotification(k);
                }
                groupReservedList.remove(name);

                return true;
            }
        } else if (!isGroup && individualReservedList.containsKey(name)) {
            Passenger pas = individualReservedList.get(name);
            if (airplane.removeIndividual(pas)) {
                popCancellationNotification(pas);
                individualReservedList.remove(name);
                return true;
            }
        } else {
            throw new Exception();
        }
        return false;
    }

    /**
     * Gets the char representation of the column.
     * @param col The column.
     * @return The char representation
     */
    private char convertSeatColToString(int col) {
        return ((char) (col + CONVERT_NUM_TO_CHAR));
    }

    /**
     * Gets the string representation for the service class.
     * @param isEconomy The service class.
     * @return The string representation.
     */
    private StringBuilder addServiceClassHeader(boolean isEconomy) {
        StringBuilder headerStr = new StringBuilder();
        String serviceClass = isEconomy ? ECONOMY_CLASS : FIRST_CLASS;
        headerStr.append(serviceClass);
        headerStr.append(":\n");
        return headerStr;
    }

    /**
     * Checks if all seats are reserved
     * @return  true if they are all reserved
     */
    public boolean isSeatAllReserved() {
        return airplane.isFull();
    }

    /**
     * Gets reserved passengers list
     * @param isEconomy   if request is for economy class or first class
     * @return  a list of all vacant seats
     */
    public String getManifestList(boolean isEconomy) {
        if (individualReservedList.size() + groupReservedList.size() == 0) {
            return "Manifest is Empty";
        }
        ArrayList<Passenger> manifestList = airplane.getAllReservedPas(isEconomy);

        StringBuilder manifestInfo = addServiceClassHeader(isEconomy);

        for (Passenger pas : manifestList) {
            Seat s = pas.getSeat();
            String seatInfo = String.format("%d%c: %s  ", s.getRow(),
                    convertSeatColToString(s.getCol()), pas.getName());
            manifestInfo.append(seatInfo);
            manifestInfo.append("\n");
        }
        manifestInfo.append("\n");
        return manifestInfo.toString();
    }

    /**
     * Gets available seats list
     * @param isEconomy   if request is for economy class or first class
     * @return  a list of all vacant seats
     */
    public String getAvailabilityList(boolean isEconomy) {

        StringBuilder vacantSeatInfo = addServiceClassHeader(isEconomy);

        TreeMap<Integer, ArrayList<Integer>> seatList = airplane.getAllVacantSeats(isEconomy);
        Iterator<Integer> iter = seatList.keySet().iterator();

        while (iter.hasNext()) {
            int row = iter.next();
            ArrayList<Integer> colList = seatList.get(row);
            vacantSeatInfo.append(row);
            vacantSeatInfo.append(":\t");
            for (int k : colList) {
                char seatCol = convertSeatColToString(k);
                vacantSeatInfo.append(seatCol);
                vacantSeatInfo.append("\t");
            }
            vacantSeatInfo.append("\n");
        }
        vacantSeatInfo.append("\n");
        return vacantSeatInfo.toString();
    }

    /**
     * Reads and transform the information from the reserved passenger to string
     * @param pas the reserved passenger.
     * @return a string contains the passenger information
     */
    private String getReservedPassInfo(Passenger pas) {
        StringBuilder passInfo = new StringBuilder();

        Seat seat = pas.getSeat();

        passInfo.append(pas.getName());
        passInfo.append(",");
        passInfo.append(pas.isEconomy());
        passInfo.append(",");
        passInfo.append(seat.getRow());
        passInfo.append(",");
        passInfo.append(seat.getCol());
        passInfo.append(",");
        passInfo.append(pas.isGrouped());
        passInfo.append(",");

        if (pas.isGrouped())
            passInfo.append(pas.getGroupName());
        else
            passInfo.append(pas.getSeatPref());

        return passInfo.toString();
    }

    /**
     * Saves all the reservations to a file to retrieve later.
     * @param file File to save to.
     */
    public void saveInfoToFile(File file) {
        PrintWriter printFile = null;
        try {
            printFile = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.getStackTrace();
            return;
        }
        ArrayList<Passenger> reservedPas = airplane.getAllReservedPas(false);
        reservedPas.addAll(airplane.getAllReservedPas(true));

        for (Passenger k : reservedPas) {
            String info = getReservedPassInfo(k);
            printFile.println(info);
        }
        printFile.close();
    }

    /**
     * Add passenger directly to the airplane when restoring the information from previous runs.
     * @param pas    an passenger to add
     * @param seatRow   the row of seat
     * @param seatCol   the column of seat
     */
    private void addPasDirectlyToSeat(Passenger pas, int seatRow, int seatCol) {
        airplane.getSeatRow(seatRow).addToSeat(pas, seatCol);
        airplane.changeTotalVacantSeats(-1,pas.isEconomy());
    }

    /**
     * Restores the reservation made from the previous run
     * @param file  a text file that stores the information
     */
    public void restoreInfoFromFile(File file) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        }catch(IOException e){
            e.getStackTrace();
        }
        HashMap<String, ArrayList<Passenger>> pasGroupList = new HashMap<>();
        ArrayList<Passenger> individualList = new ArrayList<Passenger>();
        Passenger pas;
        boolean isGrouped;
        boolean isEconomy;
        String seatPref;
        String name;
        String groupName;
        int seatCol;
        int seatRow;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] info = line.split(",");
            name = info[0];
            isEconomy = Boolean.valueOf(info[1]);
            seatRow = Integer.valueOf(info[2]);
            seatCol = Integer.valueOf(info[3]);
            isGrouped = Boolean.valueOf(info[4]);
            if (isGrouped) {
                groupName = info[5];
                pas = new Passenger(name, isEconomy, isGrouped, groupName);
                if (!pasGroupList.containsKey(groupName)) {
                    pasGroupList.put(groupName, new ArrayList<Passenger>());
                }
                pasGroupList.get(groupName).add(pas);
            } else {
                seatPref = info[5];
                pas = new Passenger(name, isEconomy, seatPref);
                individualList.add(pas);
            }
            addPasDirectlyToSeat(pas, seatRow, seatCol);
        }
        updateTrackingList(pasGroupList, individualList);

        scanner.close();
    }

    /**
     * Updates the passenger tracking list with the given info.
     * @param pasGroupList The list of group reservations.
     * @param individualList The list of individual reservations.
     */
    private void updateTrackingList(HashMap<String, ArrayList<Passenger>> pasGroupList, ArrayList<Passenger> individualList) {
        Iterator<String> iter = pasGroupList.keySet().iterator();
        while (iter.hasNext()) {
            String gName = iter.next();
            ArrayList<Passenger> group = pasGroupList.get(gName);
            groupReservedList.put(gName, new GroupOfPassenger(group, gName, group.get(1).isEconomy()));
        }

        for (Passenger k : individualList) {
            individualReservedList.put(k.getName(), k);
        }
    }

}
