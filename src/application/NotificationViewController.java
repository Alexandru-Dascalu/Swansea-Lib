package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.DBHelper;
import model.Notification;
import model.User;

public class NotificationViewController {
    
    @FXML
    private BorderPane newNottificationPane;
    
    @FXML
    private VBox newNotificationsBox;
    
    @FXML
    private VBox pastNotificationsBox;
    
    @FXML
    private Button markReadBtn;
    
    @FXML
    public void initialize() {
        List<Notification> userNotifications = ((User)ScreenManager.
                getCurrentUser()).getNotifications();
        
        for(Notification notification: userNotifications) {
            if(notification.isRead()) {
                pastNotificationsBox.getChildren().add(notification.getNotificationBox());
            } else {
                newNotificationsBox.getChildren().add(notification.getNotificationBox());
            }
        }
        
        System.out.println("vbox "+newNotificationsBox.getPrefWidth());
    }
    
    @FXML
    private void markAllAsRead() {
        User currentUser = (User) ScreenManager.getCurrentUser();
        List<Notification> userNotifications = currentUser.getNotifications();
        
        for(Notification notif: userNotifications) {
            if(!notif.isRead()) {
                notif.setRead();
            }
        }
        
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement updateStatement = dbConnection.prepareStatement(
                "UPDATE userNotifications SET seen = ? WHERE username = ? AND seen" +
                " = ?")) {
            
            updateStatement.setBoolean(1, true);
            updateStatement.setString(2, currentUser.getUsername());
            updateStatement.setBoolean(3, false);
            
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert("Because the SQLite database library we use " +
                    " for this program, notifications for this user" +
                    " could not be loaded (database locks up for no reason, says it is" +
                    " busy). Close the program and restart it to see your notifications.");
        }
        
        pastNotificationsBox.getChildren().addAll(newNotificationsBox.getChildren());
        newNotificationsBox.getChildren().clear();
    }
}
