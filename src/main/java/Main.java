import org.sqlite.SQLiteConfig;
import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {


        openDatabase("testDatabase.db");

        menu();

        closeDatabase();
    }
    public static void menu(){
        Scanner sc = new Scanner(System.in);
        int option;
        System.out.println("1. New player");
        System.out.println("2. Display players");
        System.out.println("3. Update player");
        System.out.println("4. Delete player");
        option = sc.nextInt();
        if(option==1) {
            Players.createPlayer();
        }else if(option==2){
            Players.displayPlayers();
        }else if(option==3){
            Players.updatePlayer();
        }else if(option==4){
            Players.deletePlayer();
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
