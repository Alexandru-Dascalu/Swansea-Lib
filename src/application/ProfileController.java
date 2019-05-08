package application;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Book;
import model.Copy;
import model.DBHelper;
import model.DVD;
import model.EventNotification;
import model.Game;
import model.Laptop;
import model.Librarian;
import model.Notification;
import model.Person;
import model.Resource;
import model.ResourceNotification;
import model.User;

/**
 * Profile Controller is responsible for handling events within the library resources,
 * my profile, and staff profile tabs.
 * @author Kane Miles
 * @version 1.1
 */
public class ProfileController {

	@FXML
	private HBox resourceImages;//hbox that holds the resource images

	@FXML
	private ScrollPane scrollPane;//allows the user to scroll through the images

	@FXML
	private VBox vResourceBox;//vbox that contains the hbox of resources

	@FXML
	private TextField searchTextBox;//allows the user to search for resources

	@FXML
	private LoginController TextField;//log out link

	//User Profile
	@FXML
	private Label userLabel;//label displaying "Username"
	@FXML
	private Label fullnameLabel;//label displaying "Full name"
	@FXML
	private Label phoneLabel;//label displaying "Phone number"
	@FXML
	private Label addressLabel;//label displaying "Address"
	@FXML
	private Label postcodeLabel;//label displaying "Post code"
	@FXML
	private Label lastLoginLabel1;//label displaying "Last Login"
	@FXML
	private Label balanceLabel;//label displaying "Balance"

	//Staff Profile
	@FXML
	private Label userLabel1;//label displaying the username from the database
	@FXML
	private Label fullnameLabel1;//label displaying the fullanme of the user from the database
	@FXML
	private Label phoneLabel1;//label displaying the phone number of the user from the database
	@FXML
	private Label addressLabel1;//label displaying the address of the user from the database
	@FXML
	private Label postcodeLabel1;//label displaying the postcode of the user from the database
	@FXML
	private Label dateLabel1;//label displaying the employment date of the staff from the database
	@FXML
	private Label staffIDLabel1;//label displaying the staff id from the database
	@FXML
	private Label lastLoginLabel2;//label displaying the staff last login time

	@FXML
	private Tab userProfileTab;//my profile tab

	@FXML
	private Tab resourcesTab;// library resources tab

	@FXML
	private Tab transactionTab;//transaction tab

	@FXML
	private Tab staffProfileTab;//staff profile tab

	@FXML
	private TabPane tabs;//tab pane that holds the tabs

	@FXML
	private Label accountBalance;//label that displays an users balance

	@FXML
	private Button userEditProfileButton;//allows user to edit their profile

	@FXML
	private Button staffEditProfileButton;//allows staff to edit their profile

	@FXML
	private Button staffEditAvatar;//allows staff to edit avatar
	@FXML
	private Button userEditAvatar;//allows user to edit avatar
	
	@FXML
	private Button statsButton;

	//check boxes
	@FXML
	private CheckBox dvdCheck;
	@FXML
	private CheckBox bookCheck;
	@FXML
	private CheckBox laptopCheck;
	@FXML
	private CheckBox gameCheck;

	//Copies Explorer
	@FXML
	private Button staffOverdueFilter;
	@FXML
	private Button staffRequestedFilter;
	@FXML
	private Button staffAllFilter;
	@FXML
	private Button staffHistoryFind;
	@FXML
	private Button staffApproveCopy;
	@FXML
	private Button staffReturnCopy;
	@FXML
	private TextField staffCopyIDField;
	@FXML
	private TableView staffCopiesExplorerTable;

	//Manage Users
	@FXML
	private TableView<Person> staffUsersTable;
	@FXML
	private Label selectedUserLabel;
	@FXML
	private TextField staffAmountField;

	@FXML
	private VBox leftVbox;

	//Avatars
	@FXML
	private ImageView staffAvatarView;
	@FXML
	private ImageView userAvatarView;
	
	@FXML
	private TableView<model.Event> eventTable;
	
	@FXML
	private TableColumn<model.Event, String> eventTitleField;
	
	@FXML
	private TableColumn<model.Event, String> eventDetailsField;
	
	@FXML
	private TableColumn<model.Event, String> eventTimeField;
	
	@FXML
	private TableColumn<model.Event, Integer> eventSpacesField;
	
	@FXML
	private TableView<model.Event> userEventTable;
	
	@FXML
	private TableColumn<model.Event, String> userEventTitleField;
	
	@FXML
	private TableColumn<model.Event, String> userEventDetailsField;
	
	@FXML
	private TableColumn<model.Event, String> userEventTimeField;
	
	@FXML
	private TableColumn<model.Event, Integer> userEventSpacesField;
	
	
	@FXML
	private Button eventViewButton;
	
	@FXML
	private Button refreshEventsButton;
	
	@FXML
	private Button joinEventButton;
	
	@FXML
	private Button createEventButton;
	
	@FXML
	private Button staffStatsButton;

	@FXML
	private ImageView notificationBellView;
	
	@FXML
	private StackPane notificationStack;
	
	//may remove fixed size resource images
	//when dealing with window resizing.
	private final int RES_IMG_WIDTH = 220;
	private final int RES_IMG_HEIGHT = 330;

	private Person currentPerson;
	private ArrayList<Resource> resources;

	/**
	 * Sets new scene on stage within program using fxml file provided.
	 * @param sceneFXML Scene location
	 * @param event The mouse event.
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
	 * Switches the tabs.
	 * @param event Event
	 */
	@FXML
	private void searchBarSwitch (MouseEvent event) {
		//tabPane.getSelectionModel().select(2);
	}

	/**
	 * Method that searches for resources
	 * @param event when something is typed into the search box
	 * @throws ParseException 
	 */
	@FXML
    void searchThis(Event event) throws ParseException {
		tabs.getSelectionModel().select(resourcesTab);
		vResourceBox.getChildren().clear();
		HBox hbox = new HBox();
		hbox.setSpacing(5);
		vResourceBox.getChildren().add(hbox);
		loadResourceImages();
	}


	/**
	 * Called when logout link is clicked.
	 * @param event Passed when mouse event occurs
	 */
	@FXML
	public void logoutAction(MouseEvent event) {
		changeScene(event, "/fxml/loginScene.fxml");
	}
	

	/**
	 * Loads user information from Screen Manager class, so that
	 * it can be displayed within the UI.
	 */
	private void loadUserInformation() {
		if (ScreenManager.getCurrentUser() instanceof User) {

			//change text in labels to appropriate user information.
			userLabel.setText(currentPerson.getUsername());
			fullnameLabel.setText("Full Name: " + currentPerson.getFirstName() + " "
			+ currentPerson.getLastName());
			addressLabel.setText("Address: " + currentPerson.getAddress());
			postcodeLabel.setText("Post Code: " + currentPerson.getPostcode());
			phoneLabel.setText("Phone Number: " + currentPerson.getPhoneNumber());
			lastLoginLabel1.setText("Last Login: " + currentPerson.getLastLogin());
			
			try {
				((User) currentPerson).loadUserEvents();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			Double userBalance = ((User) currentPerson).getAccountBalance();
			
			//WARNING: the '�' character differs in GIT and in Java
			//TODO: Maybe set GIT & Java to UTF-8?
			accountBalance.setText("\u00a3" + Double.toString(userBalance));
			
			userAvatarView.setImage(new Image(currentPerson.getAvatar()));
		}else {
			//get all information in about user from ScreenManager class.
			Librarian staff = (Librarian) currentPerson;
			String fullname = staff.getFirstName() + " " + staff.getLastName();

			userLabel1.setText(staff.getUsername());
			fullnameLabel1.setText("Full name: " +
					" " + fullname);
			addressLabel1.setText("Address: " +
					" " + staff.getAddress());
			phoneLabel1.setText("Phone Number: " +
					" " + staff.getPhoneNumber());
			postcodeLabel1.setText("Postcode: " +
					" " + staff.getPostcode());
			dateLabel1.setText("Employment Date: " +
					" " + staff.getEmploymentDate());
			staffIDLabel1.setText("Staff ID: " +
					" " + staff.getStaffID());
			lastLoginLabel2.setText("Last login: " +
					" " + staff.getLastLogin());

			staffAvatarView.setImage(new Image(currentPerson.getAvatar()));
		}
	}

	/**
	 * Event handler that handles when a resource is clicked.
	 */
	final EventHandler<MouseEvent> enterHandler = event -> {
		StackPane currentPane = (StackPane) event.getSource();
		currentPane.getChildren().get(4).setVisible(true);
		currentPane.getChildren().get(3).setVisible(true);
	};

	/**
	 * Event handler that handles when a resource is clicked.
	 */
	final EventHandler<MouseEvent> exitHandler = event -> {
		StackPane currentPane = (StackPane) event.getSource();
		currentPane.getChildren().get(4).setVisible(false);
		currentPane.getChildren().get(3).setVisible(false);
	};

	final EventHandler<MouseEvent> clickHandler = event -> {

		//find the resource that was clicked.
		for(Resource resource : resources) {
			if(resource.getUniqueID() ==
					Integer.parseInt(((StackPane) event.getSource()).getId())) {
				ScreenManager.setCurrentResource(resource);
			}
		}

		try {
			FXMLLoader fxmlLoader =
					new FXMLLoader(getClass().getResource("/fxml/copyScene.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Resource Information");
            stage.setScene(new Scene(root1));
            stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	};

	/**
	 * Makes the image and resource text for the resource
	 * @param copyResource the copy of the resource
	 * @param width width of image
	 * @param height height of image
	 * @return the image pane
	 */
	private StackPane createImage(Resource copyResource, int width, int height) {

		//create stackpane to add image layers to.
		StackPane imagePane = new StackPane();

		//create white backround just in case image is small.
		Rectangle background = new Rectangle();
		background.setWidth(width);
		background.setHeight(height);
		background.setFill(Color.WHITE);

		//create text containing resource information
		//this text only shows when mouse enters image.
		Text resourceText = new Text();
		resourceText.setFont(Font.font("Arial", 20));
		resourceText.setStyle("-fx-font-weight: bold");
		resourceText.setFill(Color.WHITE);
		resourceText.setText("ID: " + copyResource.getUniqueID() + "\n" +
		copyResource.getTitle() + "\n" + copyResource.getYear());
		resourceText.setVisible(false);
		resourceText.setTextAlignment(TextAlignment.CENTER);
		resourceText.setWrappingWidth(width);

		//create imageview containg resource image.
		ImageView image = new ImageView();
		image.setFitWidth(width);
		image.setFitHeight(height);
		image.setImage(copyResource.getThumbnail());

		//if image is of a laptop, keep aspect ratio.
		if(copyResource instanceof Laptop) {
			image.setPreserveRatio(true);
		}

		//make image as smooth as possible.
		image.setCache(true);
		image.setCacheHint(CacheHint.SCALE);
		image.setSmooth(true);

		//add black colour overlay
		//only shows when mouse is in image.
		Rectangle rect = new Rectangle();
		rect.setWidth(width);
		rect.setHeight(height);
		rect.setFill(Color.BLACK);
		rect.setOpacity(0.7);
		rect.setVisible(false);

		//add all elements to stack pane.
		imagePane.getChildren().add(background);
		imagePane.getChildren().add(image);
		imagePane.getChildren().add(new ImageView());
		imagePane.getChildren().add(rect);
		imagePane.getChildren().add(resourceText);

		//set id of imagePane to it's index so it can be accessed
		//within the event handler.
		imagePane.setId(String.valueOf(copyResource.getUniqueID()));

		return imagePane;
	}

	/**
	 * Search resources and checks to see if the checkboxes are selected which filter the search
	 * @param i ID of the resource
	 * @return the search results or false
	 * @throws ParseException 
	 */
	private boolean search(int i) throws ParseException {
		//Get the Resource
		Resource r = resources.get(i);
		String searchText = searchTextBox.getText();

		if(bookCheck.isSelected() && r instanceof Book) {
			return r.contains(searchText);
		}
		if(dvdCheck.isSelected() && r instanceof DVD) {
			return r.contains(searchText);
		}
		if(laptopCheck.isSelected() && r instanceof Laptop) {
			return r.contains(searchText);
		}
		
		//Added on 16/03/19 by Charles Day
		if(gameCheck.isSelected() && r instanceof Game) {
		    return r.contains(searchText);
		}

		return false;

	}

	@FXML
	private void reloadVisuals(Event event) throws ParseException {

		vResourceBox.getChildren().clear();
		vResourceBox.getChildren().add(new HBox());

		if(resourceImages != null) {
		    resourceImages.getChildren().clear();
		}
		
		Resource.loadDatabaseResources();
		
		if(ScreenManager.getCurrentUser() instanceof User) {
		    User currentUser = (User) ScreenManager.getCurrentUser();
		    currentUser.loadUserCopies();
		}
		
		try
		{
			loadUserInformation();
			loadCopies();
			loadRequested();
			loadBorrowHistory();
			loadResourceImages();
		}
		catch(NullPointerException e)
		{
			System.out.println("Caught stupind null pointer exception caused by JavaFX.");
		}	
	}


	/**
	 * Loads resource images from Resource class, so that they can
	 * be displayed within the UI.
	 * @throws ParseException 
	 */
	private void loadResourceImages() throws ParseException {
		if (ScreenManager.getCurrentUser() instanceof Librarian) {
			staffProfileTab.setDisable(false);
			userProfileTab.setDisable(true);
		} else {
			staffProfileTab.setDisable(true);
			userProfileTab.setDisable(false);
		}
		
		//get resources
		resources = Resource.getResources();

		System.out.println(resources.size());
		ScreenManager.setResources(resources);
		
		//for each resource in resources array
		for(int i = 0; i < resources.size(); i++) {
		    System.out.println(resources.get(i).getTitle());
		    
			if(search(i)) {
				StackPane imagePane;
				
				if(resources.get(i).compareTimeDifference(currentPerson)) {
					imagePane = createImage(resources.get(i), RES_IMG_WIDTH, RES_IMG_HEIGHT);

					((ImageView) imagePane.getChildren().get(2)).setFitWidth(RES_IMG_WIDTH);
					((ImageView) imagePane.getChildren().get(2)).setImage(
						new Image("/graphics/" + "New.png")); //TODO: Possible bug: 'java.lang.OutOfMemoryError: Java heap space'
					((ImageView) imagePane.getChildren().get(2)).setPreserveRatio(true);
				}
				else {
					imagePane = createImage(resources.get(i),
							RES_IMG_WIDTH, RES_IMG_HEIGHT);
				}
				
				//get last image in last resource HBox.
				HBox latestHBox = (HBox)
						vResourceBox.getChildren().get(
								vResourceBox.getChildren().size() - 1);
				latestHBox.setSpacing(5);

				//if there is at least one image in last resource HBox
				if(!latestHBox.getChildren().isEmpty()) {
					//if the number of resources in resource HBox is more than
					//the width of the resource VBox / the width of a resource image
					if(latestHBox.getChildren().size() > (vResourceBox.getPrefWidth() 
					        - RES_IMG_WIDTH) / RES_IMG_WIDTH) {
						//create new HBox below last HBox
						HBox hResourceBox = new HBox();
						hResourceBox.setSpacing(5);
						//add image to new HBox
						hResourceBox.getChildren().add(imagePane);
						vResourceBox.getChildren().add(hResourceBox);
					} else {
						latestHBox.getChildren().add(imagePane); //add new image to last HBox
					}
				} else {
					latestHBox.getChildren().add(imagePane); //add new image to last HBox
				}

				imagePane.setOnMouseEntered(enterHandler);
				imagePane.setOnMouseExited(exitHandler);
				imagePane.setOnMouseClicked(clickHandler);
			}
		}	
	}

	/**
	 * Loads the resource images from the arraylist.
	 * @param resources instances of the resources.
	 * @param bannerName name of the banner.
	 */
	private void loadCopyImages(ArrayList<Resource> resources, String bannerName) {

		for(Resource resource : resources) {

			StackPane imagePane = createImage(resource, RES_IMG_WIDTH, RES_IMG_HEIGHT);

			((ImageView) imagePane.getChildren().get(2)).setFitWidth(RES_IMG_WIDTH);
			((ImageView) imagePane.getChildren().get(2)).setImage(
				new Image("/graphics/" + bannerName));
			((ImageView) imagePane.getChildren().get(2)).setPreserveRatio(true);

			resourceImages.getChildren().add(imagePane);

			imagePane.setOnMouseEntered(enterHandler);
			imagePane.setOnMouseExited(exitHandler);
			imagePane.setOnMouseClicked(clickHandler);

			}
	}

	/**
	 * Method that loads copies that the user is currently borrowing
	 */
	private void loadCopies() {

		if(currentPerson instanceof User) {
			ArrayList<Copy> userCopies = ((User) currentPerson).getBorrowedCopies();
			ArrayList<Resource> copyResources = new ArrayList<Resource>();

			for(Copy copy : userCopies) {
				copyResources.add(copy.getResource());
			}

			loadCopyImages(copyResources, "borrowed.png");
		}
	}

	/**
	 * Loads the resources that have been requested so the librarian can confirm then
	 */
	@FXML
	private void loadRequested() {
		if(currentPerson instanceof User) {
			ArrayList<Resource> requestedResources =
					((User) currentPerson).getRequestedResources();
			loadCopyImages(requestedResources, "requested.png");
		}

	}

	@FXML
	private void loadBorrowHistory() {
		if(currentPerson instanceof User) {
			ArrayList<Resource> borrowHistory =
					((User) currentPerson).loadUserHistory();
			loadCopyImages(borrowHistory, "returned.png");
		}
	}

    /**
     * intialize method that starts when the scene is intialized
     * 
     * @throws ParseException
     */
    @FXML
    public void initialize() throws ParseException {

        currentPerson = ScreenManager.getCurrentUser();
        resources = ScreenManager.getResources();

        if(currentPerson instanceof User) {
            User currentUser = (User) currentPerson;
            currentUser.checkForNearingEvents();
            currentUser.checkImminentFines();
            currentUser.loadNotifications();
            
            if(currentUser.hasUnreadNotifications()) {
                Circle circle = new Circle (7, Color.RED);
                notificationStack.getChildren().add(circle);
                notificationStack.setAlignment(circle, Pos.TOP_RIGHT);
            }
        } else {
            notificationStack.setVisible(false);
        }
        
        loadUserTableColumns();
        displayAll();

        loadResourceImages();
        loadEventTable();

        createEventButton.setDisable(ScreenManager.getCurrentUser() 
            instanceof User);
        joinEventButton.setDisable(ScreenManager.getCurrentUser() 
            instanceof Librarian);

        scrollPane.setHvalue(0.5);
    }

	//
	//Staff Profile -----------------------------------------------------------
	//

	//
	//Staff: Copies Explorer
	//
	
	@FXML
	private void displayAll() {
		staffHistoryFind.setDisable(false);
		staffApproveCopy.setDisable(true);
		staffReturnCopy.setDisable(false);

		loadExplorerTableColumns("all");
		ObservableList<ExplorerRow> copiesList = FXCollections.observableArrayList();

		try {
			Connection conn = DBHelper.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM copies,"
					+ " resource WHERE copies.rID = resource.rID");

			while(rs.next()) {
				copiesList.add(new ExplorerRow(
						rs.getString(9),
						rs.getString(3),
						rs.getInt(1),
						rs.getInt(2),
						rs.getInt(4),
						rs.getString(5),
						rs.getString(6),
						rs.getString(7)
						));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		staffCopiesExplorerTable.setItems(copiesList);
		staffCopiesExplorerTable.autosize();
	}

	/**
	 * Displays what is overdue
	 */
	@FXML
	private void displayOverdue() {
		staffHistoryFind.setDisable(false);
		staffApproveCopy.setDisable(true);
		staffReturnCopy.setDisable(false);

		loadExplorerTableColumns("overdue");
		ObservableList<ExplorerRow> copiesList = FXCollections.observableArrayList();

		try {
			Connection conn = DBHelper.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM fines, "
					+ "resource, copies WHERE fines.rID = resource.rID AND "
					+ "copies.rID = resource.rID AND copies.keeper = "
					+ "fines.username");

			while(rs.next()) {
				copiesList.add(new ExplorerRow(
						rs.getString(9),
						rs.getString(2),
						rs.getInt(12),
						rs.getInt(3),
						rs.getString(16),
						rs.getString(18)
						));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		staffCopiesExplorerTable.setItems(copiesList);
		staffCopiesExplorerTable.autosize();
	}

	/**
	 * Displays what is requested
	 */
	@FXML
	private void displayRequested() {
		staffHistoryFind.setDisable(true);
		staffApproveCopy.setDisable(false);
		staffReturnCopy.setDisable(true);

		loadExplorerTableColumns("requested");
		ObservableList<ExplorerRow> copiesList = FXCollections.observableArrayList();

		try {
			Connection conn = DBHelper.getConnection();
			Statement stmt = conn.createStatement();
			/*ResultSet rs = stmt.executeQuery("SELECT * FROM requestsToApprove, "
					+ "users where requestsToApprove.userName = users.username");*/

			ResultSet rs = stmt.executeQuery("SELECT * FROM requestsToApprove");
			
			while(rs.next()) {
				copiesList.add(new ExplorerRow(
						rs.getString(2),
						rs.getInt(1)
						));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		staffCopiesExplorerTable.setItems(copiesList);
		staffCopiesExplorerTable.autosize();
	}

	/**
	 * prints display history
	 */
	@FXML
	private void displayCopyHistory() {
		String copyID = staffCopyIDField.getText();
		if (copyID.equals("")) {
			System.out.println("No copyID entered!");

		} else {
			if (checkCopyID(copyID) == true) {
				loadExplorerTableColumns("history");
				ObservableList<ExplorerRow> historyList = 
						FXCollections.observableArrayList();

				try {
					Connection conn = DBHelper.getConnection();
					PreparedStatement sqlStatement = conn.prepareStatement(
							"SELECT * FROM borrowRecords WHERE copyID = ?");
					sqlStatement.setInt(1, Integer.parseInt(copyID));
					ResultSet rs = sqlStatement.executeQuery();

					while(rs.next()) {

						historyList.add(new ExplorerRow(
								rs.getInt(2),
								rs.getString(3),
								rs.getString(4)));
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

				staffCopiesExplorerTable.setItems(historyList);
				staffCopiesExplorerTable.autosize();

			} else {
				System.out.println("Copy not found!");
				staffCopyIDField.setText("");
			}
		}
	}

	/**
	 * Approves the loaning of a copy to a user from the Staff Copies Explorer.
	 * @throws IllegalStateException if this method is called when the user is 
	 * over the request limit. The request button should not have been available
	 * in the first place.
	 */
	@FXML
	private void approveCopy() throws IllegalStateException{
		ExplorerRow row  = (ExplorerRow)
				staffCopiesExplorerTable.getSelectionModel().getSelectedItem();
		int resourceID = row.getResourceID();
		String username = row.getKeeper();
		Resource resourceToLoan = Resource.getResource(resourceID);
		
		User user = (User)Person.loadPerson(username);
        if (!resourceToLoan.loanToUser(user)) {
            AlertBox.showInfoAlert("Waiting for free copy");
        } else {
            if (user.getBorrowLoad() + resourceToLoan.getLimitAmount() > 5) {
                throw new IllegalStateException("approveCopy should not have "
                		+ "been called if user is over request limit.");
            } else {
                try {
                    Connection conn = DBHelper.getConnection();

                    PreparedStatement sqlStatement = conn.prepareStatement("DELETE" +
                            " FROM requestsToApprove WHERE rID = ? AND userName = ?");
                    sqlStatement.setInt(1, resourceID);
                    sqlStatement.setString(2, username);
                    sqlStatement.executeUpdate();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

		System.out.println("Approved copy!");
		displayRequested();
		ResourceNotification.makeApprovalNotification(resourceToLoan);;
	}

	/**
	 * Returns the selected copy from a user in the Copies Explorer
	 */
	@FXML
	private void returnCopy() {
		ExplorerRow row  = (ExplorerRow)
				staffCopiesExplorerTable.getSelectionModel().getSelectedItem();
		int copyID = row.getCopyID();

		for (Resource resource : Resource.getResources()) {
			for (Copy copy : resource.getCopies()) {
				if (copy.getCopyID() == copyID) {
					resource.processReturn(copy);
				}
			}
		}
		System.out.println("Returned copy!");
		displayAll();
	}

	/**
	 * Checks whether a copyID exists in the database
	 * @param copyID String
	 * @return true if copyId exists
	 */
	private boolean checkCopyID(String copyID) {
		try {
			Connection conn = DBHelper.getConnection();
			PreparedStatement sqlStatement = conn.prepareStatement(
					"SELECT COUNT(*) FROM copies WHERE copyID = ?");
			sqlStatement.setString(1, copyID);
			ResultSet rs = sqlStatement.executeQuery();
            if (rs.getInt(1) == 1) {
            	return true;
            }

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Opens the profile editor when the button is clicked
	 * @param event button being clicked
	 */
	@FXML
	private void explorerTableClicked(MouseEvent event) {
		ExplorerRow row  = (ExplorerRow)
				staffCopiesExplorerTable.getSelectionModel().getSelectedItem();
		staffCopyIDField.setText(Integer.toString(row.getCopyID()));
	}

	//
	//Staff: Profile
	//


	@FXML
	void pickAvatar(Event e) {
		  FileChooser chooser = new FileChooser();
		    FileChooser.ExtensionFilter extentionFilter =
		    		new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
			chooser.getExtensionFilters().add(extentionFilter);
		    chooser.setTitle("Open File");
		    File file = chooser.showOpenDialog(new Stage());
		    System.out.println(file.getAbsolutePath());


		    ScreenManager.getCurrentUser().setAvatar(
		    		new File(file.getAbsolutePath()).toURI().toString());
		    userAvatarView.setImage(new Image(currentPerson.getAvatar()));
		    staffAvatarView.setImage(new Image(currentPerson.getAvatar()));

	}

	/**
     * Sets the current user avatar to the image in the given file.
     * @param imageFile The file containing the new avatar image.
     */
    public void changeUserAvatar(File imageFile) {
            ScreenManager.getCurrentUser().setAvatar(new
                File(imageFile.getAbsolutePath()).toURI().toString());
            userAvatarView.setImage(new Image(currentPerson.getAvatar()));
            staffAvatarView.setImage(new Image(currentPerson.getAvatar()));
    }

	@FXML
	private void openProfileEditor(MouseEvent event) {
		System.out.println("Launch staff editing profile.");

		try {
			FXMLLoader fxmlLoader =
					new FXMLLoader(getClass().getResource("/fxml/staffEdit.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root1));
            stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Opens the avatar drawing scene when the button is clicked
	 * @param event button being clicked
	 */
	@FXML
	private void openAvatarEditor(MouseEvent event) {
		try {
			FXMLLoader fxmlLoader =
					new FXMLLoader(getClass().getResource("/fxml/drawAvatar.fxml"));
			Parent root1 = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Avatar!");
			stage.setScene(new Scene(root1));
			stage.show();

		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println("Launch avatar editor.");
	}
	
	/**
	 * Opens the statistics scene when the button is clicked
	 * Different scene opens depending on current user
	 * @param event button being clicked
	 */
	@FXML
	private void openStatistics(MouseEvent event) {
		try {
			FXMLLoader fxmlLoader =
					new FXMLLoader(getClass().getResource("/fxml/userStatistics.fxml"));
			Parent root1 = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Statistics");
			stage.setScene(new Scene(root1));
			stage.show();

		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println("Open user statistics.");
	}
	
	/**
	 * Opens the statistics scene when the button is clicked
	 * Different scene opens depending on current user
	 * @param event button being clicked
	 */
	@FXML
	private void openStaffStatistics(MouseEvent event) {
		try {
			FXMLLoader fxmlLoader =
					new FXMLLoader(getClass().getResource("/fxml/LibrarianStatistics.fxml"));
			Parent root1 = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Statistics");
			stage.setScene(new Scene(root1));
			stage.show();

		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println("Open librarian statistics.");
	}


	//Integer.toString(cd.getValue().getLoanDuration())

	/**
	 * Loads the user table so the staff can manage the users.
	 * @param tableToLoad The table requested (all,overdue,history,requested)
	 */
	private void loadExplorerTableColumns(String tableToLoad) {

		TableColumn<ExplorerRow, String> titleCol = new TableColumn<ExplorerRow,
				String>("Title");
		titleCol.setCellValueFactory(cd ->
		new SimpleStringProperty(cd.getValue().getResourceTitle()));

		TableColumn<ExplorerRow, String> keeperCol = new TableColumn<ExplorerRow,
				String>("Keeper");
		keeperCol.setCellValueFactory(cd ->
		new SimpleStringProperty(cd.getValue().getKeeper()));

		TableColumn<ExplorerRow, String> copyIDCol = new TableColumn<ExplorerRow,
				String>("Copy ID");
		copyIDCol.setCellValueFactory(cd ->
		new SimpleStringProperty(Integer.toString(cd.getValue().getCopyID())));

		TableColumn<ExplorerRow, String> resourceIDCol = new
				TableColumn<ExplorerRow, String>("Resource ID");
		resourceIDCol.setCellValueFactory(cd ->
		new SimpleStringProperty(Integer.toString(cd.getValue().getResourceID())));

		TableColumn<ExplorerRow, String> loanDurationCol = new
				TableColumn<ExplorerRow, String>("Loan Duration");
		loanDurationCol.setCellValueFactory(cd ->
		new SimpleStringProperty(Integer.toString(cd.getValue().getLoanDuration())));

		TableColumn<ExplorerRow, String> orderNumberCol = new
				TableColumn<ExplorerRow, String>("Order Number");
		orderNumberCol.setCellValueFactory(cd ->
		new SimpleStringProperty(Integer.toString(cd.getValue().getOrderNumber())));

		TableColumn<ExplorerRow, String> borrowDateCol = new
				TableColumn<ExplorerRow, String>("Borrow Date");
		borrowDateCol.setCellValueFactory(cd ->
		new SimpleStringProperty(cd.getValue().getBorrowDate()));

		TableColumn<ExplorerRow, String> lastRenewalCol = new
				TableColumn<ExplorerRow, String>("Last Renewal");
		lastRenewalCol.setCellValueFactory(cd ->
		new SimpleStringProperty(cd.getValue().getLastRenewal()));

		TableColumn<ExplorerRow, String> dueDateCol = new
				TableColumn<ExplorerRow, String>("Due Date");
		dueDateCol.setCellValueFactory(cd ->
		new SimpleStringProperty(cd.getValue().getDueDate()));

		TableColumn<ExplorerRow, String> historyCol = new
				TableColumn<ExplorerRow, String>("History");
		historyCol.setCellValueFactory(cd ->
		new SimpleStringProperty(cd.getValue().getHistory()));

		switch (tableToLoad) {
			case "all":
				staffCopiesExplorerTable.getColumns().clear();
				staffCopiesExplorerTable.getColumns().addAll(
						copyIDCol,resourceIDCol,titleCol,keeperCol,loanDurationCol,
						borrowDateCol,lastRenewalCol,dueDateCol);
				break;
			case "overdue":
				staffCopiesExplorerTable.getColumns().clear();
				staffCopiesExplorerTable.getColumns().addAll(
						titleCol,keeperCol,copyIDCol,resourceIDCol,borrowDateCol,
						dueDateCol);
				break;

			case "requested":
				staffCopiesExplorerTable.getColumns().clear();
				staffCopiesExplorerTable.getColumns().addAll(
						keeperCol,resourceIDCol);
				break;

			case "history":
				staffCopiesExplorerTable.getColumns().clear();
				staffCopiesExplorerTable.getColumns().addAll(
						copyIDCol,keeperCol,historyCol);
		}

	}

	//
	//Staff: Manage Users
	//

	private void loadUserTableColumns() {
		TableColumn<Person, String> usernameCol =
				new TableColumn<Person, String>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

		TableColumn<Person, String> firstnameCol =
				new TableColumn<Person, String>("Firstname");
		firstnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));


		TableColumn<Person, String> lastnameCol =
				new TableColumn<Person, String>("Lastname");
		lastnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

		TableColumn<Person, Integer> telephoneCol =
				new TableColumn<Person, Integer>("Telephone");
		telephoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

		TableColumn<Person, String> addressCol =
				new TableColumn<Person, String>("Address");
		addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

		TableColumn<Person, String> postcodeCol =
				new TableColumn<Person, String>("Postcode");
		postcodeCol.setCellValueFactory(new PropertyValueFactory<>("postcode"));

		TableColumn<Person, String> avatarCol =
				new TableColumn<Person, String>("Avatar Path");
		avatarCol.setCellValueFactory(new PropertyValueFactory<>("avatarPath"));

		TableColumn<Person, String> accountBalanceCol =
				new TableColumn<Person, String>("accountBalance");
		accountBalanceCol.setCellValueFactory(
				new PropertyValueFactory<>("accountBalance"));

		staffUsersTable.getColumns().clear();
		staffUsersTable.getColumns().addAll(usernameCol,firstnameCol,
				lastnameCol,addressCol,postcodeCol,accountBalanceCol);
	}
	/**
	 * loads all the users from the table which aren't staff when the button is clicked
	 * @param event the button being clicked
	 */
	@FXML
	private void loadUsersTable(MouseEvent event) {
		ObservableList<Person> usersList = FXCollections.observableArrayList();
		try {
			Connection conn = DBHelper.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(
					"SELECT username FROM users WHERE "
					+ "username NOT IN (SELECT username FROM staff);");
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
            	usersList.add(Person.loadPerson(rs.getString(1)));
            }

            staffUsersTable.setItems(usersList);
            staffUsersTable.autosize();


		} catch (SQLException e) {
			System.out.println("Error: Failed to load users from Database.");
			e.printStackTrace();
		}

	}

	/**
	 * Method that shows the table of the selected user when button is clicked
	 * @param event when the button is clicked
	 */
	@FXML
	private void userTableClicked(MouseEvent event) {
		Person selectedUser = staffUsersTable.getSelectionModel().getSelectedItem();
		selectedUserLabel.setText(selectedUser.getUsername());
		//System.out.println("Cell clicked?");
	}

	/**
	 * Deletes a user from the database when the button is clicked
	 * @param event the button being clicked
	 */
	@FXML
	private void userDeleteButton(MouseEvent event) {
		if (selectedUserLabel.getText().equals("-")) {
			System.out.println("No user selected!");
		} else {
			System.out.println("Delete User: " + selectedUserLabel.getText());
			if(selectedUserLabel.getText().equals(
					ScreenManager.getCurrentUser().getUsername())) {
				AlertBox.showInfoAlert("You can't delete yourself!");
			}else {
				if(Person.removePerson(selectedUserLabel.getText())) {

				}else {
					AlertBox.showInfoAlert("They can't be deleted at the moment");
				}
			}
			//Delete user
			selectedUserLabel.setText("-");
			staffUsersTable.getItems().clear();
			loadUsersTable(null);
		}

	}

	//
	// End of Staff Tab
	//

	

	/**
	 * Method that allows librarian to add funds to a user when the button is clicked
	 * @param event the button being pressed
	 */
	@FXML
	private void userAddFundsButton(MouseEvent event) {
		if (selectedUserLabel.getText().equals("-")) {
			System.out.println("No user selected!");
		} else {
			try {
				double amount = Float.parseFloat(staffAmountField.getText());
				if (amount > 0.01) {
					System.out.println("Add: " +
				amount + " to " + selectedUserLabel.getText());
					User.addBalance(selectedUserLabel.getText(), amount);
				}
			} catch (RuntimeException e) {
				System.out.println("User entered non-float.");
			} finally {
				staffAmountField.setText("");
				staffUsersTable.getItems().clear();
				loadUsersTable(null);
			}
		}

	}
	
	/**
	 * Reloads data from database for both Upcoming and User event tables.
	 */
	@FXML
	private void loadEventTable() {
		
		try {
			
			model.Event.loadEventsFromDB(); //loads appropriate events from DB depending on user.
			
			//set upcoming event table cell property value to event attribute names.
			eventTitleField.setCellValueFactory(new PropertyValueFactory<>("title"));
			eventDetailsField.setCellValueFactory(new PropertyValueFactory<>("details"));
			eventTimeField.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
			eventSpacesField.setCellValueFactory(new PropertyValueFactory<>("maxAttending"));
			
			//set user event table cell property value to event attribute names.
			userEventTitleField.setCellValueFactory(new PropertyValueFactory<>("title"));
			userEventDetailsField.setCellValueFactory(new PropertyValueFactory<>("details"));
			userEventTimeField.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
			userEventSpacesField.setCellValueFactory(new PropertyValueFactory<>("maxAttending"));
			
			ObservableList<model.Event> tableData = FXCollections.observableArrayList();
			ObservableList<model.Event> userTableData = FXCollections.observableArrayList();
			
			tableData.addAll(model.Event.getAllEvents()); //add all appropriate events to upcoming events data.
			userTableData.addAll(model.Event.getUserEvents()); //add all user events to user events table data.

			eventTable.setItems(tableData);
			userEventTable.setItems(userTableData);
			
			eventTable.refresh();
			userEventTable.refresh();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Called when user selects an event within the upcoming events table.
	 * @throws SQLException
	 * @throws ParseException
	 */
	@FXML
	private void onTableSelection() throws SQLException, ParseException {
		
		joinEventButton.setDisable(true);
		
		//get selected event from upcoming events table.
		model.Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
		
		//if user is not a librarian and has selected an event
		if(ScreenManager.getCurrentUser() instanceof User && selectedEvent != null) {
			//if event is happening in the future
			if(model.Event.checkFutureDate(selectedEvent.getDateTime())) {
				ArrayList<Integer> usersEvents = ((User) ScreenManager.getCurrentUser()).loadUserEvents();
				//if event has spaces and user isn't already going
				if(selectedEvent.getMaxAttending() > 0 && !(usersEvents.contains(selectedEvent.getID()))) {
					joinEventButton.setDisable(false); //enable join event button.
				}
			}
			
		}

	}
	
	/**
	 * Called when user clicks the join event button (will only be called when join event button
	 * is enabled). 
	 * @throws SQLException
	 * @throws ParseException
	 */
	@FXML
	private void onJoinEventClick() {
		
		model.Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
		int selectedIndex = eventTable.getSelectionModel().getSelectedIndex();

		//decrease no. of available spaces for event by one, and update in DB.
		selectedEvent.setMaxAttending(selectedEvent.getMaxAttending()-1);
		ArrayList<model.Event> newEvents = model.Event.getAllEvents();
		newEvents.set(selectedIndex, selectedEvent);
		model.Event.updateEventInDB(selectedEvent);
		
		//add event to list of current user events.
		model.Event.addUserEventInDB(ScreenManager.getCurrentUser().getUsername(), selectedEvent.getID());
		
		loadEventTable(); //reload tables.
	}
	
	/**
	 * Opens Event Scene when librarian clicks "create event" button.
	 */
	@FXML
	private void openEventCreator() {
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createEvent.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Create Event");
            stage.setScene(new Scene(root1));
            stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void showNotifications() {
	    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/" +
	        "notificationView.fxml"));
	    
        Parent notificationsRoot = null;
        try {
            notificationsRoot = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        Stage notificationWindow = new Stage();
        notificationWindow.initModality(Modality.APPLICATION_MODAL);
        notificationWindow.setTitle("Your Notifications");
        notificationWindow.setScene(new Scene(notificationsRoot));
        notificationWindow.show();
	}
	
	@FXML
	private void setNotificationBellNormal() {
	    notificationBellView.setEffect(null);
	}
	
	@FXML
	private void setNotificationBellHover() {
	    Glow notBellGlow = new Glow();
	    notBellGlow.setLevel(1);
        notificationBellView.setEffect(notBellGlow);
    }
}
