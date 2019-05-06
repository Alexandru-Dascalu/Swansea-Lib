package model;

import javafx.scene.layout.HBox;

public abstract class Notification {
	
	protected final String message;
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
	
	public abstract HBox getNotificationBox();
}
