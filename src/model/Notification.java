package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Notification {
	
	private final String message;
	
	public static void makeNewNotification(Resource resource) {
		try {
			Connection dbConnection = DBHelper.getConnection();
			PreparedStatement insertStatement = dbConnection.prepareStatement(
					"INSERT INTO notification (message, image) VALUES (?, ?)");
			
			insertStatement.setString(1, ResourceNotification.getNewAdditionMsg(resource));
			insertStatement.setString(2, resource.getThumbnail().impl_getUrl());
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
