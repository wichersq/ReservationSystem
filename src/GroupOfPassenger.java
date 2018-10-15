import java.util.ArrayList;
import java.util.Collection;

/**
 * One object of GroupOfPassenger class holds information of a group of passenger.
 */
public class GroupOfPassenger {
    private ArrayList<Passenger> passengerGroup;
    private String nameOfGroup;
    private boolean isEconomy;

    /**
     * Constructor of the class
     *
     * @param nameOfAllPass name of all passengers in the group
     * @param nameOfGroup   name of the group
     * @param isEconomy     if the group is first or economy class
     */
    public GroupOfPassenger(String[] nameOfAllPass, String nameOfGroup, boolean isEconomy) {
        this.nameOfGroup = nameOfGroup;
        passengerGroup = new ArrayList<Passenger>();
        this.isEconomy = isEconomy;
        for (String k : nameOfAllPass) {
            passengerGroup.add(new Passenger(k, isEconomy, true,  nameOfGroup));
        }
    }

    public GroupOfPassenger(Collection<Passenger> pasList, String nameOfGroup, boolean isEconomy){
        this.nameOfGroup = nameOfGroup;
        passengerGroup = new ArrayList<>(pasList);
        this.isEconomy = isEconomy;
    }


    /**
     * Accessor of passengerGroup
     *
     * @return a list of passengers in group
     */
    public ArrayList<Passenger> getPassengerGroup() {
        return passengerGroup;
    }

    /**
     * Accessor of group name
     *
     * @return the name of the group
     */
    public String getGroupName() {
        return nameOfGroup;
    }

    /**
     * Gets size of the group
     *
     * @return size of the group
     */
    public int size() {
        return passengerGroup.size();
    }

    /**
     * Check if the group is in economy class
     *
     * @return true if the group is economy otherwise false
     */
    public boolean isEconomy() {
        return isEconomy;
    }
}
