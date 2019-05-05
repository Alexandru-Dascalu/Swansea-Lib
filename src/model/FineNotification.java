package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javafx.scene.image.Image;

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
	
    public static void makeNotification(Copy copy) {
        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO notification (message, image, date) VALUES (?, ?, ?)");
            
            int daysUntilDue = copy.getDaysUntilDue();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            
            insertStatement.setString(1, getFineMsg(copy.getResource(), daysUntilDue));
            insertStatement.setString(2, copy.getResource().getThumbnail().impl_getUrl());
            insertStatement.setString(3, dateFormatter.format(copy.getDueDate()));
            insertStatement.executeUpdate();
            
            int notificationID = insertStatement.getGeneratedKeys().getInt(1);
            insertStatement = dbConnection.prepareStatement(
                "INSERT INTO userNotifications VALUES (?, ?, false)");

            for (String username : getFineNotificationUsers()) {
                insertStatement.setInt(1, notificationID);
                insertStatement.setString(2, username);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
	
    public static ArrayList<String> getFineNotificationUsers() {
        ArrayList<String> notificationUsers = new ArrayList<>();
        
        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement insertStatement = dbConnection
                .prepareStatement("SELECT username FROM users");
            ResultSet usernames = insertStatement.executeQuery();

            while (usernames.next()) {
                notificationUsers.add(usernames.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        return notificationUsers;
    }
    
	public FineNotification(String message, boolean isRead, String date, String imagePath) {
		super(message, isRead);
		this.date = date;
		image = new Image(imagePath, 30, 50, true, true);
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
}
