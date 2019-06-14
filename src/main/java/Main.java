import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        openDatabase("testDatabase.db");
        select();
        insert();

        closeDatabase();
    }

    public static void select() {
        try {
            PreparedStatement ps = db.prepareStatement("SELECT UserID, FirstName, LastName, Username, Password FROM Users");

            ResultSet results = ps.executeQuery();
            while (results.next()) {
                //the numbers match the index of the columns in the table
                int userID = results.getInt(1);
                String firstName = results.getString(2);
                String lastName = results.getString(3);
                String username = results.getString(4);
                String password = results.getString(5);
                System.out.println("User ID: " + userID);
                System.out.println("Full Name:  " + firstName + " " + lastName);
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void insert() {
        Scanner sc = new Scanner(System.in);
        try {
            //the question marks are placeholders
            //user ID is auto-incrementing so it is not inserted
            PreparedStatement ps = db.prepareStatement("INSERT INTO Users (FirstName, LastName, Username, Password) VALUES (?,?,?,?)");

            //the question marks get set a value
            //the numbers do not match the index of the columns in the table, but instead match which question mark is set
            ps.setString(1, sc.nextLine());
            ps.setString(2, sc.nextLine());
            ps.setString(3, sc.nextLine());
            ps.setString(4, sc.nextLine());

            //this actually execute the SQL
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }


    //acts like global variable
    public static Connection db = null;

    private static void openDatabase(String dbFile){
        try{
            //loads the database
            Class.forName("org.sqlite.JDBC");

            //database settings
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);

            //opens database
            db = DriverManager.getConnection("jdbc:sqlite:resources/" + dbFile, config.toProperties());
            System.out.println("Database connection successfully established.");

        } catch (Exception exception){
            System.out.println("Database connection error: " + exception.getMessage());
        }
    }

    public static  void closeDatabase(){
        try{
            db.close();
            System.out.println("Disconnected from database.");
        }catch (Exception exception){
            System.out.println("database disconnection error: " + exception.getMessage());
        }
    }
}
