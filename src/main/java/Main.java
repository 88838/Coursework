import org.sqlite.SQLiteConfig;
import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        openDatabase("testDatabase.db");

        System.out.println("1. New user");
        System.out.println("2. Display users");
        if(sc.nextInt()==1) {
            Users.createUser();
        }else if(sc.nextInt()==2){
            Users.displayUsers();
        }


        closeDatabase();
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
