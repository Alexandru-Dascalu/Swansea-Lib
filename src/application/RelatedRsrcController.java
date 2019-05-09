package application;

import java.util.List;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import model.Resource;
import model.ResourceComparator;

public class RelatedRsrcController {

    @FXML
    private Button saveBtn;
    
    @FXML
    private VBox resourcesBox;
    
    private Resource originalResource;
    
    private static HBox getRelatedResourceHBox(Resource resource, 
            EventHandler<MouseEvent> clickHandler) {
        HBox resourceBox = new HBox();
        resourceBox.setSpacing(20);
        resourceBox.setAlignment(Pos.CENTER_LEFT);
        resourceBox.setPadding(new Insets(10, 10, 10, 20));
        
        StackPane stackPane = new StackPane();
        ImageView resourceImageView = new ImageView(resource.getThumbnail());
        resourceImageView.setFitHeight(200);
        resourceImageView.setFitWidth(120);
        stackPane.getChildren().add(resourceImageView);
        stackPane.setId(resource.getUniqueID() + "");
        stackPane.setOnMouseClicked(clickHandler);
        resourceBox.getChildren().add(stackPane);
        
        VBox labelBox = new VBox();
        labelBox.setSpacing(5);
        labelBox.setAlignment(Pos.CENTER);
        resourceBox.getChildren().add(labelBox);
        
        labelBox.getChildren().add(new Label("Title:"));
        labelBox.getChildren().add(new Label("Year:"));
        
        VBox infoBox = new VBox();
        infoBox.setSpacing(5);
        infoBox.setAlignment(Pos.CENTER);
        resourceBox.getChildren().add(infoBox);
        
        infoBox.getChildren().add(new Text(resource.getTitle()));
        infoBox.getChildren().add(new Text("" + resource.getYear()));
        
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(false);
        
        resourceBox.getChildren().add(checkBox);
        return resourceBox;
    }
    
    public void loadResourceHboxes(String selectionMode, Resource originalResource, 
            EventHandler<MouseEvent> clickHandler) {
        List<Resource> displayedResources = resourcesToDisplay(selectionMode);
        this.originalResource = originalResource;
        
        for(Resource resource: displayedResources) {
           resourcesBox.getChildren().add(getRelatedResourceHBox(resource, clickHandler));
        }
    }
    
    public void onStageClosed() {
        ScreenManager.setCurrentResource(originalResource);
    }
    
    private List<Resource> resourcesToDisplay(String selectionMode) {
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

                for (Resource resource : displayedResources) {
                    if (resource == ScreenManager.currentResource ||
                        originalResource.isPossiblySameSeries(resource)) {
                        displayedResources.remove(resource);
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
                throw new IllegalArgumentException("Selection mode string is not valid!");
        }
        
        return displayedResources;
    }
}
