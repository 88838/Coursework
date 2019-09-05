import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Skins {
    public static void readSkins(){
        try{
            PreparedStatement ps = Main.db.prepareStatement("SELECT SkinID, SkinName, ImageFile, Cost  FROM Skins");

            ResultSet results = ps.executeQuery();
            //returns false and stops the loop when there are no more records
            while (results.next()) {
                //the column index matches  the columns in the table
                int skinID = results.getInt(1);
                String skinName = results.getString(2);
                String imageFile = results.getString(3);
                int cost = results.getInt(4);
                System.out.println("SkinID: " + skinID);
                System.out.println("Skin Name:  " + skinName);
                System.out.println("Image File: " + imageFile);
                System.out.println("Cost: " + cost);
                System.out.println();
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
}
