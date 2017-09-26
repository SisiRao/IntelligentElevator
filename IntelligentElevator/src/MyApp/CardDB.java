package MyApp;

import org.sqlite.JDBC;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by sorpaas on 11/23/16.
 */
public class CardDB {
    private Connection connection;

    public CardDB(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
    }

    public void clear() throws SQLException {
        String sqlCreate = "CREATE TABLE CARDS(ID VARCHAR(20) PRIMARY KEY NOT NULL, FLOOR INT NOT NULL)";

        Statement stmt = connection.createStatement();
        stmt.execute(sqlCreate);
        stmt.close();
    }

    public void addCard(String id, int floor) throws SQLException {
        String sqlInsert = "INSERT INTO CARDS(ID, FLOOR) VALUES (?, ?)";

        PreparedStatement stmt = connection.prepareStatement(sqlInsert);
        stmt.setString(1, id);
        stmt.setInt(2, floor);
        stmt.executeUpdate();
        stmt.close();
    }

    public void removeCard(String id) throws SQLException {
        String sqlRemove = "DELETE FROM CARDS WHERE ID=?";

        PreparedStatement stmt = connection.prepareStatement(sqlRemove);
        stmt.setString(1, id);
        stmt.executeUpdate();
        stmt.close();
    }

    public int checkCard(String id) throws SQLException {
        String sqlQuery = "SELECT FLOOR FROM CARDS WHERE ID=?";
        int floor = -1;
        PreparedStatement stmt = connection.prepareStatement(sqlQuery);
        stmt.setString(1, id);
        ResultSet result = stmt.executeQuery();
        if (!result.isClosed()) {
            floor = Integer.parseInt(result.getString("FLOOR"));
        }
        stmt.close();
        return floor;
    }

    public void insertFromFile(String filename) throws SQLException{
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();

            while (line != null) {
                String[] tmp = line.split(" ");
                this.addCard(tmp[0], Integer.parseInt(tmp[1]));
                line = br.readLine();
            }
        } catch (FileNotFoundException e){
           System.out.println("Sorry, file "+ filename +" does not exist!!");
        } catch (IOException e){
            System.out.println("Error occurred when reading the file");
        }
    }

    public String getAllMapping() throws SQLException{
        String sqlQuery = "SELECT * FROM CARDS";
        PreparedStatement stmt = connection.prepareStatement(sqlQuery);
        ResultSet result = stmt.executeQuery();
        String output = "";
        while (!result.isClosed() && result.next()) {
            output += result.getString("ID") + "," + result.getString("FLOOR") + " ";
        }
        stmt.close();
        return output;
    }

    public static void main(String [] args){
        try {
            CardDB db = new CardDB("jdbc:sqlite:C:\\Users\\LRX\\Desktop\\COMP4007\\IntelligentElevator\\CardDB\\test.db");
//            db.insertFromFile("C:\\Users\\LRX\\Desktop\\COMP4007\\IntelligentElevator\\CardDB\\test.txt");
            db.removeCard("010");
            System.out.println(db.getAllMapping());
            //db.clear();

            System.out.println(db.checkCard("002"));
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
