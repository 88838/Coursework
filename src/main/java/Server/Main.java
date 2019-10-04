package Server;

import Controllers.playersController;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.sqlite.SQLiteConfig;
import java.sql.*;

public class Main {

    /*public static void main(String[] args) {
        openDatabase("terminalVelocityDatabase.db");

        closeDatabase();
    }*/

    public static void main(String[] args) {

        openDatabase("terminalVelocityDatabase.db");

        ResourceConfig config = new ResourceConfig();
        config.packages("Controllers");
        config.register(MultiPartFeature.class);
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));

        Server server = new Server(8081);
        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addServlet(servlet, "/*");

        try {
            server.start();
            System.out.println("Server successfully started.");
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
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

    /*public static  void closeDatabase(){
        try{
            db.close();
            System.out.println("Disconnected from database.");
        }catch (Exception exception){
            System.out.println("Database disconnection error: " + exception.getMessage());
        }
    }*/
}
