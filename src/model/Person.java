package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;

/**
 * This class represents a person from which both the user and librarian classes
 * are inherited from. It holds all the standard information to create a
 * profile.
 * 
 * @author Charles Day
 * @version 1.0
 */
public abstract class Person {

    /** The persons user name. */
    protected final String username;

    /** The persons first name. */
    private String firstName;

    /** The persons last name. */
    private String lastName;

    /** The persons phone number. */
    private String phoneNumber;

    /** The persons street address. */
    private String address;

    /** The persons post code. */
    private String postcode;

    /** The persons chosen avatar image. */
    private String avatarPath;
    
    private String lastLogin;

    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    /**
     * Creates a new Person object from the given arguments.
     * @param username The user name of this person.
     * @param firstName First Name of this person.
     * @param lastName Last name of this person.
     * @param phoneNumber Phone number of this person.
     * @param address Address of this person.
     * @param postcode Post code of this person.
     * @param avatarPath Path to the avatar image of this person.
     */
    public Person(String username, String firstName, String lastName,
    		String phoneNumber, String address,
            String postcode, String avatarPath, String lastLogin) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.postcode = postcode;
        this.avatarPath = avatarPath;
        this.lastLogin = lastLogin;
        System.out.println("Last login: "+lastLogin);
    }

    /**
     * Returns the user name of the person.
     * @return The user name of the person.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the first name of the person.
     * @return The first name of the person.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the person.
     * Updates the database as well.
     * @param firstName the new first name of the person.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        Person.updateDatabase("users", this.getUsername(), "firstName", firstName);
    }

    /**
     * Returns the last name of the person.
     * @return The new last name of the person.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the person.
     * Updates the database as well.
     * @param lastName The new last name of the person.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
        Person.updateDatabase("users", this.getUsername(), "lastName", lastName);
    }

    /**
     * Returns the phone number of the person.
     * @return The phone number of this person.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Return the lastLogin timestamp
     * @return lastLogin timestamp
     */
    public String getLastLogin() {
    	return lastLogin;
    }
    
    /**
     * Sets the phone number of the person.
     * Updates the database as well.
     * @param phoneNumber The new phone number of the person.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        Person.updateDatabase("users", this.getUsername(), "telephone", phoneNumber);
    }

    /**
     * Returns the address of the person.
     * @return The address of the person.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the person.
     * * Updates the database as well.
     * @param address The new address of the person.
     */
    public void setAddress(String address) {
        this.address = address;
        Person.updateDatabase("users", this.getUsername(), "address", address);
    }

    /**
     * Returns the post code of the person.
     * @return The post code of the person.
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * Sets the post code of the person.
     * Updates the database as well.
     * @param postCode new post code of this person.
     */
    public void setPostcode(String postCode) {
        this.postcode = postCode;
        Person.updateDatabase("users", this.getUsername(), "postcode", postCode);
    }

    /**
     * Returns the avatar the person has chosen.
     * @return The path of the avatar image of the person.
     */
    public String getAvatar() {
        return avatarPath;
    }

    /**
     * Sets the avatar path to the one the person has chosen.
     * Updates the database as well.
     * @param avatarPath New image path.
     */
    public void setAvatar(String avatarPath) {
        this.avatarPath = avatarPath;
        Person.updateDatabase("users", this.getUsername(), "avatarPath", avatarPath);
    }

    /**
     * Loads up a user or librarian from the database and returnes them casted as a Person.
     * @param userName The user name of the person we want to load.
     * @return either a librarian or a user from the database.
     */
    public static Person loadPerson(String userName) {
        try (Connection dbConnection = DBHelper.getConnection()){
            
            PreparedStatement sqlStatement = dbConnection
                .prepareStatement("SELECT COUNT(*) FROM users WHERE users.username = ?");
            sqlStatement.setString(1, userName);
            ResultSet persons = sqlStatement.executeQuery();

            //Selects the amount of people who are staff
            if (persons.getInt(1) == 1) {
                sqlStatement = dbConnection.prepareStatement("SELECT COUNT "
                		+ "(*) FROM staff WHERE username = ?");
                sqlStatement.setString(1, userName);
                persons = sqlStatement.executeQuery();

                //Loads up all staff users
                if (persons.getInt(1) == 1) {
                    sqlStatement = dbConnection.prepareStatement(
                        "SELECT * FROM users, staff WHERE users.username = "
                        + "staff.username and users.username = ?");
                    sqlStatement.setString(1, userName);
                    persons = sqlStatement.executeQuery();

                    String usernameResult = persons.getString(1);
                    String firstnameResult = persons.getString(2);
                    String lastnameResult = persons.getString(3);
                    String telephoneResult = persons.getString(4);
                    String addressResult = persons.getString(5);
                    String postcodeResult = persons.getString(6);
                    String pathResult = persons.getString(7);
                    int staffIDResult = persons.getInt(11);
                    String employmentDateResult = persons.getString(12);
                    String stamp = persons.getString(9);
                    
                    persons.close();
                    sqlStatement.close();
                    return new Librarian(usernameResult, firstnameResult, lastnameResult, telephoneResult,
                        addressResult, postcodeResult, pathResult, stamp, employmentDateResult,
                       staffIDResult);
                }
                else {
                    //Loads up normal users
                    sqlStatement = dbConnection.prepareStatement("SELECT * "
                    		+ "FROM users WHERE username = ?");
                    sqlStatement.setString(1, userName);
                    persons = sqlStatement.executeQuery();

                    String usernameResult = persons.getString(1);
                    String firstnameResult = persons.getString(2);
                    String lastnameResult = persons.getString(3);
                    String telephoneResult = persons.getString(4);
                    String addressResult = persons.getString(5);
                    String postcodeResult = persons.getString(6);
                    String pathResult = persons.getString(7);
                    String balanceResult = persons.getString(8);
                    String stamp = persons.getString(9);

                    persons.close();
                    sqlStatement.close();
                    return new User(usernameResult, firstnameResult, lastnameResult, telephoneResult, addressResult,
                        postcodeResult, pathResult, Double.parseDouble(balanceResult),stamp);
                }
            } else {
                persons.close();
                sqlStatement.close();
            }
        }
        catch (SQLException e) {
        	e.printStackTrace();
        }
        // By default return null.
        return null;
    }

    /**
     * Changes last login in the database
     */
    public void updateLogin() {
    	   Connection connectionToDb;
		try {
			connectionToDb = DBHelper.getConnection();
		
			 Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			 String stamp = timestamp.toString();
			
           PreparedStatement sqlStatement = connectionToDb.prepareStatement("UPDATE users SET lastLogin=? WHERE userName=?");
           sqlStatement.setString(1,stamp);
           sqlStatement.setString(2, getUsername());
           sqlStatement.executeUpdate();
           
           sqlStatement.close();
           connectionToDb.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
    }
    
    
    /**
     * Method that updates the table in the database specified, at the row 
     * with given username, and at the given column.
     * @param table The table where the change needs to be made.
     * @param username The user name that needs to be updated.
     * @param column The name of the field that will be updated.
     * @param data The new value of the field.
     * @return True if the database updates and false if it does not
     */
    protected static boolean updateDatabase(String table, String username, String column, String data) {
        try {
            Connection connectionToDB = DBHelper.getConnection();
            PreparedStatement sqlStatement = connectionToDB.prepareStatement("UPDATE " + 
                table + " SET " + column + " = ? WHERE username = ?");
            sqlStatement.setString(1, data);
            sqlStatement.setString(2, username);
            if (sqlStatement.executeUpdate() == 1) {
                connectionToDB.close();
                return true;
            }
            connectionToDB.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Removes the user from the database.
     * 
     * @param username The username of the user to remove.
     * @return True if it as removed, false if not.
     */
    public static boolean removePerson(String username) {
        try {
            if (!userBorrowing(username)) {

                Connection connectionToDB = DBHelper.getConnection();
                PreparedStatement sqlStatement = connectionToDB.prepareStatement("DELETE FROM userRequests WHERE userName= ?");
                sqlStatement.setString(1, username);
                sqlStatement.executeUpdate();
                sqlStatement = connectionToDB.prepareStatement("DELETE FROM users WHERE userName= ?");
                sqlStatement.setString(1, username);
                sqlStatement.executeUpdate();
                
                connectionToDB.close();
                return true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method that gets a user that is currently borrowing the resource.
     * @param username The user name of the person we are checking.
     * @return True if the given user has ever borrowed a resource, false if not.
     * @throws SQLException If the data base connection fails.
     */
    private static boolean userBorrowing(String username) throws SQLException {
        Connection connectionToDB = DBHelper.getConnection();
        PreparedStatement sqlStatement = connectionToDB.prepareStatement("SELECT" + 
            " * FROM borrowRecords WHERE username= ?");
        
        sqlStatement.setString(1, username);
        ResultSet results = sqlStatement.executeQuery();
        connectionToDB.close();
        
        if (results.next()) {
            return true;
        }
        return false;
    }
}
