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
import javafx.scene.text.Text;

public abstract class Notification {
	
    protected static final int IMAGE_HEIGHT = 150;
    protected static final int IMAGE_WIDTH = 90;
    private static final int TEXT_WRAP_WIDTH = 400;
    
	protected final String message;
	private boolean isRead;
	
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
            AlertBox.showErrorAlert("Because the SQLite database library we use " +
                    " for this program, notifications for this user" +
                    " could not be loaded (database locks up for no reason, says it is" +
                    " busy). Close the program and restart it to see your notifications.");
        }
    }
    
    public static int getExistingNotificationID(String message, String date) {
        int id;
        
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement selectStatement = dbConnection.prepareStatement("" +
                "SELECT * FROM notification WHERE message = ? AND date = ?")) {
            selectStatement.setString(1, message);
            selectStatement.setString(2, date);
            ResultSet existingNotification = selectStatement.executeQuery();
            
            if(existingNotification.next()) {
                 id = existingNotification.getInt(1);
            } else {
                id = -1;
            }
            
            existingNotification.close();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert("Because the SQLite database library we use " +
                    " for this program, notifications for this user" +
                    " could not be loaded (database locks up for no reason, says it is" +
                    " busy). Close the program and restart it to see your notifications.");
            return Integer.MIN_VALUE;
        }
        
        return id;
    }
    
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
            AlertBox.showErrorAlert("Because the SQLite database library we use " +
                    " for this program, notifications for this user" +
                    " could not be loaded (database locks up for no reason, says it is" +
                    " busy). Close the program and restart it to see your notifications.");
            return false;
        } 
        
        return alreadyExists;
    }
    
	public Notification(String message, boolean isRead) {
		this.message = message;
		this.isRead = isRead;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isRead() {
	    return isRead;
	}
	
	public void setRead() {
	    if(isRead) {
	        throw new IllegalStateException("Called setRead when notification" +
	            " is already read!");
	    }
	    isRead = true;
	}
	
	public abstract String getStyle();
	
	public HBox getNotificationBox() {
	    HBox notificationBox = new HBox();
        notificationBox.setSpacing(20);
        notificationBox.setStyle(getStyle());
        notificationBox.setAlignment(Pos.CENTER_LEFT);
        notificationBox.setPadding(new Insets(0, 0, 0, 20));
        return notificationBox;
	}
	
	protected Text getMessageTextElement() {
	    Text messageText = new Text(message);
	    messageText.setWrappingWidth(TEXT_WRAP_WIDTH);
	    
	    return messageText;
	}
	
	protected Node createSpacer() 
    {
        final Region spacer =  new Region();
        //spacer.setPrefHeight(IMAGE_HEIGHT);
        //spacer.setPrefWidth(IMAGE_WIDTH);
        
        spacer.setMinHeight(IMAGE_HEIGHT/2);
        spacer.setMinWidth(IMAGE_WIDTH);
        HBox.setHgrow(spacer, Priority.NEVER);
        return spacer;
    }
}
