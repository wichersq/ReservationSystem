import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

    /**
     * An airplane reservation system. Allows a user to reserve and remove seats for groups or individuals.
     */
    public class ReservationSystem {
        private final String ADD_INDIVIDUAL = "P";
        private final String ADD_GROUP = "G";
        private final String CANCEL_REQUEST = "C";
        private final String CANCEL_INDIVIDUAL = "I";
        private final String CANCEL_GROUP = "G";
        private final String ECONOMY_CLASS = "E";
        private final String FIRST_CLASS = "F";
        private final String AVAILABILITY_CHART = "A";
        private final String MANIFEST = "M";
        private final String QUIT = "Q";
        private final String RETURN = "R";
        private File file;
        public Scanner userInput;
        ReservationManager management;

        /**
         * Constructor of the class.
         *
         * @param fileName a file name to restore and save the info after done.
         */
        public ReservationSystem(String fileName) {
            management = new ReservationManager();
            userInput = new Scanner(System.in);
            maybeCreateFile(fileName);
            runMenu();
        }

        /**
         * Checks if the file exists, if not creates a file.
         *
         * @param fileName the file name
         */
        private void maybeCreateFile(String fileName) {
            this.file = new File(fileName);
            if (file.exists()) {
                management.restoreInfoFromFile(file);
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.getStackTrace();
            }
        }

        /**
         * Asks a user to enter cancellation info and
         * cancels the reservation if the info is in the system.
         */
        private void cancelRequestPromt() {
            System.out.println("Cancellation: [I]ndividual or [G]roup");
            String cancelType = userInput.nextLine();
            String name;
            boolean isGroup;

            switch (cancelType) {

                case CANCEL_INDIVIDUAL:
                    isGroup = false;
                    break;

                case CANCEL_GROUP:
                    isGroup = true;
                    break;

                default:
                    System.out.println("Cannot recognize the option.");
                    return;
            }

            System.out.println(!isGroup ? "Name:  " : "Group Name:  ");
            name = userInput.nextLine();
            try {
                if (!management.cancelReservation(name, isGroup)) {
                    System.out.println("Failed to remove the reservation. Please try Again");
                }
            } catch (Exception e) {
                System.out.printf("%s %s is not on the reservation list.\n", isGroup ? "Group" : "Passenger", name);
            }
        }

        /**
         * Check if the class has enough seats. If not, ask to choose again or return to menu.
         *
         * @param tryAgain  times to try again if the system can't do the request.
         * @param numOfPass number of passenger for this request.
         * @return a service class for the request.
         */
        private String checkServiceClassRequest(int tryAgain, int numOfPass) {

            String serviceClass;
            serviceClass = promptServiceClass();

            boolean isEconomy = (serviceClass.equals(ECONOMY_CLASS));
            if (management.getVacantSeats(isEconomy) >= numOfPass) {
                return serviceClass;
            } else {
                System.out.printf("Not enough seats available in the chosen service class.\nPlease [C]hoose another class or [R]eturn to the menu\n",
                        serviceClass);
                String decision = userInput.nextLine();
                if (decision.equalsIgnoreCase(RETURN)) {
                    return null;
                }
                tryAgain--;
            }
            if (tryAgain <= 0) {
                return null;
            }
            return checkServiceClassRequest(tryAgain, numOfPass);
        }

        /**
         * Asks a user to enter service class for the request.
         * @return The service class.
         */
        private String promptServiceClass() {
            System.out.println("Service Class: [F]irst or [E]conomy.");
            String serviceClass = userInput.nextLine();
            serviceClass = serviceClass.toUpperCase();
            if (serviceClass.equals(ECONOMY_CLASS) || serviceClass.equals(FIRST_CLASS)) {
                return serviceClass;
            } else {
                System.out.println("Invalid request. Please try again.");
                return promptServiceClass();
            }
        }

        /**
         * Asks user to enter the information for the passenger and
         * does the reservation request when the information is found in the system.
         */
        private void individualReservation() {
            if (management.isSeatAllReserved()) {
                System.out.println("Airplane Seats are All Reserved!!!");
                return;
            }

            System.out.println("Name:  ");
            String name = userInput.nextLine();
            String serviceClass;
            if (management.isNameDuplicated(false, name)){
                System.out.println("The name is already in the system. Please try another reservation");
                return;
            }
                serviceClass = checkServiceClassRequest(2, 1);

            if (serviceClass == null) {
                System.out.println("Failed to add the passenger. Please try again");
                return;
            }

            boolean isEconomy = (serviceClass.equalsIgnoreCase(ECONOMY_CLASS));

            boolean isAddSuccessful;
            do {
                System.out.println((isEconomy) ? "Seat preference: [W]indow, [C]enter or [A]isle." :
                        "Seat Preference: [W]indow or [A]isle.");
                String seatPref = userInput.nextLine();
                seatPref = seatPref.toUpperCase();
                isAddSuccessful = management.makeIndividualReservation(name, isEconomy, seatPref);
            } while (!isAddSuccessful);
        }

        /**
         * Splits a string that holds all the names of passengers into individual name.
         *
         * @param nameOfAllPass name of all passengers in the group.
         * @return the string array of all the names.
         */
        private static String[] convertNameList(String nameOfAllPass) {
            return nameOfAllPass.split(",");
        }

        /**
         * Asks user to enter the information for the group and
         * does the reservation request when the information is found in the system.
         */
        private void groupReservation() {
            if (management.isSeatAllReserved()) {
                System.out.println("Airplane Seats are All Reserved!!!");
                return;
            }
            System.out.println("Group Name:  ");
            String gName = userInput.nextLine();
            if (management.isNameDuplicated(true, gName)){
                System.out.println("The name is already in the system. Please try another reservation");
                return;
            }
            System.out.println("Names (Please separate each member's name by a comma):  ");
            String name = userInput.nextLine();
            String[] nameOfPass = convertNameList(name);

            String serviceClass = checkServiceClassRequest(2, nameOfPass.length);

            if (serviceClass != null) {
                boolean isEconomy = (serviceClass.equalsIgnoreCase(ECONOMY_CLASS));
                if (management.makeGroupReservation(nameOfPass, gName, isEconomy))
                    return;
            }

            System.out.println("Failed to add the passengers. Please try again");
        }

        /**
         * Prints the manifest or avalibility list. Prompts the user for the service class.
         *
         * @param isManifest True to print the manifest list, false to print availability list.
         */
        private void printList(boolean isManifest) {
            String serviceClass;
            try {
                serviceClass = promptServiceClass();
            } catch (Exception e) {
                System.out.println("Cannot recognize the request");
                return;
            }
            boolean isEconomy = (serviceClass.equalsIgnoreCase(ECONOMY_CLASS));
            if (isManifest) {
                System.out.println(management.getManifestList(isEconomy));
            } else
                System.out.println(management.getAvailabilityList(isEconomy));
        }

        /**
         * Saves and exits the program.
         */
        private void quit() {
            management.saveInfoToFile(file);
            userInput.close();
            System.exit(0);

        }

        /**
         * Calls the necessary function to fulfill the user request.
         * @param action The user command.
         */
        public void doRequest(String action) {
            switch (action) {
                default:
                    System.out.println("Invalid request. Please try again");
                    break;
                case ADD_INDIVIDUAL:
                    individualReservation();
                    break;
                case ADD_GROUP:
                    groupReservation();
                    break;
                case CANCEL_REQUEST:
                    cancelRequestPromt();
                    break;
                case AVAILABILITY_CHART:
                    printList(false);
                    break;
                case MANIFEST:
                    printList(true);
                    break;
                case QUIT:
                    quit();
                    break;
            }
        }

        /**
         * Prompts the user for commands.
         */
        public void runMenu() {
            while (true) {
                System.out.println("Add [P]assenger, Add [G]roup, [C]ancel Reservation, Print Seating [A]vailability Chart, Print [M]anifest, [Q]uit");
                try {
                    String input = userInput.nextLine();
                    doRequest(input.toUpperCase());
                }catch (NoSuchElementException e){
                    quit();
                }
            }
        }


    public static void main(String[] args) {

        if (args.length <= 0) {
            System.out.println("Please enter a file name for saving data:");
            Scanner userInput = new Scanner(System.in);
            String fileName  = userInput.nextLine();
            new ReservationSystem(fileName);}
        else {new ReservationSystem(args[0]);}

    }
}
