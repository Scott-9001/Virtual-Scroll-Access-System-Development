public class User {
    public enum UserType {
        ADMIN,  // Represents an admin user
        GUEST,   // Represents a guest user
        USER    // Represents a normal user
    }

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String idKey;
    private String username;
    private String password;
    private UserType userType;

    // Constructor for a user with a username and password
    public User(String firstName, String lastName, String email, String phoneNumber, String idKey, String username, String password, UserType userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.idKey = idKey;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    // Constructor for an admin user
    public User(String username, String password, UserType userType) {
        this.firstName = "Admin";
        this.lastName = null;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.idKey = "Admin";
    }

    // Constructor for a guest user, no password required
    public User(UserType userType) {
        this.firstName = "Guest";
        this.lastName = null;
        this.userType = userType;
        this.idKey = "Guest";
    }

    // Getters + Setters
    // Name
    public String getFullName() {
        if (lastName == null) {
            return firstName;
        } else {
            return firstName + " " + lastName;
        }
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    //Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //Phone Number
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    //Id
    public String getIdKey() {
        return idKey;
    }

    //Username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //Password
    // Suggest adding salt but work on later - Justin
    public void setHashedPassword(String password) {
        this.password = password;
    }

    public String getHashedPassword() {
        return password;
    }


    //User type
    public String getUserType() {
        String userTypeStr;
        switch (userType) {
            case ADMIN:
                userTypeStr = "Admin";
                break;
            case GUEST:
                userTypeStr = "Guest";
                break;
            case USER:
                userTypeStr = "User";
                break;
            default:
                userTypeStr = "Guest";
        }
        return userTypeStr;
    }

    // should not change, at least, not realistically
    // public void setUserType(String userType) {
    //     this.userType = userType;
    // }

    @Override
    public String toString() {
        return username + "," + password + "," + phoneNumber + "," + email + "," + firstName + "," + lastName + "," + idKey + "," + userType.toString();
    }

    public String profileDisplay() {
        return "Name: " + firstName + " " + lastName + ", Username: " + username + ", Password: " + password + ", Phone Numer: " + phoneNumber + ", Email: " + email + ", User Type: " + getUserType();
    }

}
