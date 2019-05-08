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
        User currentUser = (User) ScreenManager.getCurrentUser();
        
        
    }
}
