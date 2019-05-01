package application;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Book;
import model.DBHelper;
import model.DVD;
import model.Game;
import model.Laptop;
import model.Resource;
import model.ResourceNotification;

/**
* Resource Controller is a class that setups up the resources and updates them.
* @author Oliver Harris
* 
* With Game Resource added by Charles Day on 16/03/19
*/
public class ResourceController {

	@FXML
	VBox resourceBlock;
	
	/**
	 * Sets up the book resource screen.
	 */
	private void setupBook() {
		//creates instance of a book
		Book book = (Book) ScreenManager.getCurrentResource();
		
		//inserts the common resource attributes
		HBox titleBox = new HBox();
		Text titleText = new Text("Title");
		TextField titleField = new TextField (book.getTitle());
		titleBox.getChildren().addAll(titleText,titleField);
		
		HBox yearBox = new HBox();
		Text yearText = new Text("Year");
		TextField yearField = new TextField (String.valueOf(book.getYear()));
		yearBox.getChildren().addAll(yearText,yearField);
		
		//the rest are not from resource
		
		HBox authorBox = new HBox();
		Text authorText = new Text("Author");
		TextField authorField = new TextField (book.getAuthor());
		authorBox.getChildren().addAll(authorText,authorField);
		
		HBox publishBox = new HBox();
		Text publishText = new Text("Publisher");
		TextField publishField = new TextField (book.getPublisher());
		publishBox.getChildren().addAll(publishText,publishField);
		
		HBox genreBox = new HBox();
		Text genreText = new Text("Genre");
		TextField genreField = new TextField (book.getGenre());
		genreBox.getChildren().addAll(genreText,genreField);
		
		HBox iBox = new HBox();
		Text iText = new Text("ISBN");
		TextField iField = new TextField (book.getISBN());
		iBox.getChildren().addAll(iText,iField);
		
		HBox languageBox = new HBox();
		Text languageText = new Text("Language");
		TextField languageField = new TextField (book.getLanguage());
		languageBox.getChildren().addAll(languageText,languageField);
		
		HBox imgBox = new HBox();
		Text imgText = new Text("Path to image");
		TextField imgField = new TextField ();
		imgBox.getChildren().addAll(imgText,imgField);
		
		Button button = new Button("Save");
		button.setOnAction(e -> {
			updateBook(titleField.getText(), yearField.getText(), 
					authorField.getText(), publishField.getText(), 
					genreField.getText(), iField.getText(), 
					languageField.getText(), imgField.getText());
			makeNewNotification(book);
		});
		resourceBlock.getChildren().addAll(titleBox,yearBox,
				authorBox,publishBox,genreBox,iBox,languageBox,imgBox,button);
	}
	
	
	/**
	 * Sets up the game resource screen.
	 */
	private void setupGame() {
		//creates instance of a game
		Game game = (Game) ScreenManager.getCurrentResource();
		
		//inserts the common resource attributes
		HBox titleBox = new HBox();
		Text titleText = new Text("Title");
		TextField titleField = new TextField (game.getTitle());
		titleBox.getChildren().addAll(titleText,titleField);
		
		HBox yearBox = new HBox();
		Text yearText = new Text("Year");
		TextField yearField = new TextField (String.valueOf(game.getYear()));
		yearBox.getChildren().addAll(yearText,yearField);
		
		//the rest are not from resource
		
		HBox publishBox = new HBox();
		Text publishText = new Text("Publisher");
		TextField publishField = new TextField (game.getPublisher());
		publishBox.getChildren().addAll(publishText,publishField);
		
		HBox genreBox = new HBox();
		Text genreText = new Text("Genre");
		TextField genreField = new TextField (game.getGenre());
		genreBox.getChildren().addAll(genreText,genreField);
		
		HBox ratingBox = new HBox();
		Text ratingText = new Text("Rating");
		TextField ratingField = new TextField (game.getRating());
		ratingBox.getChildren().addAll(ratingText,ratingField);
		
		HBox multiBox = new HBox();
		Text multiText = new Text("Multiplayer");
		TextField multiField = new TextField (game.getMultiplayerSupport());
		multiBox.getChildren().addAll(multiText,multiField);
		
		HBox imgBox = new HBox();
		Text imgText = new Text("Path to image");
		TextField imgField = new TextField ();
		imgBox.getChildren().addAll(imgText,imgField);
		
		Button button = new Button("Save");
		button.setOnAction(e -> {
			updateGame(titleField.getText(), yearField.getText(), publishField.getText(), 
					genreField.getText(), ratingField.getText(), 
					multiField.getText(), imgField.getText());
			makeNewNotification(game);
		});
		resourceBlock.getChildren().addAll(titleBox,yearBox,
				publishBox,genreBox,ratingBox,multiBox,imgBox,button);
	}
	
	
	/**
	 * Setups the DVD resource.
	 */
	private void setupDVD() {
		DVD dvd = (DVD) ScreenManager.getCurrentResource();
		
		//sets up the common attributes of all resrouces
		HBox titleBox = new HBox();
		Text titleText = new Text("Title");
		TextField titleField = new TextField (dvd.getTitle());
		titleBox.getChildren().addAll(titleText,titleField);
		
		HBox yearBox = new HBox();
		Text yearText = new Text("Year");
		TextField yearField = new TextField (String.valueOf(dvd.getYear()));
		yearBox.getChildren().addAll(yearText,yearField);
		
		//the rest are not from resource
		
		HBox directorBox = new HBox();
		Text directorText = new Text("Director");
		TextField directorField = new TextField (dvd.getDirector());
		directorBox.getChildren().addAll(directorText,directorField);
		
		HBox runtimeBox = new HBox();
		Text runtimeText = new Text("Runtime");
		TextField runtimeField = new TextField (String.valueOf(dvd.getRuntime()));
		runtimeBox.getChildren().addAll(runtimeText,runtimeField);
		
		HBox langBox = new HBox();
		Text langText = new Text("Language");
		TextField langField = new TextField (dvd.getLanguage());
		langBox.getChildren().addAll(langText,langField);
		
		HBox subtitlesBox = new HBox();
		Text subtitlesText = new Text("Subtitle language");
		String subs = "";
		for(String sub : dvd.getSubtitleLanguages()) {
			if(subs.equals("")) {
				subs = sub;
			}else {
				subs +=","+sub;
			}
		}
		TextField subtitlesField = new TextField (subs);
		subtitlesBox.getChildren().addAll(subtitlesText,subtitlesField);
		
		HBox imgBox = new HBox();
		Text imgText = new Text("Path to image");
		TextField imgField = new TextField ();
		imgBox.getChildren().addAll(imgText,imgField);
		
		
		Button button = new Button("Save");
		button.setOnAction(e -> {
			updateDVD(titleField.getText(),yearField.getText(),
					directorField.getText(),runtimeField.getText(),
					langField.getText(),subtitlesField.getText(),
					imgField.getText());
			makeNewNotification(dvd);
		});
		resourceBlock.getChildren().addAll(titleBox,yearBox,
				directorBox,runtimeBox,langBox,subtitlesBox,imgBox,button);
	}
	
	
	/**
	 * Setups the Laptop resource.
	 */
	private void setupLaptop() {
		Laptop laptop = (Laptop) ScreenManager.getCurrentResource();
		
		//sets up the common attributes of all resrouces
		HBox titleBox = new HBox();
		Text titleText = new Text("Title");
		TextField titleField = new TextField (laptop.getTitle());
		titleBox.getChildren().addAll(titleText,titleField);
		
		HBox yearBox = new HBox();
		Text yearText = new Text("Year");
		TextField yearField = new TextField (String.valueOf(laptop.getYear()));
		yearBox.getChildren().addAll(yearText,yearField);
		
		//the rest are not from resource
		
		HBox manuBox = new HBox();
		Text manuText = new Text("Manufacturer");
		TextField manuField = new TextField (laptop.getManufacturer());
		manuBox.getChildren().addAll(manuText,manuField);
		
		HBox modelBox = new HBox();
		Text modelText = new Text("Model");
		TextField modelField = new TextField (String.valueOf(laptop.getModel()));
		modelBox.getChildren().addAll(modelText,modelField);
		
		HBox OSBox = new HBox();
		Text OSText = new Text("OS");
		TextField OSField = new TextField (laptop.getOS());
		OSBox.getChildren().addAll(OSText,OSField);
	
		HBox imgBox = new HBox();
		Text imgText = new Text("Path to image");
		TextField imgField = new TextField ();
		imgBox.getChildren().addAll(imgText,imgField);
		
		Button button = new Button("Save");
		button.setOnAction(e -> {
			updateLaptop(titleField.getText(),yearField.getText(),
					manuField.getText(),modelField.getText(),OSField.getText(),
					imgField.getText());
			makeNewNotification(laptop);
		});
		resourceBlock.getChildren().addAll(titleBox,yearBox,manuBox,modelBox,
				OSBox,imgBox,button);
	}
	

	/**
	 * Updates a laptop in the database.
	 * @param title of laptop.
	 * @param year of laptop.
	 * @param manu of laptop.
	 * @param model of laptop.
	 * @param OS of laptop.
	 * @param img of laptop.
	 */
	private void updateLaptop(String title,String year,String manu,String model,
		String OS, String img) {
		//Checks if the year is a number
		boolean goAhead = true;
		Image image = null;
		try {
			Integer.parseInt(year);
		}catch (NumberFormatException  e) {
			goAhead = false;
			AlertBox.showInfoAlert("Year must be a number");
		}
		
		try {
			if(!img.equals("")) {
			image = new Image(new File(img).toURI().toString());
			}
		}catch (Exception e) {
			goAhead = false;
			AlertBox.showInfoAlert("Image not found");
		}
		
		if(goAhead) {
			Laptop laptop = (Laptop) ScreenManager.getCurrentResource();
			if(!img.equals("")) {
				laptop.setThumbnail(image);
				laptop.setThumbnailDatabase(img);
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
	 * @param title of the game.
	 * @param year of the game.
	 * @param author of the game.
	 * @param publish of the game.
	 * @param genre of the game.
	 * @param rating of the game.
	 * @param MultiplayerSupport of the game.
	 * @param img of the game.
	 */
	private void updateGame(String title, String year,
			String publisher, String genre, String rating, String multiplayer, 
			String img){
		//Checks if the year is a number
		boolean goAhead = true;
		Image image = null;
		try {
			Integer.parseInt(year);
		}catch (NumberFormatException  e) {
			goAhead = false;
			AlertBox.showInfoAlert("Year must be a number");
		}
		
		try {
			if(!img.equals("")) {
				image = new Image(new File(img).toURI().toString());
			}
		}catch (Exception e) {
			goAhead = false;
			System.out.println(e);
			AlertBox.showInfoAlert("Image not found");
		}
		
		//If the year is a number, update the book attributes
		if (goAhead) {
			Game resource = (Game) ScreenManager.getCurrentResource();
			if(!img.equals("")) {
				resource.setThumbnail(image);
				resource.setThumbnailDatabase(img);
				}
			resource.setTitle(title);
			resource.setYear(Integer.parseInt(year));
			resource.setPublisher(publisher);
			resource.setGenre(genre);
			resource.setRating(rating);
			resource.setMultiplayerSupport(multiplayer);

			AlertBox.showInfoAlert("Updated!");
		}
	}
	
		
	/**
	 * Updates a book in the database.
	 * @param title of the book.
	 * @param year of the book.
	 * @param author of the book.
	 * @param publish of the book.
	 * @param genre of the book.
	 * @param ISBN of the book.
	 * @param language of the book.
	 * @param img of the book.
	 */
	private void updateBook(String title, String year, String author,
			String publish, String genre, String ISBN, String language, 
			String img){
		//Checks if the year is a number
		boolean goAhead = true;
		Image image = null;
		try {
			Integer.parseInt(year);
		}catch (NumberFormatException  e) {
			goAhead = false;
			AlertBox.showInfoAlert("Year must be a number");
		}
		
		try {
			if(!img.equals("")) {
				image = new Image(new File(img).toURI().toString());
			}
		}catch (Exception e) {
			goAhead = false;
			System.out.println(e);
			AlertBox.showInfoAlert("Image not found");
		}
		
		//If the year is a number, update the book attributes
		if (goAhead) {
			Book resource = (Book) ScreenManager.getCurrentResource();
			if(!img.equals("")) {
				resource.setThumbnail(image);
				resource.setThumbnailDatabase(img);
				}
			resource.setTitle(title);
			resource.setYear(Integer.parseInt(year));
			resource.setAuthor(author);
			resource.setPublisher(publish);
			resource.setGenre(genre);
			resource.setISBN(ISBN);
			resource.setLanguage(language);

			AlertBox.showInfoAlert("Updated!");
		}
	}
	
	
	/**
	 * Updates the DVD in the database.
	 * @param title of dvd.
	 * @param year of dvd.
	 * @param director of dvd.
	 * @param runtime of dvd.
	 * @param language of dvd.
	 * @param subtitles of dvd.
	 * @param img of dvd.
	 */
	private void updateDVD(String title, String year, String director, 
			String runtime, String language, String subtitles, String img){
		boolean goAhead = true;
		Image image = null;
		try {
			Integer.parseInt(year);
			Integer.parseInt(runtime);
		}catch (NumberFormatException  e) {
			goAhead = false;
			AlertBox.showInfoAlert("Year and runtime must be a number");
		}
		
		try {
			if(!img.equals("")) {
				image = new Image(new File(img).toURI().toString());
			}
		}catch (Exception e) {
			goAhead = false;
			AlertBox.showInfoAlert("Image not found");
		}
		
		if (goAhead) {
			DVD resource = (DVD) ScreenManager.getCurrentResource();
			resource.setTitle(title);
			resource.setYear(Integer.parseInt(year));
			resource.setDirector(director);
			resource.setLanguage(language);
			
			String[] subs = subtitles.split(",");
			for(String sub : subs) {
				resource.deleteSubtitle(sub);
				resource.addSubtitle(sub);
			}
			
			if(!img.equals("")) {
				resource.setThumbnail(image);
				resource.setThumbnailDatabase(img);
				}
			resource.setRuntime(Integer.parseInt(runtime));
			
			AlertBox.showInfoAlert("Updated!");
		}
	}
	
	private void makeNewNotification(Resource resource) {
		try {
			Connection dbConnection = DBHelper.getConnection();
			PreparedStatement insertStatement = dbConnection.prepareStatement(
					"INSERT INTO notification (message, image) VALUES (?, ?)");
			
			insertStatement.setString(1, ResourceNotification.getNewAdditionMsg(resource));
			insertStatement.setString(2, resource.getThumbnail().impl_getUrl());
			insertStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Initalize method that creates the resource on start up depending on the resource.
	 */
	@FXML
	 public void initialize() {
		Resource resource = ScreenManager.getCurrentResource();
		if(resource instanceof DVD) {
			setupDVD();
		}else if(resource instanceof Book) {
			setupBook();
		}else if(resource instanceof Laptop) {
			setupLaptop();
		}else if(resource instanceof Game) {
			setupGame();
		}
	}
}
