package model;

import javafx.scene.image.Image;

public class FineNotification extends Notification {
	
	private String date;
	private Image image;
	
	public static String getFineMsg(Resource resource, int days) {
		return "Warning! You are about to receive a fine in " + days + " days for a "
				+ resource.getClass().getName() +" you are currently borrowing: "
				+ resource.getTitle() +".";
	}
	
	public FineNotification(String message, String date, String imagePath) {
		super(message);
		this.date = date;
		image = new Image(imagePath, 30, 50, true, true);
	}
	
	public String getDate() {
		return date;
	}
	
	public Image getImage() {
		return image;
	}
	
	public String getStyle() {
		return "-fx-background-color: firebrick;";
	}
}
