package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import model.DBHelper;
import model.Resource;
import model.ResourceComparator;

/**
 * A controller for a window showing up after making a new resource letting the 
 * librarian select resources that are related to the newly added resources.
 * @author Alexandru Dascalu
 */
public class RelatedRsrcController {

    /**
     * The wrap width of the text showing the message of the resource title in the
     * GUI. Its value is {@value}.
     */
    private static final int TEXT_WRAP_WIDTH = 250;
    
    /**A button that for saving the selection of resources as related resources.*/
    @FXML
    private Button saveBtn;

    /**A VBox inside a scroll pane used to store HBoxes showing resources that 
     * might be related to the newly made resource.*/
    @FXML
    private VBox resourcesVBox;
    
    /**The original resource, for which the librarian can associate with resources that are related.*/
    private Resource originalResource;

    /**A string representing what resources to display to the librarian to see.
     * He can select from resources that the program thinks can from the same 
     * series, or from other resources that might be related.*/
    private String selectionMode;

    /**An event handler for mouse clicks to display the detailed information
     * view of a resource whose image was clicked on in the window of this controller.*/
    private final EventHandler<MouseEvent> clickHandler;

    /**
     * Makes a new related resources controller and makes a new click handler
     * meant for an ImageView to display the detailed resource view of the
     * resource whose image is in the ImageView.
     */
    public RelatedRsrcController() {
        clickHandler = event -> {
            for (Resource resource : ScreenManager.getResources()) {
                if (resource.getUniqueID() == Integer.parseInt(((ImageView)
                        event.getSource()).getId())) {
                    ScreenManager.setCurrentResource(resource);
                }
            }

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/fxml/copyScene.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Resource Information");
                stage.setScene(new Scene(root1));
                stage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
    
    /**Builds an HBox representing details of a resource, complete with an 
     * image view of its thumbnail that you can click on, and a checkbox that 
     * will be used to save the given resource as related to the original 
     * resource.
     * @param resource The resource for which this box is made.
     * @param clickHandler The click handler used for the image view of the
     * image of the given resource.
     * @return An HBox used for selecting related resources.*/
    private static HBox getRelatedResourceHBox(Resource resource,
            EventHandler<MouseEvent> clickHandler) {
        HBox resourceBox = new HBox();
        resourceBox.setSpacing(20);
        resourceBox.setAlignment(Pos.CENTER);
        resourceBox.setPadding(new Insets(10, 10, 10, 20));

        ImageView resourceImageView = new ImageView(resource.getThumbnail());
        resourceImageView.setFitHeight(200);
        resourceImageView.setFitWidth(120);
        resourceImageView.setId(resource.getUniqueID() + "");
        resourceImageView.setOnMouseClicked(clickHandler);
        resourceBox.getChildren().add(resourceImageView);

        VBox resourceInfoBox = new VBox();
        resourceInfoBox.setSpacing(5);
        resourceInfoBox.setAlignment(Pos.CENTER);
        
        HBox titleBox = new HBox();
        titleBox.setSpacing(5);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(new Label("Title:"));
        Text titleText = new Text(resource.getTitle());
        titleText.setWrappingWidth(TEXT_WRAP_WIDTH);
        titleBox.getChildren().add(titleText);
        resourceInfoBox.getChildren().add(titleBox);
        
        HBox yearBox = new HBox();
        yearBox.setSpacing(5);
        yearBox.setAlignment(Pos.CENTER);
        yearBox.getChildren().add(new Label("Year:"));
        yearBox.getChildren().add(new Text("" + resource.getYear()));
        resourceInfoBox.getChildren().add(yearBox);
        resourceBox.getChildren().add(resourceInfoBox);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(false);

        VBox.setVgrow(resourceBox, Priority.ALWAYS);
        resourceBox.getChildren().add(checkBox);
        return resourceBox;
    }

    /**
     * Loads an hbox for the resources the librarian can set as related into the GUI window.
     * @param selectionMode A string saying if the librarian selects from among 
     * resources that could be part of the same series, or any other resource.
     * @param originalResource The resource for which we display resources so they
     * can be set as related to the given resource.
     */
    public void loadResourceHboxes(String selectionMode,
            Resource originalResource) {
        this.originalResource = originalResource;
        this.selectionMode = selectionMode;
        List<Resource> displayedResources = getResourcesToDisplay();

        for (Resource resource : displayedResources) {
            HBox h = getRelatedResourceHBox(resource, clickHandler);
            resourcesVBox.getChildren().add(h);
        }
    }

    /**Ensures that when the window of this controller is closed, then the 
     * current resource is reset to the resource for which this window 
     * was made.*/
    public void onStageClosed() {
        ScreenManager.setCurrentResource(originalResource);
    }

    /**Saves the selection of related resources of originalResource to the 
     * database and to its lists of related resources.*/
    @FXML
    private void saveRelated() {
        for (Node node : resourcesVBox.getChildren()) {
            HBox hbox = (HBox) node;
            int selectedResourceID = Integer.parseInt(hbox.getChildren().
                get(0).getId());

            CheckBox checkBox = (CheckBox) hbox.getChildren().get(hbox.getChildren().
                size() - 1);

            if (checkBox.isSelected()) {
                String table = null;

                if (selectionMode.equals("same series")) {
                    table = "resourceSeries";
                    originalResource.getSameSeriesResources().add(
                        Integer.valueOf(selectedResourceID));
                }
                else if (selectionMode.equals("other related")) {
                    table = "related";
                    originalResource.getOtherRelatedResources().add(
                        Integer.valueOf(selectedResourceID));
                }

                try (Connection dbConnection = DBHelper.getConnection();
                        PreparedStatement insertStatement = dbConnection.prepareStatement(
                            "INSERT INTO " + table + " VALUES (?, ?)")) {

                    insertStatement.setInt(1, originalResource.getUniqueID());
                    insertStatement.setInt(2, selectedResourceID);
                    insertStatement.executeUpdate();

                }
                catch (SQLException e) {
                    e.printStackTrace();
                    AlertBox.showErrorAlert(e.getMessage());
                }
            }
        }

        Stage settingsStage = (Stage) saveBtn.getScene().getWindow();
        settingsStage.close();

        AlertBox.showInfoAlert("Related resources have been saved successf" +
            "ully!");
    }

    /**
     * Calculates a list of resources to be part of the suggestion made to the 
     * librarian, based on the selection mode.
     * @return a list of resources to be part of the suggestion made to the 
     * librarian.
     */
    private List<Resource> getResourcesToDisplay() {
        List<Resource> displayedResources;

        switch (selectionMode) {
            case "same series":
                displayedResources = originalResource.getSameSeriesSuggestions();
                break;
            case "other related":
                displayedResources = ScreenManager.getResources();
                ResourceComparator comparator = new ResourceComparator(
                    originalResource);
                displayedResources.sort(comparator);

                for (int i = 0; i < displayedResources.size(); i++) {
                    Resource resource = displayedResources.get(i);
                    if (resource == ScreenManager.currentResource ||
                        originalResource.isPossiblySameSeries(resource)) {
                        displayedResources.remove(i);
                        i--;
                    }
                }
                break;
            case "all":
                displayedResources = ScreenManager.getResources();

                for (Resource resource : displayedResources) {
                    if (resource.equals(originalResource)) {
                        displayedResources.remove(resource);
                        break;
                    }
                }
                break;
            default:
                throw new IllegalArgumentException(
                    "Selection mode string is not valid!");
        }

        return displayedResources;
    }
}
