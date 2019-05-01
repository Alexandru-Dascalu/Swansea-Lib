package model;

import javafx.scene.image.Image;

public class ResourceNotification extends Notification {
	
	private final Image resourceImage;
	
	public static String getNewAdditionMsg(Resource resource) {
		return "A new " + resource.getClass().getName() + "has been added "
				+ "since your last log in! " + resource.getTitle() + 
				" is now in the library!";
	}
	
	public static String getRequestApprvlMsg(Resource resource) {
		return "Your request to borrow " + resource.getTitle() + " has"
				+ " been approved! You are now borrowing said " + 
				resource.getClass().getName() +".";
	}
	
	public ResourceNotification(String message, String imagePath) {
		super(message);
		this.resourceImage = new Image(imagePath, 30, 50, true, true);
	}
	
	public Image getImage() {
		return resourceImage;
	}
	
	public String getStyle() {
		return "-fx-background-color: gold;";
	}
}
