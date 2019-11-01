package Controllers;

import Server.Main;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path ("kills/")
public class killsController {

    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String killsUpdate(
            @CookieParam("Token") String token, @FormDataParam("MonsterID") String monsterIDTemp, @FormDataParam("SessionKills") String sessionKillsTemp) {
        System.out.println("kills/update");
        try {
            if(token == null || monsterIDTemp == null || sessionKillsTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            int playerID = playersController.identifyPlayer(token);
            int monsterID = Integer.parseInt(monsterIDTemp);
            int sessionKills = Integer.parseInt(sessionKillsTemp);

            //this SQL statement checks whether the player has killed that monster
            PreparedStatement psCheckMonsterID = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM Kills WHERE PlayerID = ? AND MonsterID = ?)");
            psCheckMonsterID.setInt(1, playerID);
            psCheckMonsterID.setInt(2, monsterID);
            ResultSet monsterIDResults = psCheckMonsterID.executeQuery();

            int exists = 0;
            while (monsterIDResults.next()) {
                exists = monsterIDResults.getInt(1);
            }

            //if the player hasn't killed the monster, then a new kill is created, using the MonsterID from the form data parameters
            if(exists==0){
                //this is the first instance of the kill, so the session kill don't need to be added to anything and can be used as the number of kills
                PreparedStatement psNewKill = Main.db.prepareStatement("INSERT INTO Kills (PlayerID, MonsterID, NumberOfKills) VALUES (?,?,?)");
                psNewKill.setInt(1, playerID);
                psNewKill.setInt(2, monsterID);
                psNewKill.setInt(3, sessionKills);
                psNewKill.executeUpdate();
                return "{\"status\": \"OK\"}";
            }

            PreparedStatement psGetOldNumberOfKills = Main.db.prepareStatement("SELECT NumberOfKills FROM Kills WHERE PlayerID = ? AND MonsterID = ?");
            psGetOldNumberOfKills.setInt(1, playerID);
            psGetOldNumberOfKills.setInt(2, monsterID);
            ResultSet oldNumberOfKillsResults = psGetOldNumberOfKills.executeQuery();

            int oldNumberOfKills = 0;
            while (oldNumberOfKillsResults.next()) {
                oldNumberOfKills = oldNumberOfKillsResults.getInt(1);
            }

            //the old number of kills is added to the kills the player got in their last life to form a new total
            int newNumberOfKills = oldNumberOfKills + sessionKills;

            PreparedStatement psUpdateKill = Main.db.prepareStatement("UPDATE Kills SET NumberOfKills = ? WHERE PlayerID = ? AND MonsterID = ?");

            psUpdateKill.setInt(1, newNumberOfKills);
            psUpdateKill.setInt(2, playerID);
            psUpdateKill.setInt(3, monsterID);
            psUpdateKill.executeUpdate();

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error code: " + exception.getMessage());
            return "{\"error\": \"Unable to update kill. Please see server console for more info.\"}";
        }
    }
}