package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
                "INSERT INTO notification (message, image, date, read) VALUES (?, ?, ?, false)");
            
            int daysUntilDue = copy.getDaysUntilDue();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/mm/yyyy");
            
            insertStatement.setString(1, getFineMsg(copy.getResource(), daysUntilDue));
            insertStatement.setString(2, copy.getResource().getThumbnail().impl_getUrl());
            insertStatement.setString(3, dateFormatter.format(copy.getDueDate()));
            
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
	
	public FineNotification(String message, String date, String imagePath) {
		super(message);
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
