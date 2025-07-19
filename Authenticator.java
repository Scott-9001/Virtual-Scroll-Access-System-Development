import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.*;
import java.time.LocalDateTime;


public class Authenticator {
    private final String adminUsername = "admin";
    private final String adminPassword = "713bfda78870bf9d1b261f565286f85e97ee614efe5f0faf7c34e7ca4f65baca";
    private static final String LOG_PATH = "log.txt";

    private Map<String, User> usersByUsername = new HashMap<>();
    private Map<String, User> usersByIdKey = new HashMap<>();
    private static final String FILE_PATH = "users.txt";
    private int successfulLoad;

    public Authenticator() {
        this.successfulLoad = loadUsersFromFile();
    }

    //Methods
    public int getSuccess() {
        return successfulLoad;
    }

    // Authenticate username and password for admin
    public User loginAdmin(Scanner scanner) {
        while (true) {
            System.out.print("Enter admin username or 'cancel' to escape: ");
            String username = scanner.nextLine().trim();
            if (username.equalsIgnoreCase("cancel")) {
                return null;
            }
            System.out.print("Enter admin password or 'cancel' to escape: ");
            String password = scanner.nextLine().trim();
            if (password.equalsIgnoreCase("cancel")) {
                return null;
            }

            //Checking the details are correct
            if (adminUsername.equals(username) && adminPassword.equals(hashPassword(password))) {
                System.out.println("Login successful! Welcome, Admin.");
                return new User(adminUsername, adminPassword, User.UserType.ADMIN); // Create an Admin User
            } else {
                //Incorrect details? Request the admin to try again
                System.out.println("Invalid credentials. Please try again.");
            }
        }
    }

    // Authenticate username and password for user
    public User loginUser(Scanner scanner) {
        while (true) {
            System.out.print("Enter username or 'cancel' to escape: ");
            String username = scanner.nextLine().trim();
            if (username.equalsIgnoreCase("cancel")) {
                return null;
            }
            System.out.print("Enter password or 'cancel' to escape: ");
            String password = scanner.nextLine().trim();
            if (password.equalsIgnoreCase("cancel")) {
                return null;
            }
    
            User user = usersByUsername.get(username);
            String hashedPassword = hashPassword(password); // Hash the input password for comparison
            if (user != null && user.getHashedPassword().equals(hashedPassword)) {
                System.out.println("Login successful! Welcome " + user.getFullName() + ".");
                return user;
            } else {
                System.out.println("Incorrect username or password.");
            }    
        }
    }

    public boolean addUserByAdmin(Scanner scanner, User admin) {
        System.out.println("Adding a new user (Admin privileges required):");
        if(registerUser(scanner, admin) == null) {
            return false;
        } else {
            return true;
        }
    }

    // Delete a user by Admin
    public boolean deleteUserByAdmin(Scanner scanner, User admin) {
        System.out.print("Enter username to delete: ");
        String usernameToDelete = scanner.nextLine().trim();

        // Check if user exists
        if (!usersByUsername.containsKey(usernameToDelete)) {
            System.out.println("User not found.");
            return false;
        }

        // Remove user and update file
        User userToDelete = usersByUsername.get(usernameToDelete);
        String id = userToDelete.getIdKey();
        usersByUsername.remove(usernameToDelete);
        usersByIdKey.remove(userToDelete.getIdKey());

        if (saveAllUsersToFile() == 0) {
            System.out.println("User deleted successfully.");
            appendLogFile(admin, "deleted User " + id);
            return true;
        } else {
            System.out.println("Issue deleting user. Please inform an administrator.");
            return false;
        }
    }

    // Add user
    public User registerUser(Scanner scanner) {
        return registerUser(scanner, null);
    }

    public User registerUser(Scanner scanner, User user) {
        String firstName = "";
        String lastName = "";
        String email = "";
        String phoneNumber = "";
        String idKey = "";
        String username = "";
        String password = "";
        Pattern noComma = Pattern.compile("[^,]+");
        Pattern validEmail = Pattern.compile("[^,@\\s]+@[^,@\\s]+\\.[^,@\\s]+");
        // Pattern validPhoneNumber = Pattern.compile("[0-9\\(\\)\\- ]+");

        while (true) {
            System.out.print("Enter first name or 'cancel' to escape: ");
            firstName = scanner.nextLine().trim();
            if (firstName.length() == 0) {
                System.out.println("You can not leave this field blank.");
            } else if (firstName.equalsIgnoreCase("cancel")) {
                return null;
            } else if (noComma.matcher(firstName).matches()) {
                break;
            } else {
                System.out.println("Invalid first name.");
            }
        }
        while (true) {
            System.out.print("Enter last name or 'cancel' to escape: ");
            lastName = scanner.nextLine().trim();
            if (lastName.length() == 0) {
                System.out.println("You can not leave this field blank.");
            } else if (lastName.equalsIgnoreCase("cancel")) {
                return null;
            } else if (noComma.matcher(lastName).matches()) {
                break;
            } else {
                System.out.println("Invalid last name.");
            }
        }
        while (true) {
            System.out.print("Enter email or 'cancel' to escape: ");
            email = scanner.nextLine().trim();
            if (email.length() == 0) {
                System.out.println("You can not leave this field blank.");
            } else if (email.equalsIgnoreCase("cancel")) {
                return null;
            } else if (validEmail.matcher(email).matches()) {
                break;
            } else {
                System.out.println("Invalid email.");
            }
        }
        while (true) {
            System.out.print("Enter phone number or 'cancel' to escape: ");
            phoneNumber = scanner.nextLine().trim();
            if (phoneNumber.length() == 0) {
                System.out.println("You can not leave this field blank.");
            } else if (phoneNumber.equalsIgnoreCase("cancel")) {
                return null;
            } else if (phoneNumber.matches("\\d{5,15}")) {  // Regex allows only digits (5-15 digits)
                break;
            } else {
                System.out.println("Invalid phone number. Please enter a number between 5 and 15 digits.");
            }
        }
        while (true) {
            System.out.print("Enter unique ID key or 'cancel' to escape. Note: You can not change this once it has been set: ");
            idKey = scanner.nextLine().trim();
            if (idKey.length() == 0) {
                System.out.println("You can not leave this field blank.");
            } else if (usersByIdKey.containsKey(idKey)) {
                System.out.println("ID Key already exists.");
            } else if (idKey.equalsIgnoreCase("cancel")) {
                return null;
            } else if (idKey.equalsIgnoreCase("admin") | idKey.equalsIgnoreCase("guest")) {
                System.out.println("Illegal ID");
            } else if (noComma.matcher(idKey).matches()) {
                break;
            } else {
                System.out.println("Invalid ID key.");
            }
        }
        while (true) {
            System.out.print("Enter username or 'cancel' to escape: ");
            username = scanner.nextLine().trim();    
            if (username.length() == 0) {
                System.out.println("You can not leave this field blank.");
            } else if (usersByUsername.containsKey(username)) {
                System.out.println("Username already exists.");
            } else if (username.equalsIgnoreCase("cancel")) {
                return null;
            } else if (noComma.matcher(username).matches()) {
                break;
            } else {
                System.out.println("Invalid username.");
            }
        }
        while (true) {
            System.out.print("Enter password or 'cancel' to escape: ");
            password = scanner.nextLine().trim();
            if (password.length() == 0) {
                System.out.println("You can not leave this field blank.");
            } else if (password.equalsIgnoreCase("cancel")) {
                return null;
            } else if (noComma.matcher(password).matches()) {
                break;
            } else {
                System.out.println("Invalid password.");
            }
        }

        String hashedPassword = hashPassword(password);
        User newUser = new User(firstName, lastName, email, phoneNumber, idKey, username, hashedPassword, User.UserType.USER);
        usersByUsername.put(username, newUser);
        usersByIdKey.put(idKey, newUser);
        if (saveUserToFile(newUser) == 0) {
            System.out.println("Registration successful!");
            if (user == null) {
                appendLogFile(newUser, "registered an account");
            } else {
                appendLogFile(user, "registered an account for User " + newUser.getIdKey());
            }
            
            return newUser;
        } else {
            System.out.println("Issue creating user. Please inform an administrator");
            return null;
        }
    }

    // Update User Profile
    public void updateUserProfile(Scanner scanner, User user) {
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        Pattern noComma = Pattern.compile("[^,]+");
        Pattern validEmail = Pattern.compile("[^,@\\s]+@[^,@\\s]+\\.[^,@\\s]+");
        Pattern validPhoneNumber = Pattern.compile("[0-9\\(\\)\\- ]+");

        System.out.println("Full name: " + user.getFullName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Phone Number: " + user.getPhoneNumber());
        System.out.println("ID Key: " + user.getIdKey());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Select the information you want to update: \n1. First Name\n2. Last Name\n3. Email\n4. Phone Number\n5. Username\n6. Password\n7. Cancel");
        int choice = scanner.nextInt();
        scanner.nextLine().trim();

        switch (choice) {
            case 1:
                while (true) {
                    System.out.print("Enter new first name or 'cancel' to escape: ");
                    String newFirstName = scanner.nextLine().trim();
                    if (newFirstName.length() == 0) {
                        System.out.println("You can not leave this field blank.");
                    } else if (newFirstName.equalsIgnoreCase("cancel")) {
                        return;
                    } else if (noComma.matcher(newFirstName).matches()) {
                        user.setFirstName(newFirstName);
                        break;
                    } else {
                        System.out.println("Invalid first name.");
                    }
                }
                break;
            case 2:
                while (true) {
                    System.out.print("Enter new last name or 'cancel' to escape: ");
                    String newLastName = scanner.nextLine().trim();    
                    if (newLastName.length() == 0) {
                        System.out.println("You can not leave this field blank.");
                    } else if (newLastName.equalsIgnoreCase("cancel")) {
                        return;
                    } else if (noComma.matcher(newLastName).matches()) {
                        user.setLastName(newLastName);
                        break;
                    } else {
                        System.out.println("Invalid last name.");
                    }
                }
                break;
            case 3:
                while (true) {
                    System.out.print("Enter new email or 'cancel' to escape: ");
                    String newEmail = scanner.nextLine().trim();
                    if (newEmail.length() == 0) {
                        System.out.println("You can not leave this field blank.");
                    } else if (newEmail.equalsIgnoreCase("cancel")) {
                        return;
                    } else if (validEmail.matcher(newEmail).matches()) {
                        user.setEmail(newEmail);
                        break;
                    } else {
                        System.out.println("Invalid email.");
                    }
                }
                break;
            case 4:
                while (true) {
                    System.out.print("Enter new phone number or 'cancel' to escape: ");
                    String newPhoneNumber = scanner.nextLine().trim();
                    if (newPhoneNumber.length() == 0) {
                        System.out.println("You can not leave this field blank.");
                    } else if (newPhoneNumber.equalsIgnoreCase("cancel")) {
                        return;
                    } else if (validPhoneNumber.matcher(newPhoneNumber).matches()) {
                        user.setPhoneNumber(newPhoneNumber);
                        break;
                    } else {
                        System.out.println("Invalid phone number.");
                    }
                }
                break;
            case 5:
                while (true) {
                    System.out.print("Enter new username or 'cancel' to escape: ");
                    String newUsername = scanner.nextLine().trim();
                    if (newUsername.length() == 0) {
                        System.out.println("You can not leave this field blank.");
                    } else if (usersByUsername.containsKey(newUsername)) {
                        System.out.println("Username already exists.");
                    } else if (newUsername.equalsIgnoreCase("cancel")) {
                        return;
                    } else if (noComma.matcher(newUsername).matches()) {
                        usersByUsername.remove(user.getUsername());
                        user.setUsername(newUsername);
                        usersByUsername.put(newUsername, user);
                        break;
                    } else {
                        System.out.println("Invalid username.");
                    }
                }
                break;
            case 6:
                while (true) {
                    System.out.print("Enter new password or 'cancel' to escape: ");
                    String newPassword = scanner.nextLine().trim();
                    if (newPassword.length() == 0) {
                        System.out.println("You can not leave this field blank.");
                    } else if (newPassword.equalsIgnoreCase("cancel")) {
                        return;
                    } else if (noComma.matcher(newPassword).matches()) {
                        user.setHashedPassword(hashPassword(newPassword));
                        break;
                    } else {
                        System.out.println("Invalid password.");
                    }
                }
                break;
            case 7:
                return;
            default:
                System.out.println("Invalid option.");
                return;
        }
        if (saveAllUsersToFile() == 0) {
            System.out.println("Profile updated successfully!");
            appendLogFile(user, "updated profile");
        } else {
            System.out.println("Issue saving changes. Please inform an administrator");
        }
    }

    // Password Hasher
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // throw new RuntimeException("Error hashing password", e);
            return null;
        }
    }

    // Load file
    private String getUserFile() {
        try {
            ClassLoader classLoader = Authenticator.class.getClassLoader();
            return classLoader.getResource(FILE_PATH).toURI().getPath();
        } catch (Exception e) {
            return "";
        }
    }
    
    private String getLogFile() {
        try {
            ClassLoader classLoader = ScrollManager.class.getClassLoader();
            return classLoader.getResource(LOG_PATH).toURI().getPath();
        } catch (Exception e) {
            return "";
        }
    }

    public void showAllProfile() {
        for (Map.Entry<String, User> entry : usersByIdKey.entrySet()) {
            String idKey = entry.getKey();
            User user = entry.getValue();
            System.out.println("User ID Key: " + idKey);
            System.out.println("Profile: " + user.profileDisplay());
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

    private int loadUsersFromFile() {
        String pathString = getUserFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(pathString))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 8) {
                    String username = parts[0];
                    String password = parts[1];
                    String phoneNumber = parts[2];
                    String email = parts[3];
                    String firstName = parts[4];
                    String lastName = parts[5];
                    String idKey = parts[6];
                    User.UserType userType = User.UserType.valueOf(parts[7]);
                    User user = new User(firstName, lastName, email, phoneNumber, idKey, username, password, userType);
                    usersByUsername.put(username, user);
                    usersByIdKey.put(idKey, user);
                }
            }
            return 0;
        } catch (IOException e) {
            System.out.println("No user data file found. Please inform admin.");
                return 1;
        }
    }

    // Save to file
    private int saveUserToFile(User user) {
        String pathString = getUserFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathString, true))) {
            writer.write(user.toString());
            writer.newLine();
            return 0;
        } catch (IOException e) {
            // System.out.println("Error saving user: " + e.getMessage());
            return 1;
        }
    }

    private int saveAllUsersToFile() {
        String pathString = getUserFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathString))) {
            for (User user : usersByUsername.values()) {
                writer.write(user.toString());
                writer.newLine();
            }
            return 0;
        } catch (IOException e) {
            // System.out.println("Error saving users: " + e.getMessage());
            return 1;
        }
    }

    private int appendLogFile(User user, String action) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getLogFile(), true))) {
            writer.write(LocalDateTime.now().toString() + ": " + user.getIdKey() + " " + action);
            writer.newLine();
            return 0;
        } catch (IOException e) {
            return 1;
        }
    }
}
