package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
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
import model.DBHelper;
import model.DVD;
import model.Game;
import model.Laptop;
import model.Resource;
import model.Review;
import model.User;

/**
 * The gui that appears when a resource is clicked, shows the information about
 * a resource and allows the user to request a copy if there is a free copy
 * available.
 * 
 * @author Joe Wright
 * @author Oliver Harris
 */
public class ResourceInformationController {

    /**The width of the image showing related resources. Its value is {@value}.*/
    private static final int PREVIEW_IMG_WIDTH = 110;
    
    /**The height of the image showing related resources. Its value is {@value}.*/
    private static final int PREVIEW_IMG_HEIGHT = 140;
    
    private static final int RES_IMG_WIDTH = 200;
    private static final int RES_IMG_HEIGHT = 200;

    private static final double ROUND = 100.0;
    private static final double REVIEW_SPACING = 8;

    // star, name,what,when
    private static final int STAR_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int REVIEW_INDEX = 2;
    private static final int WHEN_INDEX = 3;

    @FXML
    private BorderPane borderpane1;// borderpane

    @FXML
    private AnchorPane leftanchor;// anchor for left side of borderpane

    @FXML
    private VBox leftvbox;// vbox in left anchor

    @FXML
    private ImageView resourceimage;// resource image holder

    @FXML
    private AnchorPane centeranchor;// anchor pane in the center of borderpane

    @FXML
    private VBox centervbox;// vbox in center anchor

    @FXML
    private TextArea centertextarea;// textarea in center anchor

    @FXML
    private AnchorPane bottomanchor;// anchor for bottom of borderpane

    @FXML
    private Button requestbutt;// request copy button

    @FXML
    private Button viewTrailerButton;

    @FXML
    private VBox leftVbox;// vbox in left anchor pane

    @FXML
    private Label copytext;// textbox showing copies available

    @FXML
    private Label resourceName;// resources name

    @FXML
    private Text overLimit;

    @FXML
    private VBox seeReviews;

    /**An Hbox used to show resources that are part of the same series.*/
    @FXML
    private HBox seriesBox;

    /**An Hbox used to show resources that are related to this one.*/
    @FXML
    private HBox otherBox;
    
    /**The resource for which info is displayed by the scene of this controller.*/
    private Resource resource;

    /**
     * Mouse Click Handler for clicking related resources images and displaying
     * their detailed info.
     */
    private final EventHandler<MouseEvent> clickHandler;

    /**
     * Makes a new copy controller and makes a new click handler meant for an
     * ImageView to display the detailed resource view of the resource whose
     * image is in the ImageView.
     */
    public ResourceInformationController() {
        clickHandler = event -> {
        	Resource newSceneResource = Resource.getResource(Integer.parseInt((
            		(ImageView) event.getSource()).getId()));

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/fxml/resourceInfoScene.fxml"));
                Parent sceneRoot = (Parent) fxmlLoader.load();
                ResourceInformationController controller = fxmlLoader.getController();
                controller.setResource(newSceneResource);
                
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Resource Information");
                stage.setScene(new Scene(sceneRoot));
                stage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    /**
     * Sets new scene on stage within program using fxml file provided.
     *
     * @param sceneFXML The scene location.
     * @param event The event.
     */
    public void changeScene(MouseEvent event, String sceneFXML) {
        try {
            // create new scene object
            Parent root = FXMLLoader.load(getClass().getResource(sceneFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene()
                .getWindow();
            stage.getScene().setRoot(root);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method that deals with reviews and if the user is staff, a remove button
     * appears.
     */
    private void dealWithReviews() {
        int resourceId = resource.getUniqueID();
        boolean hasReviews = Review.hasReviews(resourceId);

        if (hasReviews) {

            HBox avg = new HBox(); // ready for images
            Text avgText = new Text("Rating: " +
                Math.round(Review.getAvgStar(resourceId) * ROUND) /
                    ROUND);
            avgText.setStyle("-fx-font: 24 arial;");
            avg.getChildren().add(avgText);
            seeReviews.getChildren().add(avg);

            // Now for the actual reviews
            for (String[] review : Review.getReviews(resourceId)) {

                VBox topV = new VBox();
                HBox topH = new HBox();
                // star, name,what,when
                Text title = new Text(" from " + review[NAME_INDEX]);
                Text when = new Text(" [" + review[WHEN_INDEX] + "]");
                when.setStyle("-fx-font:12 arial;");
                Text star = new Text("Rating: " + review[STAR_INDEX]);
                star.setFill(Color.GREEN);
                Text reviewText = new Text(review[REVIEW_INDEX]);
                reviewText.setStyle("-fx-font:15 arial;");
                topH.getChildren().addAll(star, title, when);

                // if staff , add button.
                if (ScreenManager.getCurrentUser() instanceof model.Librarian) {

                    Button removeReview = new Button("Remove");
                    removeReview.setOnAction(event -> {
                        Review.removeReview(Integer.valueOf(review[4]));
                        seeReviews.getChildren().clear();

                        dealWithReviews();
                    });
                    topV.getChildren().addAll(topH, reviewText, removeReview);
                }
                else {
                    topV.getChildren().addAll(topH, reviewText);
                }
                seeReviews.getChildren().add(topV);

            }
            seeReviews.setSpacing(REVIEW_SPACING);

        }
        else {
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
        int uniqueId = resource.getUniqueID();
        String title = resource.getTitle();
        int year = resource.getYear();
        dealWithReviews();

        resourceName.setText(title);
        resourceName.setWrapText(true);
        // Adds all the common attributes to the text area
        centertextarea.appendText("UniqueID: " + Integer.toString(uniqueId) +
            "\nTitle: " + title + "\nYear: " + Integer.toString(year));

        // If the resource is a Book, it will add the book attributes to the
        // text area.
        if (resource instanceof Book) {
            Book currentBook = (Book) resource;
            String author = currentBook.getAuthor();
            String publisher = currentBook.getPublisher();
            String genre = currentBook.getGenre();
            String isbn = currentBook.getISBN();
            String language = currentBook.getLanguage();

            centertextarea.appendText("\nAuthor: " + author + "\nPublisher: " +
                publisher + "\nGenre: " + genre + "\nISBN: " + isbn +
                "\nLanguage: " + language);

            // If the resource is a Laptop, it will add the laptop attributes to
            // the text area.
        }
        else if (resource instanceof Laptop) {
            Laptop currentLaptop = (Laptop) resource;
            String manufacturer = currentLaptop.getManufacturer();
            String model = currentLaptop.getModel();
            String operatingSystem = currentLaptop.getOS();

            centertextarea.appendText("\nManufacturer: " + manufacturer +
                "\nModel: " + model + "\nOS: " + operatingSystem);

            // If the resource is a DVD, it will add the attributes of a dvd to
            // the text area.
        }
        else if (resource instanceof DVD) {
            DVD currentDVD = (DVD) resource;
            String director = currentDVD.getDirector();
            int runtime = currentDVD.getRuntime();
            String language = currentDVD.getLanguage();

            centertextarea
                .appendText("\nDirector: " + director + "\nRuntime: " +
                    Integer.toString(runtime) + "\nLanguage: " + language);

            // If the resource is a Game, it will add the attributes of a Game
            // to
            // the text area.
        }
        else if (resource instanceof Game) {
            Game currentGame = (Game) resource;

            String publisher = currentGame.getPublisher();
            String genre = currentGame.getGenre();
            String rating = currentGame.getRating();
            String multiplayer = currentGame.getMultiplayerSupport();

            centertextarea.appendText("\nPublisher: " + publisher +
                "\nGenre: " + genre + "\nRating: " + rating +
                "\nHas Mulitplayer? " + multiplayer);
        }

        // This sets the textbox depending if the number of copies is equal to 0
        // or not.
        if (resource.getNrOfCopies() == 0) {
            copytext.setText("All Copies are currently being borrowed.");
        }
        else {
            copytext.setText("Copies: " + Integer
                .toString(resource.getNrOfCopies()));
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
        resourceimage.setImage(resource.getThumbnail());
    }

    /**
     * When the button is clicked, it will send a request to a librarian and
     * they can either decline or accept said request.
     *
     * @param event button being pressed.
     */
    @FXML
    public void requestCopy(MouseEvent event) {
        resource.addPendingRequest((User) ScreenManager.getCurrentUser());
        AlertBox.showInfoAlert("Requested!");
    }

    /**
     * The method that gets called every time the View Trailer button is
     * clicked. It opens a new window showign an embedded youtube video of a
     * trailer for the selected DVD or video game.
     * 
     * @param actionEvent The event that triggers the call of this method.
     */
    @FXML
    public void showTrailerWindow(ActionEvent actionEvent) {
    	
    	for (Integer i : resource.getSameSeriesResources()) {
            System.out.println(i.toString());
        }
        System.out.println();
        for (Integer i : resource.getOtherRelatedResources()) {
            System.out.println(i.toString());
        }

        if (resource.getClass() == DVD.class) {
            DVD currentMovie = (DVD) resource;

            String title = currentMovie.getTitle();

            MovieTrailerView trailerView = new MovieTrailerView(title);

            if (trailerView.getWebView() != null) {
                Scene trailerScene = new Scene(trailerView.getWebView(),
                    trailerView.getPrefViewWidth(),
                    trailerView.getPrefViewHeight());

                Stage trailerWindow = new Stage();
                trailerWindow.setTitle(trailerView.getTrailerDescription().getName());

                trailerWindow.setOnHidden(e -> {
                    trailerView.stop();
                });

                trailerWindow.setScene(trailerScene);
                trailerWindow.show();
            }
        }
        else if (resource.getClass() == Game.class) {
            Game currentGame = (Game) resource;

            String title = currentGame.getTitle();

            GameTrailerView trailerView = new GameTrailerView(title);

            if (trailerView.getWebView() != null) {
                Scene trailerScene = new Scene(trailerView.getWebView(),
                    trailerView.getPrefViewWidth(),
                    trailerView.getPrefViewHeight());

                Stage trailerWindow = new Stage();
                trailerWindow.setTitle(trailerView.getVideoName());

                trailerWindow.setOnHidden(e -> {
                    trailerView.stop();
                });

                trailerWindow.setScene(trailerScene);
                trailerWindow.show();
            }
        }
    }

    /**
     * This checks if the current user is currently borrowing the resource, if
     * they have then it will disable the request copy button.
     */
    private void checkIfBorrowed() {
        User user = (User) ScreenManager.getCurrentUser();

        if (user.isBorrowing(resource)) {

            requestbutt.setDisable(true);
        }
    }

    /**
     * These are only buttons that appear when the user is a staff, these
     * buttons allow the librarian to manage the resources.
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
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        });
        Button editResource = new Button("Edit resource");
        editResource.setOnAction(e -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/fxml/editResource.fxml"));
                Parent sceneRoot = (Parent) fxmlLoader.load();
                ResourceController editController = fxmlLoader.getController();
                editController.setResource(resource);
                
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                // stage.initStyle(StageStyle.UNDECORATED);
                stage.setTitle("Resource");
                stage.setScene(new Scene(sceneRoot));
                stage.show();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        });
        leftVbox.getChildren().addAll(editCopies, editResource);
    }

    /**
     * Disable buttons if the user cannot borrow the resource due to limits.
     */
    private void setupLimit() {
        User user = (User) ScreenManager.getCurrentUser();
        if (user.exceedLimit(resource)) {
            requestbutt.setDisable(true);
            overLimit.setVisible(true);

        }
    }

    /**
     * Initializes the window. Loads the resource images, information, hides
     * view trailer button if needed and loads the images of resources related
     * to this one.
     */
    public void prepare() {
        if (ScreenManager.getCurrentUser() instanceof User) {
            checkIfBorrowed();
            setupLimit();
        }
        else {
            requestbutt.setDisable(true);
            setupStaffButtons();

        }
        loadResourceImage();
        loadResourceInformation();

        if (!(resource.getClass() == DVD.class || resource.getClass() == Game.class)) 
        {
            viewTrailerButton.setDisable(true);
            viewTrailerButton.setVisible(false);
        }

        for (Integer id : resource.getSameSeriesResources()) {
            Resource sameSeriesResource = Resource.getResource(id);

            ImageView resourceImageView = new ImageView(
                sameSeriesResource.getThumbnail());
            resourceImageView.setFitHeight(PREVIEW_IMG_HEIGHT);
            resourceImageView.setFitWidth(PREVIEW_IMG_WIDTH);
            resourceImageView.setId(id + "");
            resourceImageView.setOnMouseClicked(clickHandler);

            seriesBox.getChildren().add(resourceImageView);
        }

        for (Integer id : resource.getOtherRelatedResources()) {
            Resource otherRelatedResource = Resource.getResource(id);

            ImageView resourceImageView = new ImageView(
                otherRelatedResource.getThumbnail());
            resourceImageView.setFitHeight(PREVIEW_IMG_HEIGHT);
            resourceImageView.setFitWidth(PREVIEW_IMG_WIDTH);
            resourceImageView.setId(id + "");
            resourceImageView.setOnMouseClicked(clickHandler);

            otherBox.getChildren().add(resourceImageView);
        }
    }
    
    /**
     * Gets the resource whose info is displayed by the window of this controller.
     * @return the resource whose info is displayed by the window of this controller.
     */
    public Resource getResource()
    {
    	return resource;
    }
    
    /**
	 * Changes the resource whose info is displayed by the scene of this controller.
	 * @param resource The new resource to be viewed.
	 */
    public void setResource(Resource resource)
    {
    	this.resource = resource;
    	prepare();
    }
}
