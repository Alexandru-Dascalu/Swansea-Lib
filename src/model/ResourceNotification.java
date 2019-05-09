package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.AlertBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A notification about a new resource or about the approval of a request to
 * borrow a copy. A resource notification has a small image representing the
 * thumbnail of the resource associated with the notification.
 * 
 * @author Alexandru Dascalu
 */
public class ResourceNotification extends Notification {

    /**
     * A small image representing the resource associated with the notification. Is
     * is a small copy of the thumbnail of the resource.
     */
    private final Image resourceImage;

    /**
     * Makes a new resource notification.
     * 
     * @param message The message of the new notification.
     * @param isRead Whether the notification has been marked read by the user.
     * @param imagePath The path to the thumbnail of the resource for which the
     *        notification is made.
     */
    public ResourceNotification(String message, boolean isRead, String imagePath) {
        super(message, isRead);
        this.resourceImage = new Image(imagePath, IMAGE_WIDTH, IMAGE_HEIGHT, true, true);
    }

    /**
     * Makes the message that should be displayed for a notification when the given
     * event has just been added.
     * 
     * @param resource The newly added resource for which this message is made.
     * @return a message for a notification for a new resource.
     */
    public static String getNewAdditionMsg(Resource resource) {
        return "A new " + getClassName(resource) + " has been added " + "since your last log in! " +
            resource.getTitle() + " is now in the library!";
    }

    /**
     * Makes the message that should be displayed for a notification when the given
     * resource has just been added.
     * 
     * @param resource The resource for which a borrow request has been approved for
     *        which this message is made.
     * @return a message for a new approval notification.
     */
    public static String getRequestApprvlMsg(Resource resource) {
        return "Your request to borrow " + resource.getTitle() + " has" +
            " been approved! You are now borrowing said " + getClassName(resource) + ".";
    }

    /**
     * Makes a new notification in the database for a newly made resource. It also
     * associates in the database the newly made notification with users with the
     * new resource notification setting turned on.
     * 
     * @param resource the new resource for which a notification is made.
     */
    public static void makeNewRsrcNotification(Resource resource) {

        int notificationID;
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO notification (message, image) VALUES (?, ?)")) {

            insertStatement.setString(1, ResourceNotification.getNewAdditionMsg(resource));
            insertStatement.setString(2, resource.getThumbnailPath());
            insertStatement.executeUpdate();

            notificationID = insertStatement.getGeneratedKeys().getInt(1);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        List<String> notificationUsers = getNewResourcesNotificationUsers();
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                "INSERT INTO userNotifications VALUES (?, ?, false)")) {

            for (String username : notificationUsers) {
                insertStatement.setInt(1, notificationID);
                insertStatement.setString(2, username);
                insertStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes a new notification in the database for a newly approved borrow request,
     * if the given users notification settings allow. It also associates in the
     * database the newly made notification with users with the new event
     * notification setting turned on.
     * 
     * @param resource the resource for which the user made a request for which a
     *        notification is made.
     * @param borrower the user whose request has just been approved.
     */
    public static void makeApprovalNotification(Resource resource, User borrower) {
        borrower.loadNotificationSettings();
        if (borrower.getNotificationSettings()[1]) {
            int notificationID;
            try (Connection dbConnection = DBHelper.getConnection();
                    PreparedStatement insertStatement = dbConnection.prepareStatement(
                        "INSERT INTO notification (message, image) VALUES (?, ?)")) {

                insertStatement.setString(1, ResourceNotification.getRequestApprvlMsg(resource));

                insertStatement.setString(2, resource.getThumbnailPath());
                insertStatement.executeUpdate();

                notificationID = insertStatement.getGeneratedKeys().getInt(1);

            }
            catch (SQLException e) {
                e.printStackTrace();
                AlertBox.showErrorAlert(e.getMessage());
                return;
            }

            try (Connection dbConnection = DBHelper.getConnection();
                    PreparedStatement insertStatement = dbConnection.prepareStatement(
                    "INSERT INTO userNotifications VALUES (?, ?, false)")) {

                insertStatement.setInt(1, notificationID);
                insertStatement.setString(2, borrower.getUsername());
                insertStatement.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
                AlertBox.showErrorAlert(e.getMessage());
            }
        }
    }

    /**
     * Gets a list of users names from the database of users with the new resource
     * notification setting turned on.
     * 
     * @return list of users names of users with the new resource notification
     *         setting turned on.
     */
    public static ArrayList<String> getNewResourcesNotificationUsers() {
        ArrayList<String> notificationUsers = new ArrayList<>();

        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                    "SELECT username FROM userSettings WHERE " + "newResourcesSetting = true");
                ResultSet usernames = insertStatement.executeQuery()) {

            while (usernames.next()) {
                notificationUsers.add(usernames.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return notificationUsers;
    }

    /**
     * Gets a miniature version of the thumbnail of the resource related to this
     * notification.
     * 
     * @return a small image of the resource related to this notification.
     */
    public Image getImage() {
        return resourceImage;
    }

    /**
     * Returns a string that represents CSS properties which will be used when the
     * notification is displayed. It sets a gold background colour with a linear
     * gradient, padding, and a border with a yellow-brown colour and width.
     * 
     * @return A CSS string with the style properties used to display this
     *         notification.
     */
    public String getStyle() {
        return "-fx-background-color: linear-gradient(gold, #a07000);\r\n" + "-fx-padding: 8;\r\n" +
            "-fx-border-color: #996a00;\r\n" + "-fx-border-width: 5;\r\n" +
            "-fx-border-style: solid;";
    }

    /**
     * Makes a new empty HBox which will be used to display this notification. It
     * sets the padding, alignment, CSS style (based on getStyle()), spacing and
     * growth policy of the new HBox. It adds the message of the notification and
     * the image of the associated resource to the HBox.
     * 
     * @return the new HBox to display this notification.
     */
    public HBox getNotificationBox() {
        HBox notificationBox = super.getNotificationBox();
        notificationBox.getChildren().add(new ImageView(resourceImage));
        notificationBox.getChildren().add(getMessageTextElement());
        return notificationBox;
    }
}
