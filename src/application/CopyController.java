package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Book;
import model.DVD;
import model.Game;
import model.Laptop;
import model.Resource;
import model.Review;
import model.User;

/**
 * The gui that appears when a resource is clicked, shows the information 
 * about a resource and allows the user to request a copy if there is a free 
 * copy available.
 * @author Joe Wright
 *
 */
public class CopyController {

	@FXML
	private BorderPane borderpane1;//borderpane

	@FXML
	private AnchorPane leftanchor;//anchor for left side of borderpane

	@FXML
	private VBox leftvbox;//vbox in left anchor

	@FXML
	private ImageView resourceimage;//resource image holder

	@FXML
	private AnchorPane centeranchor;//anchor pane in the center of borderpane

	@FXML
	private VBox centervbox;//vbox in center anchor

	@FXML
	private TextArea centertextarea;//textarea in center anchor

	@FXML
	private AnchorPane bottomanchor;//anchor for bottom of borderpane

	@FXML
	private Button requestbutt;//request copy button

	@FXML
	private Button viewTrailerButton;
	
	@FXML
	private VBox leftVbox;//vbox in left anchor pane

	@FXML
	private Label copytext;//textbox showing copies available

	@FXML
	private Label resourceName;//resources name

	private Resource currentResource;//current instance of resource
	
	
	@FXML
	private VBox seeReviews;

	private int RES_IMG_WIDTH = 200;
	private int RES_IMG_HEIGHT = 200;

	/**
	 * Sets new scene on stage within program using fxml file provided.
	 *
	 * @param sceneFXML The scene location.
	 * @param event The event.
	 * 
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
	*Method that deals with reviews and if the user is staff, a remove button appears
	*
	*/
	private void dealWithReviews() {
		int resourceId = ScreenManager.currentResource.getUniqueID();
		boolean hasReviews = model.Review.hasReviews(resourceId);
		
		if(hasReviews) {
	
			HBox avg = new HBox(); //ready for images
			Text avgText = new Text("Rating: "+Math.round(model.Review.getAvgStar(resourceId)*100.0)/100.0);
			avgText.setStyle("-fx-font: 24 arial;");
			avg.getChildren().add(avgText);
			seeReviews.getChildren().add(avg);

			//Now for the actual reviews
			for(String[] review : Review.getReviews(resourceId)) {
			
				VBox topV = new VBox();
				HBox topH = new HBox();
				//star, name,what,when
				Text title = new Text(" from "+review[1]);
				Text when = new Text(" ["+review[3]+"]");
				when.setStyle("-fx-font:12 arial;");
				Text star = new Text("Rating: "+review[0]);
				star.setFill(Color.GREEN);
				Text reviewText = new Text(review[2]);
				reviewText.setStyle("-fx-font:15 arial;");
				topH.getChildren().addAll(star,title,when);
			
				//if staff , add button.
				if(ScreenManager.getCurrentUser() instanceof model.Librarian) {
				
					Button removeReview = new Button("Remove");
					removeReview.setOnAction((event) -> {
					   Review.removeReview(Integer.valueOf(review[4]));
					   seeReviews.getChildren().clear();
			
					   dealWithReviews();
					});
					topV.getChildren().addAll(topH,reviewText,removeReview);
				}else {
					topV.getChildren().addAll(topH,reviewText);
				}
				
				
				
				
				seeReviews.getChildren().add(topV);
				
				
				
			}
			
			seeReviews.setSpacing(8);
			
			
		}else {
			Text avgText = new Text("No reviews yet!");
			seeReviews.getChildren().add(avgText);
		}
		
	
		
	}
	
	

	/**
	 * Loads resource information from Screen Manager class, so that it can be
	 * displayed within the UI. Shows different information depending on the
	 * resource.
	 */
	private void loadResourceInformation() {

		// Gets the common attributes between each resource
		int uniqueId = ScreenManager.currentResource.getUniqueID();
		String title = ScreenManager.currentResource.getTitle();
		int year = ScreenManager.currentResource.getYear();
		dealWithReviews();

		resourceName.setText(title);

		// Adds all the common attributes to the text area
		centertextarea.appendText(
				"uniqueID: " + Integer.toString(uniqueId) + "\ntitle: " +
		title + "\nyear: " + Integer.toString(year));

		// If the resource is a Book, it will add the book attributes to the
		// text area.
		if (ScreenManager.currentResource instanceof Book) {
			ScreenManager.currentBook = (Book) ScreenManager.currentResource;
			String author = ScreenManager.currentBook.getAuthor();
			String publisher = ScreenManager.currentBook.getPublisher();
			String genre = ScreenManager.currentBook.getGenre();
			String ISBN = ScreenManager.currentBook.getISBN();
			String language = ScreenManager.currentBook.getLanguage();

			centertextarea.appendText("\nauthor: " + author + "\npublisher: " +
			publisher + "\ngenre: " + genre
					+ "\nISBN: " + ISBN + "\nlanguage: " + language);

			// If the resource is a Laptop, it will add the laptop attributes to
			// the text area.
		} else if (ScreenManager.currentResource instanceof Laptop) {
			ScreenManager.currentLaptop = (Laptop) ScreenManager.currentResource;
			String manufacturer = ScreenManager.currentLaptop.getManufacturer();
			String model = ScreenManager.currentLaptop.getModel();
			String OS = ScreenManager.currentLaptop.getOS();

			centertextarea.appendText("\nmanufacturer: " + manufacturer +
					"\nmodel: " + model + "\nOS: " + OS);

			// If the resource is a DVD, it will add the attributes of a dvd to
			// the text area.
		} else if (ScreenManager.currentResource instanceof DVD) {
			ScreenManager.currentDVD = (DVD) ScreenManager.currentResource;
			String director = ScreenManager.currentDVD.getDirector();
			int runtime = ScreenManager.currentDVD.getRuntime();
			String language = ScreenManager.currentDVD.getLanguage();

			centertextarea.appendText(
					"\ndirector: " + director + "\nruntime: " + 
			Integer.toString(runtime) + "\nlanguage: " + language);

		}
		
		
		
		
		
		//**********
		
		// This sets the textbox depending if the number of copies is equal to 0
		// or not.
		if (ScreenManager.currentResource.getNrOfCopies() == 0) {
			copytext.setText("All Copies are currently being borrowed.");
		} else {
			copytext.setText("Copies: " + Integer.toString(
					ScreenManager.currentResource.getNrOfCopies()));
		}

	}

	/**
	 * Loads resource image from Resource class, so that they can be displayed
	 * within the UI.
	 */
	private void loadResourceImage() {
		// create new resource image to be added.
		resourceimage.setFitWidth(RES_IMG_WIDTH);
		resourceimage.setFitHeight(RES_IMG_HEIGHT);
		resourceimage.setImage(ScreenManager.currentResource.getThumbnail());
	}

	/**
	 * When the button is clicked, it will send a request to a librarian and
	 * they can either decline or accept said request.
	 *
	 * @param event button being pressed.
	 */
	@FXML
	public void requestCopy(MouseEvent event) {
		ScreenManager.currentResource.addPendingRequest((User) 
				ScreenManager.getCurrentUser());
		AlertBox.alertDone("Requested!");
		// ScreenManager.currentResource.loanToUser((User)ScreenManager.getCurrentUser());
	}
	
	@FXML
	public void showTrailerWindow(ActionEvent actioEvent)
	{
	    Resource currentResource = ScreenManager.currentResource;
	    
	    if(currentResource.getClass() == DVD.class) {
	        DVD currentMovie = (DVD) currentResource;
	        
	        String title = currentMovie.getTitle();
	        
	        MovieTrailerView trailerView = new MovieTrailerView(title);
	        
	        Scene trailerScene = new Scene(trailerView.getWebView(), 1600, 900);
	        
	        Stage trailerWindow = new Stage();
	        trailerWindow.setTitle(trailerView.getTrailerDescription().getName());
	        
	        trailerWindow.setOnHidden(e -> {
	            trailerView.stop();
	        });
	        
	        trailerWindow.setScene(trailerScene);
	        trailerWindow.show();
	    } else if(currentResource.getClass() == Game.class) {
	        Game currentGame = (Game) currentResource;
	        
	        String title = currentGame.getTitle();
	        
	        GameTrailerView trailerView = new GameTrailerView(title);
	        
	        Scene trailerScene = new Scene(trailerView.getWebView(), 1600, 900);
	        
	        Stage trailerWindow = new Stage();
	        trailerWindow.setTitle(trailerView.getTrailerDescription().getName());
            
            trailerWindow.setOnHidden(e -> {
                trailerView.stop();
            });
            
            trailerWindow.setScene(trailerScene);
            trailerWindow.show();
	    }
	   
	}

	/**
	 * This checks if the current user is currently borrowing the resource, if
	 * they have then it will disable the request copy button.
	 */
	private void checkIfBorrowed() {
		User user = (User) ScreenManager.getCurrentUser();

		if (user.isBorrowing(ScreenManager.currentResource)) {

			requestbutt.setDisable(true);
		}
	}

	/**
	 * These are only buttons that appear when the user is a staff, 
	 * these buttons allow the librarian to manage the resources.
	 */
	private void setupStaffButtons() {
		Button editCopies = new Button("Edit copies");
		editCopies.setOnAction(e -> {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(
						getClass().getResource("/fxml/editCopies.fxml"));
				Parent root1 = (Parent) fxmlLoader.load();
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				// stage.initStyle(StageStyle.UNDECORATED);
				stage.setTitle("Copies");
				stage.setScene(new Scene(root1));
				stage.show();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		});
		Button editResource = new Button("Edit resource");
		editResource.setOnAction(e -> {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(
						getClass().getResource("/fxml/editResource.fxml"));
				Parent root1 = (Parent) fxmlLoader.load();
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				// stage.initStyle(StageStyle.UNDECORATED);
				stage.setTitle("Resource");
				stage.setScene(new Scene(root1));
				stage.show();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		});
		leftVbox.getChildren().addAll(editCopies, editResource);
	}

	/**
	 * An initialize method that checks the user if its a staff, and loads the resource image and information when started.
	 */
	@FXML
	public void initialize() {
		if (ScreenManager.getCurrentUser() instanceof User) {
			checkIfBorrowed();
		} else {
			requestbutt.setDisable(true);
			setupStaffButtons();

		}
		loadResourceImage();
		loadResourceInformation();
		
		if(!(ScreenManager.currentResource.getClass() == DVD.class || 
		        ScreenManager.currentResource.getClass() == Game.class))
		{
		    viewTrailerButton.setDisable(true);
		}

	}

}
