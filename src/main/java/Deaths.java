import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Deaths {
    public static void createDeath() {
        try {
            int playerID = 1;
            int livesLeft = 2;
            int deathLocationX = 1;
            int deathLocationY = 2;
            int stageID = 1;

            //the question marks are placeholders
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Deaths (PlayerID, LivesLeft, DeathLocationX, DeathLocationY, StageID) VALUES (?,?,?,?,?)");


            //the parameter index corresponds with each question mark
            ps.setInt(1, playerID);
            ps.setInt(2, livesLeft);
            ps.setInt(3, deathLocationX);
            ps.setInt(4, deathLocationY);
            ps.setInt(5, stageID);
            ps.executeUpdate();


        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void readDeaths(){
        try{
            PreparedStatement ps = Main.db.prepareStatement("SELECT PlayerID, LivesLeft, DeathLocationX, DeathLocationY, StageID  FROM Death");

            ResultSet results = ps.executeQuery();
            //returns false and stops the loop when there are no more records
            while (results.next()) {
                //the column index matches  the columns in the table
                int playerID = results.getInt(1);
                int livesLeft = results.getInt(2);
                int deathLocationX = results.getInt(3);
                int deathLocationY = results.getInt(3);
                int stageID = results.getInt(3);
                System.out.println("PlayerID: " + playerID);
                System.out.println("Lives Left:  " + livesLeft);
                System.out.println("Death Location X: " + deathLocationX);
                System.out.println("Death Location Y: " + deathLocationY);
                System.out.println("StageID: " + stageID);

                System.out.println();
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void updateKills() {
        try {
            int deathLocationX = 1;
            int deathLocationY = 2;
            int stageID = 1;
            int playerID = 1;
            int livesleft = 2;

            //the question mark is a placeholder
            //the player can be updated depending on the playerID that is inputted
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET DeathLocationX = ?, DeathLocationY = ?, StageID = ? WHERE PlayerID = ? AND WHERE LivesLeft = ?");

            ps.setInt(1, deathLocationX);
            ps.setInt(2, deathLocationY);
            ps.setInt(3, stageID);
            ps.setInt(4, playerID);
            ps.setInt(5, livesleft);
            ps.executeUpdate();


        } catch (Exception exception) {
            System.out.println(exception.getMessage());

        }
    }
    public static void deleteKills(){
        try {
            int playerID = 1;

            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Deaths WHERE PlayerID = ?");
            ps.setInt(1, playerID);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println(exception.getMessage());

        }

    }
}
