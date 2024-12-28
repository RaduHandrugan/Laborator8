import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection
{

    public static void main(String[] args)
    {
        String url = "jdbc:mysql://localhost:3306/lab8";
        String username = "root";
        String password = "radu2003";

        //testare daca a fost realizata conexiunea
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            System.out.println("Conexiunea cu baza de date a fost realizata cu succes!");


        } catch (SQLException e)
        {
            System.out.println("Eroare la conectarea la baza de date " + e.getMessage());
        }
    }
}
