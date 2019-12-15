package Controllers;

import Server.Main;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path ("kills/")
public class KillsController {

    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String killsUpdate(
            @CookieParam("token") String token, @FormDataParam("monsterid") String monsteridTemp, @FormDataParam("sessionKills") String sessionKillsTemp) {
        System.out.println("kills/update");
        try {
            if(token == null || monsteridTemp == null || sessionKillsTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            int playerid = PlayersController.identifyPlayer(token);
            int monsterid = Integer.parseInt(monsteridTemp);
            int sessionKills = Integer.parseInt(sessionKillsTemp);

            //this SQL statement checks whether the player has killed that monster
            PreparedStatement psCheckMonsterid = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM Kills WHERE playerid = ? AND monsterid = ?)");
            psCheckMonsterid.setInt(1, playerid);
            psCheckMonsterid.setInt(2, monsterid);
            ResultSet monsteridResults = psCheckMonsterid.executeQuery();

            int exists = 0;
            while (monsteridResults.next()) {
                exists = monsteridResults.getInt(1);
            }

            //if the player hasn't killed the monster, then a new kill is created, using the MonsterID from the form data parameters
            if(exists==0){
                //this is the first instance of the kill, so the session kill don't need to be added to anything and can be used as the number of kills
                PreparedStatement psNewKill = Main.db.prepareStatement("INSERT INTO Kills (playerid, mosterid, numberOfKills) VALUES (?,?,?)");
                psNewKill.setInt(1, playerid);
                psNewKill.setInt(2, monsterid);
                psNewKill.setInt(3, sessionKills);
                psNewKill.executeUpdate();
                return "{\"status\": \"OK\"}";
            }

            PreparedStatement psGetOldNumberOfKills = Main.db.prepareStatement("SELECT numberOfKills FROM Kills WHERE playerid = ? AND monsterid = ?");
            psGetOldNumberOfKills.setInt(1, playerid);
            psGetOldNumberOfKills.setInt(2, monsterid);
            ResultSet oldNumberOfKillsResults = psGetOldNumberOfKills.executeQuery();

            int oldNumberOfKills = 0;
            while (oldNumberOfKillsResults.next()) {
                oldNumberOfKills = oldNumberOfKillsResults.getInt(1);
            }

            //the old number of kills is added to the kills the player got in their last life to form a new total
            int newNumberOfKills = oldNumberOfKills + sessionKills;

            PreparedStatement psUpdateKill = Main.db.prepareStatement("UPDATE Kills SET numberOfKills = ? WHERE playerid = ? AND monsterid = ?");

            psUpdateKill.setInt(1, newNumberOfKills);
            psUpdateKill.setInt(2, playerid);
            psUpdateKill.setInt(3, monsterid);
            psUpdateKill.executeUpdate();

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error code: " + exception.getMessage());
            return "{\"error\": \"Unable to update kill. Please see server console for more info.\"}";
        }
    }
}