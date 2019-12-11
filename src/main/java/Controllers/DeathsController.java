package Controllers;

import Server.Main;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path ("deaths/")
public class DeathsController {

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String deathsList(){
        System.out.println("deaths/list");
        JSONArray list = new JSONArray();
        try{
            PreparedStatement psGetDeaths = Main.db.prepareStatement("SELECT PlayerID, LivesLeft, DeathLocationX, DeathLocationY, StageID  FROM Deaths");

            ResultSet deathsResults = psGetDeaths.executeQuery();
            while (deathsResults.next()) {
                JSONObject item = new JSONObject();
                item.put("PlayerID", deathsResults.getInt(1));
                item.put("LivesLeft", deathsResults.getInt(2));
                item.put("DeathLocationX", deathsResults.getInt(3));
                item.put("DeathLocationY", deathsResults.getInt(4));
                item.put("StageID", deathsResults.getInt(5));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error code: " + exception.getMessage());
            return "{\"error\": \"Unable to list deaths. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String deathsUpdate(
            @CookieParam("Token") String token, @FormDataParam("LivesLeft") String livesLeftTemp, @FormDataParam("DeathLocationX") String deathLocationXTemp, @FormDataParam("DeathLocationY") String deathLocationYTemp, @FormDataParam("StageID") String stageIDTemp) {
        System.out.println("kills/update");
        try {
            if(token == null || livesLeftTemp == null || deathLocationXTemp == null || deathLocationYTemp == null || stageIDTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            int playerID = PlayersController.identifyPlayer(token);
            int livesLeft= Integer.parseInt(livesLeftTemp);
            int deathLocationX = Integer.parseInt(deathLocationXTemp);
            int deathLocationY = Integer.parseInt(deathLocationYTemp);
            int stageID = Integer.parseInt(stageIDTemp);

            //this SQL statement checks whether the player has ever died with that many lives left
            PreparedStatement psCheckLivesLeft = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM Deaths WHERE PlayerID = ? AND LivesLeft = ?)");
            psCheckLivesLeft.setInt(1, playerID);
            psCheckLivesLeft.setInt(2, livesLeft);
            ResultSet livesLeftResults = psCheckLivesLeft.executeQuery();

            int exists = 0;
            while (livesLeftResults.next()) {
                exists = livesLeftResults.getInt(1);
            }

            //if the player hasn't died yet with that many lives left, then a new death is created, using the LivesLeft from the form data parameters
            if(exists==0){
                PreparedStatement psNewDeath = Main.db.prepareStatement("INSERT INTO Deaths (PlayerID, LivesLeft, DeathLocationX, DeathLocationY, StageID) VALUES (?,?,?,?,?)");

                psNewDeath.setInt(1, playerID);
                psNewDeath.setInt(2, livesLeft);
                psNewDeath.setInt(3, deathLocationX);
                psNewDeath.setInt(4, deathLocationY);
                psNewDeath.setInt(5, stageID);
                psNewDeath.executeUpdate();
                return "{\"status\": \"OK\"}";
            }

            PreparedStatement psUpdateDeath = Main.db.prepareStatement("UPDATE Deaths SET DeathLocationX = ?, DeathLocationY = ?, StageID = ? WHERE PlayerID = ? AND LivesLeft = ?");
            psUpdateDeath.setInt(1, deathLocationX);
            psUpdateDeath.setInt(2, deathLocationY);
            psUpdateDeath.setInt(3, stageID);
            psUpdateDeath.setInt(4, playerID);
            psUpdateDeath.setInt(5, livesLeft);
            psUpdateDeath.executeUpdate();

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error code: " + exception.getMessage());
            return "{\"error\": \"Unable to update death. Please see server console for more info.\"}";
        }
    }
}