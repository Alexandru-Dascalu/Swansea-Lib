package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Notification {
	
	private final String message;
	
	public static void makeNewNotification(Resource resource, boolean newAddition) {
		try {
			Connection dbConnection = DBHelper.getConnection();
			PreparedStatement insertStatement = dbConnection.prepareStatement(
					"INSERT INTO notification (message, image, read) VALUES (?, ?, false)");
			
			if(newAddition) {
				insertStatement.setString(1, ResourceNotification.getNewAdditionMsg(resource));
			} else {
				insertStatement.setString(1, ResourceNotification.getRequestApprvlMsg(resource));
			}
			
			insertStatement.setString(2, resource.getThumbnail().impl_getUrl());
			insertStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void makeNewNotification(Event event, boolean newAddition) {
		try {
			Connection dbConnection = DBHelper.getConnection();
			PreparedStatement insertStatement = dbConnection.prepareStatement(
					"INSERT INTO notification (message, date, read) VALUES (?, ?, false)");
			
			if(newAddition) {
				insertStatement.setString(1, EventNotification.getNewEventMsg(event));
			} else {
				insertStatement.setString(1, EventNotification.getNearingEventMsg(event));
			}
			
			insertStatement.setString(2, event.getDateTime());
			insertStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Notification(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public abstract String getStyle();
}
