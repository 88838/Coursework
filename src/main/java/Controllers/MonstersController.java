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
            PreparedStatement psGetMonsters = Main.db.prepareStatement("SELECT MonsterID, MonsterName, MovementType, AttackType, ImageFile, StageID  FROM Monsters");

            ResultSet monstersResults = psGetMonsters.executeQuery();
            while (monstersResults.next()) {
                JSONObject item = new JSONObject();
                item.put("MonsterID", monstersResults.getInt(1));
                item.put("MonsterName", monstersResults.getString(2));
                item.put("MovementType", monstersResults.getString(3));
                item.put("AttackType", monstersResults.getString(4));
                item.put("ImageFile", monstersResults.getString(5));
                item.put("StageID", monstersResults.getInt(6));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error code: " + exception.getMessage());
            return "{\"error\": \"Unable to list monsters. Please see server console for more info.\"}";
        }
    }
}