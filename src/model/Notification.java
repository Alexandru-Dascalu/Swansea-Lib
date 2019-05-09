package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.AlertBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A class that models a notification that users can have. A base notification
 *  has just a message, and can be read or not.
 * @author Alexandru Dascalu
 */
public abstract class Notification {
	
    /**The height of the image view an image associated with this notification
     *  has. Its value is {@value}.*/
    protected static final int IMAGE_HEIGHT = 200;
    
    /**The width of the image view an image associated with this notification
     *  has. Its value is {@value}.*/
    protected static final int IMAGE_WIDTH = 120;
    
    /**The wrap width of the text showing the message of the notification in 
     * the GUI. Its value is {@value}.*/
    private static final int TEXT_WRAP_WIDTH = 400;
    
    /**The message of this notification.*/
	protected final String message;
	
	/**A flag that shows if this notification has been read by the user.*/
	private boolean isRead;
	
	/**Associates the notification with the given id to the given user in
	 *  the database.
	 * @param notificationID The id of the notification that is sent to this 
	 *  user.
	 * @param username The name of the user to receive the notification.*/
	public static void makeUserNotification(int notificationID, String username) {
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO userNotifications VALUES (?, ?, false)")){
            
            insertStatement.setInt(1, notificationID);
            insertStatement.setString(2, username);
            
            insertStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
        }
    }
    
	/**
	 * Searches the database for a notification with the given message and date.
	 *  Notifications are considered to be identical if they have the same
	 *  message and date. So users do not get repeating notifications, we check
	 *  if a notification like we need already exists.
	 * @param message The message of a notification we search for.
	 * @param date The date of a notification we search for.
	 * @return The unique ID of an already existing notification that has the 
	 * given details, or -1 of no such notification exists in the database.
	 */
    public static int getExistingNotificationID(String message, String date) {
        int notificationId;
        
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement selectStatement = dbConnection.prepareStatement("" +
                "SELECT * FROM notification WHERE message = ? AND date = ?")) {
            selectStatement.setString(1, message);
            selectStatement.setString(2, date);
            ResultSet existingNotification = selectStatement.executeQuery();
            
            if(existingNotification.next()) {
                 notificationId = existingNotification.getInt(1);
            } else {
                notificationId = -1;
            }
            
            existingNotification.close();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
            return Integer.MIN_VALUE;
        }
        
        return notificationId;
    }
    
    /**
     * Checks in the database to see if a notification is already associated
     *  with a user or not.
     * @param notificationID The ID of the notification.
     * @param userName The user name of the user.
     * @return True if the user is already associated with the notification
     * in the database, false if not.
     */
    public static boolean existUserNotification(int notificationID, String userName) {
        boolean alreadyExists;
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement selectStatement = dbConnection.prepareStatement(
                "SELECT * FROM userNotifications WHERE nID = ? AND username = ?")){
            selectStatement.setInt(1, notificationID);
            selectStatement.setString(2, userName);
            
            ResultSet existingNotification = selectStatement.executeQuery();
            alreadyExists =  existingNotification.next();
            
            existingNotification.close();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
            return false;
        } 
        
        return alreadyExists;
    }
    
    /**
     * Makes a new notification.
     * @param message The message of the new notification.
     * @param isRead Whether the notification has been marked read by the user.
     */
	public Notification(String message, boolean isRead) {
		this.message = message;
		this.isRead = isRead;
	}
	
	/**
	 * Gets the message of this notification.
	 * @return the message of this notification.
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Says if this notification has been read or not.
	 * @return true if the notification has been read, false if not.
	 */
	public boolean isRead() {
	    return isRead;
	}
	
	/**Sets the isRead flag to true if it was false. If not, it throws an 
	 * exception.
	 * @throws IllegalArgumentException if this method is called when the 
	 * notification is already read.*/
	public void setRead() {
	    if(isRead) {
	        throw new IllegalStateException("Called setRead when notification" +
	            " is already read!");
	    }
	    isRead = true;
	}
	
	/**
	 * Returns a string that represents CSS properties which will be used when
	 *  the notification is displayed.
	 * @return A CSS string with the style properties used to display this 
	 * notification.
	 */
	public abstract String getStyle();
	
	/**
	 * Makes a new empty HBox which will be used to display this notification. 
	 * It sets the padding, alignment, CSS style (based on getStyle()), 
	 * spacing and growth policy of the new HBox.
	 * @return the new HBox to display this notification.
	 */
	public HBox getNotificationBox() {
	    HBox notificationBox = new HBox();
        notificationBox.setSpacing(20);
        notificationBox.setStyle(getStyle());
        notificationBox.setAlignment(Pos.CENTER_LEFT);
        notificationBox.setPadding(new Insets(0, 0, 0, 20));
        VBox.setVgrow(notificationBox, Priority.ALWAYS);
        return notificationBox;
	}
	
	/**
	 * Gets the an empty Text element which should be used to display the
	 *  message. It has the wrapping width set to TEXT_WRAP_WIDTH.
	 * @return an empty Text element with the wrapping width set.
	 */
	protected Text getMessageTextElement() {
	    Text messageText = new Text(message);
	    messageText.setWrappingWidth(TEXT_WRAP_WIDTH);
	    
	    return messageText;
	}
	
	/**
	 * Makes an empty node that can be used to space out elements in an HBox. 
	 * Used with event notifications so text is aligned with the other 
	 * notifications, since event notifications do not display images.
	 * Its height depends on the height of the ImageViews used to display
	 * resource images.
	 * @return an empty node used to space out elements in an HBox.
	 */
	protected Node createSpacer() 
    {
        final Region spacer =  new Region();
        
        spacer.setMinHeight(IMAGE_HEIGHT/2);
        spacer.setMinWidth(IMAGE_WIDTH);
        HBox.setHgrow(spacer, Priority.NEVER);
        return spacer;
    }
}
