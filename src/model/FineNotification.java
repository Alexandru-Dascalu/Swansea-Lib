package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
	
    public static void makeNotification(Copy copy, User user) {
        int daysUntilDue = copy.getDaysUntilDue();
        String fineMessage = getFineMsg(copy.getResource(), daysUntilDue);
        
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        String fineDate = dateFormatter.format(copy.getDueDate());
        
        try {
            Connection dbConnection = DBHelper.getConnection();
            PreparedStatement selectStatement = dbConnection.prepareStatement("" +
                "SELECT * FROM notification WHERE message = ? AND date = ?");
            selectStatement.setString(1, fineMessage);
            selectStatement.setString(2, fineDate);
            ResultSet existingNotification = selectStatement.executeQuery();
            
            if (!existingNotification.next()) {
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                    "INSERT INTO notification (message, image, date) VALUES (?, ?, ?)");

                insertStatement.setString(1, getFineMsg(copy.getResource(), 
                    daysUntilDue));
                insertStatement.setString(2, copy.getResource().getThumbnail().
                    impl_getUrl());
                insertStatement.setString(3, dateFormatter.format(
                    copy.getDueDate()));
                insertStatement.executeUpdate();

                int notificationID = insertStatement.getGeneratedKeys().getInt(1);
                insertStatement = dbConnection.prepareStatement(
                    "INSERT INTO userNotifications VALUES (?, ?, false)");

                insertStatement.setInt(1, notificationID);
                insertStatement.setString(2, user.getUsername());
                insertStatement.executeUpdate();
            } else {
                int notificationID = existingNotification.getInt(1);
                selectStatement = dbConnection.prepareStatement("SELECT * FROM userNotifications WHERE nID = ? AND username = ?");
                selectStatement.setInt(1, notificationID);
                selectStatement.setString(2, user.getUsername());
                
                existingNotification = selectStatement.executeQuery();
                
                if(!existingNotification.next()) {
                    PreparedStatement insertStatement = dbConnection.prepareStatement(
                            "INSERT INTO userNotifications VALUES (?, ?, false)");

                    existingNotification.close();
                    selectStatement.close();
                    
                    while(!existingNotification.isClosed() && !selectStatement.isClosed()) {}
                    
                    insertStatement.setInt(1, notificationID);
                    insertStatement.setString(2, user.getUsername());
                    insertStatement.executeUpdate();
                }
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
	
	public HBox getNotificationBox() {
        HBox notificationBox = new HBox();
        notificationBox.setSpacing(30);
        notificationBox.getChildren().add(new ImageView(image));
        notificationBox.getChildren().add(new Text(message));
        notificationBox.getChildren().add(new Label("Fine date:"));
        notificationBox.getChildren().add(new Text(date));
        notificationBox.setStyle(getStyle());
        return notificationBox;
    }
}
