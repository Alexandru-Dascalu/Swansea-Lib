package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class represents a fine. Holding all the information about the fine
 * 
 * @author Oliver Harris
 */
public class Fine {

    private double amount;
    private String dateTime;
    private int resource;
    private int daysOver;
    private int fineId;
    private String username;
    private boolean isPaid;

    /**Creates a new fine with the given arguments.
     * @param amount The amount owed.
     * @param dateTime The date and time stamp.
     * @param user The user who owns this fine.
     * @param resource The resource that caused this fine.
     * @param daysOver Number of days over due.
     * @param fineId The fineId in the database.
     * @param isPaid If the fine has been paid fully.
     */
    public Fine(double amount, String dateTime, String user, int resource,
    		int daysOver, int fineId, boolean isPaid) {
        this.amount = amount;
        this.username = user;
        this.dateTime = dateTime;
        this.resource = resource;
        this.daysOver = daysOver;
        this.fineId = fineId;
        this.isPaid = isPaid;
    }

    /**
     * Generate the fines for a user.
     * 
     * @param username The username to find the fines for.
     * @return The ArrayList of fines for the username.
     */
    public static ArrayList<Fine> getFines(String username) {
        ArrayList<Fine> fines = new ArrayList<Fine>();
        try {
            Connection connection = DBHelper.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT *" + 
            " FROM fines WHERE username=?");
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                boolean finePaid;
                if (results.getInt("paid") == 1) {
                    finePaid = true;
                }
                else {
                    finePaid = false;
                }

                fines.add(
                    new Fine(results.getDouble("amount"), 
                    		results.getString("dateTime"), results.getString("username"),
                        results.getInt("rID"), results.getInt("daysOver"), 
                        results.getInt("fineID"), finePaid));
            }
            return fines;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all the fines in the database.
     * 
     * @return The ArrayList of all fines.
     */
    public static ArrayList<Fine> getFines() {
        ArrayList<Fine> fines = new ArrayList<Fine>();
        try (Connection connection = DBHelper.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM fines");
                ResultSet results = statement.executeQuery()) {
            
            while (results.next()) {
                boolean finePaid;
                if (results.getInt("paid") == 1) {
                    finePaid = true;
                }
                else {
                    finePaid = false;
                }

                fines.add(
                    new Fine(results.getDouble("amount"), 
                    		results.getString("dateTime"), 
                    		results.getString("username"),
                        results.getInt("rID"), results.getInt("daysOver"), 
                        results.getInt("fineID"), finePaid));
            }
            return fines;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return if the fine has been fully paid.
     * 
     * @return If it has been paid.
     */
    public boolean isPaid() {
        return isPaid;
    }

    /**
     * Change if the fine has been paid.
     * @param isPaid True if the fine has been paid, false if not.
     */
    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
        try {
            Connection connection = DBHelper.getConnection();
            int paidInt = isPaid ? 1 : 0; // replace true with 1, 0 with false
            PreparedStatement pstmt = connection.prepareStatement("UPDATE fines "
            + "set paid = ? WHERE fineId = ?");
            pstmt.setInt(1, paidInt);
            pstmt.setInt(2, fineId);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the amount owed from this fine.
     * 
     * @return The amount owed.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Get the date and time this fine was made.
     * 
     * @return The date and time stamp.
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Get the resource that caused this fine.
     * 
     * @return The resource id.
     */
    public int getResource() {
        return resource;
    }

    /**
     * Get the username that owns this fine.
     * 
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the resource name.
     * 
     * @return the resource name.
     */
    public String getResourceName() {
        return Resource.getResource(resource).getTitle();

    }

    /**
     * Get the number of days the fine is over due.
     * 
     * @return The number of days it is overdue.
     */
    public int getDaysOver() {
        return daysOver;
    }

    /**
     * Get the fines database id.
     * 
     * @return The fine id.
     */
    public int getFineId() {
        return fineId;
    }

    /**
     * Check if this fine contains a value in the date time stamp.
     * 
     * @param search The value to check against.
     * @return If this fine contains the search value.
     */
    public boolean contains(String search) {
        if (this.getDateTime().contains(search)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Check if this fine contains a value in the username.
     * 
     * @param search The value to check against.
     * @return If this fine contains the search value.
     */
    public boolean containsUser(String search) {
        if (this.getUsername().toUpperCase().contains(search.toUpperCase())) {
            return true;
        }
        else {
            return false;
        }
    }
}
