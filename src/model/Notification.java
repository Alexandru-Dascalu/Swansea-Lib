package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Notification {
	
	private final String message;
	private boolean isRead;
	
	public Notification(String message, boolean isRead) {
		this.message = message;
		this.isRead = isRead;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isRead() {
	    return isRead;
	}
	
	public void setRead() {
	    if(isRead) {
	        throw new IllegalStateException("Called setRead when notification" +
	            " is already read!");
	    }
	    isRead = true;
	}
	
	public abstract String getStyle();
}
