package Controllers;

import Server.Main;
import org.json.simple.JSONArray;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    //the createPlayer function is needed for when the player is creating an account
    public static void createPlayer(String username, String password) {
        try {
            //UserID is auto-incrementing so it is not needed in the SQL statement
            //SkinID is assigned a default value in SQL so it is not needed in the SQL statement
            //High Score and Currency can be null, and will have to start off as null because the player will have neither when they create their account, therefore it is also not needed
            //the question marks are placeholders
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Players (Username, Password) VALUES (?,?)");

            //the checkPassword function is run so that the exception is caught if the password does not meet the constraints
            checkPassword(password);

            //the first parameter corresponds with the index of each question mark
            //the second parameter is a variable that replaces the question marks
            ps.setString(1, username);
            ps.setString(2, password);

            //this actually execute the SQL
            ps.executeUpdate();

        } catch (SQLException exception) {
            //this prints the database error caused by the exception, and the error code in case it isn't clear and needs to be researched
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        } catch(passwordConstraintsException exception){
            //this prints out the message that I created in the checkPassword function if the exception is caught
            System.out.println(exception.getMessage());
        }
    }

    //the readPlayers function is needed to print out all the data from the players table
    public static void readPlayers() {
        try {
            //rather than doing select * from, each individual field is in the SQL query, so I can print them out separately to test them
            PreparedStatement ps = Main.db.prepareStatement("SELECT PlayerID, Username, Password, HighScore, Currency, SkinID  FROM Players");

            //results is used to store all of the results of the query
            ResultSet results = ps.executeQuery();

            //because results contains all the data, it needs to be split up into rows to get each record from the table
            //this while loop returns false and stops the loop when there are no more records
            while (results.next()) {
                //the parameter corresponds with the index of the columns in the table
                int playerID = results.getInt(1);
                String username = results.getString(2);
                String password = results.getString(3);
                String highScore = results.getString(4);
                String currency = results.getString(5);
                int skinID = results.getInt(6);
                System.out.println("Player ID: " + playerID);
                System.out.println("Username:  " + username);
                System.out.println("Password: " + password);
                System.out.println("High Score: " + highScore);
                System.out.println("Currency: " + currency);
                System.out.println("SkinID: " + skinID);
                System.out.println();
            }
        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
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
