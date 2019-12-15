package Controllers;

import Server.Main;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path ("unlockedSkins/")
public class UnlockedSkinsController {

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String unlockedSkinsList(){
        System.out.println("unlockedSkins/list");
        JSONArray list = new JSONArray();
        try{
            PreparedStatement psGetUnlockedSkins = Main.db.prepareStatement("SELECT playerid, skinid  FROM UnlockedSkins");
            ResultSet unlockedSkinsResults = psGetUnlockedSkins.executeQuery();
            while (unlockedSkinsResults.next()) {
                JSONObject item = new JSONObject();
                item.put("playerid", unlockedSkinsResults.getInt(1));
                item.put("skinid", unlockedSkinsResults.getInt(2));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to list unlocked skins. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String unlockedSkinsNew(
            @CookieParam("token") String token, @FormDataParam("skinid") String skinidTemp) {
        System.out.println("unlockedSkins/new");
        try {
            if(token == null || skinidTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            int skinid = Integer.parseInt(skinidTemp);
            int playerid = PlayersController.identifyPlayer(token);

            PreparedStatement psGetSkinid = Main.db.prepareStatement("SELECT skinid FROM UnlockedSkins WHERE playerid = ?");
            psGetSkinid.setInt(1, playerid);
            ResultSet skinidResults = psGetSkinid.executeQuery();
            while (skinidResults.next()) {
                //if the player has already unlocked that skin, then an error should be returned
                int unlockedSkinid = skinidResults.getInt(1);
                if (unlockedSkinid == skinid) {
                    return "{\"error\": \"Unable to buy skin. Player has already unlocked this skin.\"}";
                }
            }

            PreparedStatement psGetSkinCost = Main.db.prepareStatement("SELECT cost FROM Skins WHERE skinid = ?");
            psGetSkinCost.setInt(1, skinid);
            ResultSet skinCostResults = psGetSkinCost.executeQuery();
            int cost = 0;
            while (skinCostResults.next()) {
                cost = skinCostResults.getInt(1);
            }

            PreparedStatement psGetPlayerCurrency = Main.db.prepareStatement("SELECT currency FROM Players WHERE playerid = ?");
            psGetPlayerCurrency.setInt(1, playerid);
            ResultSet currencyResults = psGetPlayerCurrency.executeQuery();
            int currency = 0;
            while (currencyResults.next()) {
                currency = currencyResults.getInt(1);
            }

            //if the player has less currency than the cost of the skin, then an error is returned
            if (currency < cost){
                return "{\"error\": \"Unable to buy skin. Player does not have enough currency.\"}";
            }
            //the new currency is the currency minus the cost of the skin
            currency = currency - cost;

            try{
                Main.db.setAutoCommit(false);

                PreparedStatement psNewUnlockedSkin = Main.db.prepareStatement("INSERT INTO UnlockedSkins (playerid, skinid) Values (?,?)");
                psNewUnlockedSkin.setInt(1, playerid);
                psNewUnlockedSkin.setInt(2, skinid);
                psNewUnlockedSkin.executeUpdate();

                PreparedStatement psUpdateCurrency = Main.db.prepareStatement("UPDATE Players SET currency = ? WHERE playerid = ?");
                psUpdateCurrency.setInt(1, currency);
                psUpdateCurrency.setInt(2, playerid);
                psUpdateCurrency.executeUpdate();

                Main.db.commit();
            } catch (Exception exception){
                Main.db.rollback();
                System.out.println("Database error: " + exception.getMessage());
                return "{\"error\": \"Unable to buy skin. Please see server console for more info.\"}";
            }

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to buy skin. Please see server console for more info.\"}";
        }
    }
}