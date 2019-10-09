package Controllers;

import Server.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class killsController {

    public static void createKill(int playerID, int monsterID, int numberOfKills) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Kills (PlayerID, MonsterID, NumberOfKills) VALUES (?,?,?)");

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

            while (results.next()) {
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

    public static void updateKills(int playerID, int monsterID, int numberOfKills) {
        try {
            //the kills can be updated for each specific monster using the MonsterID
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Kills SET NumberOfKills = ? WHERE PlayerID = ? AND MonsterID = ?");

            ps.setInt(1, numberOfKills);
            ps.setInt(2, playerID);
            ps.setInt(3, monsterID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());

        }
    }

    public static void deleteKills(int playerID){
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Kills WHERE PlayerID = ?");
            ps.setInt(1, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }
}
