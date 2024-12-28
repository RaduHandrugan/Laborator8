import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MainApp
{
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        try (Connection connection = DatabaseConnection.getConnection())
        {
            boolean conectat = true;   //daca a fost conectata baza de date

            while (conectat)
            {
                System.out.println("\nMeniu:");
                System.out.println("1. Adauga persoana");
                System.out.println("2. Adauga excursie");
                System.out.println("3. Afiseaza persoane si excursii");
                System.out.println("4. Afiseaza excursiile unei persoane");
                System.out.println("5. Afiseaza persoane care au vizitat o destinatie");
                System.out.println("6. Afiseaza persoane care au facut excursii intr-un an");
                System.out.println("7. Sterge o excursie");
                System.out.println("8. Sterge o persoana");
                System.out.println("0. Iesire");
                System.out.print("Introduceti o optiune: ");

                int optiune = Integer.parseInt(scanner.nextLine());

                switch (optiune)
                {
                    case 1 -> adaugaPersoana(connection);
                    case 2 -> adaugaExcursie(connection);
                    case 3 -> afisarePersoaneSiExcursii(connection);
                    case 4 -> afisareExcursiiNume(connection, scanner);
                    case 5 -> afisPersDestinatie(connection, scanner);
                    case 6 -> afisPersAnul(connection, scanner);
                    //case 7 -> stergeExcursie(connection);  // nu functioneaza stergerea
                    //case 8 -> stergePersoana(connection);   // nu functioneaza stergerea
                    case 0 ->
                    {
                        conectat = false;
                        System.out.println("Iesire");
                    }
                    default -> System.out.println("Optiune invalida!");
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
//1
    private static void adaugaPersoana(Connection connection) throws SQLException
    {
        System.out.print("Nume: ");
        String nume = scanner.nextLine();
        System.out.print("Varsta: ");
        int varsta = Integer.parseInt(scanner.nextLine());

        String query = "INSERT INTO persoane (nume, varsta) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, nume);
            statement.setInt(2, varsta);
            statement.executeUpdate();
            System.out.println("Persoana a fost adaugata cu succes!");
        }
    }

    //2
    private static void adaugaExcursie(Connection connection) throws SQLException
    {
        System.out.print("ID persoana: ");
        int idPersoana = Integer.parseInt(scanner.nextLine());

        String verificaPersoana = "SELECT * FROM persoane WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(verificaPersoana))
        {
            statement.setInt(1, idPersoana);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next())
            {
                System.out.println("Persoana nu exista in table!");
                return;
            }
        }

        System.out.print("Destinatia: ");
        String destinatia = scanner.nextLine();
        System.out.print("Anul: ");
        int anul = Integer.parseInt(scanner.nextLine());

        String query = "INSERT INTO excursii (id_persoana, destinatia, anul) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setInt(1, idPersoana);
            statement.setString(2, destinatia);
            statement.setInt(3, anul);
            statement.executeUpdate();
            System.out.println("Excursie  adaugata cu succes!");
        }
    }

    //3
    private static void afisarePersoaneSiExcursii(Connection connection)
    {
        String query = """
        SELECT p.id, p.nume, p.varsta, e.destinatia, e.anul
  FROM persoane p
    LEFT JOIN excursii e ON p.id = e.id_persoana
        ORDER BY p.id;
        """;
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            int currentId = -1;
            while (resultSet.next())
            {
                int idPersoana = resultSet.getInt("id");
                String nume = resultSet.getString("nume");
                int varsta = resultSet.getInt("varsta");
                String destinatia = resultSet.getString("destinatia");
                int anul = resultSet.getInt("anul");

                if (idPersoana != currentId)
                {
                    System.out.println("\nPersoana: ID=" + idPersoana + ", Nume=" + nume + ", Varsta=" + varsta);
                    currentId = idPersoana;
                }

                if (destinatia != null)
                {
                    System.out.println("  Excursie: Destinatia=" + destinatia + ", Anul=" + anul);
                }
            }
        } catch (SQLException e)
        {
            System.out.println("Eroare la afisare. " + e.getMessage());
        }
    }


    //4
    private static void afisareExcursiiNume(Connection connection, Scanner scanner)
    {
        System.out.print("Introdu numele persoanei dorite: ");
        String nume = scanner.nextLine();

        String query = """
        SELECT e.destinatia, e.anul
        FROM persoane p
        JOIN excursii e ON p.id = e.id_persoana
        WHERE p.nume = ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, nume);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Excursiile pentru " + nume + ":");
            boolean areexcursie = false;

            while (resultSet.next()) {
                areexcursie = true;
                String destinatia = resultSet.getString("destinatia");
                int anul = resultSet.getInt("anul");
                System.out.println("  Destinatia: " + destinatia + ", Anul: " + anul);
            }

        } catch (SQLException e)
        {
            System.out.println("Eroare la afisare" );
        }
    }
//5
private static void afisPersDestinatie(Connection connection, Scanner scanner)
{
    System.out.print("Introdu destinatia: ");
    String destinatia = scanner.nextLine();

    String query = """
        SELECT DISTINCT p.nume, p.varsta
        FROM persoane p
        JOIN excursii e ON p.id = e.id_persoana
        WHERE e.destinatia = ?;
        """;

    try (PreparedStatement statement = connection.prepareStatement(query))
    {
        statement.setString(1, destinatia);
        ResultSet resultSet = statement.executeQuery();

        System.out.println("Persoanele care au vizitat " + destinatia + ":");
        boolean aVizitat = false;

        while (resultSet.next())
        {
            aVizitat = true;
            String nume = resultSet.getString("nume");
            int varsta = resultSet.getInt("varsta");
            System.out.println("  Nume: " + nume + ", Varsta: " + varsta);
        }

        if (!aVizitat)
        {
            System.out.println("  Nu exista persoane care au vizitat aceasta destinatie.");
        }
    } catch (SQLException e) {
        System.out.println("Eroare la afisarea persoanelor: " + e.getMessage());
    }
}

//6

    private static void afisPersAnul(Connection connection, Scanner scanner)
    {
        System.out.print("Introdu anul dorit: ");
        int anul = scanner.nextInt();

        String query = """
        SELECT DISTINCT p.nume, p.varsta
        FROM persoane p
        JOIN excursii e ON p.id = e.id_persoana
        WHERE e.anul = ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setInt(1, anul);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Persoanele care au facut excursii in anul " + anul + ":");
            boolean areAn = false;

            while (resultSet.next())
            {
                areAn = true;
                String nume = resultSet.getString("nume");
                int varsta = resultSet.getInt("varsta");
                System.out.println("  Nume: " + nume + ", Varsta: " + varsta);
            }

            if (!areAn) {
                System.out.println("  Nu exista persoane care au facut excursii in anul selectat");
            }
        } catch (SQLException e) {
            System.out.println("Eroare la afisare " + e.getMessage());
        }
    }

    //7
/*
    private static void stergeExcursie(Connection connection, Scanner scanner)
    {
        System.out.print("Introdu ID-ul excursiei de sters: ");
        int idExcursie = scanner.nextInt();
        scanner.nextLine();

        String query = "DELETE FROM excursii WHERE id_excursie = ";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idExcursie);
            int rowsDeleted = statement.executeUpdate();

                System.out.println("Excursie stearsa cu succes!");

        } catch (SQLException e)
        {
            System.out.println("Eroare " + e.getMessage());
        }
    }


 */

}
