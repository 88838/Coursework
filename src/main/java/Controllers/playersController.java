package Controllers;

import Server.Main;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Path ("players/")
public class playersController {

    //the checkPassword function is needed to check that the password follows the constraints set out in phase 1 design
    //this could not be done through SQL and therefore has to be done in Java
    public static void checkPassword(String password) throws passwordConstraintsException{

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
            throw new passwordConstraintsException("Abort due to constraint violation (Password must be bigger than 8 characters, contain an uppercase and lowercase letter, contain a digit)");
        }
    }

    //this class is needed to extend the Exception class that is part of the regular Java package
    //by extending the Exception class, a checked exception is defined which means it must be caught or thrown
    public static class passwordConstraintsException extends Exception {
        public passwordConstraintsException(String message){
            //the message parameter is passed into the super class, which is the Exception class
            super(message);
        }
    }

    //these annotations turn the method into an HTTP request handler
    @POST
    @Path("new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    //this method was previously 'createPlayer' however I have now renamed it to be the same format as the API path
    public String playerNew(
            @FormDataParam("Username") String username, @FormDataParam("Password") String password) {
        try {
            //the API needs to check whether the player has put a username and password, otherwise a player can't be created
            if(username == null || password == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request");
            }
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Players (Username, Password) VALUES (?,?)");

            checkPassword(password);

            ps.setString(1, username);
            ps.setString(2, password);

            ps.executeUpdate();

            return "{\"status\": \"OK\"}";

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            //this will show up on Git Bash because the console will provide more information through the exception message
            return "{\"error\": \"Unable to create new player, please see server console for more info.\"}";
        }
    }


    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    //the method has to be public so the Jersey library can interact with it
    //previously called 'readPlayers'
    public String playersList() {
        System.out.println("players/list");
        //he JSON is prepared using the 'Simple JSON' Library
        //a JSON array is created using a series of JSON objects with the values from the database
        JSONArray list = new JSONArray();
        try {
            //the JSON array can be split in Javascript for those different uses, rather than having different APIs for each attribute.
            PreparedStatement psKillInfo = Main.db.prepareStatement("SELECT Players.PlayerID, Kills.NumberOfKills, Kills.MonsterID FROM Players, Kills WHERE Players.PlayerID = Kills.PlayerID");  // info about the players' kills

            //results is used to store all of the results of the query
            ResultSet killInfoResults = psKillInfo.executeQuery();

            HashMap<Integer, ArrayList<JSONObject>> playerKills = new HashMap<>();
            while (killInfoResults.next()) {
                int PlayerID = killInfoResults.getInt(1);
                if (!playerKills.containsKey(PlayerID)) {
                    playerKills.put(PlayerID, new ArrayList<JSONObject>());
                }
                JSONObject killDetails = new JSONObject();
                killDetails.put("MonsterID", killInfoResults.getInt(3));
                killDetails.put("NumberOfKills", killInfoResults.getInt(2));
                playerKills.get(PlayerID).add(killDetails);
            }

            PreparedStatement psPlayerInfo = Main.db.prepareStatement("SELECT Players.PlayerID, Players.Username, Players.HighScore, Players.Currency, Players.SkinID FROM Players");  // info about the players

            ResultSet playerInfoResults = psPlayerInfo.executeQuery();

            //because results contains all the data, it needs to be split up into rows to get each record from the table
            //this while loop returns false and stops the loop when there are no more records
            while (playerInfoResults.next()) {
                JSONObject item = new JSONObject();
                int playerId = playerInfoResults.getInt(1);
                item.put("PlayerID", playerId);
                item.put("Username", playerInfoResults.getString(2));
                item.put("HighScore", playerInfoResults.getString(3));
                item.put("Currency", playerInfoResults.getString(4));
                item.put("SkinID", playerInfoResults.getString(5));
                item.put("Kills", playerKills.get(playerId));
                list.add(item);
            }
            //the method returns a string in terms of JSON
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error code: " + exception.getMessage());
            return "{\"error\": \"Unable to list players, please see server console for more info.\"}";

        }
    }

    //the updateUsername function is needed for when the player wants to change their username
    public static void updateUsername(int playerID, String username) {
        try {
            //the username can be updated depending on the PlayerID that is inputted
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET Username = ? WHERE PlayerID = ?");

            ps.setString(1, username);
            ps.setInt(2, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }

    //the updatePassword function is needed for when the player wants to change their password
    public static void updatePassword(int playerID, String password) {
        try {
            //the password can be updated depending on the PlayerID that is inputted
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET Password = ? WHERE PlayerID = ?");

            checkPassword(password);

            ps.setString(1, password);
            ps.setInt(2, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        } catch(passwordConstraintsException exception){
            System.out.println(exception.getMessage());
        }

    }

    //the updateSkin function is needed for when the player wants to change their current skin
    public static void updateSkin(int playerID, int skinID) {
        try {
            //the skin can be updated depending on the PlayerID that is inputted
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET SkinID = ? WHERE PlayerID = ?");

            ps.setInt(1, skinID);
            ps.setInt(2, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }

    //the updateHighScore function is needed each time the player dies so that their new high score can be saved
    public static void updateHighScore(int playerID, int highScore) {
        try {
            //the high score can be updated depending on the PlayerID that is inputted
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET HighScore = ? WHERE PlayerID = ?");

            ps.setInt(1, highScore);
            ps.setInt(2, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }

    //the updateCurrency function is needed each time the player dies so that the amount of stars that they collected in that life can be saved
    public static void updateCurrency(int playerID, int currency) {
        try {
            //the currency can be updated depending on the PlayerID that is inputted
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Players SET Currency = ? WHERE PlayerID = ?");

            ps.setInt(1, currency);
            ps.setInt(2, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }

    //the deletePlayer function is needed when the player wants to delete their account
    public static void deletePlayer(int playerID){
        try {
            //all the records referring to the PlayerID are deleted
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Players WHERE PlayerID = ?");

            ps.setInt(1, playerID);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }


}
