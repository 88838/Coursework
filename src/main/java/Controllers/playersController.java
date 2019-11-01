package Controllers;

import Server.Main;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Path ("players/")
public class playersController {

    //this method is used to get the PlayerID using the token of the login session and will be used several times
    public static int identifyPlayer(String token) {
        int playerID = 0;
        try {
            PreparedStatement psGetPlayerID = Main.db.prepareStatement("SELECT PlayerID FROM Players WHERE Token = ?");
            psGetPlayerID.setString(1, token);
            ResultSet playerIDResults = psGetPlayerID.executeQuery();
            while (playerIDResults.next()) {
                playerID = playerIDResults.getInt(1);
            }
        } catch (Exception exception) {
            System.out.println("Database error:" + exception.getMessage());
        }
        return playerID;
    }

    //the checkPassword function is needed to check that the password follows the constraints set out in phase 1 design
    //this could not be done through SQL and therefore has to be done in Java
    public static void checkPassword(String password) throws Exception{

        //all variables these need to be set to false by default before the password can be checked
        boolean containsUpper = false;
        boolean containsLower = false;
        boolean containsDigit = false;
        boolean correctLength = false;

        //the other constraints don't need to be checked if the basic length check fails
        if (password.length() >= 8) {
            correctLength = true;
            //loops through every single character in the password and checks whether it is uppercase, lowercase or a digit
            for(int i = 0; i < password.length(); i ++){
                char currentCharacter = password.charAt(i);
                if (Character.isLowerCase(currentCharacter)) {
                    containsLower = true;
                }else if(Character.isUpperCase(currentCharacter)){
                    containsUpper = true;
                }else if(Character.isDigit(currentCharacter)){
                    containsDigit = true;
                }
            }
        }
        //if any of the variables are false then an exception is thrown, which contains a custom message
        if (!(containsUpper && containsLower && containsDigit && correctLength)) {
            throw new Exception("Abort due to constraint violation (Password must be bigger than 8 characters, contain an uppercase and lowercase letter, contain a digit)");
        }
    }

    //these annotations turn the method into an HTTP request handler
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    //the method has to be public so the Jersey library can interact with it
    //this method was previously 'readPlayers' however I have now renamed it to be the same format as the API path
    public String playersList() {
        System.out.println("players/list");
        //the JSON is prepared using the 'Simple JSON' Library
        //a JSON array is created using a series of JSON objects with the values from the database
        JSONArray list = new JSONArray();
        try {
            //the JSON array can be split in Javascript for those different uses, rather than having different APIs for each attribute.
            PreparedStatement psKillInfo = Main.db.prepareStatement("SELECT Players.PlayerID, Kills.NumberOfKills, Kills.MonsterID FROM Players, Kills WHERE Players.PlayerID = Kills.PlayerID");

            //these are the results from the killInfo prepared statement
            ResultSet killInfoResults = psKillInfo.executeQuery();

            //this Hash Map is an example of an associative array
            HashMap<Integer, ArrayList<JSONObject>> playerKills = new HashMap<>();
            while (killInfoResults.next()) {
                int playerID = killInfoResults.getInt(1);
                //pushes playerID if it has not yet been mapped in the map
                if (!playerKills.containsKey(playerID)) {
                    playerKills.put(playerID, new ArrayList<JSONObject>());
                }
                JSONObject killDetails = new JSONObject();
                killDetails.put("MonsterID", killInfoResults.getInt(3));
                killDetails.put("NumberOfKills", killInfoResults.getInt(2));
                //the killDetails JSONObject is added to the element with the playerID index
                playerKills.get(playerID).add(killDetails);
            }

            PreparedStatement psPlayerInfo = Main.db.prepareStatement("SELECT PlayerID, Username, HighScore, Currency, SkinID FROM Players");

            //there are the results from the PlayerInfo prepared statements
            ResultSet playerInfoResults = psPlayerInfo.executeQuery();

            while (playerInfoResults.next()) {
                JSONObject item = new JSONObject();
                int playerID = playerInfoResults.getInt(1);
                item.put("PlayerID", playerID);
                item.put("Username", playerInfoResults.getString(2));
                item.put("HighScore", playerInfoResults.getString(3));
                item.put("Currency", playerInfoResults.getString(4));
                item.put("SkinID", playerInfoResults.getString(5));
                //this adds all the kill details with the playerID from the hash map into the JSON object
                item.put("Kills", playerKills.get(playerID));
                //the item is then added to the list
                list.add(item);
            }
            //the method returns a string in terms of JSON
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            //this will show up on Git Bash because the console will provide more information through the exception message
            return "{\"error\": \"Unable to list players. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    //this method was previously 'createPlayer' however I have now renamed it to be the same format as the API path
    public String playersNew(
            //these are the parameters that will be filled using Git Bash when testing
            @FormDataParam("Username") String username, @FormDataParam("Password") String password) {
        System.out.println("players/new");
        try {
            //the API needs to check whether the player has put a username and password, otherwise a player can't be created
            if(username == null || password == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }
            PreparedStatement psNewPlayer = Main.db.prepareStatement("INSERT INTO Players (Username, Password) VALUES (?,?)");

            checkPassword(password);

            psNewPlayer.setString(1, username);
            psNewPlayer.setString(2, password);
            psNewPlayer.executeUpdate();

            //for this API, only a confirmation that everything has worked correctly is needed. This will be the case for all of the APIs that aren't listing anything in JSON
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to create new player. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("changeUsername")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    //this method was previously 'updateUsername' however I have now renamed it to be the same format as the API path
    //the cookie parameter is similar to the form data parameter however will be used specifically for tokens
    public String playersChangeUsername(
            @CookieParam("Token") String token, @FormDataParam("NewUsername") String newUsername) {
        System.out.println("players/changeUsername");
        try {
            if(token == null || newUsername == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }
            int playerID = playersController.identifyPlayer(token);

            //this SQL statement gets the username of the player that was just identified
            PreparedStatement psGetOldUsername = Main.db.prepareStatement("SELECT Username FROM Players WHERE PlayerID = ?");
            psGetOldUsername.setInt(1, playerID);
            ResultSet oldUsernameResults = psGetOldUsername.executeQuery();
            String oldUsername = "";
            while (oldUsernameResults.next()) {
                oldUsername = oldUsernameResults.getString(1);
            }

            //if the new username is the same as the old username an error is returned
            if (newUsername.equals(oldUsername)){
                return "{\"error\": \"Unable to change username. New username can't be the same as old username.\"}";
            }

            PreparedStatement psChangeUsername = Main.db.prepareStatement("UPDATE Players SET Username = ? WHERE PlayerID = ?");
            psChangeUsername.setString(1, newUsername);
            psChangeUsername.setInt(2, playerID);
            psChangeUsername.executeUpdate();

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to change username. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("changePassword")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    //this method was previously 'updatePassword' however I have now renamed it to be the same format as the API path
    public String playersChangePassword(
            @CookieParam("Token") String token, @FormDataParam("NewPassword") String newPassword) {
        System.out.println("players/changePassword");
        try {
            if(token == null || newPassword == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }
            int playerID = playersController.identifyPlayer(token);

            PreparedStatement psGetOldPassword = Main.db.prepareStatement("SELECT Password FROM Players WHERE PlayerID = ?");
            psGetOldPassword.setInt(1, playerID);
            ResultSet oldPasswordResults = psGetOldPassword.executeQuery();

            String oldPassword = "";
            while (oldPasswordResults.next()) {
                oldPassword = oldPasswordResults.getString(1);
            }

            //if the new password is the same as the old password an error is returned
            if (newPassword.equals(oldPassword)){
                return "{\"error\": \"Unable to change password. New password can't be the same as old password.\"}";
            }

            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET Password = ? WHERE PlayerID = ?");

            checkPassword(newPassword);

            ps.setString(1, newPassword);
            ps.setInt(2, playerID);
            ps.executeUpdate();

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to change password. Please see server console for more info.\"}";
        }

    }

    @POST
    @Path("changeSkin")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    //this method was previously 'updateSkin' however I have now renamed it to be the same format as the API path
    public String playersChangeSkin(
            @CookieParam("Token") String token, @FormDataParam("SkinID") String skinIDTemp) {
        System.out.println("players/changeSkin");
        try {
            if(token == null || skinIDTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            //the only way to check if the parameter is in the HTTP request is to have it as a string because it is a non-primitive data type
            //for the purposes of the rest of the API method, skinID has to be an integer
            int skinID = Integer.parseInt(skinIDTemp);
            int playerID = playersController.identifyPlayer(token);

            //this sql statement checks whether a player with the PlayerID owns a skin with the SkinID
            PreparedStatement psCheckUnlockedSkin = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM UnlockedSkins WHERE PlayerID = ? and SkinID = ?)");

            psCheckUnlockedSkin.setInt(1, playerID);
            psCheckUnlockedSkin.setInt(2, skinID);
            ResultSet unlockedSkinResults = psCheckUnlockedSkin.executeQuery();

            int exists = 0;
            while (unlockedSkinResults.next()) {
                exists = unlockedSkinResults.getInt(1);
            }

            //if the player doesn't own the skin then an error is returned
            if(exists==0){
                //the skin with SkinID 1 is automatically unlocked because it is the default, so it doesn't need to be in the UnlockedSkins table
                if(skinID != 1) {
                    return "{\"error\": \"Unable to change skin. Player has not unlocked this skin.\"}";
                }
            }

            PreparedStatement psChangeSkin = Main.db.prepareStatement("UPDATE Players SET SkinID = ? WHERE PlayerID = ?");

            psChangeSkin.setInt(1, skinID);
            psChangeSkin.setInt(2, playerID);
            psChangeSkin.executeUpdate();

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to change skin. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("updateHighScore")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String playersUpdateHighScore(
            @CookieParam("Token") String token, @FormDataParam("NewHighScore") String newHighScoreTemp) {
        System.out.println("players/updateHighScore");
        try {
            if(token == null || newHighScoreTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            int newHighScore = Integer.parseInt(newHighScoreTemp);
            int playerID = playersController.identifyPlayer(token);

            PreparedStatement psUpdateHighScore = Main.db.prepareStatement("UPDATE Players SET HighScore = ? WHERE PlayerID = ?");

            psUpdateHighScore.setInt(1, newHighScore);
            psUpdateHighScore.setInt(2, playerID);
            psUpdateHighScore.executeUpdate();

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error code: " + exception.getMessage());
            return "{\"error\": \"Unable to update high score. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("updateCurrency")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String playersUpdateCurrency(
            @CookieParam("Token") String token, @FormDataParam("SessionCurrency") String sessionCurrencyTemp) {
        System.out.println("players/updateCurrency");
        try {
            if(token == null || sessionCurrencyTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            int sessionCurrency = Integer.parseInt(sessionCurrencyTemp);
            int playerID = playersController.identifyPlayer(token);

            PreparedStatement psGetOldCurrency = Main.db.prepareStatement("SELECT Currency FROM Players WHERE PlayerID = ?");

            psGetOldCurrency.setInt(1, playerID);
            ResultSet oldCurrencyResults = psGetOldCurrency.executeQuery();

            int oldCurrency = 0;
            while (oldCurrencyResults.next()) {
                oldCurrency = oldCurrencyResults.getInt(1);
            }

            //the old currency of the player and the currency that they just got in their last life are added together to form a new currency
            int newCurrency = oldCurrency + sessionCurrency;

            PreparedStatement psUpdateCurrency = Main.db.prepareStatement("UPDATE Players SET Currency = ? WHERE PlayerID = ?");

            psUpdateCurrency.setInt(1, newCurrency);
            psUpdateCurrency.setInt(2, playerID);
            psUpdateCurrency.executeUpdate();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error code: " + exception.getMessage());
            return "{\"error\": \"Unable to update currency. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String playersDelete(
            @CookieParam("Token") String token, @FormDataParam("Password") String password) {
        System.out.println("players/delete");
        try {
            if(token == null || password == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }
            int playerID = playersController.identifyPlayer(token);

            PreparedStatement psGetPassword = Main.db.prepareStatement("SELECT Password FROM Players WHERE PlayerID = ?");

            psGetPassword.setInt(1, playerID);
            ResultSet passwordResults = psGetPassword.executeQuery();

            String correctPassword = "";
            while (passwordResults.next()) {
                correctPassword = passwordResults.getString(1);
            }

            //if the password doesn't match the password in the database then it is incorrect and an error is returned
            if (!(password.equals(correctPassword))){
                return "{\"error\": \"Unable to delete player. Incorrect password.\"}";
            }
            try {
                //start of transaction
                Main.db.setAutoCommit(false);

                //all the prepared statements are done in the same order as when it was a transaction
                PreparedStatement psDeleteFromUnlockedSkins = Main.db.prepareStatement("DELETE FROM UnlockedSkins WHERE PlayerID = ?");
                psDeleteFromUnlockedSkins.setInt(1, playerID);
                psDeleteFromUnlockedSkins.executeUpdate();

                PreparedStatement psDeleteFromKills = Main.db.prepareStatement("DELETE FROM Kills WHERE PlayerID = ?");
                psDeleteFromKills.setInt(1, playerID);
                psDeleteFromKills.executeUpdate();

                PreparedStatement psDeleteFromDeaths = Main.db.prepareStatement("DELETE FROM Deaths WHERE PlayerID = ?");
                psDeleteFromDeaths.setInt(1, playerID);
                psDeleteFromDeaths.executeUpdate();

                PreparedStatement psDeleteFromPlayers = Main.db.prepareStatement("DELETE FROM Players WHERE PlayerID = ?");
                psDeleteFromPlayers.setInt(1, playerID);
                psDeleteFromPlayers.executeUpdate();

                //end of transaction
                Main.db.commit();
            }catch (Exception exception){
                //if anything goes wrong, then none of the changes will commit and the database will roll back to it's previous stable state
                Main.db.rollback();
                System.out.println("Database error: " + exception.getMessage());
                return "{\"error\": \"Unable to delete player. Please see server console for more info.\"}";
            }

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to delete player. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String playersLogin(
            @FormDataParam("Username") String username, @FormDataParam("Password") String password) {
        System.out.println("players/login");
        try {
            if(username == null || password == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            //this sql statement checks whether the player with that username exists
            PreparedStatement psCheckUsername = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM Players WHERE Username = ?)");
            psCheckUsername.setString(1, username);

            //similar code to the players/updateSkin API
            ResultSet usernameResults = psCheckUsername.executeQuery();
            int exists = 0;
            while (usernameResults.next()) {
                exists = usernameResults.getInt(1);
            }

            //if the username doesn't exist then an error is returned
            if(exists==0){
                 {
                    return "{\"error\": \"Unable to login. Player does not exist.\"}";
                }
            }

            //this sql statement is similar to the one in the identifyPlayer() method, however this time it's identifying the player based on their username instead of their token
            PreparedStatement psGetPlayerID = Main.db.prepareStatement("SELECT PlayerID FROM Players WHERE Username = ?");
            psGetPlayerID.setString(1, username);

            int playerID = 0;
            ResultSet playerIDResults = psGetPlayerID.executeQuery();
            while (playerIDResults.next()) {
                playerID = playerIDResults.getInt(1);
            }

            //similar code the players/delete API
            PreparedStatement psGetPassword = Main.db.prepareStatement("SELECT Password FROM Players WHERE PlayerID = ?");

            psGetPassword.setInt(1, playerID);
            ResultSet passwordResults = psGetPassword.executeQuery();

            String correctPassword = "";
            while (passwordResults.next()) {
                correctPassword = passwordResults.getString(1);
            }

            if (!(password.equals(correctPassword))){
                return "{\"error\": \"Unable to login. Incorrect password.\"}";
            }

            //this generates a random Universally Unique Identifier
            String token  = UUID.randomUUID().toString();

            PreparedStatement psUpdateToken = Main.db.prepareStatement("UPDATE Players SET Token = ? WHERE PlayerID = ?");
            psUpdateToken.setString(1, token);
            psUpdateToken.setInt(2, playerID);
            psUpdateToken.executeUpdate();

            return "{\"Token\": \"" + token + "\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to login. Please see server console for more info.\"}";
        }
    }
}