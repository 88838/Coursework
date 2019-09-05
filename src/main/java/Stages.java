import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Stages {
    public static void readStages(){
        try{
            PreparedStatement ps = Main.db.prepareStatement("SELECT StageID, LocationY  FROM Stages");

            ResultSet results = ps.executeQuery();
            //returns false and stops the loop when there are no more records
            while (results.next()) {
                //the column index matches  the columns in the table
                int stageID = results.getInt(1);
                int locationY = results.getInt(2);
                System.out.println("StageID: " + stageID);
                System.out.println("Location Y: " + locationY);
                System.out.println();
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
}
