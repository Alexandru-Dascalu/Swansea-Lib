package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import application.AlertBox;
import application.ScreenManager;

/**
 * Event class object used for creating, updating and deleting event objects
 * both locally and within the database.
 * @author Kane
 * @version 1.0
 */
public class Event {
	
	private int ID;
	private String title;
	private String details;
	private String dateTime;
	private int maxAttending;
	
	private static int totalEventNo = 0; //total no. of events (tracks eventID).

	//upcoming events if user, or all events if librarian.
	private static ArrayList<Event> allEvents = new ArrayList<Event>();
	//all events user has joined to.
	private static ArrayList<Event> usersEvents = new ArrayList<Event>();

	/**
	 * Makes a new even with the given title, details, date and attending limit.
	 * @param title The title of the new event.
	 * @param details The description of the new event.
	 * @param date The date when the event will be held.
	 * @param maxAttending The limit of people that can attend this event.
	 */
	public Event(String title, String details, String date, int maxAttending) {
		this.title = title;
		this.details = details;
		this.dateTime = date;
		this.maxAttending = maxAttending;
	}
	
	/**
	 * Gets the total number of events the library has ever had.
	 * @return the number of events that have ever existed.
	 */
	public static int getTotalEventNo() {
		return totalEventNo;
	}

	/**
	 * Sets the total number of events the library has ever had.
	 * @param totalEventNo The total number of events.
	 */
	public static void setTotalEventNo(int totalEventNo) {
		Event.totalEventNo = totalEventNo;
	}
	
	/**
	 * Checks if date string is in the future or present/past.
	 * @param dateString date string to be checked.
	 * @return returns true if future date, false if otherwise.
	 */
	public static boolean checkFutureDate(String dateString) {
		try {
			LocalDateTime localDate = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime eventDate = LocalDateTime.parse(dateString, formatter);
			return eventDate.isAfter(localDate);
		} catch (DateTimeParseException e ) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Loads appropriate events from database. Users will see upcoming events they're
	 * currently not joined to in one table, and events they're joined to in another.
	 * Librarians will see all events.
	 */
    public static void loadEventsFromDB() {

        try (Connection connectionToDB = DBHelper.getConnection();
                Statement stmt = connectionToDB.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM events")) {

            allEvents.clear();
            usersEvents.clear();

            // if normal user, get all events IDs they're joined to.
            ArrayList<Integer> usersEventIDs = null;
            if (ScreenManager.getCurrentUser() instanceof User) {
                usersEventIDs = ((User) ScreenManager.getCurrentUser()).loadUserEvents();
            }

            while (rs.next()) {

                totalEventNo += 1;

                if (ScreenManager.getCurrentUser() instanceof User) {
                    // if event is upcoming and user isn't already joined to it
                    if (checkFutureDate(rs.getString(4)) &&
                        !(usersEventIDs.contains(rs.getInt(1)))) {
                        allEvents.add(new Event(rs.getString(2), rs.getString(3), rs.getString(4),
                            rs.getInt(5)));
                        allEvents.get(allEvents.size() - 1).setID(rs.getInt(1));
                        // if user is already joined to event
                    }
                    else if (usersEventIDs.contains(rs.getInt(1))) {
                        usersEvents.add(new Event(rs.getString(2), rs.getString(3), rs.getString(4),
                            rs.getInt(5)));
                        usersEvents.get(usersEvents.size() - 1).setID(rs.getInt(1));
                    }
                }
                else {
                    // add all events if librarian
                    allEvents.add(
                        new Event(rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)));
                    allEvents.get(allEvents.size() - 1).setID(rs.getInt(1));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
        }
    }
	
	/**
	 * Updates the given event in the database.
	 * @param event The event to be updated.
	 */
	public static void updateEventInDB(Event event) {
		try {
			Connection connectionToDB = DBHelper.getConnection();
	        Statement stmt = connectionToDB.createStatement();
	        stmt.executeUpdate("UPDATE events SET title = '" + event.title +
	        "', details = '" + event.details + "', date = '" + event.dateTime +
	        "', maxAllowed = " + event.maxAttending + " WHERE eID = " + event.ID);
	        connectionToDB.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds user/event relationship to database when a user joins to an event/
	 * @param username The user who has joined to an event.
	 * @param eventID The event ID.
	 */
	public static void addUserEventInDB(String username, int eventID) {
		try {
			Connection connectionToDB = DBHelper.getConnection();
	        PreparedStatement sqlStatement = connectionToDB.prepareStatement("INSERT INTO userEvents VALUES (?,?)");
	        sqlStatement.setInt(1, eventID);
	        sqlStatement.setString(2, username);
	        sqlStatement.execute();
	        connectionToDB.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a new event to the static arrayList of events.
	 * @param title Title of event
	 * @param details Details of event
	 * @param date date and time of event
	 * @param maxAllowed total number of people who can attend the event.
	 * @return An instance of the new event that was added.
	 */
	public static Event addEvent(String title, String details, String date, int maxAllowed) {
		Event newEvent = new Event(title, details, date, maxAllowed);
		allEvents.add(newEvent);
		return newEvent;
	}

	/**
	 * Returns Event ID.
	 * @return The event's ID attribute.
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Sets new ID for event.
	 * @param iD The event's new ID attribute.
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * Returns title of event.
	 * @return The event's title attribute.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets new title for event.
	 * @param title The event's new title attribute.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Returns details of new event.
	 * @return The event's details attribute.
	 */
	public String getDetails() {
		return details;
	}
	
	/**
	 * Sets details for new event.
	 * @param details The event's new details attribute.
	 */
	public void setDetails(String details) {
		this.details = details;
	}
	
	/**
	 * Returns date and time of event.
	 * @return The date and time attribute.
	 */
	public String getDateTime() {
		return dateTime;
	}
	
	/**
	 * Sets new date and time of event.
	 * @param date The event's new new date and time attribute.
	 */
	public void setDate(String date) {
		this.dateTime = date;
	}
	
	/**
	 * Returns max allowed attendants of event.
	 * @return The maxAttending attribute.
	 */
	public int getMaxAttending() {
		return maxAttending;
	}
	
	/**
	 * Sets new max allowed attendants for event.
	 * @param maxAttending The event's new maxAttending attribute.
	 */
	public void setMaxAttending(int maxAttending) {
		this.maxAttending = maxAttending;
	}
	
	/**
	 * Calculates the number of days until an event is to be held. If the event 
	 * is in the past, it returns a negative number.
	 * @return the number of days until the event will be held.
	 */
	public int getDaysUntilEvent() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date eventDate = null;
		try {
			eventDate = dateFormatter.parse(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		long timeUntilEvent = eventDate.getTime() - System.currentTimeMillis();
		return (int) TimeUnit.DAYS.convert(timeUntilEvent, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Returns ArrayList containing all events from DB
	 * available to the user.
	 * @return The allEvents ArrayList.
	 */
	public static ArrayList<Event> getAllEvents() {
		return allEvents;
	}

	/**
	 * Sets new ArrayList of events.
	 * @param newEvents New ArrayList of events.
	 */
	public static void setAllEvents(ArrayList<Event> newEvents) {
		allEvents = newEvents;
	}
	
	/**
	 * Returns all events that current user has enrolled in.
	 * @return The userEvents ArrayList.
	 */
	public static ArrayList<Event> getUserEvents() {
		return usersEvents;
	}

}
