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
import javafx.scene.paint.Color;
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
        
        int notificationID;
        try (Connection dbConnection = DBHelper.getConnection(); 
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO notification (message, image, date) VALUES (?, ?, ?)")) {

            insertStatement.setString(1,
                getFineMsg(copy.getResource(), daysUntilDue));
            insertStatement.setString(2, copy.getResource().getThumbnailPath());
            insertStatement.setString(3,
                dateFormatter.format(copy.getDueDate()));
            insertStatement.executeUpdate();

            notificationID = insertStatement.getGeneratedKeys().getInt(1);
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert("Because the SQLite database library we use " +
                    " for this program, notifications for this user" +
                    " could not be loaded (database locks up for no reason, says it is" +
                    " busy). Close the program and restart it to see your notifications.");
            return -1;
        }
        
        return notificationID;
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
        return "-fx-background-color: linear-gradient(firebrick, #d81111);\r\n" +
               "-fx-padding: 8;\r\n" +
               "-fx-border-color: #6d0000;\r\n" + 
               "-fx-border-width: 5;\r\n" +
               "-fx-border-style: solid;";
    }
	
	public HBox getNotificationBox() {
        HBox notificationBox = super.getNotificationBox();
        notificationBox.getChildren().add(new ImageView(image));
        notificationBox.getChildren().add(getMessageTextElement());
        
        Label dateLabel = new Label("Imminent fine date:");
        dateLabel.setTextFill(Color.BLACK);
        dateLabel.setStyle("-fx-font-weight: bold;");
        
        notificationBox.getChildren().add(dateLabel);
        notificationBox.getChildren().add(new Text(date));
        
        System.out.println(notificationBox.getPrefWidth());
        return notificationBox;
    }
}
