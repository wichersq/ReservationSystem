

/**
 * One object of Passenger class represents a passenger in the reservation.
 */
public class Passenger {
    private String name;
    private Seat seat;
    private String seatPref = " ";
    private boolean isGrouped = false;
    private boolean isEconomy;
    private String groupName;
    /**
     * Constructor of the class
     *
     * @param name      first and last name of passenger
     * @param isEconomy if the passenger is in economy service class
     * @param seatPref  seat preference of the passenger
     */
    public Passenger(String name, boolean isEconomy, String seatPref) {
        this.name = name;
        this.seatPref = seatPref;
        this.isEconomy = isEconomy;
//        System.out.println("seatPref assigedto Pas : " + seatPref);
    }

    /**
     * Constructor of the class
     *
     * @param name      first and last name of passenger
     * @param isGrouped if the passenger is individual or in a group
     * @param isEconomy if the passenger is in economy service class
     * @param groupName name of group the passenger is in

     */
    public Passenger(String name, boolean isEconomy, boolean isGrouped, String groupName) {
        this(name,isEconomy, null);
        this.isGrouped = isGrouped;
        this.groupName = groupName;
    }

    /**
     * Accessor of passenger name
     *
     * @return the name of passenger
     */
    public String getName() {
        return name;
    }

    /**
     * Accessor of group name
     * @return the group name.
     */
    public String getGroupName(){return groupName;}

    /**
     * Accessor for grouped.
     * @return True if the passenger is part of a group.
     */
    public boolean isGrouped(){return isGrouped;}

    /**
     * Gets seat type if the passenger has seat preference
     *
     * @return
     */
    public String getSeatPref() {
        return seatPref;
    }

    /**
     * Sets seat to passenger after successfully make reservation
     *
     * @param s a assigned seat
     */
    public void setSeat(Seat s) {
        seat = s;
    }

    /**
     * Accessor of seat that is reserved for the passenger
     *
     * @return The seat.
     */
    public Seat getSeat() {
        return seat;
    }

    /**
     * Checks if the passenger is in first or economy service class
     *
     * @return true if it is economy and false if it is first
     */
    public boolean isEconomy() {
        return isEconomy;
    }

}

