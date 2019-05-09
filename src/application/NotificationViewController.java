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

/**
 * A controller for the window showing the user their notifications.
 * 
 * @author Alexandru Dascalu
 */
public class NotificationViewController {

    /**The VBox inside a scrollpane that contains the HBoxes that display
     *  the unread user notifications.*/
    @FXML
    private VBox newNotificationsBox;

    /**The VBox inside a scrollpane that contains the HBoxes that display
     *  the read user notifications.*/
    @FXML
    private VBox pastNotificationsBox;

    /**A button that when clicked marks all new notifications as read and
     *  moves them to the past notifications tab.*/
    @FXML
    private Button markReadBtn;

    /**
     * Initializes the window of this controller by getting the notifications of
     * this user from the database and adding a corresponding HBox to the correct
     * pane.
     */
    @FXML
    public void initialize() {
        List<Notification> userNotifications = ((User) 
                ScreenManager.getCurrentUser()).getNotifications();

        for (Notification notification : userNotifications) {
            if (notification.isRead()) {
                pastNotificationsBox.getChildren().add(notification.getNotificationBox());
            }
            else {
                newNotificationsBox.getChildren().add(notification.getNotificationBox());
            }
        }
    }

    /**
     * Marks all notifications displayed in the new notification pane as having been
     * read, updates the database accordingly and moves to the past notifications
     * pane.
     */
    @FXML
    private void markAllAsRead() {
        User currentUser = (User) ScreenManager.getCurrentUser();
        List<Notification> userNotifications = currentUser.getNotifications();

        for (Notification notif : userNotifications) {
            if (!notif.isRead()) {
                notif.setRead();
            }
        }

        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement updateStatement = dbConnection.prepareStatement(
                    "UPDATE userNotifications SET seen = ? WHERE username = ? AND seen" + " = ?")) {

            updateStatement.setBoolean(1, true);
            updateStatement.setString(2, currentUser.getUsername());
            updateStatement.setBoolean(3, false);

            updateStatement.executeUpdate();
        }
        catch (SQLException e) {
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
