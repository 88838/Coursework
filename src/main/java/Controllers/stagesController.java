package Controllers;

import Server.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class stagesController {

    public static void readStages(){
        try{
            PreparedStatement ps = Main.db.prepareStatement("SELECT StageID, LocationY, ImageFile  FROM Stages");

            ResultSet results = ps.executeQuery();

            while (results.next()) {

                int stageID = results.getInt(1);
                int locationY = results.getInt(2);
                int imgFile = results.getInt(3);
                System.out.println("StageID: " + stageID);
                System.out.println("Location Y: " + locationY);
                System.out.println("Image File: " + locationY);
                System.out.println();
            }
        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }
}
