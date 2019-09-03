package application;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Book;
import model.DBHelper;
import model.DVD;
import model.EventNotification;
import model.Game;
import model.Laptop;
import model.Notification;
import model.Resource;
import model.ResourceNotification;
import model.User;

/**
 * Resource Controller is a class that setups up the resources and updates them.
 * 
 * @author Oliver Harris With Game Resource added by Charles Day on 16/03/19
 */
public class ResourceController {

    @FXML
    private VBox resourceBlock;

    /**The resource for which the editing window is being shown.*/
    private Resource resource;
    
    /**
	 * Changes the resource being edited through this controller.
	 * @param resource The new resource that will be edited.
	 */
    public void setResource(Resource resource)  
    {
    	this.resource = resource;
    }
    
    /**
     * Sets up the book resource screen. Shows suggestions for the librarian to set
     * related resources, but only if the resource has just been made and is not
     * just edited.
     */
    private void setupBook() {
        // creates instance of a book
        Book book = (Book) resource;

        // inserts the common resource attributes
        HBox titleBox = new HBox();
        Text titleText = new Text("Title");
        TextField titleField = new TextField(book.getTitle());
        titleBox.getChildren().addAll(titleText, titleField);

        HBox yearBox = new HBox();
        Text yearText = new Text("Year");
        TextField yearField = new TextField(String.valueOf(book.getYear()));
        yearBox.getChildren().addAll(yearText, yearField);

        // the rest are not from resource

        HBox authorBox = new HBox();
        Text authorText = new Text("Author");
        TextField authorField = new TextField(book.getAuthor());
        authorBox.getChildren().addAll(authorText, authorField);

        HBox publishBox = new HBox();
        Text publishText = new Text("Publisher");
        TextField publishField = new TextField(book.getPublisher());
        publishBox.getChildren().addAll(publishText, publishField);

        HBox genreBox = new HBox();
        Text genreText = new Text("Genre");
        TextField genreField = new TextField(book.getGenre());
        genreBox.getChildren().addAll(genreText, genreField);

        HBox iBox = new HBox();
        Text iText = new Text("ISBN");
        TextField iField = new TextField(book.getISBN());
        iBox.getChildren().addAll(iText, iField);

        HBox languageBox = new HBox();
        Text languageText = new Text("Language");
        TextField languageField = new TextField(book.getLanguage());
        languageBox.getChildren().addAll(languageText, languageField);

        HBox imgBox = new HBox();
        Text imgText = new Text("Path to image");
        TextField imgField = new TextField();
        imgBox.getChildren().addAll(imgText, imgField);

        Button button = new Button("Save");
        button.setOnAction(e -> {
            boolean isNewAddition = book.getTitle() == null;
            updateBook(titleField.getText(), yearField.getText(), authorField.getText(),
                publishField.getText(), genreField.getText(), iField.getText(),
                languageField.getText(), imgField.getText());

            if (isNewAddition) {
                ResourceNotification.makeNewRsrcNotification(book);

                try {
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/relatedResourceEditor.fxml"));
                    Parent otherReleatedRoot = loader.load();
                    RelatedRsrcController controller = loader.getController();
                    controller.loadResourceHboxes("other related", book);

                    Stage otherResourcesStage = new Stage();
                    otherResourcesStage.setTitle("Other resources that might " + "be related");
                    otherResourcesStage.setScene(new Scene(otherReleatedRoot));
                    otherResourcesStage.setResizable(false);
                    otherResourcesStage.show();

                    loader = new FXMLLoader(
                        getClass().getResource("/fxml/relatedResourceEditor.fxml"));
                    Parent sameSeriesRoot = loader.load();
                    RelatedRsrcController sameSeriesController = loader.getController();
                    sameSeriesController.loadResourceHboxes("same series", book);

                    Stage sameSeriesStage = new Stage();
                    sameSeriesStage.setTitle("Resources that might be part of the same series");
                    sameSeriesStage.setScene(new Scene(sameSeriesRoot));
                    sameSeriesStage.setResizable(false);
                    sameSeriesStage.show();

                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
        resourceBlock.getChildren().addAll(titleBox, yearBox, authorBox, publishBox, genreBox, iBox,
            languageBox, imgBox, button);
    }

    /**
     * Sets up the game resource screen. Shows suggestions for the librarian to set
     * related resources, but only if the resource has just been made and is not
     * just edited.
     */
    private void setupGame() {
        // creates instance of a game
        Game game = (Game) resource;

        // inserts the common resource attributes
        HBox titleBox = new HBox();
        Text titleText = new Text("Title");
        TextField titleField = new TextField(game.getTitle());
        titleBox.getChildren().addAll(titleText, titleField);

        HBox yearBox = new HBox();
        Text yearText = new Text("Year");
        TextField yearField = new TextField(String.valueOf(game.getYear()));
        yearBox.getChildren().addAll(yearText, yearField);

        // the rest are not from resource

        HBox publishBox = new HBox();
        Text publishText = new Text("Publisher");
        TextField publishField = new TextField(game.getPublisher());
        publishBox.getChildren().addAll(publishText, publishField);

        HBox genreBox = new HBox();
        Text genreText = new Text("Genre");
        TextField genreField = new TextField(game.getGenre());
        genreBox.getChildren().addAll(genreText, genreField);

        HBox ratingBox = new HBox();
        Text ratingText = new Text("Rating");
        TextField ratingField = new TextField(game.getRating());
        ratingBox.getChildren().addAll(ratingText, ratingField);

        HBox multiBox = new HBox();
        Text multiText = new Text("Multiplayer");
        TextField multiField = new TextField(game.getMultiplayerSupport());
        multiBox.getChildren().addAll(multiText, multiField);

        HBox imgBox = new HBox();
        Text imgText = new Text("Path to image");
        TextField imgField = new TextField();
        imgBox.getChildren().addAll(imgText, imgField);

        Button button = new Button("Save");
        button.setOnAction(e -> {
            boolean isNewAddition = game.getTitle() == null;
            updateGame(titleField.getText(), yearField.getText(), publishField.getText(),
                genreField.getText(), ratingField.getText(), multiField.getText(),
                imgField.getText());

            if (isNewAddition) {
                ResourceNotification.makeNewRsrcNotification(game);

                try {
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/relatedResourceEditor.fxml"));
                    Parent otherReleatedRoot = loader.load();
                    RelatedRsrcController controller = loader.getController();
                    controller.loadResourceHboxes("other related", game);

                    Stage otherResourcesStage = new Stage();
                    otherResourcesStage.setTitle("Other resources that might be related");
                    otherResourcesStage.setScene(new Scene(otherReleatedRoot));
                    otherResourcesStage.setResizable(false);
                    otherResourcesStage.show();

                    loader = new FXMLLoader(
                        getClass().getResource("/fxml/relatedResourceEditor.fxml"));
                    Parent sameSeriesRoot = loader.load();
                    RelatedRsrcController sameSeriesController = loader.getController();
                    sameSeriesController.loadResourceHboxes("same series", game);

                    Stage sameSeriesStage = new Stage();
                    sameSeriesStage.setTitle("Resources that might be part of " +
                        "the same series");
                    sameSeriesStage.setScene(new Scene(sameSeriesRoot));
                    sameSeriesStage.setResizable(false);
                    sameSeriesStage.show();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
        resourceBlock.getChildren().addAll(titleBox, yearBox, publishBox, genreBox, ratingBox,
            multiBox, imgBox, button);
    }

    /**
     * Setups the DVD resource. Shows suggestions for the librarian to set related
     * resources, but only if the resource has just been made and is not just
     * edited.
     */
    private void setupDVD() {
        DVD dvd = (DVD) resource;

        // sets up the common attributes of all resrouces
        HBox titleBox = new HBox();
        Text titleText = new Text("Title");
        TextField titleField = new TextField(dvd.getTitle());
        titleBox.getChildren().addAll(titleText, titleField);

        HBox yearBox = new HBox();
        Text yearText = new Text("Year");
        TextField yearField = new TextField(String.valueOf(dvd.getYear()));
        yearBox.getChildren().addAll(yearText, yearField);

        // the rest are not from resource

        HBox directorBox = new HBox();
        Text directorText = new Text("Director");
        TextField directorField = new TextField(dvd.getDirector());
        directorBox.getChildren().addAll(directorText, directorField);

        HBox runtimeBox = new HBox();
        Text runtimeText = new Text("Runtime");
        TextField runtimeField = new TextField(String.valueOf(dvd.getRuntime()));
        runtimeBox.getChildren().addAll(runtimeText, runtimeField);

        HBox langBox = new HBox();
        Text langText = new Text("Language");
        TextField langField = new TextField(dvd.getLanguage());
        langBox.getChildren().addAll(langText, langField);

        HBox subtitlesBox = new HBox();
        Text subtitlesText = new Text("Subtitle language");
        String subs = "";
        for (String sub : dvd.getSubtitleLanguages()) {
            if (subs.equals("")) {
                subs = sub;
            }
            else {
                subs += "," + sub;
            }
        }
        TextField subtitlesField = new TextField(subs);
        subtitlesBox.getChildren().addAll(subtitlesText, subtitlesField);

        HBox imgBox = new HBox();
        Text imgText = new Text("Path to image");
        TextField imgField = new TextField();
        imgBox.getChildren().addAll(imgText, imgField);

        Button button = new Button("Save");
        button.setOnAction(e -> {
            boolean isNewAddition = (dvd.getTitle() == null);
            updateDVD(titleField.getText(), yearField.getText(), directorField.getText(),
                runtimeField.getText(), langField.getText(), subtitlesField.getText(),
                imgField.getText());

            if (isNewAddition) {
                ResourceNotification.makeNewRsrcNotification(dvd);

                try {
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/relatedResourceEditor.fxml"));
                    Parent otherReleatedRoot = loader.load();
                    RelatedRsrcController controller = loader.getController();
                    controller.loadResourceHboxes("other related", dvd);

                    Stage otherResourcesSTage = new Stage();
                    otherResourcesSTage.setTitle("Other resources that might be related");
                    otherResourcesSTage.setScene(new Scene(otherReleatedRoot));
                    otherResourcesSTage.show();

                    loader = new FXMLLoader(
                        getClass().getResource("/fxml/relatedResourceEditor.fxml"));
                    Parent sameSeriesRoot = loader.load();
                    RelatedRsrcController sameSeriesController = loader.getController();
                    sameSeriesController.loadResourceHboxes("same series", dvd);

                    Stage sameSeriesStage = new Stage();
                    sameSeriesStage.setTitle("Resources that might be part of the same series");
                    sameSeriesStage.setScene(new Scene(sameSeriesRoot));
                    sameSeriesStage.show();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
        resourceBlock.getChildren().addAll(titleBox, yearBox, directorBox, runtimeBox, langBox,
            subtitlesBox, imgBox, button);
    }

    /**
     * Setups the Laptop resource. Shows suggestions for the librarian to set
     * related resources, but only if the resource has just been made and is not
     * just edited.
     */
    private void setupLaptop() {
        Laptop laptop = (Laptop) resource;

        // sets up the common attributes of all resrouces
        HBox titleBox = new HBox();
        Text titleText = new Text("Title");
        TextField titleField = new TextField(laptop.getTitle());
        titleBox.getChildren().addAll(titleText, titleField);

        HBox yearBox = new HBox();
        Text yearText = new Text("Year");
        TextField yearField = new TextField(String.valueOf(laptop.getYear()));
        yearBox.getChildren().addAll(yearText, yearField);

        // the rest are not from resource

        HBox manuBox = new HBox();
        Text manuText = new Text("Manufacturer");
        TextField manuField = new TextField(laptop.getManufacturer());
        manuBox.getChildren().addAll(manuText, manuField);

        HBox modelBox = new HBox();
        Text modelText = new Text("Model");
        TextField modelField = new TextField(String.valueOf(laptop.getModel()));
        modelBox.getChildren().addAll(modelText, modelField);

        HBox OSBox = new HBox();
        Text OSText = new Text("OS");
        TextField OSField = new TextField(laptop.getOS());
        OSBox.getChildren().addAll(OSText, OSField);

        HBox imgBox = new HBox();
        Text imgText = new Text("Path to image");
        TextField imgField = new TextField();
        imgBox.getChildren().addAll(imgText, imgField);

        Button button = new Button("Save");
        button.setOnAction(e -> {
            boolean isNewAddition = (laptop.getTitle() == null);
            updateLaptop(titleField.getText(), yearField.getText(), manuField.getText(),
                modelField.getText(), OSField.getText(), imgField.getText());

            if (isNewAddition) {
                ResourceNotification.makeNewRsrcNotification(laptop);
            }
        });
        resourceBlock.getChildren().addAll(titleBox, yearBox, manuBox, modelBox, OSBox, imgBox,
            button);
    }

    /**
     * Updates a laptop in the database.
     * 
     * @param title of laptop.
     * @param year of laptop.
     * @param manu of laptop.
     * @param model of laptop.
     * @param OS of laptop.
     * @param thumbnailPath of laptop.
     */
    private void updateLaptop(String title, String year, String manu, String model, String OS,
            String thumbnailPath) {
        // Checks if the year is a number
        boolean goAhead = true;
        Image image = null;
        try {
            Integer.parseInt(year);
        }
        catch (NumberFormatException e) {
            goAhead = false;
            AlertBox.showInfoAlert("Year must be a number");
        }

        try {
            if (!thumbnailPath.equals("")) {
                image = new Image(new File(thumbnailPath).toURI().toString());
            }
        }
        catch (Exception e) {
            goAhead = false;
            AlertBox.showInfoAlert("Image not found");
        }

        if (goAhead) {
            Laptop laptop = (Laptop) resource;
            if (!thumbnailPath.equals("")) {
                laptop.setThumbnail(image);
                laptop.setThumbnailDatabase(thumbnailPath);
                laptop.setThumnailPath(thumbnailPath);
            }
            laptop.setTitle(title);
            laptop.setYear(Integer.parseInt(year));
            laptop.setManufacturer(manu);
            laptop.setModel(model);
            laptop.setOS(OS);
            AlertBox.showInfoAlert("Updated!");
        }
    }

    /**
     * Updates a game in the database.
     * 
     * @param title of the game.
     * @param year of the game.
     * @param publisher The publisher of the game.
     * @param genre The genre of the game.
     * @param rating The rating of the game.
     * @param multiplayer The multiplayer support of the game.
     * @param thumbnailPath of the game.
     */
    private void updateGame(String title, String year, String publisher, String genre,
            String rating, String multiplayer, String thumbnailPath) {
        // Checks if the year is a number
        boolean goAhead = true;
        Image image = null;
        try {
            Integer.parseInt(year);
        }
        catch (NumberFormatException e) {
            goAhead = false;
            AlertBox.showInfoAlert("Year must be a number");
        }

        try {
            if (!thumbnailPath.equals("")) {
                image = new Image(new File(thumbnailPath).toURI().toString());
            }
        }
        catch (Exception e) {
            goAhead = false;
            System.out.println(e);
            AlertBox.showInfoAlert("Image not found");
        }

        // If the year is a number, update the book attributes
        if (goAhead) {
            Game game = (Game) resource;
            if (!thumbnailPath.equals("")) {
            	game.setThumbnail(image);
            	game.setThumbnailDatabase(thumbnailPath);
            	game.setThumnailPath(thumbnailPath);
            }
            game.setTitle(title);
            game.setYear(Integer.parseInt(year));
            game.setPublisher(publisher);
            game.setGenre(genre);
            game.setRating(rating);
            game.setMultiplayerSupport(multiplayer);

            AlertBox.showInfoAlert("Updated!");
        }
    }

    /**
     * Updates a book in the database.
     * 
     * @param title of the book.
     * @param year of the book.
     * @param author of the book.
     * @param publish of the book.
     * @param genre of the book.
     * @param ISBN of the book.
     * @param language of the book.
     * @param thumnbnailPath of the book.
     */
    private void updateBook(String title, String year, String author, String publish, String genre,
            String ISBN, String language, String thumnbnailPath) {
        // Checks if the year is a number
        boolean goAhead = true;
        Image image = null;
        try {
            Integer.parseInt(year);
        }
        catch (NumberFormatException e) {
            goAhead = false;
            AlertBox.showInfoAlert("Year must be a number");
        }

        try {
            if (!thumnbnailPath.equals("")) {
                image = new Image(new File(thumnbnailPath).toURI().toString());
            }
        }
        catch (Exception e) {
            goAhead = false;
            System.out.println(e);
            AlertBox.showInfoAlert("Image not found");
        }

        // If the year is a number, update the book attributes
        if (goAhead) {
            Book book = (Book) resource;
            if (!thumnbnailPath.equals("")) {
                book.setThumbnail(image);
                book.setThumbnailDatabase(thumnbnailPath);
            }

            book.setTitle(title);
            book.setYear(Integer.parseInt(year));
            book.setAuthor(author);
            book.setPublisher(publish);
            book.setGenre(genre);
            book.setISBN(ISBN);
            book.setLanguage(language);
            book.setThumnailPath(thumnbnailPath);

            AlertBox.showInfoAlert("Updated!");
        }
    }

    /**
     * Updates the DVD in the database.
     * 
     * @param title of dvd.
     * @param year of dvd.
     * @param director of dvd.
     * @param runtime of dvd.
     * @param language of dvd.
     * @param subtitles of dvd.
     * @param thumbnailPath of dvd.
     */
    private void updateDVD(String title, String year, String director, String runtime,
            String language, String subtitles, String thumbnailPath) {
        boolean goAhead = true;
        Image image = null;
        try {
            Integer.parseInt(year);
            Integer.parseInt(runtime);
        }
        catch (NumberFormatException e) {
            goAhead = false;
            AlertBox.showInfoAlert("Year and runtime must be a number");
        }

        try {
            if (!thumbnailPath.equals("")) {
                image = new Image(new File(thumbnailPath).toURI().toString());
            }
        }
        catch (Exception e) {
            goAhead = false;
            AlertBox.showInfoAlert("Image not found");
        }

        if (goAhead) {
            DVD dvd = (DVD) resource;
            dvd.setTitle(title);
            dvd.setYear(Integer.parseInt(year));
            dvd.setDirector(director);
            dvd.setLanguage(language);

            String[] subs = subtitles.split(",");
            for (String sub : subs) {
                dvd.deleteSubtitle(sub);
                dvd.addSubtitle(sub);
            }

            if (!thumbnailPath.equals("")) {
                dvd.setThumbnail(image);
                dvd.setThumbnailDatabase(thumbnailPath);
                dvd.setThumnailPath(thumbnailPath);
            }
            dvd.setRuntime(Integer.parseInt(runtime));

            AlertBox.showInfoAlert("Updated!");
        }
    }

    /**
     * Initalize method that creates the resource on start up depending on the
     * resource.
     */
    @FXML
    public void initialize() {
        if (resource instanceof DVD) {
            setupDVD();
        }
        else if (resource instanceof Book) {
            setupBook();
        }
        else if (resource instanceof Laptop) {
            setupLaptop();
        }
        else if (resource instanceof Game) {
            setupGame();
        }
    }
}
