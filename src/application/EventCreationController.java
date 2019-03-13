package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DBHelper;
import model.Event;

public class EventCreationController {

	@FXML
	private TextField eventNameField;
	
	@FXML
	private TextArea eventDetailsField;
	
	@FXML
	private DatePicker datePickerField;
	
	@FXML
	private TextField maxAttendingField;
	
	@FXML
	private Button createEventButton;
	
	@FXML
	 public void initialize() {
		
		datePickerField.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.compareTo(today) < 0 );
			}
		});
		
		//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy");  
		//LocalDateTime now = LocalDateTime.now();
		//System.out.println(dtf.format(now));
		
	}
	
	public void createEvent() {
		
		try {

            Connection connectionToDB = DBHelper.getConnection();
            PreparedStatement sqlStatement = connectionToDB.prepareStatement("INSERT INTO events VALUES (?,?,?,?,?)");
            
            String eventDate = datePickerField.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String eventName = eventNameField.getText();
            String eventDetails = eventDetailsField.getText();
            int maxAttending = Integer.parseInt(maxAttendingField.getText());
            
            sqlStatement.setInt(1, Event.getAllEvents().size()+1);
            sqlStatement.setString(2, eventName);
            sqlStatement.setString(3, eventDetails);
            sqlStatement.setString(4, eventDate);
            sqlStatement.setInt(5, maxAttending);
            
            sqlStatement.execute();
            
            System.out.println("added event successfully!");
            
            Event.addEvent(eventName, eventDetails, eventDate, maxAttending);
            
            Stage stage = (Stage) createEventButton.getScene().getWindow();
    	    stage.close();
    
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

	}

}
