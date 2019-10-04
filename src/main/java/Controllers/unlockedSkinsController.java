package Controllers;

import Server.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class unlockedSkinsController {

    public static void createUnlockedSkin(int playerID, int skinID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO UnlockedSkins (PlayerID, SkinID) VALUES (?,?)");

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
            while (results.next()) {
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

    public static void deleteUnlockedSkins(int playerID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM UnlockedSkins WHERE PlayerID = ?");
            ps.setInt(1, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }
}
