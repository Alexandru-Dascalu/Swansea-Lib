package application;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import model.User;

public class NotificationSettingController {
    
    @FXML
    private GridPane settingsGrid;
    
    @FXML
    private CheckBox newResourcesBox;
    
    @FXML
    private CheckBox requestApprovalBox;
    
    @FXML
    private CheckBox newEventBox;
    
    @FXML
    private CheckBox nearingEventBox;
    
    @FXML
    public void initialize() {
        boolean[] userSettings = ((User) ScreenManager.getCurrentUser()).getNotificationSettings();
        
        newResourcesBox.setSelected(userSettings[0]);
        requestApprovalBox.setSelected(userSettings[1]);
        newEventBox.setSelected(userSettings[2]);
        nearingEventBox.setSelected(userSettings[3]);
    }
}
