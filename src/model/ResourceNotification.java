package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	    
	    int notificationID;
	    try (Connection dbConnection = DBHelper.getConnection();
	            PreparedStatement insertStatement = dbConnection.prepareStatement(
	                    "INSERT INTO notification (message, image) VALUES (?, ?)")) {

            insertStatement.setString(1,
                ResourceNotification.getNewAdditionMsg(resource));
            insertStatement.setString(2, resource.getThumbnailPath());
            insertStatement.executeUpdate();
            
            notificationID = insertStatement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
	    
	    List<String> notificationUsers = getNewNotificationUsers();
	    try (Connection dbConnection = DBHelper.getConnection();
	            PreparedStatement insertStatement = dbConnection.prepareStatement(
	            "INSERT INTO userNotifications VALUES (?, ?, false)")) {
	        
	        for (String username : notificationUsers) {
                insertStatement.setInt(1, notificationID);
                insertStatement.setString(2, username);
                insertStatement.executeUpdate();
            }
	    } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public static void makeApprovalNotification(Resource resource) {
	    int notificationID;
	    try (Connection dbConnection = DBHelper.getConnection();
	            PreparedStatement insertStatement = dbConnection.prepareStatement(
	            "INSERT INTO notification (message, image) VALUES (?, ?)")) {
            
            insertStatement.setString(1,
                ResourceNotification.getRequestApprvlMsg(resource));

            insertStatement.setString(2, resource.getThumbnailPath());
            insertStatement.executeUpdate();
            
            notificationID = insertStatement.getGeneratedKeys().getInt(1);
            
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
            return;
        }
	    
	    List<String> notificationUsers = getApprovalNotificationUsers();
	    try (Connection dbConnection = DBHelper.getConnection();
	            PreparedStatement insertStatement = dbConnection.prepareStatement(
	            "INSERT INTO userNotifications VALUES (?, ?, false)")) {
	        
	        for (String username : notificationUsers) {
	                insertStatement.setInt(1, notificationID);
	                insertStatement.setString(2, username);
	                insertStatement.executeUpdate();
	        }
	    } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
        }
	}
    
    public static ArrayList<String> getNewNotificationUsers() {
        ArrayList<String> notificationUsers = new ArrayList<>();
        
        try (Connection dbConnection= DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                        "SELECT username FROM userSettings WHERE " +
                        "newResourcesSetting = true");
                ResultSet usernames = insertStatement.executeQuery()) {
            
            while(usernames.next()) {
                notificationUsers.add(usernames.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
     
        return notificationUsers;
    }
    
    public static ArrayList<String> getApprovalNotificationUsers() {
        ArrayList<String> notificationUsers = new ArrayList<>();
        
        try (Connection dbConnection= DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                        "SELECT username FROM userSettings WHERE " +
                        "requestApprvlSetting = true");
                ResultSet usernames = insertStatement.executeQuery()) {
            
            while(usernames.next()) {
                notificationUsers.add(usernames.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
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
		return "-fx-background-color: linear-gradient(gold, #a07000);\r\n" +
		       "-fx-padding: 8;\r\n" +
		       "-fx-border-color: #996a00;\r\n" + 
		       "-fx-border-width: 5;\r\n" +
		       "-fx-border-style: solid;";
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
