package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.AlertBox;

public class EventNotification extends Notification {
	
	private final String date;
	
	public static String getNewEventMsg(Event event) {
		return "A new event has been added since your last login: " 
				+ event.getTitle() + ".";
	}
	
	public static String getNearingEventMsg(Event event) {
		int days = event.getDaysUntilEvent();
		if(days > 1) {
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
