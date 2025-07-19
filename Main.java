import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    //Start method will commence the program
    public void start() {
        Authenticator auth = new Authenticator();
        ScrollManager scrollMan = new ScrollManager();
        if (auth.getSuccess() == 1) {
            System.out.println("Aborting!");
            return;
            //adding to test jenkins
        }
        if (scrollMan.getSuccess() == 1) {
            System.out.println("Aborting!");
            return;
        }

        // Loops until quit
        boolean startFlag = true;
        Scanner scanner = new Scanner(System.in);
        User currentUser;
    
        while (startFlag) {
            currentUser = null;
            //Authenticate non-guest Users
            boolean menuFlag;

            // Loops if invalid option
            do {
                menuFlag = false;
                //Intro- requesting the user to identify themselves
                System.out.println("Welcome to the Virtual Scroll Access System");
                System.out.println("What shall you be choosing today?");
                System.out.println("1. Admin Login");
                System.out.println("2. Continue as Guest");
                System.out.println("3. Create an Account");
                System.out.println("4. Login to an Account");
                System.out.println("5. Quit");
                System.out.print("Choose an option: [1|2|3|4|5] ");

                String choice = scanner.nextLine().trim();

                if (choice.equals("1")) {
                    // Admin login path
                    currentUser = auth.loginAdmin(scanner);  // Authenticate admin
                    if (currentUser != null) {
                        adminMenu(auth, scrollMan, currentUser, scanner);  // Show admin menu
                    }
                }
                else if (choice.equals("2")) {
                    // If they are just a guest, they can come straight in
                    currentUser = new User(User.UserType.GUEST);  // Directly create a guest user
                    User newUser = guestMenu(auth, scrollMan, currentUser, scanner);  // Show guest menu
                    if (newUser != null) {
                        System.out.println("Welcome " + newUser.getFullName() + ".");
                        userMenu(auth, scrollMan, newUser, scanner);
                    }
                }
                else if (choice.equals("3")) {
                    // User create path
                    currentUser = auth.registerUser(scanner);  // Create user
                    if (currentUser != null) {
                        System.out.println("Welcome " + currentUser.getFullName() + ".");
                        userMenu(auth, scrollMan, currentUser, scanner);  // Show admin menu
                    }
                }
                else if (choice.equals("4")) {
                    // User login path
                    currentUser = auth.loginUser(scanner);  // Authenticate user
                    if (currentUser != null) {
                        userMenu(auth, scrollMan, currentUser, scanner);  // Show admin menu
                    }
                }
                else if (choice.equals("5")) {
                    System.out.println("Have a great day!");
                    startFlag = false;
                }
                else {
                    //They have to choose something.
                    System.out.println("Invalid option.");
                    menuFlag = true;
                }
            } while (menuFlag);
        }
        scanner.close();
    }

    // Admin menu with options specific to the admin user typle
    private void adminMenu(Authenticator auth, ScrollManager scrollMan, User currentUser, Scanner scanner) {
        // Need view all user and profile, add + delete user, view stats
        // and normal user options related to scroll
        while (true) {
            System.out.println("\nWelcome, " + currentUser.getFullName() + " <" + currentUser.getUserType() + ">! What would you like to do today?:");
            System.out.println("Digital Scrolls");
            System.out.println("  1. Upload New Digital Scroll");
            System.out.println("  2. Edit / Update Digital Scroll");
            System.out.println("  3. Delete Digital Scroll");
            System.out.println("  4. View Scrolls");
            System.out.println("Admin");
            System.out.println("  5. Add User");
            System.out.println("  6. Delete User");
            System.out.println("  7. View Users");
            System.out.println("  8. View Scroll Statistics");
            System.out.println("System");
            System.out.println("  9. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    scrollMan.addScroll(scanner, currentUser);
                    break;
                case "2":
                    scrollMan.updateScroll(scanner, currentUser);
                    break;
                case "3":
                    scrollMan.deleteScroll(scanner, currentUser);
                    break;
                 case "4":
                    scrollMan.viewScrolls(scanner, currentUser);
                    break;
                case "5":
                    auth.addUserByAdmin(scanner, currentUser);
                    break;
                case "6":
                    auth.deleteUserByAdmin(scanner, currentUser);
                    break;
                case "7":
                    auth.showAllProfile();
                    break;
                case "8":
                    scrollMan.showAllStats();
                    break;
                case "9":
                    System.out.println("Have a good day!\n");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Guest menu with options specific to the guest user type
    private User guestMenu(Authenticator auth, ScrollManager scrollMan, User currentUser, Scanner scanner) {
        // Need view scroll only
        while (true) {
            System.out.println("\nWelcome, " + currentUser.getFullName() + " <" + currentUser.getUserType() + ">! What would you like to do today?:");
            System.out.println("Digital Scrolls");
            System.out.println("  1. Upload New Digital Scroll (Restricted)");
            System.out.println("  2. View Scrolls");
            System.out.println("System");
            System.out.println("  3. Create Account");
            System.out.println("  4. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    scrollMan.addScroll(scanner, currentUser);
                    break;
                case "2":
                    scrollMan.viewScrolls(scanner, currentUser);
                    break;
                case "3":
                    User newUser = auth.registerUser(scanner);  // Create user
                    if (newUser != null) {
                        return newUser;
                    }
                    break;
                case "4":
                    System.out.println("Have a good day!\n");
                    return null;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // User menu with options specific to the normal user type
    private void userMenu(Authenticator auth, ScrollManager scrollMan, User currentUser, Scanner scanner) {
        // view, upload, download scroll + update profile
        while (true) {
            System.out.println("\nWelcome, " + currentUser.getFullName() + " <" + currentUser.getUserType() + ">! What would you like to do today?:");
            System.out.println("Digital Scrolls");
            System.out.println("  1. Upload New Digital Scroll");
            System.out.println("  2. Edit / Update Digital Scroll");
            System.out.println("  3. Delete Digital Scroll");
            System.out.println("  4. View Scrolls");
            System.out.println("System");
            System.out.println("  5. Update Profile");
            System.out.println("  6. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    scrollMan.addScroll(scanner, currentUser);
                    break;
                case "2":
                    scrollMan.updateScroll(scanner, currentUser);
                    break;
                case "3":
                    scrollMan.deleteScroll(scanner, currentUser);
                    break;
                 case "4":
                    scrollMan.viewScrolls(scanner, currentUser);
                     break;
                case "5":
                    auth.updateUserProfile(scanner, currentUser);
                    break;
                case "6":
                    System.out.println("Have a good day!\n");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
