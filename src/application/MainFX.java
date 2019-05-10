package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import model.DBHelper;
import model.Resource;
import javafx.scene.Parent;
import javafx.scene.Scene;
/**
 * Class that loads the database and loads the log in scene.
 * @author Kane Miles.
 * @version 1.
 */
public class MainFX extends Application {

	//stores current scene (updated during each scene change).
	private static Scene currentScene;

	//stores minimum width and height of stage.
	private final int MIN_WIDTH = 600;
	private final int MIN_HEIGHT = 400;

	/*public static void main(String args[]) {
		launch();
	}*/
	
	/**
	 * Initialises stage object and shows login scene on start-up.
	 * @param primaryStage stage the main screen.
	 */
	@Override
	public void start(Stage primaryStage) {

		//Check to see if the database ready
		DBHelper.tableCheck();
		Resource.loadDatabaseResources();

		try {
			//create initial scene object.
			Parent root = FXMLLoader.load(
					getClass().getResource("/fxml/loginScene.fxml"));
			currentScene = new Scene(root);
			currentScene.getStylesheets().add(
					getClass().getResource("/css/application.css").toExternalForm());
			//set stage attributes.
			primaryStage.setScene(currentScene); //set scene within stage
			primaryStage.setMinWidth(MIN_WIDTH); //set minimum width of stage
			primaryStage.setMinHeight(MIN_HEIGHT); //set minimum height of stage
			primaryStage.setMaximized(true); //make stage full screen
			primaryStage.show(); //show stage to user
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
