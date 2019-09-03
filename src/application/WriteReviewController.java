package application;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.Resource;
import model.Review;

/**
 * Controller class for the write a review GUI
 * @author Oliver Harris
 * @author Joe Wright
 */
public class WriteReviewController {
    @FXML
    private AnchorPane writeReviewBox;

    @FXML
    private TextArea reviewBox;// Displays reviews

    @FXML
    private ToggleGroup star;// rating

    @FXML
    private RadioButton r1;// inputs 1 rating
    @FXML
    private RadioButton r2;// inputs 2 rating
    @FXML
    private RadioButton r3;// inputs 3 rating
    @FXML
    private RadioButton r4;// inputs 4 rating
    @FXML
    private RadioButton r5;// inputs 5 rating

    /**The resource for which we want to write a review.*/
    private Resource reviewedResource;
    
    /**
     * Changes the resource for which a review is made.
     * @param resource the resource for which the review will be made.
     */
    public void setResource(Resource resource)
    {
    	reviewedResource = resource;
    }
    
    /**
     * Method that allows the user to submit a review and rating
     * 
     * @param event event of the button being pressed
     */
    @FXML
    public void submitReview(MouseEvent event) {
        int starValue = 0;

        // Changes the rating depending on what radio button is clicked
        System.out.println(star.getSelectedToggle());
        Toggle selected = star.getSelectedToggle();
        if (selected.equals(r1)) {
            System.out.println("r1..");
            starValue = 1;
        }
        else if (selected.equals(r2)) {
            starValue = 2;
        }
        else if (selected.equals(r3)) {
            starValue = 3;
        }
        else if (selected.equals(r4)) {
            starValue = 4;
        }
        else if (selected.equals(r5)) {
            starValue = 5;
        }
        else {
            System.out.println("No value selected, this shouldn't be possible");
            return;
        }

        // sets text variable to whatever the user has wrote in the review box
        String text = reviewBox.getText();

        Review.addReview(ScreenManager.getCurrentUser().getUsername(), 
        		reviewedResource.getUniqueID(), starValue, text);

        reviewBox.setText("Your review has been added!");
        reviewBox.setEditable(false);

    }

    /**
     * Displays review box if user has borrowed and hasn't already reviewed.
     */
    @FXML
    public void initialize() {

        if (!Review.hasBorrowed(ScreenManager.getCurrentUser().getUsername(),
        		reviewedResource.getUniqueID()) ||
            Review.hasReviewed(ScreenManager.getCurrentUser().getUsername(),
            		reviewedResource.getUniqueID())) {
            writeReviewBox.setVisible(false);
        }

    }

}
