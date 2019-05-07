package model;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class AnnouncementNotification extends Notification {
	
	private boolean isCritical;
	private Image iconImage;
	
	public AnnouncementNotification(String message, boolean isRead, boolean isCritical) {
		super(message, isRead);
		this.isCritical = isCritical;
		
		if(isCritical) {
			iconImage = new Image("src\\graphics\\warning.png", 30, 50, true, true);
		} else {
			iconImage = new Image("src\\graphics\\info.png", 30, 50, true, true);
		}
	}
	
	public Image getIconImage() {
		return iconImage;
	}
	
	public String getStyle() {
		if(isCritical) {
			return "-fx-background-color: firebrickred;";
		} else {
			return "-fx-background-color: aquamarine;";
		}
	}
	
	public HBox getNotificationBox() {
	    HBox notificationBox = super.getNotificationBox();
        
        notificationBox.getChildren().add(getMessageTextElement());
        notificationBox.getChildren().add(new ImageView(iconImage));
        return notificationBox;
    }
}
