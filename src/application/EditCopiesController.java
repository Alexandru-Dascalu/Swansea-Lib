package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Copy;
import model.DBHelper;
import model.Fine;
import model.Payment;
import model.Resource;
import model.Transactions;
import model.User;

/**
 * EditCopiesController is a class that creates a GUI for the librarian to edit
 * the copies table.
* @author Oliver Harris.
*
*/
public class EditCopiesController {

	private ArrayList<Copy> copies; //arraylist of the copies
	ObservableList<Copy> copyData = FXCollections.observableArrayList();//arraylist of the copy data
	
	@FXML
	private TextField loanDur; //textbox which holds the loan duration
	
	@FXML
	private TableView<Copy> copiesTable;//table for copies
	
	/**Resource whose copies we want to edit.*/
	private Resource resource;
	
	/**
	 * Calls the setup copy method when the program starts.
	 */
	@FXML
	 public void initialize() {
		setupCopy();
	}
	
	/**
	 * Changes the resource whose copies are edited through this controller.
	 * @param resource The new resource whose copies will be edited.
	 */
	public void setResource(Resource resource)
	{
		this.resource = resource;
	}
	
	/**
	 * Method that calls  creates the table.
	 */
	private void setupCopy() {
		
		repop();
		//create the table
		TableColumn<Copy, String> idCol = new TableColumn<Copy, String>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("copyID"));

		TableColumn<Copy, Number> loanDurCol = 
				new TableColumn<Copy, Number>("Duration");
		loanDurCol.setCellValueFactory(new PropertyValueFactory<>("loanDuration"));
		
		//generates rows of copyid and loanduration for each copy
		copiesTable.setItems(copyData);
		
		copiesTable.setRowFactory( tv -> {
			TableRow<Copy> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					Copy rowData = row.getItem();
					updateDuration(rowData);
				}
			});
			return row ;
		});

		//adds the columns to the tables and auto sizes them
		copiesTable.getColumns().addAll(idCol,loanDurCol);
		copiesTable.autosize();
	}
	
	/**
	 * Repopulates the copies table.
	 */
	private void repop() {
		resource.loadCopyList();
		copies = resource.getCopies();
		copyData = FXCollections.observableArrayList();
		for (Copy copy : copies) {
		
			copyData.add(copy);
		}
		
		copiesTable.setItems(copyData);

	}
	
	/**
	 * Removes the copy from the table.
	 * @param event the event of the button being clicked.
	 */
	@FXML
	private void deleteButton(ActionEvent event) {
		Copy copy = copiesTable.getSelectionModel().getSelectedItem();
		if(copy.getBorrower() != null) {
			AlertBox.showInfoAlert("Copy is being borrowed");
		}else {
			resource.removeCopy(copy);
			repop();
		}
	}
	
	/**
	 * Adds the copy to the table.
	 * @param event the event of the button being clicked.
	 */
	@FXML
	private void addCopy(ActionEvent event) {
		String duration = loanDur.getText();
		boolean goAhead = true;
		//converts the duration to text, if there is a format error it throws an exception and declines the add copy.
		try {
			Integer.parseInt(duration);
		} catch (NumberFormatException e){
			goAhead = false;
			AlertBox.showInfoAlert("Enter a loan duration.");
		}
		
		//if it doesnt throw an exception, add the copy to the table
		if(goAhead) {
			System.out.println("Copy adding");
			int id = makeId();
			Copy copy = new Copy(resource ,id ,null ,Integer.parseInt(duration));
			resource.addCopy(copy);
			System.out.println("Copy.. "+copy.getLoanDuration());
			
			repop();
		}
	}

	/**
	 * Randomly makes an id for a copy.
	 * @return n the random id.
	 */
	private int makeId() {
		Random rand = new Random();
		boolean goAhead = false;
		int n = 0;
		while(!goAhead) {
			n = rand.nextInt(5000) + 1;
			goAhead = checkId(n);
		}
		return n;
		
	}
	
	/**
	 * A method that checks if the id that is generated is the same as an existing id.
	 * @param id the randomly generated id.
	 * @return true if its a new id, false if its the same.
	 */
	private boolean checkId(int id) {
		try (Connection connection = DBHelper.getConnection(); 
				PreparedStatement statement = connection.prepareStatement("SELECT * "
						+ "FROM copies WHERE copyID=" + id);
				ResultSet results = statement.executeQuery()) {
			
			if(results.next()) {
				return false;
			} else {
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Updates the duration of a copy.
	 * @param copy passes through a copy.		
	 */
	private void updateDuration(Copy copy){
		String duration = loanDur.getText();
		boolean goAhead = true;
		try {
			Integer.parseInt(duration);
		} catch (NumberFormatException e){
			goAhead = false;
		}
		
		if(goAhead) {
			int durationNumber = Integer.parseInt(duration);
			copy.setLoanDuration(durationNumber);
			System.out.println("Duration is being set to: "+durationNumber);
			System.out.println("Copy.. "+copy.getLoanDuration());
			repop();
		}
		
	}
	
	
}
