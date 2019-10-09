package Controllers;

import Server.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class deathsController {

    public static void createDeath(int playerID, int livesLeft, int deathLocationX, int deathLocationY, int stageID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Deaths (PlayerID, LivesLeft, DeathLocationX, DeathLocationY, StageID) VALUES (?,?,?,?,?)");

            ps.setInt(1, playerID);
            ps.setInt(2, livesLeft);
            ps.setInt(3, deathLocationX);
            ps.setInt(4, deathLocationY);
            ps.setInt(5, stageID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }

    public static void readDeaths(){
        try{
            PreparedStatement ps = Main.db.prepareStatement("SELECT PlayerID, LivesLeft, DeathLocationX, DeathLocationY, StageID  FROM Deaths");

            ResultSet results = ps.executeQuery();

            while (results.next()) {
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
        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }
    public static void updateDeaths(int playerID, int livesLeft, int deathLocationX, int deathLocationY, int stageID) {
        try {
            //the deaths can be updated depending on the playerID that is inputted
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Deaths SET DeathLocationX = ?, DeathLocationY = ?, StageID = ? WHERE PlayerID = ? AND LivesLeft = ?");

            ps.setInt(1, deathLocationX);
            ps.setInt(2, deathLocationY);
            ps.setInt(3, stageID);
            ps.setInt(4, playerID);
            ps.setInt(5, livesLeft);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }

    public static void deleteDeaths(int playerID){
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Deaths WHERE PlayerID = ?");
            ps.setInt(1, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }
}