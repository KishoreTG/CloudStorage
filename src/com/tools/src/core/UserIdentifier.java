package core;

public class UserIdentifier {

    private final int userID;
    private final String username;

    public UserIdentifier(int userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

}
