import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Kills {
    public static void createKill() {
        try {
            int playerID = 1;
            int monsterID = 2;
            int numberOfKills = 1;

            //the question marks are placeholders
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Kills (PlayerID, SkinID, NumberOfKills) VALUES (?,?,?)");


            //the parameter index corresponds with each question mark
            ps.setInt(1, playerID);
            ps.setInt(2, monsterID);
            ps.setInt(3, numberOfKills);
            ps.executeUpdate();


        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void readKills(){
        try{
            PreparedStatement ps = Main.db.prepareStatement("SELECT PlayerID, MonsterID, NumberOfKills  FROM Kills");

            ResultSet results = ps.executeQuery();
            //returns false and stops the loop when there are no more records
            while (results.next()) {
                //the column index matches  the columns in the table
                int playerID = results.getInt(1);
                int monsterID = results.getInt(2);
                int numberOfKills = results.getInt(3);
                System.out.println("MonsterID: " + playerID);
                System.out.println("Monster Name:  " + monsterID);
                System.out.println("Movement type: " + numberOfKills);

                System.out.println();
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void updateKills() {
        try {
            int numberOfKills = 2;
            int playerID = 1;
            int monsterID = 1;

            //the question mark is a placeholder
            //the player can be updated depending on the playerID that is inputted
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET NumberOfKills = ? WHERE PlayerID = ? AND WHERE MonsterID = ?");

            ps.setInt(1, numberOfKills);
            ps.setInt(2, playerID);
            ps.setInt(3, monsterID);
            ps.executeUpdate();


        } catch (Exception exception) {
            System.out.println(exception.getMessage());

        }
    }
    public static void deleteKills(){
        try {
            int playerID = 1;

            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Kills WHERE PlayerID = ?");
            ps.setInt(1, playerID);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println(exception.getMessage());

        }

    }
}
