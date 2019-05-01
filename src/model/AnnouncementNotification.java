package model;

import javafx.scene.image.Image;

public class AnnouncementNotification extends Notification {
	
	private boolean isCritical;
	private Image iconImage;
	
	public AnnouncementNotification(String message, boolean isCritical) {
		super(message);
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
}
