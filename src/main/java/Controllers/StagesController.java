package Controllers;

import Server.Main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path ("stages/")
public class StagesController {

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String stagesList(){
        System.out.println("stages/list");
        JSONArray list = new JSONArray();
        try{
            PreparedStatement psGetStages = Main.db.prepareStatement("SELECT stageid, locationY, imageFile  FROM Stages");
            ResultSet stagesResults = psGetStages.executeQuery();
            while (stagesResults.next()) {
                JSONObject item = new JSONObject();
                item.put("stageid", stagesResults.getInt(1));
                item.put("locationY", stagesResults.getInt(2));
                item.put("imageFile", stagesResults.getString(3));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to list stages. Please see server console for more info.\"}";
        }
    }
}