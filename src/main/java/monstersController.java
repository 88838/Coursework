import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class monstersController {

    public static void readMonsters(){
        try{
            PreparedStatement ps = Main.db.prepareStatement("SELECT MonsterID, MonsterName, MovementType, AttackType, ImageFile  FROM Monsters");

            ResultSet results = ps.executeQuery();
            //returns false and stops the loop when there are no more records
            while (results.next()) {
                //the parameter matches the index of the columns in the table
                int monsterID = results.getInt(1);
                String monsterName = results.getString(2);
                String movementType = results.getString(3);
                String attackType = results.getString(4);
                String imageFile = results.getString(5);
                int stageID = results.getInt(6);
                System.out.println("MonsterID: " + monsterID);
                System.out.println("Monster Name:  " + monsterName);
                System.out.println("Movement type: " + movementType);
                System.out.println("Attack type: " + attackType);
                System.out.println("Image file: " + imageFile);
                System.out.println("StageID: " + stageID);
                System.out.println();
            }
        } catch (SQLException exception) {
            System.out.println("Database error code " + exception.getErrorCode() + ": " + exception.getMessage());
        }
    }
}

