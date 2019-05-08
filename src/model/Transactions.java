package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Holds all the payments a user has made.
 * 
 * @author Oliver Harris
 */
public class Transactions {

    private String username;
    private ArrayList<Payment> payments;
    private final String DATE_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    /**
     * Constructor for transactions.
     * 
     * @param username The username this transaction object is for.
     * @param payments The payments this user has done.
     */
    public Transactions(String username, ArrayList<Payment> payments) {
        this.username = username;
        this.payments = payments;

    }

    /**
     * Generate the transactions object for a username.
     * 
     * @param username The username in the database.
     * @return The transactions the user has.
     */
    public static Transactions getTransactions(String username) {
        ArrayList<Payment> payments = new ArrayList<Payment>();
        try  (Connection connection = DBHelper.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM transactions WHERE username = \"" +
                    username +"\"");
                ResultSet results = statement.executeQuery()) {
            
            while (results.next()) {
                payments.add(new Payment(results.getInt("transactionId"),
                    results.getString("username"), results.getFloat("paid"),
                    results.getString("dateTime")));

            }
            return new Transactions(username, payments);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets the username.
     * 
     * @return username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the list of payments this user has.
     * 
     * @return ArrayList of payments.
     */
    public ArrayList<Payment> getPayments() {
        return payments;
    }

}
