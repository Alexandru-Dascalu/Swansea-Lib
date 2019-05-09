package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.AlertBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * A notification about a new event or about an event being near to the current 
 * date. Event notification have a date.
 * @author Alexandru Dascalu
 */
public class EventNotification extends Notification {

    /** The date of the event for which this notification is made. */
    private final String date;

    /**
     * Makes a new EventNotification.
     * 
     * @param message The message of the new notification.
     * @param isRead Whether the notification has been marked read by the user.
     * @param date The date of the event for which the notification is made.
     */
    public EventNotification(String message, boolean isRead, String date) {
        super(message, isRead);
        this.date = date;
    }

    
    /**
     * Makes the message that should be displayed for a notification when the given
     * event has just been added.
     * 
     * @param event The new event for which this message is made.
     * @return a message for a new event notification.
     */
    public static String getNewEventMsg(Event event) {
        return "A new event has been added since your last login: " + event.getTitle() + ".";
    }

    /**
     * Makes the message that should be displayed for a notification when the given
     * event will happen in the near future.
     * 
     * @param event the event that happens in the near future.
     * @return the message for a nearing event notification.
     */
    public static String getNearingEventMsg(Event event) {
        int days = event.getDaysUntilEvent();
        if (days > 0) {
            return "You have one event that will be held in " + days + " days: " +
                event.getTitle() + ".";
        }
        else {
            return "You have one event that will be in less than a day: " + event.getTitle() + ".";
        }
    }

    /**
     * Makes a new notification in the database for a newly made event. It also
     * associates in the database the newly made notification with users with the
     * new event notification setting turned on.
     * 
     * @param event the new event for which a notification is made.
     */
    public static void makeNewEventNotification(Event event) {
        int notificationID;
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO notification (message, date) VALUES (?, ?)")) {
            insertStatement.setString(1, EventNotification.getNewEventMsg(event));

            insertStatement.setString(2, event.getDateTime());
            insertStatement.executeUpdate();

            notificationID = insertStatement.getGeneratedKeys().getInt(1);
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
            return;
        }

        List<String> notificationUsers = getNewEventNotificationUsers();
        for (String username : notificationUsers) {
            Notification.makeUserNotification(notificationID, username);
        }
    }

    /**
     * Makes a new notification in the database for an event a user is attending and
     * that is close to the current date. It also associates in the database the
     * newly made notification with users with the nearing event notification
     * setting turned on.
     * 
     * @param event the near future event for which a notification is made.
     * @return the unique ID of the notification created in the database.
     */
    public static int makeNearingEventNotification(Event event) {
        int notificationID;

        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO notification (message, date) VALUES (?, ?)")) {
            insertStatement.setString(1, EventNotification.getNearingEventMsg(event));

            insertStatement.setString(2, event.getDateTime());
            insertStatement.executeUpdate();

            notificationID = insertStatement.getGeneratedKeys().getInt(1);
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
            return -1;
        }

        return notificationID;
    }

    /**
     * Gets a list of users names from the database of users with the new event
     * notification setting turned on.
     * 
     * @return list of users names of users with the new event notification setting
     *         turned on.
     */
    public static ArrayList<String> getNewEventNotificationUsers() {
        ArrayList<String> notificationUsers = new ArrayList<>();

        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                    "SELECT userName FROM userSettings WHERE newEventSetting = true");
                ResultSet usernames = insertStatement.executeQuery()) {

            while (usernames.next()) {
                notificationUsers.add(usernames.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
            return null;
        }

        return notificationUsers;
    }

    /**
     * Gets a list of users names from the database of users with the nearing event
     * notification setting turned on.
     * 
     * @return list of users names of users with the neaering event notification
     *         setting turned on.
     */
    public static ArrayList<String> getNearingNotificationUsers() {
        ArrayList<String> notificationUsers = new ArrayList<>();

        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                "SELECT username FROM users");
                ResultSet usernames = insertStatement.executeQuery()) {

            while (usernames.next()) {
                notificationUsers.add(usernames.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
        }

        return notificationUsers;
    }

    /**
     * Gets the date of the event associated with this notification.
     * 
     * @return a string representing the date of the event associated with this
     *         notification.
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns a string that represents CSS properties which will be used when the
     * notification is displayed. It sets a blue background colour with a linear
     * gradient, padding, and a border with a blue colour and width.
     * 
     * @return A CSS string with the style properties used to display this
     *         notification.
     */
    public String getStyle() {
        return "-fx-background-color: linear-gradient(#1184aa, deepskyblue);\r\n" +
            "-fx-padding: 8;\r\n" + "-fx-border-color: #054256;\r\n" + "-fx-border-width: 5;\r\n" +
            "-fx-border-style: solid;";
    }

    /**
     * Makes a new empty HBox which will be used to display this notification. It
     * sets the padding, alignment, CSS style (based on getStyle()), spacing and
     * growth policy of the new HBox. It adds the message of the notification and
     * the event date to the HBox.
     * 
     * @return the new HBox to display this notification.
     */
    public HBox getNotificationBox() {
        HBox notificationBox = super.getNotificationBox();
        notificationBox.getChildren().add(createSpacer());
        notificationBox.getChildren().add(getMessageTextElement());

        Label dateLabel = new Label("Event date:");
        dateLabel.setTextFill(Color.BLACK);
        dateLabel.setStyle("-fx-font-weight: bold;");
        notificationBox.getChildren().add(dateLabel);

        notificationBox.getChildren().add(new Text(date));
        return notificationBox;
    }
}
