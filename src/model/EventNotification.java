package model;

public class EventNotification extends Notification {
	
	private final String date;
	
	public static String getNewEventMsg(Event event) {
		return " A new event has been added: since your last login" 
				+ event.getTitle() + ".";
	}
	
	public static String getNearingEventMsg(Event event, int days) {
		if(days != 0) {
			return "You have one event that will be held in " + days + " days: " 
					+ event.getTitle() + ".";
		} else {
			return "You have one event that will be held tomorrow: " 
					+ event.getTitle() + ".";
		}
		
	}
	
	public EventNotification(String message, String date) {
		super(message);
		this.date = date;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getStyle() {
		return "-fx-background-color: deepskyblue;";
	}
}
