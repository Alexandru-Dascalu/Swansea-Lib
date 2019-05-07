package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import application.AlertBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ResourceNotification extends Notification {
	
	private final Image resourceImage;
	
	public static String getNewAdditionMsg(Resource resource) {
		return "A new " + getResourceType(resource) + " has been added "
				+ "since your last log in! " + resource.getTitle() + 
				" is now in the library!";
	}
	
	public static String getRequestApprvlMsg(Resource resource) {
		return "Your request to borrow " + resource.getTitle() + " has"
				+ " been approved! You are now borrowing said " + 
				getResourceType(resource) + ".";
	}
	
	public static void makeNewRsrcNotification(Resource resource) {
	    try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO notification (message, image) VALUES (?, ?)");

            insertStatement.setString(1,
                ResourceNotification.getNewAdditionMsg(resource));

            insertStatement.setString(2, resource.getThumbnailPath());
            insertStatement.executeUpdate();
            
            int notificationID = insertStatement.getGeneratedKeys().getInt(1);
            insertStatement = dbConnection.prepareStatement(
                "INSERT INTO userNotifications VALUES (?, ?, false)");

            for (String username : getNewNotificationUsers()) {
                insertStatement.setInt(1, notificationID);
                insertStatement.setString(2, username);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert("Because the SQLite database library we use " +
                    " for this program, notifications for this user" +
                    " could not be loaded (database locks up for no reason, says it is" +
                    " busy). Close the program and restart it to see your notifications.");
        }
	}
	
	public static void makeApprovalNotification(Resource resource) {
	    try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO notification (message, image) VALUES (?, ?)");
            insertStatement.setString(1,
                ResourceNotification.getRequestApprvlMsg(resource));

            insertStatement.setString(2, resource.getThumbnail().impl_getUrl());
            insertStatement.executeUpdate();
            
            int notificationID = insertStatement.getGeneratedKeys().getInt(1);
            insertStatement = dbConnection.prepareStatement(
                "INSERT INTO userNotifications VALUES (?, ?, false)");

            for (String username : getApprovalNotificationUsers()) {
                insertStatement.setInt(1, notificationID);
                insertStatement.setString(2, username);
                insertStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert("Because the SQLite database library we use " +
                    " for this program, notifications for this user" +
                    " could not be loaded (database locks up for no reason, says it is" +
                    " busy). Close the program and restart it to see your notifications.");
        }
	}
    
    public static ArrayList<String> getNewNotificationUsers() {
        ArrayList<String> notificationUsers = new ArrayList<>();
        
        Connection dbConnection;
        try {
            dbConnection = DBHelper.getConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(
                    "SELECT username FROM users");
            ResultSet usernames = insertStatement.executeQuery();
            
            while(usernames.next()) {
                notificationUsers.add(usernames.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
     
        return notificationUsers;
    }
    
    public static ArrayList<String> getApprovalNotificationUsers() {
        ArrayList<String> notificationUsers = new ArrayList<>();
        
        Connection dbConnection;
        try {
            dbConnection = DBHelper.getConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(
                    "SELECT username FROM users");
            ResultSet usernames = insertStatement.executeQuery();
            
            while(usernames.next()) {
                notificationUsers.add(usernames.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
     
        return notificationUsers;
    }
	
	public ResourceNotification(String message, boolean isRead, String imagePath) {
		super(message, isRead);
		this.resourceImage = new Image(imagePath, IMAGE_WIDTH, IMAGE_HEIGHT, true, true);
	}
	
	public Image getImage() {
		return resourceImage;
	}
	
	public String getStyle() {
		return "-fx-background-color: linear-gradient(#ff7f50, gold);";
	}
	
	public HBox getNotificationBox() {
	    HBox notificationBox = super.getNotificationBox();
	    notificationBox.getChildren().add(new ImageView(resourceImage));
	    notificationBox.getChildren().add(getMessageTextElement());
	    return notificationBox;
	}
	
	private static String getResourceType(Resource resource) {
	    String className = resource.getClass().getName();
	    className = className.substring(6);
	    return className;
	}
}
