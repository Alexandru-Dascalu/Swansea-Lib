package application;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import model.Notification;
import model.User;

public class NotificationViewController {
    
    @FXML
    private VBox newNotificationsBox;
    
    @FXML
    private VBox pastNotificationsBox;
    
    @FXML
    public void initialize() {
        List<Notification> userNotifications = ((User)ScreenManager.getCurrentUser()).getNotifications();
        
        for(Notification notification: userNotifications) {
            if(notification.isRead()) {
                pastNotificationsBox.getChildren().add(notification.getNotificationBox());
            } else {
                newNotificationsBox.getChildren().add(notification.getNotificationBox());
            }
        }
        
        System.out.println("vbox "+newNotificationsBox.getPrefWidth());
    }
}
