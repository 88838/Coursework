import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Users {
    public static void displayUsers() {
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, Username, Password, HighScore, Currency  FROM Users");

            ResultSet results = ps.executeQuery();
            while (results.next()) {
                //the numbers match the index of the columns in the table
                int userID = results.getInt(1);
                String username = results.getString(2);
                String password = results.getString(3);
                String highScore = results.getString(4);
                String currency = results.getString(5);
                System.out.println("User ID: " + userID);
                System.out.println("Username:  " + username);
                System.out.println("Password: " + password);
                System.out.println("High Score: " + highScore);
                System.out.println("Currency: " + currency);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void createUser() {
        Scanner sc = new Scanner(System.in);
        try {
            //the question marks are placeholders
            //user ID is auto-incrementing so it is not inserted
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (Username, Password, SkinID) VALUES (?,?,?)");


            //the question marks get set a value
            //the numbers do not match the index of the columns in the table, but instead match which question mark is set
            System.out.println("Please enter the user's username");
            ps.setString(1, sc.nextLine());
            System.out.println("Please enter the user's password");
            ps.setString(2, sc.nextLine());
            ps.setInt(3, 1);

            //this actually execute the SQL
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
}
