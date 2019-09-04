import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Players {
    public static void displayPlayers() {
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT PlayerID, Username, Password, HighScore, Currency, SkinID  FROM Players");

            ResultSet results = ps.executeQuery();
            while (results.next()) {
                //the numbers match the index of the columns in the table
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
            Main.menu();


        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void createPlayer() {
        try {
            String username = "test";
            String password = "test";

            //the question marks are placeholders and get set a value
            //user ID is auto-incrementing so it is not inserted
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Players (Username, Password, SkinID) VALUES (?,?,?)");


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
    public static void updatePlayer() {
        try {
            String username = "test";
            String password = "test";
            int skinID = 1;
            int playerID = 1;


            //the question marks are placeholders
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET Username = ?, Password = ?, SkinID = ? WHERE PlayerID = ?");

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, skinID);
            ps.setInt(3, playerID);
            ps.executeUpdate();

            Main.menu();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }
    public static void deletePlayer(){
        try {
            int playerID = 1;
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Players WHERE PlayerID = ?");
            ps.setInt(1, playerID);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

    }
}
