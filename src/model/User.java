package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import application.AlertBox;
import application.ScreenManager;

/**
 * This class represents a user of the library, allowing them to borrow copies
 * of recourses and make payments towards any fines against them. It has all the
 * attributes and methods of the Person class with the inclusion of the account
 * balance behaviour.
 * 
 * @author Charles Day
 * @version 1.0
 */
public class User extends Person {

    /** The current account balance for this User. */
    private double accountBalance;

    /** All of the copies the user has taken out. */
    private ArrayList<Copy> copiesList = new ArrayList<Copy>();
    
    /**The list of events this user will be attending.*/
    private ArrayList<Integer> eventsList = new ArrayList<Integer>();
    
    /**A list of all notifications this user has ever had.*/
    private LinkedList<Notification> notifications = new LinkedList<>();
    
    /**A series of values representing the notification preferences of this user.
     * They are in the array in this order: new resource , request approval, 
     * new events, nearing events.*/
    private boolean[] notificationSettings;

    /**
     * Creates a new User object from the given arguments.
     * @param username user's username
     * @param firstName user's firstname
     * @param lastName user's lastname
     * @param phoneNumber user's phonenumber
     * @param address users' address
     * @param postcode user's postcode
     * @param avatarPath user's avatar
     * @param accountBalance users account balance
     * @param lastLogin The last time this user logged in.
     */
    public User(String username, String firstName, String lastName,
        String phoneNumber, String address, String postcode, String avatarPath,
        double accountBalance, String lastLogin) {
        super(username, firstName, lastName, phoneNumber, address, postcode,
            avatarPath, lastLogin);
        this.accountBalance = accountBalance;
        notificationSettings = null;
    }

    /**
     * Adds a copy of a resource that the user has withdrawn.
     * 
     * @param copy to be added
     */
    public void addBorrowedCopy(Copy copy) {
        this.copiesList.add(copy);
        //Updater not needed as copy already updates the database.
    }

    /**
     * Returns all copies that the user has currently withdrawn.
     * 
     * @return The list of all borrowed copies.
     */
    public ArrayList<Copy> getBorrowedCopies() {
        return copiesList;
    }

    /**
     * Method that gets the list of all requested resources.
     * @return the list of all requested resources 
     */
    public ArrayList<Resource> getRequestedResources() {

        ArrayList<Resource> requestedResource = new ArrayList<Resource>();

        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement sqlStatement = dbConnection.prepareStatement(
                "SELECT rID FROM requestsToApprove WHERE userName = ?");
            sqlStatement.setString(1, this.getUsername());
            ResultSet rs = sqlStatement.executeQuery();

            while (rs.next()) {
                requestedResource.add(Resource.getResource(rs.getInt("rID")));
            }
            
            dbConnection.close();
        }
        catch (SQLException e) {
            System.out.println("Cannot find requested requested resources.");
            e.printStackTrace();
        }

        return requestedResource;
    }

    
    /**
     * Removes a copy from the list of copies withdrawn.
     * 
     * @param copy to be removed
     */
    public void removeBorrowedCopy(Copy copy) {
        copiesList.remove(copy);
        // Updater not needed as copy already updates the database.
    }

    
    /**
     * Allows payments to be added to the account balance.
     * 
     * @param amount The amount the User has payed in pounds.
     */
    public void makePayment(double amount) {
        this.accountBalance += amount;
        Person.updateDatabase("users", this.getUsername(), "accountBalance",
            Double.toString(this.accountBalance));
    }

    
    /**
     * Returns the current account balance.
     * 
     * @return accountBalance The current account balance in pounds.
     */
    public double getAccountBalance() {
        return accountBalance;
    }
    
    /**
     * Gets the array of settings for receiving notifications for this user.
     * @return the notification settings of this user.
     */
    public boolean[] getNotificationSettings() {
        return notificationSettings;
    }
    
    /**
     * Reduces the users balance.
     * 
     * @param username The username in the database.
     * @param amount The amount to reduce by.
     * @throws SQLException The database could not update.
     */
    public static void reduceBalance(String username, double amount)
            throws SQLException {
        Connection connection = DBHelper.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "UPDATE users set accountBalance = accountBalance - ? WHERE username=?");
        statement.setString(2, username);
        statement.setDouble(1, amount);

        statement.executeUpdate();
    }
 
    /**
     * Checks the users balance.
     * 
     * @param username The user name in the database.
     * @param amount The amount to check it exceeds.
     * @return True f the user has enough in their balance, false if not.
     * @throws SQLException The database was unable to check.
     */
    public static boolean checkBalance(String username, double amount)
            throws SQLException {
        Connection connection = DBHelper.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "SELECT accountBalance FROM users where username=?");
        statement.setString(1, username);
        ResultSet results = statement.executeQuery();
        boolean balance = false;
        if (results.next()) {
        	
            balance =  results.getDouble("accountBalance") >= amount;
        }
        results.close();
        connection.close();
        return balance;
    }

    
    /**
     * A method that adds a balance to the user in the database.
     * @param username username of the user
     * @param value their balance
     * @return returns that it updated the database or false otherwise
     */
    public static boolean addBalance(String username, double value) {
        Connection dbConnection;
        try {
            dbConnection = DBHelper.getConnection();

            PreparedStatement sqlStatement2 = dbConnection.prepareStatement(
                "UPDATE users set accountBalance = accountBalance + ? WHERE username=?");
            sqlStatement2.setDouble(1, value);
            sqlStatement2.setString(2, username);

            int updates = sqlStatement2.executeUpdate();
            return updates >= 1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**Checks if any events that this user will be attending are within 3 days 
     * from the current date, and if so makes a new notification for this in 
     * the database, but only if the user notification setting allows so. 
     * Otherwise, it does nothing.*/
    public void checkForNearingEvents() {
        if(notificationSettings[3]) {
            LinkedList<Event> nearEvents = new LinkedList<>();
            try (Connection connectionToDB = DBHelper.getConnection();
                    PreparedStatement selectionStmt = connectionToDB.prepareStatement(
                    "SELECT title, details, date, maxAllowed FROM userEvents, events" +
                    " WHERE events.eID = userEvents.eID AND username=?")) {
                
                selectionStmt.setString(1, getUsername());
                ResultSet userEvents = selectionStmt.executeQuery();
                
                while(userEvents.next()) {
                    Event userEvent = new Event(userEvents.getString(1), userEvents.getString(2), 
                        userEvents.getString(3), userEvents.getInt(4));
                    
                    int daysUntil = userEvent.getDaysUntilEvent();
                    if(daysUntil < 4 && daysUntil > -1) {
                        nearEvents.add(userEvent);
                    }
                }
                
                userEvents.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
                AlertBox.showErrorAlert(e.getMessage());
                return;
            }
            
            for(Event event: nearEvents) {
                String eventMessage = EventNotification.getNearingEventMsg(event);
                String eventDate = event.getDateTime();
                
                int notificationID = Notification.getExistingNotificationID(eventMessage, eventDate);
                if(notificationID == -1) {
                    notificationID = EventNotification.makeNearingEventNotification(event);
                }
                
                if(!Notification.existUserNotification(notificationID, username)) {
                    Notification.makeUserNotification(notificationID, username);
                }
            }
        }
    }
    
    /**Checks if this user has any borrowed copies that are due to be returned 
     * in less than 3 days, and if so makes a notification for that copy in 
     * the database.*/
    public void checkImminentFines() {
        ArrayList<Copy> borrowedCopies = getBorrowedCopies();

        for (Copy copy : borrowedCopies) {
            if (copy.getDueDate() != null) {
                int daysUntilDue = copy.getDaysUntilDue();

                if (daysUntilDue < 3 && daysUntilDue > -1) {
                    String fineMessage = FineNotification.getFineMsg(copy.getResource(), daysUntilDue);
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                    String fineDate = dateFormatter.format(copy.getDueDate());
                    
                    int notificationID = Notification.getExistingNotificationID(fineMessage, fineDate);
                    
                    if(notificationID == -1) {
                        notificationID = FineNotification.makeNotification(copy, this);
                    }
                    
                    if(!Notification.existUserNotification(notificationID, username)) {
                        Notification.makeUserNotification(notificationID, username);
                    }
                }
            }
        }
    }
    
    /**
     * Gets the list of all events this user wants to attend.
     * @return a list of all events this user will go to.
     */
    public ArrayList<Integer> getUserEvents(){
    	return this.eventsList;
    }

    /**
     * Method that loads the users borrow history.
     * @return The list of all resources whose copies this user has ever borrowed
     * (also loads events).
     */
    public ArrayList<Resource> loadUserHistory() {

        ArrayList<Resource> borrowHistory = new ArrayList<Resource>();

        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement sqlStatement = dbConnection.prepareStatement(
                "SELECT borrowRecords.copyId, rId FROM borrowRecords, copies "
                + "WHERE username = ? " +
                    "AND copies.copyId = borrowRecords.copyId AND"
                    + " copies.keeper <> ?");
            sqlStatement.setString(1, username);
            sqlStatement.setString(2, username);
            ResultSet rs = sqlStatement.executeQuery();

            while (rs.next()) {
                System.out.println("Adding borrow History!");
                borrowHistory.add(Resource.getResource(rs.getInt("rID")));
            }
            
        }
        catch (SQLException e) {
            System.out.println("Failed to load user history;");
            e.printStackTrace();
        }
        return borrowHistory;
    }
    
    /**
     * Says if this user has any unread notifications.
     * @return True if the user has any unread notifications, false if not.
     */
    public boolean hasUnreadNotifications() {
        for(Notification n: notifications) {
            if(!n.isRead()) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Loads the copies the user is currently is borrowing and adds them to
     * this object's copy list.
     */
    public void loadUserCopies() {

        copiesList.clear();

        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement sqlStatement = dbConnection.prepareStatement("SELECT * FROM " + 
                "copies WHERE keeper = ?");
            sqlStatement.setString(1, username);
            ResultSet rs = sqlStatement.executeQuery();

            while (rs.next()) {
                copiesList.add(Resource.getResource(rs.getInt("rID"))
                    .getCopy(rs.getInt("copyID")));
            }
        }
        catch (SQLException e) {
            System.out.println("Failed to load copies into user.");
            e.printStackTrace();
        }
    }
    
    /**Loads the notifications of this user from the database and adds it to 
     * the list of user notifications.*/
    public void loadNotifications() {
        notifications.clear();

        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement selectStatement = dbConnection.prepareStatement(
                "SELECT message, image, date, seen FROM notification, " +
                 "userNotifications WHERE nID = id AND username = '" + username + "'");
                ResultSet notificationData = selectStatement.executeQuery()){

            while (notificationData.next()) {
                String message = notificationData.getString(1);
                String imagePath = notificationData.getString(2);
                String date = notificationData.getString(3);
                boolean isRead = notificationData.getBoolean(4);

                if (imagePath != null && date != null) {
                    notifications.add(new FineNotification(message, isRead,
                        date, imagePath));
                } else if (imagePath != null && date == null) {
                    notifications.add(new ResourceNotification(message, isRead,
                        imagePath));
                } else if (imagePath == null && date != null) {
                    notifications.add(new EventNotification(message, isRead, 
                        date));
                } else {
                    throw new IllegalStateException();
                }
            }
        } catch (SQLException e) {
            AlertBox.showErrorAlert(e.getMessage());
            e.printStackTrace();
        }
    }
    
   /**
    * Loads the notification settings of this user from the database.
    */
   public void loadNotificationSettings() {
       try (Connection dbConnection = DBHelper.getConnection();
               PreparedStatement selectStatement = dbConnection.prepareStatement(
               "SELECT * FROM userSettings WHERE userName = ?")) {
           
           selectStatement.setString(1, username);
           ResultSet settings = selectStatement.executeQuery();
           
           notificationSettings = new boolean[4];
           notificationSettings[0] = settings.getBoolean(2);
           notificationSettings[1] = settings.getBoolean(3);
           notificationSettings[2] = settings.getBoolean(4);
           notificationSettings[3] = settings.getBoolean(5);
           
           settings.close();
       } catch (SQLException e) {
           e.printStackTrace();
           AlertBox.showErrorAlert(e.getMessage());
       }
    }
   
    /**
     * Updates the user notification setting in the database, for the given
     * notification type.
     * 
     * @param notificationColumn The notification type whose setting is changed.
     * @param newValue the new value of the setting.
     * @throws IllegalArgumentException when the name of the column notification is
     *         not valid.
     */
    public void updateNotificationSetting(String notificationColumn, boolean newValue) {
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement updateStatement = dbConnection
                    .prepareStatement("UPDATE userSettings SET " + notificationColumn +
                        " = ? WHERE " + "userName = ?")) {

            updateStatement.setBoolean(1, newValue);
            updateStatement.setString(2, username);
            updateStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
        }

        switch (notificationColumn) {
            case "newResourcesSetting":
                notificationSettings[0] = newValue;
                break;
            case "requestApprvlSetting":
                notificationSettings[1] = newValue;
                break;
            case "newEventSetting":
                notificationSettings[2] = newValue;
                break;
            case "nearingEventSetting":
                notificationSettings[3] = newValue;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**Loads the events this user will attend from the database and returns them.
     * @return a list of events this user will attend.*/
    public ArrayList<Integer> loadUserEvents() {

        try (Connection dbConnection = DBHelper.getConnection();
                Statement selectStatement = dbConnection.createStatement()) {
            ResultSet userEvents = selectStatement.executeQuery("SELECT eID," +
                " username FROM userEvents WHERE username = '" + 
                ScreenManager.getCurrentUser().getUsername() + "'");

            while (userEvents.next()) {
                eventsList.add(userEvents.getInt(1));
            }

            userEvents.close();
        }
        catch (SQLException e) {
            System.out.println("Failed to load user events;");
            e.printStackTrace();
        }
        return eventsList;
    }


    /**
     * A method that checks if a user has any outstanding fines.
     * @return If they have outstanding fines.
     */
    public boolean hasOutstandingFines() {
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement sqlStatement = dbConnection.prepareStatement(
                "SELECT COUNT(*) FROM fines WHERE username = ? AND paid = 0;")) {
            
            sqlStatement.setString(1, this.getUsername());
            ResultSet rs = sqlStatement.executeQuery();
            
            if (rs.getInt(1) == 0) {
                rs.close();
                return false;
            }
            else {
                rs.close();
                return true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Boolean method that checks if the resource is being borrowed.
     * @param resource that is being borrowed
     * @return true if the resource is being borrowed, false otherwise
     */
    public boolean isBorrowing(Resource resource) {
        for (Copy copy : getBorrowedCopies()) {
            if (copy.getResource() == resource) {
                return true;
            }
        }
        return false;
    }

    /**
     * A method that gets a list of resources recommended for the user based on what they 
     * have previously borrowed.
     * @return the recommended resource based on the user
     */
    public ArrayList<Resource> getRecommendations() {
        
        ArrayList<Resource> borrowedResources;
        try (Connection connectionToDB = DBHelper.getConnection();
                PreparedStatement sqlStatement = connectionToDB.prepareStatement(
                "select copyID from borrowRecords where username= \"" +
                username + "\"");
                ResultSet borrowedCopiesID = sqlStatement.executeQuery()) {
            
            PreparedStatement selectRID = connectionToDB.prepareStatement("select rID from copies where copyID=?");
            borrowedResources = new ArrayList<>();
            
            while (borrowedCopiesID.next()) {
                int copyID = borrowedCopiesID.getInt("copyID");

                selectRID.setInt(1, copyID);
                ResultSet resourceIDResult = selectRID.executeQuery();

                /*
                 * Since copyID is the primary key of copies, the result to
                 * selecting rID will always have only one row.
                 */
                int resourceID = resourceIDResult.getInt("rID");
                System.out.println("rID: " + resourceID);
                Resource borrowedResource = Resource.getResource(resourceID);

                if (!borrowedResources.contains(borrowedResource)) {
                    borrowedResources.add(borrowedResource);
                }

                resourceIDResult.close();
            }
            
            selectRID.close();
                
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
            return null;
        }
        
        //The arraylist that stores the recommendation scores
        ArrayList<ResourceRecommender> resourceScores = new ArrayList<>();
        ArrayList<Resource> resources = Resource.getResources();
        for (Resource resource : resources) {
            if (!borrowedResources.contains(resource)) {
                ResourceRecommender resourceScore = new ResourceRecommender();
                resourceScore.setResource(resource);
                resourceScore.setBorrowedResources(borrowedResources);
                resourceScores.add(resourceScore);
            }
        }

        resourceScores.sort(null);

        //Arraylist that stores the recommended resource
        ArrayList<Resource> recommendedResource = new ArrayList<>();
        for (ResourceRecommender resourceScore : resourceScores) {
            if (resourceScore.calculateLikeness() > 0) {
                recommendedResource.add(resourceScore.getResource());
            }
        }

        return recommendedResource;
    }
    
    /**
     * Get the total load of borrowed copies by the user. Each borrowed copy 
     * weighs depending on the limit amount of that resource.
     * @return total load of borrowed resources.
     */
	public int getBorrowLoad() {
		int requestLimit = 0;
		for (int i = 0; i < copiesList.size(); i++) {
			requestLimit += copiesList.get(i).getResource().getLimitAmount();
		}
		
		return requestLimit;
	}
	
	/**
	 * Gets the list of all notifications of this user.
	 * @return a list of all notifications of this user.
	 */
	public List<Notification> getNotifications() {
	    return notifications;
	}
	
    /**
	 * Check of the user have over requested an item.
	 * @param resource The resource, that if allowed to be requested, would 
	 * make the borrow load of the user go over the limit.
	 * @return true if the user have over requested, false otherwise
	 */
	public boolean exceedLimit(Resource resource) {
		int availableRequest = resource.getLimitAmount() + this.getBorrowLoad();
		if (availableRequest > 5) {
			return true;
		} else {
			return false;
		}
	}
}
