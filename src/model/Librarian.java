package model;

import java.text.ParseException;
/* for future implementation */
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents a librarian of the library. A librarian is allowed to
 * create and edit a new resource, loan copy to a user, process any return
 * copies, and authorize a fine payment to a user. it has all the attributes
 * from a person class with the extends of employment date and staff number.
 * 
 * @author leezhinghang
 * @version 1.0
 */
public class Librarian extends Person {

    /** The employment date of the librarian. */
    private final Date employmentDate;

    /** The librarian's staff ID. */
    private final int staffID;

    /**
    * Creates a new librarian with the given data.
    * @param userName The user name of the librarian.
    * @param firstName The librarian's first name.
    * @param lastName The last name of the librarian.
    * @param phoneNumber The phone number of the librarian.
    * @param address The address of the librarian.
    * @param postcode The post code of the librarian.
    * @param avatarPath The path to the avatar image of the librarian.
    * @param timeStamp The time when this librarian last logged in.
    * @param employmentDate The date the librarian as employed.
    * @param staffID The ID of the librarian.
    */
    public Librarian(String userName, String firstName, String lastName, 
    		String phoneNumber, String address,
            String postcode, String avatarPath, String timeStamp, String employmentDate, 
            int staffID) {
        super(userName, firstName, lastName, phoneNumber, address, 
        		postcode, avatarPath, timeStamp);
        this.staffID = staffID;

        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(employmentDate);
        }
        catch (ParseException e) {
            System.out.println("Failed to load date.");
        }
        finally {
            this.employmentDate = date;
        }
    }

    /**
     * Loans a copy to the user.
     * 
     * @param copy Copy that a user has loaned
     * @param user The user who borrowes the copy.
     */
    public void loanCopy(Copy copy, User user) {
        copy.getResource().loanToUser(user);
    }

    /**
     * Handles the process of the return copies from the user.
     * 
     * @param user The user that returns the copy
     * @param copy The copy that has been borrowed by the user
     */
    public void processReturn(User user, Copy copy) {
        copy.getResource().processReturn(copy);
    }

    /**Authorises the payment of a fine.
     * @param user User that needs to make the payment.
     * @param amount The amount of the part of the fine being paid.
     */
    public void authorizeFinePayment(User user, double amount) {
        user.makePayment(amount);
    }
    
    /**
     * Returns the unique staff number of the librarian.
     * 
     * @return Staff number of the librarian
     */
    public int getStaffID() {
        return this.staffID;
    }

    /**
     * Return the date when the librarian was hired.
     * 
     * @return Employment date of the librarian.
     */
    public Date getEmploymentDate() {
        return this.employmentDate;
    }
}
