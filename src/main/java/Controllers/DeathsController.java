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
            PreparedStatement psGetDeaths = Main.db.prepareStatement("SELECT playrid, livesLeft, deathLocationX, deathLocationY, stageid  FROM Deaths");

            ResultSet deathsResults = psGetDeaths.executeQuery();
            while (deathsResults.next()) {
                JSONObject item = new JSONObject();
                item.put("playerid", deathsResults.getInt(1));
                item.put("livesLeft", deathsResults.getInt(2));
                item.put("deathLocationX", deathsResults.getInt(3));
                item.put("deathLocationY", deathsResults.getInt(4));
                item.put("stageid", deathsResults.getInt(5));
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
            @CookieParam("token") String token, @FormDataParam("livesLeft") String livesLeftTemp, @FormDataParam("deathLocationX") String deathLocationXTemp, @FormDataParam("deathLocationY") String deathLocationYTemp, @FormDataParam("stageid") String stageidTemp) {
        System.out.println("kills/update");
        try {
            if(token == null || livesLeftTemp == null || deathLocationXTemp == null || deathLocationYTemp == null || stageidTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            int playerid = PlayersController.identifyPlayer(token);
            int livesLeft= Integer.parseInt(livesLeftTemp);
            int deathLocationX = Integer.parseInt(deathLocationXTemp);
            int deathLocationY = Integer.parseInt(deathLocationYTemp);
            int stageid = Integer.parseInt(stageidTemp);

            //this SQL statement checks whether the player has ever died with that many lives left
            PreparedStatement psCheckLivesLeft = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM Deaths WHERE playerid = ? AND livesLeft = ?)");
            psCheckLivesLeft.setInt(1, playerid);
            psCheckLivesLeft.setInt(2, livesLeft);
            ResultSet livesLeftResults = psCheckLivesLeft.executeQuery();

            int exists = 0;
            while (livesLeftResults.next()) {
                exists = livesLeftResults.getInt(1);
            }

            //if the player hasn't died yet with that many lives left, then a new death is created, using the LivesLeft from the form data parameters
            if(exists==0){
                PreparedStatement psNewDeath = Main.db.prepareStatement("INSERT INTO Deaths (playerid, livesLeft, deathLocationX, deathLocationY, stageid) VALUES (?,?,?,?,?)");

                psNewDeath.setInt(1, playerid);
                psNewDeath.setInt(2, livesLeft);
                psNewDeath.setInt(3, deathLocationX);
                psNewDeath.setInt(4, deathLocationY);
                psNewDeath.setInt(5, stageid);
                psNewDeath.executeUpdate();
                return "{\"status\": \"OK\"}";
            }

            PreparedStatement psUpdateDeath = Main.db.prepareStatement("UPDATE Deaths SET deathLocationX = ?, deathLocationY = ?, stageid = ? WHERE playerid = ? AND livesLeft = ?");
            psUpdateDeath.setInt(1, deathLocationX);
            psUpdateDeath.setInt(2, deathLocationY);
            psUpdateDeath.setInt(3, stageid);
            psUpdateDeath.setInt(4, playerid);
            psUpdateDeath.setInt(5, livesLeft);
            psUpdateDeath.executeUpdate();

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error code: " + exception.getMessage());
            return "{\"error\": \"Unable to update death. Please see server console for more info.\"}";
        }
    }
}