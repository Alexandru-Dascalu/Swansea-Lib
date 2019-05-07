package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import application.AlertBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class EventNotification extends Notification {
	
	private final String date;
	
	public static String getNewEventMsg(Event event) {
		return "A new event has been added since your last login: " 
				+ event.getTitle() + ".";
	}
	
	public static String getNearingEventMsg(Event event) {
		int days = event.getDaysUntilEvent();
		if(days > 1) {
			return "You have one event that will be held in " + days + " days: "
					+ event.getTitle() + ".";
		} else {
			return "You have one event that will be held tomorrow: " 
					+ event.getTitle() + ".";
		}
	}
	
	public static void makeNewEventNotification(Event event) {
	    try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(
                    "INSERT INTO notification (message, date) VALUES (?, ?)");
            insertStatement.setString(1, EventNotification.getNewEventMsg(event));
            
            insertStatement.setString(2, event.getDateTime());
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
            System.exit(-1);
        }
	}
	
	public static void makeNearingEventNotification(Event event) {
	    try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(
                    "INSERT INTO notification (message, date) VALUES (?, ?)");
            
            insertStatement.setString(1, EventNotification.getNearingEventMsg(event));
            
            insertStatement.setString(2, event.getDateTime());
            insertStatement.executeUpdate();
            
            int notificationID = insertStatement.getGeneratedKeys().getInt(1);
            insertStatement = dbConnection.prepareStatement(
                "INSERT INTO userNotifications VALUES (?, ?, false)");

            for (String username : getNearingNotificationUsers()) {
                insertStatement.setInt(1, notificationID);
                insertStatement.setString(2, username);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
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
	
	public static ArrayList<String> getNearingNotificationUsers() {
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
	
	public EventNotification(String message, boolean isRead, String date) {
		super(message, isRead);
		this.date = date;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getStyle() {
		return "-fx-background-color: deepskyblue;";
	}
	
	public HBox getNotificationBox() {
	    HBox notificationBox = super.getNotificationBox();
	    notificationBox.getChildren().add(createSpacer());
	    notificationBox.getChildren().add(getMessageTextElement());
        
        Label dateLabel = new Label("Event date:");
        dateLabel.setTextFill(Color.BLACK);
        dateLabel.setStyle("-fx-font-weight: bold;");
        notificationBox.getChildren().add(dateLabel);
        
        notificationBox.getChildren().add(new Text(date));
        return notificationBox;
    }
}
