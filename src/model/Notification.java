package model;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

public abstract class Notification {
	
    protected static final int IMAGE_HEIGHT = 150;
    protected static final int IMAGE_WIDTH = 90;
    private static final int TEXT_WRAP_WIDTH = 400;
    
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
        notificationBox.setSpacing(20);
        notificationBox.setStyle(getStyle());
        notificationBox.setAlignment(Pos.CENTER_LEFT);
        
        return notificationBox;
	}
	
	protected Text getMessageTextElement() {
	    Text messageText = new Text(message);
	    messageText.setWrappingWidth(TEXT_WRAP_WIDTH);
	    
	    return messageText;
	}
	
	protected Node createSpacer() 
    {
        final Region spacer =  new Region();
        spacer.setPrefHeight(IMAGE_HEIGHT);
        spacer.setPrefWidth(IMAGE_WIDTH);
        HBox.setHgrow(spacer, Priority.NEVER);
        return spacer;
    }
}
