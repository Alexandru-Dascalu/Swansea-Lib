package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.image.Image;

/**
 * This class represents a resource of type laptop that the library has to offer.
 * It has a manufacturer, operating system and model. It consists of multiple 
 * copies that can be borrowed or requested.
 * @author Alexandru Dascalu
 * @author Kane Miles
 */
public class Laptop extends Resource {

    private static final int MAX_FINE_AMOUNT = 100;
    private static final int DAILY_FINE_AMOUNT = 10;

    private String manufacturer;
    private String model;
    private String operatingSystem;
    private static final int LIMIT_AMOUNT = 3;

    /**
     * Method that loads the details of all dvd resources from the dvd database table and
     * adds them to the list of all resources.
     */
    public static void loadDatabaseLaptops() {
        try {
            Connection conn = DBHelper.getConnection(); // get the connection
            Statement stmt = conn.createStatement(); // prep a statement
            ResultSet rs = stmt.executeQuery("SELECT resource.rID, "
            		+ "resource.title, resource.year, resource.thumbnail, manufacturer," +
                " model, os, timestamp FROM laptop, resource WHERE "
                + "resource.rID = laptop.rID");

            while (rs.next()) {
                String thumbnailPath = rs.getString("thumbnail");
            	Image resourceImage = new Image(thumbnailPath, true);
                resources.add(new Laptop(rs.getInt("rID"), rs.getString("title"),
                		rs.getInt("year"), resourceImage, thumbnailPath, 
                		rs.getString("timestamp"), rs.getString("manufacturer"), 
                		rs.getString("model"), rs.getString("os")));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Makes a new laptop with the given data.
     * @param uniqueID The unique number that identifies this resource.
     * @param title The title of this resource.
     * @param year The year this resource appeared.
     * @param thumbnail A small image of this resource.
     * @param thumbnailPath The path to the thumbnail of the image.
     * @param timestamp The time when this resource was added.
     * @param manufacturer The manufacturer of the laptop.
     * @param model The model of the laptop.
     * @param operatingSystem The operating system of the laptop.
     */
    public Laptop(int uniqueID, String title, int year, Image thumbnail, 
            String thumbnailPath, String timestamp, String manufacturer,
            String model, String operatingSystem) {
        super(uniqueID, title, year, thumbnail, thumbnailPath, timestamp);
        this.manufacturer = manufacturer;
        this.model = model;
        this.operatingSystem = operatingSystem;
    }

    /**
     * Gets the manufacturer of the laptop.
     * @return The manufacturer of the laptop.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the manufacturer of the laptop to a new value and updates the data
     * base.
     * @param manufacturer The new value of the manufacturer.
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        updateDBvalue("laptop", this.uniqueID, "manufacturer", manufacturer);
    }

    /**
     * Gets the model of the laptop.
     * @return The model of the laptop.
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the manufacturer of the laptop to a new value and updates the data
     * base.
     * @param model The new value of the model.
     */
    public void setModel(String model) {
        this.model = model;
        updateDBvalue("laptop", this.uniqueID, "model", model);
    }

    /**
     * Gets the operating system of the laptop.
     * @return The operating system of the laptop.
     */
    public String getOS() {
        return operatingSystem;
    }

    /**
     * Sets the operating system of the laptop to a new value and updates the 
     * data base.
     * @param operatingSystem The new OS of the laptop.
     */
    public void setOS(String operatingSystem) {
        this.operatingSystem = operatingSystem;
        updateDBvalue("laptop", this.uniqueID, "OS", operatingSystem);
    }

    /**
     * Getter for the daily fine amount for over due copies of this type of
     * resource.
     * @return The daily fine amount for over due copies of this type of resource.
     */
    public int getDailyFineAmount() {
        return DAILY_FINE_AMOUNT;
    }

    /**
     * Getter for the maximum fine amount for over due copies of this type of
     * resource.
     * @return The maximum fine amount for over due copies of this type of resource.
     */
    public int getMaxFineAmount() {
        return MAX_FINE_AMOUNT;
    }

    /**
     * Calculates an integer representing how similar this resource is to the
     * given resource. Takes into account if the other resource is also a laptop 
     * and compares their attributes.
     * @param otherResource The resource this resource is compared with.
     * @return an integer representing how similar this resource is to the
     * given resource.
     */
    public int getLikenessScore(Resource otherResource) {
        int score = 0;

        if (otherResource.getClass() == Laptop.class) {

            Laptop otherLaptop = (Laptop) otherResource;

            if (manufacturer.equals(otherLaptop.getManufacturer())) {
                score++;
            }

            if (model.equals(otherLaptop.getModel())) {
                score++;
            }

            if (operatingSystem.equals(otherLaptop.getOS())) {
                score++;
            }
        }

        score += super.getLikenessScore(otherResource);
        return score;
    }
    
    /**
     * override the limit amount from resource super class and set laptop limit
     * to 3 instead of default 1 to restrict user from over requesting.
     */
    public int getLimitAmount() {
    	return LIMIT_AMOUNT;
    }
}
