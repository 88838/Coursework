import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Players {
    public static void createPlayer() {
        try {
            String username = "test";
            String password = "test";

            //the question marks are placeholders
            //user ID is auto-incrementing so it is not needed in the SQL statement
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Players (Username, Password, SkinID) VALUES (?,?,?)");


            //the parameter index corresponds with each question mark
            ps.setString(1, username);
            ps.setString(2, password);
            //the player starts with the default skin, therefore it a variable doesn't need to passed in
            ps.setInt(3, 1);

            //this actually execute the SQL
            ps.executeUpdate();


        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void readPlayers() {
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT PlayerID, Username, Password, HighScore, Currency, SkinID  FROM Players");

            ResultSet results = ps.executeQuery();
            //returns false and stops the loop when there are no more records
            while (results.next()) {
                //the column index matches  the columns in the table
                int playerID = results.getInt(1);
                String username = results.getString(2);
                String password = results.getString(3);
                String highScore = results.getString(4);
                String currency = results.getString(5);
                int skinID = results.getInt(6);
                System.out.println("Player ID: " + playerID);
                System.out.println("Username:  " + username);
                System.out.println("Password: " + password);
                System.out.println("High Score: " + highScore);
                System.out.println("Currency: " + currency);
                System.out.println("SkinID: " + skinID);
                System.out.println();
            }


        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void updatePlayer() {
        try {
            String username = "test";
            String password = "test";
            int skinID = 1;
            int playerID = 1;


            //the question marks are placeholders
            //the player can be updated depending on the playerID that is inputted
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET Username = ?, Password = ?, SkinID = ? WHERE PlayerID = ?");

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, skinID);
            ps.setInt(4, playerID);
            ps.executeUpdate();


        } catch (Exception exception) {
            System.out.println(exception.getMessage());

        }

    }
    public static void deletePlayer(){
        try {
            int playerID = 1;

            //the parameter index corresponds with each question mark
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Players WHERE PlayerID = ?");
            ps.setInt(1, playerID);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println(exception.getMessage());

        }

    }
}
