package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DBHelper;
import model.Event;
import model.Notification;

/**
 * Controller class for Event Creation Scene.
 * @author Kane
 * @version 1.0
 *
 */
public class EventCreationController {

	@FXML
	private TextField eventNameField;
	
	@FXML
	private TextArea eventDetailsField;
	
	@FXML
	private DatePicker datePickerField;
	
	@FXML
	private TextField timePickerField;
	
	@FXML
	private TextField maxAttendingField;
	
	@FXML
	private Button createEventButton;
	
	/**
	 * Called when scene is started.
	 */
	@FXML
	 public void initialize() {
		
		//disables all dates before current date.
		datePickerField.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.compareTo(today) < 0 );
			}
		});
	}
	
	/**
	 * Called when "Create" button is clicked within Event Creation scene.
	 * Will create new event within the database and add event to allEvent array.
	 */
	public void createEvent() {
		
		try {

            Connection connectionToDB = DBHelper.getConnection();
            PreparedStatement sqlStatement = connectionToDB.prepareStatement("INSERT INTO events VALUES (?,?,?,?,?)");
            
            //get LocalDateTime object from datePickerField and timePickerField.
            LocalDateTime dateTime = LocalDateTime.of(datePickerField.getValue(), LocalTime.parse(timePickerField.getText()));
            String eventDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            //get event name, details and max attendant values from fields.
            String eventName = eventNameField.getText();
            String eventDetails = eventDetailsField.getText();
            int maxAttending = Integer.parseInt(maxAttendingField.getText());
            
            //set values in prepared statement.
            sqlStatement.setInt(1, Event.getTotalEventNo() + 1);
            sqlStatement.setString(2, eventName);
            sqlStatement.setString(3, eventDetails);
            sqlStatement.setString(4, eventDate);
            sqlStatement.setInt(5, maxAttending);
            
            //execute SQL insert and close connection.
            sqlStatement.execute();
            connectionToDB.close();
            
            //increment total event number.
            Event.setTotalEventNo(Event.getTotalEventNo() + 1);
            //add event to allEvents.
            Event newEvent = Event.addEvent(eventName, eventDetails, eventDate, maxAttending);
            Notification.makeNewNotification(newEvent, true);
            //close the stage.
            Stage stage = (Stage) createEventButton.getScene().getWindow();
    	    stage.close();
    
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DateTimeParseException e) {
        	//if invalid date/time, show error message.
        	Alert alert = new Alert(AlertType.WARNING, "Please enter a valid event time.", ButtonType.OK);
        	alert.show();
        	//if invalid max attendant number, show error message.
        } catch (NumberFormatException e) {
        	Alert alert = new Alert(AlertType.WARNING, "Please enter a valid number of spaces.", ButtonType.OK);
        	alert.show();
        }

	}

}
