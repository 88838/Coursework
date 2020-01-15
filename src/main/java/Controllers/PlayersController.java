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
public class PlayersController {

    //this method is used to get the playerid using the token of the login session and will be used several times
    public static int identifyPlayer(String token) {
        int playerid = 0;
        try {
            PreparedStatement psGetPlayerid = Main.db.prepareStatement("SELECT playerid FROM Players WHERE token = ?");
            psGetPlayerid.setString(1, token);
            ResultSet playeridResults = psGetPlayerid.executeQuery();
            while (playeridResults.next()) {
                playerid = playeridResults.getInt(1);
            }
        } catch (Exception exception) {
            System.out.println("Database error:" + exception.getMessage());
        }
        return playerid;
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
            PreparedStatement psKillInfo = Main.db.prepareStatement("SELECT Players.playerid, Kills.numberOfKills, Kills.monsterid FROM Players, Kills WHERE Players.playerid = Kills.playerid");

            //these are the results from the killInfo prepared statement
            ResultSet killInfoResults = psKillInfo.executeQuery();

            //this Hash Map is an example of an associative array
            HashMap<Integer, ArrayList<JSONObject>> playerKills = new HashMap<>();
            while (killInfoResults.next()) {
                int playerid = killInfoResults.getInt(1);
                //pushes playerid if it has not yet been mapped in the map
                if (!playerKills.containsKey(playerid)) {
                    playerKills.put(playerid, new ArrayList<JSONObject>());
                }
                JSONObject killDetails = new JSONObject();
                killDetails.put("monsterid", killInfoResults.getInt(3));
                killDetails.put("numberOfKills", killInfoResults.getInt(2));
                //the killDetails JSONObject is added to the element with the playerid index
                playerKills.get(playerid).add(killDetails);
            }

            PreparedStatement psPlayerInfo = Main.db.prepareStatement("SELECT playerid, username, highScore, currency, skinid FROM Players");

            //there are the results from the PlayerInfo prepared statements
            ResultSet playerInfoResults = psPlayerInfo.executeQuery();

            while (playerInfoResults.next()) {
                JSONObject item = new JSONObject();
                int playerid = playerInfoResults.getInt(1);
                item.put("playerid", playerid);
                item.put("username", playerInfoResults.getString(2));
                item.put("highScore", playerInfoResults.getString(3));
                item.put("currency", playerInfoResults.getString(4));
                item.put("skinid", playerInfoResults.getString(5));
                //this adds all the kill details with the playerid from the hash map into the JSON object
                item.put("kills", playerKills.get(playerid));
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

    @GET
    @Path("get/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public String playersList(@PathParam("token") String token) {
        System.out.println("players/get/" + token);
        try {
            if(token == null){
                throw new Exception("Token is missing in the HTTP request");
            }
            int playerid = identifyPlayer(token);
            PreparedStatement psPlayerInfo = Main.db.prepareStatement("SELECT skinid, highScore FROM Players WHERE playerid = ?");
            psPlayerInfo.setInt(1, playerid);
            ResultSet playerInfoResults = psPlayerInfo.executeQuery();

            JSONObject item = new JSONObject();
            if (playerInfoResults.next()) {
                item.put("playerid", playerid);
                item.put("skinid", playerInfoResults.getString(1));
                item.put("highScore", playerInfoResults.getString(2));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get player. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    //this method was previously 'createPlayer' however I have now renamed it to be the same format as the API path
    public String playersNew(
            //these are the parameters that will be filled using Git Bash when testing
            @FormDataParam("username") String username, @FormDataParam("password") String password) {
        System.out.println("players/new");
        try {
            //the API needs to check whether the player has put a username and password, otherwise a player can't be created
            if(username == null || password == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }
            PreparedStatement psNewPlayer = Main.db.prepareStatement("INSERT INTO Players (username, password) VALUES (?,?)");

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
            @CookieParam("token") String token, @FormDataParam("newUsername") String newUsername) {
        System.out.println("players/changeUsername");
        try {
            if(token == null || newUsername == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }
            int playerid = PlayersController.identifyPlayer(token);

            //this SQL statement gets the username of the player that was just identified
            PreparedStatement psGetOldUsername = Main.db.prepareStatement("SELECT username FROM Players WHERE playerid = ?");
            psGetOldUsername.setInt(1, playerid);
            ResultSet oldUsernameResults = psGetOldUsername.executeQuery();
            String oldUsername = "";
            while (oldUsernameResults.next()) {
                oldUsername = oldUsernameResults.getString(1);
            }

            //if the new username is the same as the old username an error is returned
            if (newUsername.equals(oldUsername)){
                return "{\"error\": \"Unable to change username. New username can't be the same as old username.\"}";
            }

            PreparedStatement psChangeUsername = Main.db.prepareStatement("UPDATE Players SET username = ? WHERE playerid = ?");
            psChangeUsername.setString(1, newUsername);
            psChangeUsername.setInt(2, playerid);
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
            @CookieParam("token") String token, @FormDataParam("newPassword") String newPassword) {
        System.out.println("players/changePassword");
        try {
            if(token == null || newPassword == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }
            int playerid = PlayersController.identifyPlayer(token);

            PreparedStatement psGetOldPassword = Main.db.prepareStatement("SELECT password FROM Players WHERE playerid = ?");
            psGetOldPassword.setInt(1, playerid);
            ResultSet oldPasswordResults = psGetOldPassword.executeQuery();

            String oldPassword = "";
            while (oldPasswordResults.next()) {
                oldPassword = oldPasswordResults.getString(1);
            }

            //if the new password is the same as the old password an error is returned
            if (newPassword.equals(oldPassword)){
                return "{\"error\": \"Unable to change password. New password can't be the same as old password.\"}";
            }

            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET password = ? WHERE playerid = ?");

            checkPassword(newPassword);

            ps.setString(1, newPassword);
            ps.setInt(2, playerid);
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
            @CookieParam("token") String token, @FormDataParam("skinid") String skinidTemp) {
        System.out.println("players/changeSkin");
        try {
            if(token == null || skinidTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            //the only way to check if the parameter is in the HTTP request is to have it as a string because it is a non-primitive data type
            //for the purposes of the rest of the API method, skinid has to be an integer
            int skinid = Integer.parseInt(skinidTemp);
            int playerid = PlayersController.identifyPlayer(token);

            //this sql statement checks whether a player with the PlayerID owns a skin with the SkinID
            PreparedStatement psCheckUnlockedSkin = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM UnlockedSkins WHERE playerid = ? and skinid = ?)");

            psCheckUnlockedSkin.setInt(1, playerid);
            psCheckUnlockedSkin.setInt(2, skinid);
            ResultSet unlockedSkinResults = psCheckUnlockedSkin.executeQuery();

            int exists = 0;
            while (unlockedSkinResults.next()) {
                exists = unlockedSkinResults.getInt(1);
            }

            //if the player doesn't own the skin then an error is returned
            if(exists==0){
                //the skin with SkinID 1 is automatically unlocked because it is the default, so it doesn't need to be in the UnlockedSkins table
                if(skinid != 1) {
                    return "{\"error\": \"Unable to change skin. Player has not unlocked this skin.\"}";
                }
            }

            PreparedStatement psChangeSkin = Main.db.prepareStatement("UPDATE Players SET skinid = ? WHERE playerid = ?");

            psChangeSkin.setInt(1, skinid);
            psChangeSkin.setInt(2, playerid);
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
            @CookieParam("token") String token, @FormDataParam("newHighScore") String newHighScoreTemp) {
        System.out.println("players/updateHighScore");
        try {
            if(token == null || newHighScoreTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            int newHighScore = Integer.parseInt(newHighScoreTemp);
            int playerid = PlayersController.identifyPlayer(token);

            PreparedStatement psUpdateHighScore = Main.db.prepareStatement("UPDATE Players SET highScore = ? WHERE playerid = ?");

            psUpdateHighScore.setInt(1, newHighScore);
            psUpdateHighScore.setInt(2, playerid);
            psUpdateHighScore.executeUpdate();

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to update high score. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("updateCurrency")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String playersUpdateCurrency(
            @CookieParam("token") String token, @FormDataParam("sessionCurrency") String sessionCurrencyTemp) {
        System.out.println("players/updateCurrency");
        try {
            if(token == null || sessionCurrencyTemp == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            int sessionCurrency = Integer.parseInt(sessionCurrencyTemp);
            int playerid = PlayersController.identifyPlayer(token);

            PreparedStatement psGetOldCurrency = Main.db.prepareStatement("SELECT currency FROM Players WHERE playerid = ?");

            psGetOldCurrency.setInt(1, playerid);
            ResultSet oldCurrencyResults = psGetOldCurrency.executeQuery();

            int oldCurrency = 0;
            while (oldCurrencyResults.next()) {
                oldCurrency = oldCurrencyResults.getInt(1);
            }

            //the old currency of the player and the currency that they just got in their last life are added together to form a new currency
            int newCurrency = oldCurrency + sessionCurrency;

            PreparedStatement psUpdateCurrency = Main.db.prepareStatement("UPDATE Players SET currency = ? WHERE playerid = ?");

            psUpdateCurrency.setInt(1, newCurrency);
            psUpdateCurrency.setInt(2, playerid);
            psUpdateCurrency.executeUpdate();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to update currency. Please see server console for more info.\"}";
        }
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String playersDelete(
            @CookieParam("token") String token, @FormDataParam("password") String password) {
        System.out.println("players/delete");
        try {
            if(token == null || password == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }
            int playerid = PlayersController.identifyPlayer(token);

            PreparedStatement psGetPassword = Main.db.prepareStatement("SELECT password FROM Players WHERE playerid = ?");

            psGetPassword.setInt(1, playerid);
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
                PreparedStatement psDeleteFromUnlockedSkins = Main.db.prepareStatement("DELETE FROM UnlockedSkins WHERE playerid = ?");
                psDeleteFromUnlockedSkins.setInt(1, playerid);
                psDeleteFromUnlockedSkins.executeUpdate();

                PreparedStatement psDeleteFromKills = Main.db.prepareStatement("DELETE FROM Kills WHERE playerid = ?");
                psDeleteFromKills.setInt(1, playerid);
                psDeleteFromKills.executeUpdate();

                PreparedStatement psDeleteFromDeaths = Main.db.prepareStatement("DELETE FROM Deaths WHERE playerid = ?");
                psDeleteFromDeaths.setInt(1, playerid);
                psDeleteFromDeaths.executeUpdate();

                PreparedStatement psDeleteFromPlayers = Main.db.prepareStatement("DELETE FROM Players WHERE playerid = ?");
                psDeleteFromPlayers.setInt(1, playerid);
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
            @FormDataParam("username") String username, @FormDataParam("password") String password) {
        System.out.println("players/login");
        try {
            if(username == null || password == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            //this sql statement checks whether the player with that username exists
            PreparedStatement psCheckUsername = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM Players WHERE username = ?)");
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
            PreparedStatement psGetPlayerid = Main.db.prepareStatement("SELECT playerid FROM Players WHERE username = ?");
            psGetPlayerid.setString(1, username);

            int playerid = 0;
            ResultSet playeridResults = psGetPlayerid.executeQuery();
            while (playeridResults.next()) {
                playerid = playeridResults.getInt(1);
            }

            //similar code the players/delete API
            PreparedStatement psGetPassword = Main.db.prepareStatement("SELECT password FROM Players WHERE playerid = ?");

            psGetPassword.setInt(1, playerid);
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

            PreparedStatement psUpdateToken = Main.db.prepareStatement("UPDATE Players SET token = ? WHERE playerid = ?");
            psUpdateToken.setString(1, token);
            psUpdateToken.setInt(2, playerid);
            psUpdateToken.executeUpdate();

            return "{\"token\": \"" + token + "\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to login. Please see server console for more info.\"}";
        }
    }

    @GET
    @Path("checkToken")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String playersCheckToken(
            @CookieParam("token") String token) {
        System.out.println("players/checkToken");
        try {
            if(token == null){
                return "{\"error\": \"Player is not logged in.\"}";
            }

            PreparedStatement psCheckToken = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM Players WHERE token = ?)");
            psCheckToken.setString(1, token);

            ResultSet tokenResults = psCheckToken.executeQuery();
            int exists = 0;
            while (tokenResults.next()) {
                exists = tokenResults.getInt(1);
            }

            if(exists==0){
                {
                    return "{\"error\": \"Player is not logged in.\"}";
                }
            }

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to check token. Please see server console for more info.\"}";
        }
    }

    @GET
    @Path("logout")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String playersLogout(
            @CookieParam("token") String token) {
        System.out.println("players/logout");
        try {
            if(token == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }

            PreparedStatement psCheckToken = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM Players WHERE token = ?)");
            psCheckToken.setString(1, token);

            ResultSet tokenResults = psCheckToken.executeQuery();
            int exists = 0;
            while (tokenResults.next()) {
                exists = tokenResults.getInt(1);
            }

            if(exists==0){
                {
                    return "{\"error\": \"Token does not exist.\"}";
                }
            }
            PreparedStatement psUpdateToken = Main.db.prepareStatement("UPDATE Players SET token = NULL WHERE token = ?");
            psUpdateToken.setString(1, token);
            psUpdateToken.executeUpdate();

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to logout. Please see server console for more info.\"}";
        }
    }
}
