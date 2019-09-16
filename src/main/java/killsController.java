import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class killsController {

    public static void createKill() {
        try {
            int playerID = 1;
            int monsterID = 2;
            int numberOfKills = 1;

            //the question marks are placeholders
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Kills (PlayerID, SkinID, NumberOfKills) VALUES (?,?,?)");

            //the first parameter corresponds with the index of each question mark
            //the second parameter is a variable that replaces the question marks
            ps.setInt(1, playerID);
            ps.setInt(2, monsterID);
            ps.setInt(3, numberOfKills);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }

    public static void readKills(){
        try{
            PreparedStatement ps = Main.db.prepareStatement("SELECT PlayerID, MonsterID, NumberOfKills  FROM Kills");

            ResultSet results = ps.executeQuery();
            //returns false and stops the loop when there are no more records
            while (results.next()) {
                //the parameter matches the index of the columns in the table
                int playerID = results.getInt(1);
                int monsterID = results.getInt(2);
                int numberOfKills = results.getInt(3);
                System.out.println("MonsterID: " + playerID);
                System.out.println("Monster Name:  " + monsterID);
                System.out.println("Movement type: " + numberOfKills);

                System.out.println();
            }
        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }

    public static void updateKills() {
        try {
            int numberOfKills = 2;
            int playerID = 1;
            int monsterID = 1;

            //the question mark is a placeholder
            //the kills can be updated for each specific monster using the MonsterID
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET NumberOfKills = ? WHERE PlayerID = ? AND WHERE MonsterID = ?");

            //the first parameter corresponds with the index of each question mark
            //the second parameter is a variable that replaces the question marks
            ps.setInt(1, numberOfKills);
            ps.setInt(2, playerID);
            ps.setInt(3, monsterID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());

        }
    }

    public static void deleteKills(){
        try {
            int playerID = 1;

            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Kills WHERE PlayerID = ?");
            ps.setInt(1, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());

        }

    }
}
