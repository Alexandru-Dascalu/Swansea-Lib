package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DBHelper;

/**
 * This class allows user to register into the library system. They can toggle choose whether they would like to
 * register as a new user or a new librarian.
 * @author James Finlayson
 */
public class RegisterController implements Initializable {

	@FXML
	private Label regMessage;

	@FXML
	private TextField firstName;

	@FXML
	private TextField address;

	@FXML
	private Label firstNameError;

	@FXML
	private Label addressError;

	@FXML
	private Label phoneNumberError;

	@FXML
	private Label usernameError;

	@FXML
	private Label postCodeError;

	@FXML
	private CheckBox librarianCheckBox;

	@FXML
	private ImageView avatar;

	@FXML
	private TextField lastName;

	@FXML
	private Label lastNameError;

	@FXML
	private TextField phoneNumber;

	@FXML
	private Button registerButt;

	@FXML
	private Button browseButton;

	@FXML
	private Button createButton;

	@FXML
	private TextField postCode;

	@FXML
	private TextField username;

	@FXML
	private TextField staffId;

	@FXML
	private Label staffIdError;

	@FXML
	private Label employmentDateError;

	@FXML
	private TextField employmentDate;

	@FXML
	private Pane pane;

	private String avatarPath = "/SavedAvatars/Avatar1.png";

	/**
	 * Empty Constructor.
	 */
	public RegisterController() {

	}

	/**
	 * Loads an image for the avatar.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Image newAvatar = new Image(avatarPath);
		// avatar.setImage(newAvatar);
		staffIdError.setVisible(false);
		usernameError.setVisible(false);
		firstNameError.setVisible(false);
		lastNameError.setVisible(false);
		addressError.setVisible(false);
		postCodeError.setVisible(false);
		phoneNumberError.setVisible(false);
		employmentDateError.setVisible(false);
		regMessage.setVisible(false);
		staffId.visibleProperty().bind(librarianCheckBox.selectedProperty());
		employmentDate.visibleProperty().bind(librarianCheckBox.selectedProperty());
	}

	/**
	 * Sets new scene on stage within program using fxml file provided.
	 * @param event changed that will be made after mouseevent.
	 * @param sceneFXML scene that will be changed into.
	 */
	public void changeScene(MouseEvent event, String sceneFXML) {
		try {
			// create new scene object
			Parent root = FXMLLoader.load(getClass().getResource(sceneFXML));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Returns user to main login scene.
	 * @param event changes that will be made.
	 */
	@FXML
	public void backToLogin(MouseEvent event) {
		changeScene(event, "/fxml/loginScene.fxml");
	}
	
	/**
	 * Creates an account with the details provided if they are valid. If there is a
	 * problem with the details displays error message indicating where the problem
	 * is.
	 * @param event changes that will be made.
	 */
	@FXML
	void createAccount(ActionEvent event) {

		// if any text field is invalid display error message under it.

		if (validateUsername()) {
			usernameError.setVisible(false);
		} else {
			usernameError.setVisible(true);
		}

		if (validateFirstName()) {
			firstNameError.setVisible(false);
		} else {
			firstNameError.setVisible(true);
		}

		if (validateLastName()) {
			lastNameError.setVisible(false);
		} else {
			lastNameError.setVisible(true);
		}

		if (validatePhoneNumber()) {
			phoneNumberError.setVisible(false);
		} else {
			phoneNumberError.setVisible(true);
		}

		if (validateAddress()) {
			addressError.setVisible(false);
		} else {
			addressError.setVisible(true);
		}

		if (validatePostCode()) {
			postCodeError.setVisible(false);
		} else {
			postCodeError.setVisible(true);
		}

		if (validateStaffId()) {
			staffIdError.setVisible(false);
		} else {
			staffIdError.setVisible(true);
		}

		if (!librarianCheckBox.isSelected()) {
			staffIdError.setVisible(false);
		}

		// if all text fields are valid create account.
		if (validateUsername() && validateFirstName() && 
				validateLastName() && validatePhoneNumber()
				&& validateAddress() && validatePostCode()) {

			if (isLibrarian(event)) {

				if (validateStaffId()) {

					insertIntoUser(event);
					insertIntoLibrarian(event);
					regMessage.setVisible(true);
				}
			} else {
				insertIntoUser(event);
				regMessage.setVisible(true);

			}
		}

	}

	/**
	 *Valiadates postcode.
	 * @return true if post code is valid.
	 */
	private boolean validatePostCode() {
		String postCodeText = postCode.getText();
		if (postCodeText.contains(" ")) {

			return !(postCodeText.length() > 8 || postCodeText.length() <= 6);
		}
		return !(postCodeText.length() > 7 || postCodeText.length() <= 5);
	}

	/**
	 *Validates address.
	 * @return true if address is valid.
	 */
	private boolean validateAddress() {
		String addressText = address.getText();
		return !(addressText.length() > 1000 || addressText.length() <= 0);
	}

	/**
	 *Validates phone number.
	 * @return true if phone number is valid.
	 */
	private boolean validatePhoneNumber() {
		String phoneNumberText = phoneNumber.getText();
		return !(phoneNumberText.length() > 11 || phoneNumberText.length()
				< 11 || !isNumeric(phoneNumberText));
	}

	/**
	 * Checks if a string consists of numbers.
	 *
	 * @param str
	 *            string to check.
	 * @return true if String = numbers.
	 */
	private boolean isNumeric(String str) {
		return str.matches(".*\\d+.*");
	}

	/**
	 *Validates last name.
	 * @return true if last name is valid.
	 */
	private boolean validateLastName() {
		String lastNameText = lastName.getText();

		return !(lastNameText.length() > 15 || lastNameText.length() <= 0);
	}

	/**
	 *Validates first name based on length.
	 * @return true if first name is valid.
	 */
	private boolean validateFirstName() {
		String firstNameText = firstName.getText();

		return !(firstNameText.length() > 15 || firstNameText.length() <= 0);
	}

	/**
	 *Validates username.
	 * @return true if username is valid.
	 */
	private boolean validateUsername() {
		String usernameText = username.getText();

		return !(usernameText.length() > 15 || usernameText.length() <= 0
				|| validateExistingUser(usernameText));
	}

	/**
	 * Validates if the username already exists in the database.
	 * @param username username created by the user.
	 * @return true if username is valid.
	 */
	private boolean validateExistingUser(String username) {
		try {
			Connection conn = DBHelper.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT * "
					+ "FROM users WHERE username=?");
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 *Validate staff ID.
	 * @return true if staff ID is valid.
	 */
	private boolean validateStaffId() {
		String staffIdText = staffId.getText();

		return !(staffIdText.length() > 2 || staffIdText.length() < 2 || 
				validateExistingStaffId(staffIdText));
	}

	/**
	 * Validates if the staffId already exists in the database.
	 * @param staffId unique number assigned to staff.
	 * @return true if staffId is valid.
	 */
	private boolean validateExistingStaffId(String staffId) {
		try {
			Connection conn = DBHelper.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT * "
					+ "FROM staff WHERE staffId=?");
			pstmt.setString(1, staffId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Checks to see if the user is an librarian.
	 *
	 * @param event
	 *            checkbox being checked.
	 * @return boolean of whehter checkbox was selected.
	 */
	public boolean isLibrarian(ActionEvent event) {
		boolean selected = librarianCheckBox.isSelected();
		return selected;
	}
	
	/**
	 * Builds account in database using inputted info and chosen avatar. Then return
	 * user to login page.
	 * @param event changes that will be made.
	 */
	private void insertIntoUser(ActionEvent event) {

		try {
			Connection dbConnection = DBHelper.getConnection();
			PreparedStatement sqlStatement = dbConnection.prepareStatement("INSERT INTO "
					+ "users VALUES (?,?,?,?,?,?,?,0,?)");
			sqlStatement.setString(1, username.getText());
			sqlStatement.setString(2, firstName.getText());
			sqlStatement.setString(3, lastName.getText());
			sqlStatement.setString(4, phoneNumber.getText());
			sqlStatement.setString(5, address.getText());
			sqlStatement.setString(6, postCode.getText());
			sqlStatement.setString(7, avatarPath);
			sqlStatement.setString(8, "NULL");
			sqlStatement.executeUpdate();// This can return a value to tell you if it was successful.

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Inserts the librarin attributes into the staff table.
	 *
	 * @param event
	 *            button being pressed.
	 */
	private void insertIntoLibrarian(ActionEvent event) {

		try {
			Connection conn = DBHelper.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO "
					+ "staff VALUES (?, ?, ?)");// make current date
			pstmt.setString(1, username.getText());
			pstmt.setString(2, staffId.getText());
			pstmt.setString(3, employmentDate.getText());
			pstmt.executeUpdate();// This can return a value to tell you if it was successful.

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens the avatar drawing scene when the button is clicked.
	 *
	 * @param event
	 *            button being clicked.
	 */
	@FXML
	private void openAvatarEditor(MouseEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/fxml/drawAvatar.fxml"));
			Parent root1 = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Avatar!");
			stage.setScene(new Scene(root1));
			stage.show();

		} catch (IOException e2) {
			e2.printStackTrace();
		}
		System.out.println("Launch avatar editor.");
	}

	/**
	 * Convert custom profile image to file.
	 *
	 * @param path
	 *            location of directory.
	 * @param fileName
	 *            file name.
	 */
	public void convertToFile(String fileName, String path) {

		// Create path for File class as absolute path to project

		path = path.substring(0, path.length() - 2) + "/SavedAvatars/" + fileName;

		// Create the file that will be saved.
		File file = new File(path);

		// If the file isn't null, try to save it.
		if (!(file == null)) {
			try {
				/*
				 * Convert a snapshot of the canvas to a buffered image and the write it to
				 * file.
				 */
				BufferedImage bImage = SwingFXUtils.fromFXImage(
						avatar.snapshot(null, null), null);
				ImageIO.write(bImage, "png", file);
			} catch (Exception e) {
				/*
				 * If an exception is cause, print the error message on the console.
				 */
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Sets avatar image.
	 * @param image either a png file or custom drawing by user.
	 */
	public void setAvatar(Image image) {
		this.avatar.setImage(image);
	}

	/**
	 * Sets path to avatar image.
	 * @param path path where the image has been stored.
	 */
	public void setAvatarPath(String path) {
		this.avatarPath = path;
	}

	/**
	 * select a preset avatar.
	 * @param event changes that will be made.
	 */
	@FXML
	protected void browseAvatarAction(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extentionFilter = 
				new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
		chooser.getExtensionFilters().add(extentionFilter);
		chooser.setTitle("Open File");
		File file = chooser.showOpenDialog(new Stage());
		System.out.println(file.getAbsolutePath());
		setAvatarPath(new File(file.getAbsolutePath()).toURI().toString());
		Image selectedAvatar = new Image(new File(this.avatarPath).toURI().toString());
		// Image selectedAvatar = new Image(this.avatarPath);
		avatar.setImage(selectedAvatar);
	}
}
