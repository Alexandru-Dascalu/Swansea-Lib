package application;


import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Librarian;
import model.Person;
import model.User;

/**
 * This class allows user or librarians to log in to the library system.
 * If the user or librarian foes not have an account, they can register a new account as well.
 * @author Kane Miles.
 */
public class LoginController {

	@FXML
	private TextField usernameTextBox;

	@FXML
	private Label errorLabel;

	@FXML
	private Button registerButton;

	@FXML
	private Button loginButton;


	/**
	 * Sets new scene on stage within program using fxml file provided.
	 * @param sceneFXML The scene location.
	 * @param event The event of button being pressed.
	 */
	public void changeScene(MouseEvent event, String sceneFXML) {
		try {
			//create new scene object
			Parent root = FXMLLoader.load(getClass().getResource(sceneFXML));
			Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Called when login button is clicked.
	 * @param event The event of the button being clicked.
	 */
	@FXML
	public void loginAction(MouseEvent event) {

		Person User1 = Person.loadPerson(usernameTextBox.getText());

		if(User1 != null) {
			User1.updateLogin();
			ScreenManager.setCurrentUser((Person) User1);
			changeScene(event, "/fxml/profileScene.fxml");
		} else {
			errorLabel.setText("User cannot be found.");
		}

	}

	/**
	 * Called when log out link is clicked.
	 * @param event The event of the link being clicked.
	 */
	@FXML
	public void logoutAction(MouseEvent event) {
		changeScene(event, "/fxml/loginScene.fxml");
	}

	/**
	 * changes to the register screen when button is clicked.
	 * @param event the button being clicked.
	 */
	@FXML
	public void registerAction(MouseEvent event) {
		System.out.println("Register button pressed.");
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/fxml/registerScene.fxml"));
			Parent root1 = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			// stage.initStyle(StageStyle.UNDECORATED);
			stage.setTitle("Avatar!");
			stage.setScene(new Scene(root1));
			stage.show();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//changeScene(event, "/fxml/registerScene.fxml");
	}

	@FXML
	public void initialize() {

	 }

}
