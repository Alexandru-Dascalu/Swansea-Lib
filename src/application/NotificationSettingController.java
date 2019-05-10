package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.User;

/**
 * A controller for the window that shows the user their notification 
 * settings. This controller allows the user to change their notification
 * settings.
 * @author Alexandru Dascalu.
 */
public class NotificationSettingController {

    /**A check box for selecting the view new resources notification setting.*/
    @FXML
    private CheckBox newResourcesBox;

    /**A check box for selecting the view resource request approval notification
     *  setting.*/
    @FXML
    private CheckBox requestApprovalBox;

    /**A check box for selecting the view new events notification setting.*/
    @FXML
    private CheckBox newEventBox;

    /**A check box for selecting the view nearing resources notification setting.*/
    @FXML
    private CheckBox nearingEventBox;

    /**A button that when clicked saves the current selection of check boxes to the
     *  settings in the database.*/
    @FXML
    private Button saveSettingsBtn;

    /** The current user of the application, whose details are being viewed. */
    private User currentUser;

    /**
     * Initializes the stage of this controller. It loads the user's settings into
     * the GUI.
     */
    @FXML
    public void initialize() {
        currentUser = (User) ScreenManager.getCurrentUser();
        boolean[] userSettings = currentUser.getNotificationSettings();

        newResourcesBox.setSelected(userSettings[0]);
        requestApprovalBox.setSelected(userSettings[1]);
        newEventBox.setSelected(userSettings[2]);
        nearingEventBox.setSelected(userSettings[3]);
    }

    /**
     * Saves the current check box selection of settings into the database and
     * closes this window. Also displays an information box telling the user about
     * the successful save.
     */
    @FXML
    private void saveSettings() {
        currentUser.updateNotificationSetting("newResourcesSetting", newResourcesBox.isSelected());
        currentUser.updateNotificationSetting("requestApprvlSetting",
            requestApprovalBox.isSelected());
        currentUser.updateNotificationSetting("newEventSetting", newEventBox.isSelected());
        currentUser.updateNotificationSetting("nearingEventSetting", nearingEventBox.isSelected());

        Stage settingsStage = (Stage) saveSettingsBtn.getScene().getWindow();
        settingsStage.close();

        AlertBox.showInfoAlert("Your notification settings have been saved!");
    }
}
