package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a copy of a particular resource within the system.
 * @author Joe Wright
 */
public class Copy implements Comparable<Copy> {

    private final Resource resource;
    private User borrower;
    private final int copyID;
    private int loanDuration;
    private Date borrowDate;
    private Date lastRenewal;
    private Date dueDate;

    /**
     * Makes a new copy with the given arguments as data.
     * @param resource The resource this copy belongs to.
     * @param copyID The ID of the new copy.
     * @param borrower The user borrowing the copy currently.
     * @param loanDuration The duration in days that a loan is valid, untill it
     * is renewed or until the user needs to bring it back.
     * @param borrowDate Date copy was borrowed.
     * @param lastRenewal Last date the loan was renewed.
     * @param dueDate Date by which this copy needs to be returned.
     */
    public Copy(Resource resource, int copyID, User borrower, int loanDuration,
            Date borrowDate, Date lastRenewal, Date dueDate) {
        this.resource = resource;
        this.borrower = borrower;
        this.copyID = copyID;

        this.loanDuration = loanDuration;
        this.borrowDate = borrowDate;
        this.lastRenewal = lastRenewal;
        this.dueDate = dueDate;
    }

    /**
     * Makes a new copy with the given arguments as data. The dates are initialised as null.
     * @param resource The resource this copy belongs to.
     * @param copyID The ID of the new copy.
     * @param borrower The user borrowing the copy currently.
     * @param loanDuration The duration in days that a loan is valid, until it
     * is renewed or until the user needs to bring it back.
     */
    public Copy(Resource resource, int copyID, User borrower, int loanDuration) {
        this.resource = resource;
        this.borrower = borrower;
        this.copyID = copyID;

        this.loanDuration = loanDuration;
        borrowDate = null;
        lastRenewal = null;
        dueDate = null;
    }

    /**
     * Sets the borrower variable and updates the database accordingly.
     * @param user The user that borrows this copy.
     */
    public void setBorrower(User user) {
        if (user != null) {
            // try {
            //     Connection dbConnection = DBHelper.getConnection();
            //     PreparedStatement preparedUpdateStatement = dbConnection
            //         .prepareStatement("INSERT INTO borrowRecords (copyId," +
            //         " username, description)" + " VALUES (?,?,?)");
            //
            //     preparedUpdateStatement.setInt(1, getCopyID());
            //     preparedUpdateStatement.setString(2, user.getUsername());
            //     preparedUpdateStatement.setString(3, "");
            //     preparedUpdateStatement.executeUpdate();
            //     dbConnection.close();
            // }
            // catch (SQLException e) {
            //     System.out.println("Failed to add copy borrow to borrowRecoreds.");
            //     e.printStackTrace();
            // }
        }

        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement preparedUpdateStatement = dbConnection
                .prepareStatement("UPDATE copies SET keeper = ? WHERE copyID = ?");
            if (user == null) {
                preparedUpdateStatement.setString(1, null);
            }
            else {
                preparedUpdateStatement.setString(1, user.getUsername());
            }
            preparedUpdateStatement.setInt(2, this.getCopyID());
            preparedUpdateStatement.executeUpdate();
            dbConnection.close();
        }
        catch (SQLException e) {
            System.out.println("Failed to update copies database.");
            e.printStackTrace();
        }

        this.borrower = user; // Do this last just in case the queries haven't worked.
    }

    /**
     * Method that gets the loan duration variable.
     *
     * @return The duration in days that a loan is valid, untill it
     * is renewed or until the user needs to bring it back.
     */
    public int getLoanDuration() {
        return loanDuration;
    }

    /**
     * Method that gets the resource variable.
     *
     * @return resource The resource this copy belongs to.
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Method that gets the COPY_ID final variable.
     *
     * @return the unique id of this copy.
     */
    public int getCopyID() {
        return copyID;
    }

    /**
     * Set loan duration variable and updates it in database.
     * @param duration The new loan duration, in days.
     */
    public void setLoanDuration(int duration) {
        updateDBValue(copyID, "loanDuration", duration);
        this.loanDuration = duration;
    }

    /**
     * Gets the due date variable.
     * @return due date by which the copy needs to be brought back.
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date according to the specification, such that it is tomorrow
     * or the earliest date at which the borrower has had this copy for the
     * entire loan duration.
     */
    public void setDueDate() {
        if (dueDate == null) {
            Date afterBorrowDuration = (Date) borrowDate.clone();
            afterBorrowDuration.setDate(afterBorrowDuration.getDate() +
            		loanDuration);
            Date today = new Date(System.currentTimeMillis());

            if (afterBorrowDuration.after(today)) {
                dueDate = afterBorrowDuration;
            }
            else {
                today.setDate(today.getDate() + 1);
                dueDate = today;
            }

            SimpleDateFormat normalDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            updateDBValue(copyID, "dueDate", normalDateFormat.format(dueDate));
        }
    }

    /**
     * Boolean method that checks if the renewal date is before current date, and
     * returns true if it is otherwise returns false, and sets the renewal date
     * forward in time by the loan duration.
     * @return true if renewal date is before todays date otherwise
     * returns false.
     */
    public boolean checkRenewal() {
        if (dueDate == null) {
            Date nextRenewal;

            if (lastRenewal != null) {
                nextRenewal = (Date) lastRenewal.clone();
            }
            else {
                nextRenewal = (Date) borrowDate.clone();
            }

            nextRenewal.setDate(nextRenewal.getDate() + loanDuration);
            Date today = new Date(System.currentTimeMillis());

            if (nextRenewal.before(today)) {
                lastRenewal = nextRenewal;

                SimpleDateFormat normalDateFormat = 
                		new SimpleDateFormat("dd/MM/yyyy");
                updateDBValue(copyID, "lastRenewal", 
                		normalDateFormat.format(lastRenewal));
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * Get method for borrow date variable.
     * @return The date this copy was borrowed.
     */
    public Date getBorrowDate() {
        return borrowDate;
    }

    /**
     * Sets borrow date variable and updates the database.
     * @param borrowDate Date the copy was borrowed.
     */
    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;

        SimpleDateFormat normalDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        updateDBValue(copyID, "borrowDate", normalDateFormat.format(borrowDate));
    }

    /**
     * Gets the borrower variable.
     * @return The user that borrows this copy.
     */
    public User getBorrower() {
        return this.borrower;
    }

    /**
     * Boolean method that checks if a copy is borrowed.
     * @return true is borrower is not null, false if it is null.
     */
    public boolean isBorrowed() {
        return borrower != null;
    }

    /**
     * Gets the last renewal date.
     * @return Last date the loan was renewed.
     */
    public Date getLastRenewal() {
        return lastRenewal;
    }

    /**
     * Method that resets all date variables if there is no borrower of a copy.
     * If it is currently borrowed, it throws an exception.
     * @throws IllegalArgumentException If the copy is still borrowed.
     */
    public void resetDates() throws IllegalArgumentException {
      //  if (borrower == null) {
            borrowDate = null;
            dueDate = null;
            lastRenewal = null;

            updateDBValue(copyID, "borrowDate", null);
            updateDBValue(copyID, "dueDate", null);
            updateDBValue(copyID, "lastRenewal", null);
      //  }
       // else {
       //     throw new IllegalStateException("You are trying to reset borrow," +
     //          " due and last renewal dates while this copy is still borrowed!");
    //    }

    }

    /**
     * Calculates how many days are left until this book is due.
     * @return the number of days until the book is due.
     */
    public int getDaysUntilDue() {
        long timeUntilDue = dueDate.getTime() - System.currentTimeMillis();
        return (int) TimeUnit.DAYS.convert(timeUntilDue, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Returns a string representation of this copy suitable to display to any
     * user browsing the library. For this reason, it only says the copy ID
     * and whether it is available or not and leaves the rest of the
     * information, which should not be publicly accessible.
     * @return Shortened representation of this copy suitable for public
     * viewing.
     */
    public String toString() {
        String copy = "CopyID: " + copyID + ", Available: ";

        String available;
        if (borrower == null) {
            available = "yes.";
        }
        else {
            available = "no.";
        }

        copy += available + "\n";
        return copy;
    }

    /**
     * Compares a copy date with another copy of that resource based on their due date.
     * @param otherCopy The other copy being compared to this one.
     * @return -1 if it is before another copy's due date, 1 if its after a another
     * copy's due date, otherwise return 0.
     */
    public int compareTo(Copy otherCopy) {
        if (borrowDate.before(otherCopy.getBorrowDate())) {
            return -1;
        }
        else if (borrowDate.after(otherCopy.getBorrowDate())) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * Gets the borrower but returns null if there isn't a borrower.
     * @return null if there is no borrower, borrowers username if there is a borrower.
     */
    public String getBorrowerIDSafely() {
        if (this.borrower == null) {
            return null;
        }
        else {
            return this.borrower.getUsername();
        }
    }

    /**
     * Updates the dabatabase value in the given field of the given copy.
     * @param copyID Unique ID of copy that is updated.
     * @param field The attribute of the copy that is updated.
     * @param data The new data the field will be set to.
     */
    private static void updateDBValue(int copyID, String field, String data) {
        try {
            Connection connectionToDB = DBHelper.getConnection();
            PreparedStatement sqlStatement = connectionToDB
                .prepareStatement("UPDATE copies " + "set " + field +
                " = ? WHERE copyID=?");

            sqlStatement.setString(1, data);
            sqlStatement.setInt(2, copyID);
            sqlStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the dabatabase value in the given field of the given copy.
     * @param copyID Unique ID of copy that is updated.
     * @param field The attribute of the copy that is updated.
     * @param data The new data the field will be set to.
     */
    private static void updateDBValue(int copyID, String field, int data) {
        try {
            Connection connectionToDB = DBHelper.getConnection();
            PreparedStatement sqlStatement = connectionToDB
                .prepareStatement("UPDATE copies " + "set " + field +
                " = ? WHERE copyID=?");

            sqlStatement.setInt(1, data);
            sqlStatement.setInt(2, copyID);
            sqlStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
