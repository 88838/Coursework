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
public class unlockedSkinsController {

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String unlockedSkinsList(){
        System.out.println("unlockedSkins/list");
        JSONArray list = new JSONArray();
        try{
            PreparedStatement psGetUnlockedSkins = Main.db.prepareStatement("SELECT PlayerID, SkinID  FROM UnlockedSkins");
            ResultSet unlockedSkinsresults = psGetUnlockedSkins.executeQuery();
            while (unlockedSkinsresults.next()) {
                JSONObject item = new JSONObject();
                item.put("PlayerID", unlockedSkinsresults.getInt(1));
                item.put("SkinID", unlockedSkinsresults.getInt(2));
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
            @CookieParam("Token") String token, @FormDataParam("SkinID") String skinIDTemp) {
        System.out.println("unlockedSkins/new");
        try {
            if(token == null || skinIDTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            int skinID = Integer.parseInt(skinIDTemp);
            int playerID = playersController.identifyPlayer(token);

            PreparedStatement psGetSkinID = Main.db.prepareStatement("SELECT SkinID FROM UnlockedSkins WHERE PlayerID = ?");
            psGetSkinID.setInt(1, playerID);
            ResultSet skinIDResults = psGetSkinID.executeQuery();
            while (skinIDResults.next()) {
                //if the player has already unlocked that skin, then an error should be returned
                int unlockedSkinID = skinIDResults.getInt(1);
                if (unlockedSkinID == skinID) {
                    return "{\"error\": \"Unable to buy skin. Player has already unlocked this skin.\"}";
                }
            }

            PreparedStatement psGetSkinCost = Main.db.prepareStatement("SELECT Cost FROM Skins WHERE SkinID = ?");
            psGetSkinCost.setInt(1, skinID);
            ResultSet skinCostResults = psGetSkinCost.executeQuery();
            int cost = 0;
            while (skinCostResults.next()) {
                cost = skinCostResults.getInt(1);
            }

            PreparedStatement psGetPlayerCurrency = Main.db.prepareStatement("SELECT Currency FROM Players WHERE PlayerID = ?");
            psGetPlayerCurrency.setInt(1, playerID);
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

                PreparedStatement psNewUnlockedSkin = Main.db.prepareStatement("INSERT INTO UnlockedSkins (PlayerID, SkinID) Values (?,?)");
                psNewUnlockedSkin.setInt(1, playerID);
                psNewUnlockedSkin.setInt(2, skinID);
                psNewUnlockedSkin.executeUpdate();

                PreparedStatement psUpdateCurrency = Main.db.prepareStatement("UPDATE Players SET Currency = ? WHERE PlayerID = ?");
                psUpdateCurrency.setInt(1, currency);
                psUpdateCurrency.setInt(2, playerID);
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