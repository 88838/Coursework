package Controllers;

import Server.Main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path ("monsters/")
public class MonstersController {

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String monstersList(){
        System.out.println("monsters/list");
        JSONArray list = new JSONArray();
        try{
            PreparedStatement psGetMonsters = Main.db.prepareStatement("SELECT monsterid, monsterName, movementType, attackType, imageFile, stageid  FROM Monsters");

            ResultSet monstersResults = psGetMonsters.executeQuery();
            while (monstersResults.next()) {
                JSONObject item = new JSONObject();
                item.put("monsterid", monstersResults.getInt(1));
                item.put("monsterName", monstersResults.getString(2));
                item.put("movementType", monstersResults.getString(3));
                item.put("attackType", monstersResults.getString(4));
                item.put("imageFile", monstersResults.getString(5));
                item.put("stageid", monstersResults.getInt(6));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to list monsters. Please see server console for more info.\"}";
        }
    }
}