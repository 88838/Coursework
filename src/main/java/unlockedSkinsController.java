import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class unlockedSkinsController {

    public static void createUnlockedSkin() {
        try {
            int playerID = 1;
            int skinID = 2;

            //the question marks are placeholders
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO UnlockedSkins (PlayerID, SkinID) VALUES (?,?)");


            //the first parameter corresponds with the index of each question mark
            //the second parameter is a variable that replaces the question marks
            ps.setInt(1, playerID);
            ps.setInt(2, skinID);
            ps.executeUpdate();


        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }
    public static void readUnlockedSkins(){
        try{
            PreparedStatement ps = Main.db.prepareStatement("SELECT PlayerID, SkinID  FROM UnlockedSkins");

            ResultSet results = ps.executeQuery();
            //returns false and stops the loop when there are no more records
            while (results.next()) {
                //the parameter matches the index of the columns in the table
                int playerID = results.getInt(1);
                int skinID = results.getInt(2);
                System.out.println("PlayerID: " + playerID);
                System.out.println("SkinID: " + skinID);
                System.out.println();
            }
        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }
    public static void deleteUnlockedSkins(){
        try {
            int playerID = 1;

            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM UnlockedSkins WHERE PlayerID = ?");
            ps.setInt(1, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());

        }

    }
}
