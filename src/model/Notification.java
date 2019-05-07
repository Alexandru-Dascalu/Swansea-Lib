package model;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

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
	
	public HBox getNotificationBox() {
	    HBox notificationBox = new HBox();
        notificationBox.setSpacing(30);
        notificationBox.setStyle(getStyle());
        
        return notificationBox;
	}
}
