package Controllers;

import Server.Main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path ("skins/")
public class SkinsController {

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String skinsList(){
        System.out.println("skins/list");
        JSONArray list = new JSONArray();
        try{
            PreparedStatement psGetSkins = Main.db.prepareStatement("SELECT skinid, skinName, imageFile, cost  FROM Skins");
            ResultSet skinsResults = psGetSkins.executeQuery();
            while (skinsResults.next()) {
                JSONObject item = new JSONObject();
                item.put("skinid", skinsResults.getInt(1));
                item.put("skinName", skinsResults.getString(2));
                item.put("imageFile", skinsResults.getString(3));
                item.put("cost", skinsResults.getInt(4));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to list skins. Please see server console for more info.\"}";
        }
    }
}