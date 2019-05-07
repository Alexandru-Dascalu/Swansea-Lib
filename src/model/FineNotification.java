package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import application.AlertBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class FineNotification extends Notification {
	
	private String date;
	private Image image;
	
	public static String getFineMsg(Resource resource, int days) {
		return "Warning! You are about to receive a fine in " + days + " days for a "
				+ getClassName(resource) +" you are currently borrowing: "
				+ resource.getTitle() +".";
	}
	
	private static String getClassName(Resource resource) {
	    String className = resource.getClass().getName();
	    className = className.substring(6);
	    return className;
	}
	
    public static int makeNotification(Copy copy, User user) {
        int daysUntilDue = copy.getDaysUntilDue();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        
        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO notification (message, image, date) VALUES (?, ?, ?)");

            insertStatement.setString(1,
                getFineMsg(copy.getResource(), daysUntilDue));
            insertStatement.setString(2, copy.getResource().getThumbnailPath());
            insertStatement.setString(3,
                dateFormatter.format(copy.getDueDate()));
            insertStatement.executeUpdate();

            int notificationID = insertStatement.getGeneratedKeys().getInt(1);
            insertStatement.close();
            dbConnection.close();
            return notificationID;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
            return -1;
        }
    }
    
    public static void makeUserNotification(int notificationID, User user) {
        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO userNotifications VALUES (?, ?, false)");
            insertStatement.setInt(1, notificationID);
            insertStatement.setString(2, user.getUsername());
            
            insertStatement.executeUpdate();
            
            insertStatement.close();
            dbConnection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert("Because the SQLite database library we use " +
                "for this program is a piece of fucking garbage, notifications for this user" +
                " could not be loaded (database locks up for no reason, says it is" +
                " busy). Close the program and restart it to see your notifications.");
        }
    }
    
    public static int getExistingNotificationID(Copy copy, String fineMessage, String fineDate) {
        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement selectStatement = dbConnection.prepareStatement("" +
                    "SELECT * FROM notification WHERE message = ? AND date = ?");
            selectStatement.setString(1, fineMessage);
            selectStatement.setString(2, fineDate);
            ResultSet existingNotification = selectStatement.executeQuery();
            
            int id;
            if(existingNotification.next()) {
                 id = existingNotification.getInt(1);
            } else {
                id = -1;
            }
            
            existingNotification.close();
            selectStatement.close();
            dbConnection.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
            return Integer.MIN_VALUE;
        }
    }
    
    public static boolean existUserNotification(int notificationID, String userName) {
        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement selectStatement = dbConnection.prepareStatement(
                "SELECT * FROM userNotifications WHERE nID = ? AND username = ?");
            selectStatement.setInt(1, notificationID);
            selectStatement.setString(2, userName);
            
            ResultSet existingNotification = selectStatement.executeQuery();
            boolean alreadyExists =  existingNotification.next();
            
            existingNotification.close();
            selectStatement.close();
            dbConnection.close();
            
            return alreadyExists;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
            return false;
        } 
    }
    
	public FineNotification(String message, boolean isRead, String date, String imagePath) {
		super(message, isRead);
		this.date = date;
		image = new Image(imagePath, IMAGE_WIDTH, IMAGE_HEIGHT, true, false);
	}
	
	public String getDate() {
		return date;
	}
	
	public Image getImage() {
		return image;
	}
	
	public String getStyle() {
		return "-fx-background-color: firebrick;";
	}
	
	public HBox getNotificationBox() {
        HBox notificationBox = super.getNotificationBox();
        notificationBox.getChildren().add(new ImageView(image));
        
        notificationBox.getChildren().add(getMessageTextElement());
        notificationBox.getChildren().add(new Label("Fine date:"));
        notificationBox.getChildren().add(new Text(date));
        return notificationBox;
    }
}
