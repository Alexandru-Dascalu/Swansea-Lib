package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;



/**
 * AlertBox method
* @author Oliver Harris.
*
*/
public class AlertBox {
	/**
	 * Generate a popup.
	 * @param text The text to be displayed.
	 */
	public static void alertDone(String text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText(text);

		alert.showAndWait();
	}
}