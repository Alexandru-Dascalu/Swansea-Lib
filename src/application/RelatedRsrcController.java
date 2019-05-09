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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import model.DBHelper;
import model.Resource;
import model.ResourceComparator;

public class RelatedRsrcController {

    @FXML
    private Button saveBtn;
    
    @FXML
    private VBox resourcesVBox;
    
    private Resource originalResource;
    
    private String selectionMode;
    
    private final EventHandler<MouseEvent> clickHandler;
            
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
    
    public RelatedRsrcController() {
        clickHandler = event -> {
            for(Resource resource : ScreenManager.getResources()) {
                if(resource.getUniqueID() ==
                        Integer.parseInt(((ImageView) event.getSource()).getId())) {
                    ScreenManager.setCurrentResource(resource);
                }
            }

            try {
                FXMLLoader fxmlLoader =
                        new FXMLLoader(getClass().getResource("/fxml/copyScene.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Resource Information");
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
    
    public void loadResourceHboxes(String selectionMode, Resource originalResource) {
        this.originalResource = originalResource;
        this.selectionMode = selectionMode;
        List<Resource> displayedResources = resourcesToDisplay();
        
        for(Resource resource: displayedResources) {
            HBox h = getRelatedResourceHBox(resource, clickHandler);
           resourcesVBox.getChildren().add(h);
        }
    }
    
    public void onStageClosed() {
        ScreenManager.setCurrentResource(originalResource);
    }
    
    @FXML
    private void saveRelated() {
        for(Node node: resourcesVBox.getChildren()) {
            HBox hbox = (HBox) node;
            int selectedResourceID = Integer.parseInt(hbox.getChildren()
                .get(0).getId());
            
            CheckBox checkBox = (CheckBox) hbox.getChildren().get(
                hbox.getChildren().size() - 1);
            
            if(checkBox.isSelected()) {
                String table = null;
                
                if(selectionMode.equals("same series")) {
                    table = "resourceSeries";
                    originalResource.getSameSeriesResources().add(
                        Integer.valueOf(selectedResourceID));
                } else if (selectionMode.equals("other related")) {
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
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertBox.showErrorAlert(e.getMessage());
                }
            }
        }
        
        Stage settingsStage = (Stage) saveBtn.getScene().getWindow();
        settingsStage.close();
        
        AlertBox.showInfoAlert("Related resources have been saved successfully!");
    }
    
    private List<Resource> resourcesToDisplay() {
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
