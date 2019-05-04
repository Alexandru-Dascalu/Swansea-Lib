package model;

import javafx.scene.image.Image;

public class ResourceNotification extends Notification {
	
	private final Image resourceImage;
	
	public static String getNewAdditionMsg(Resource resource) {
		return "A new " + getResourceType(resource) + " has been added "
				+ "since your last log in! " + resource.getTitle() + 
				" is now in the library!";
	}
	
	public static String getRequestApprvlMsg(Resource resource) {
		return "Your request to borrow " + resource.getTitle() + " has"
				+ " been approved! You are now borrowing said " + 
				getResourceType(resource) + ".";
	}
	
	public ResourceNotification(String message, boolean isRead, String imagePath) {
		super(message, isRead);
		this.resourceImage = new Image(imagePath, 30, 50, true, true);
	}
	
	public Image getImage() {
		return resourceImage;
	}
	
	public String getStyle() {
		return "-fx-background-color: gold;";
	}
	
	private static String getResourceType(Resource resource) {
	    String className = resource.getClass().getName();
	    className = className.substring(6);
	    return className;
	}
}
