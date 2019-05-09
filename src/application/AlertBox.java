package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * A class with some static methods that let you easily display an alert box to
 * the screen with the message you want.
 * 
 * @author Oliver Harris
 * @author Alexandru Dascalu
 */
public class AlertBox {

    /**
     * Generate an information popup with the given message.
     * 
     * @param text The text to be displayed.
     */
    public static void showInfoAlert(String text) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(text);

        alert.showAndWait();
    }

    /**
     * Generates an error message popup with the given message.
     * 
     * @param text The message to be displayed by the alert box.
     */
    public static void showErrorAlert(String text) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(text);

        alert.showAndWait();
    }
}
