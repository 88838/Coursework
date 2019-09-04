import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Users {
    public static void displayUsers() {
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, Username, Password, HighScore, Currency, SkinID  FROM Users");

            ResultSet results = ps.executeQuery();
            while (results.next()) {
                //the numbers match the index of the columns in the table
                int userID = results.getInt(1);
                String username = results.getString(2);
                String password = results.getString(3);
                String highScore = results.getString(4);
                String currency = results.getString(5);
                int skinID = results.getInt(6);
                System.out.println("User ID: " + userID);
                System.out.println("Username:  " + username);
                System.out.println("Password: " + password);
                System.out.println("High Score: " + highScore);
                System.out.println("Currency: " + currency);
                System.out.println("SkinID: " + skinID);
                System.out.println();
            }
            Main.menu();


        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void createUser() {
        try {
            String username = "test";
            String password = "test";

            //the question marks are placeholders and get set a value
            //user ID is auto-incrementing so it is not inserted
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (Username, Password, SkinID) VALUES (?,?,?)");


            //the parameterIndex corresponds with each question mark
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, 1);

            //this actually execute the SQL
            ps.executeUpdate();

            Main.menu();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void updateUser() {
        try {
            String username = "test";
            String password = "test";
            int id = 1;


            //the question marks are placeholders
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Username = ?, Password = ? WHERE UserID = ?");

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, id);
            ps.executeUpdate();

            Main.menu();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }
    public static void deleteUser(){
        try {
            int id = 1;
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Users WHERE WeightID = ?");
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

    }
}
