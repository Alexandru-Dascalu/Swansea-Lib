package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import application.AlertBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * A notification about an imminent fine the user is about to receive. Fine 
 * notifications have a date when the fine will be given, and an image of the 
 * resource of the copy for which the fine will be given.
 * @author Alexandru Dascalu
 */
public class FineNotification extends Notification {

    /** The date when the imminent fine will be given. */
    private String date;

    /**
     * The image of the of the resource of the copy for which the fine will be
     * given.
     */
    private Image image;

    /**
     * Makes a new fine notification.
     * 
     * @param message The message of the new notification.
     * @param isRead Whether the notification has been marked read by the user.
     * @param date The date when the user will receive a fine.
     * @param imagePath The path to the thumbnail of the resource of the copy for
     *        which the user will get a fine.
     */
    public FineNotification(String message, boolean isRead, String date, String imagePath) {
        super(message, isRead);
        this.date = date;
        image = new Image(imagePath, IMAGE_WIDTH, IMAGE_HEIGHT, true, false);
    }
    
    /**
     * Makes the message that should be displayed for a notification when the given
     * resource has a copy for which a user is about to receive a fine.
     * 
     * @param resource The resource which has a copy for which a user is about to
     *        receive a fine.
     * @param days The number of days untill the due date of the copy.
     * @return the message for a fine notification for the given resource.
     */
    public static String getFineMsg(Resource resource, int days) {
        return "Warning! You are about to receive a fine in " + days + " days for a " +
            getClassName(resource) + " you are currently borrowing: " + resource.getTitle() + ".";
    }

    

    /**
     * Makes a new notification in the database for an imminent fine for the given
     * user and copy. It also associates in the database the newly made notification
     * with said user.
     * 
     * @param copy The copy for which the user may receive a fine.
     * @param user The user which is about to receive a fine.
     * @return the unique ID of the notification created in the database.
     */
    public static int makeNotification(Copy copy, User user) {
        int daysUntilDue = copy.getDaysUntilDue();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

        int notificationID;
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(
                    "INSERT INTO notification (message, image, date) VALUES (?, ?, ?)")) {

            insertStatement.setString(1, getFineMsg(copy.getResource(), daysUntilDue));
            insertStatement.setString(2, copy.getResource().getThumbnailPath());
            insertStatement.setString(3, dateFormatter.format(copy.getDueDate()));
            insertStatement.executeUpdate();

            notificationID = insertStatement.getGeneratedKeys().getInt(1);
        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
            return -1;
        }

        return notificationID;
    }

    /**
     * Gets the date when the fine will be set and that is displayed by this
     * notification.
     * 
     * @return the date that is displayed by this notification.
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets a miniature version of the thumbnail of the resource of the copy for
     * which the user will get a fine.
     * 
     * @return a small image of the resource for the copy for which the fine was
     *         given.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Returns a string that represents CSS properties which will be used when the
     * notification is displayed. It sets a firebrick red background colour with a
     * linear gradient, padding, and a border with a red colour and width.
     * 
     * @return A CSS string with the style properties used to display this
     *         notification.
     */
    public String getStyle() {
        return "-fx-background-color: linear-gradient(firebrick, #d81111);\r\n" +
            "-fx-padding: 8;\r\n" + "-fx-border-color: #6d0000;\r\n" + "-fx-border-width: 5;\r\n" +
            "-fx-border-style: solid;";
    }

    /**
     * Makes a new empty HBox which will be used to display this notification. It
     * sets the padding, alignment, CSS style (based on getStyle()), spacing and
     * growth policy of the new HBox. It adds the small image of the resource, the
     * message of the notification, the imminent fine date to the HBox
     * 
     * @return the new HBox to display this notification.
     */
    public HBox getNotificationBox() {
        HBox notificationBox = super.getNotificationBox();
        notificationBox.getChildren().add(new ImageView(image));
        notificationBox.getChildren().add(getMessageTextElement());

        Label dateLabel = new Label("Imminent fine date:");
        dateLabel.setTextFill(Color.BLACK);
        dateLabel.setStyle("-fx-font-weight: bold;");

        notificationBox.getChildren().add(dateLabel);
        notificationBox.getChildren().add(new Text(date));

        System.out.println(notificationBox.getPrefWidth());
        return notificationBox;
    }
    
    /**
     * Removes the package name from the name of the class of a resource.
     * 
     * @param resource The resource for which we want its class name.
     * @return The class name of the resource, withot the package name.
     */
    private static String getClassName(Resource resource) {
        String className = resource.getClass().getName();
        className = className.substring(6);
        return className;
    }
}
