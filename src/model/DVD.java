package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.scene.image.Image;

/**
 * This class represents a resource of type dvd that the library has to offer.
 * It has a director, run time, language and a set of subtitle languages. It 
 * consists of multiple copies that can be borrowed or requested.
 * @author Alexandru Dascalu
 * @author Kane Miles
 */
public class DVD extends Resource {

    private static final int MAX_FINE_AMOUNT = 25;
    private static final int DAILY_FINE_AMOUNT = 2;

    private String director;
    private int runTime;
    private String language;
    private ArrayList<String> subtitleLanguages;

    /**
     * Makes a new dvd with the given data. The new dvd has all it's fields 
     * populated. The list of subtitles is loaded from the database.
     * @param uniqueID The unique number that identifies this resource.
     * @param title The title of this resource.
     * @param year The year this resource appeared.
     * @param thumbnail A small image of this resource.
     * @param thumbnailPath The path to the thumbnail of the image.
     * @param timestamp The time when this resource was added.
     * @param director The director of the movie.
     * @param runtime The run time of the movie.
     * @param language The language the movie is in.
     * @param subtitleList The list of all subtitle languages.
     */
    public DVD(int uniqueID, String title, int year, Image thumbnail, 
    		String thumbnailPath, String timestamp, String director, int runtime,
    		String language, ArrayList<String> subtitleList) {
        super(uniqueID, title, year, thumbnail, thumbnailPath, timestamp);
        this.director = director;
        this.runTime = runtime;
        this.language = language;

        loadSubtitles();

        if (subtitleList != null) {
            for (String subtitle : subtitleList) {
                addSubtitle(subtitle);
            }
        }
    }

    /**
     * Makes a new dvd with the given data. The list of subtitles is loaded from the database.
     * @param uniqueID The unique number that identifies this resource.
     * @param title The title of this resource.
     * @param year The year this resource appeared.
     * @param thumbnail A small image of this resource.
     * @param thumbnailPath The path to the thumbnail of the image.
     * @param director The director of the movie.
     * @param runtime The run time of the movie.
     */
    public DVD(int uniqueID, String title, int year, Image thumbnail,
    		String thumbnailPath, String timestamp, String director, int runtime) {
        super(uniqueID, title, year, thumbnail, thumbnailPath, timestamp);
        this.director = director;
        this.runTime = runtime;

        loadSubtitles();
    }
    
    /**
     * Method that loads the details of all dvd resources from the dvd database table and
     * adds them to the list of all resources.
     */
    public static void loadDatabaseDVDs() {
        try {
            Connection conn = DBHelper.getConnection(); // get the connection
            Statement stmt = conn.createStatement(); // prep a statement
            ResultSet rs = stmt.executeQuery("SELECT resource.rID, "
            		+ "resource.title, resource.year, resource.thumbnail," +
                "director, runTime, language, timestamp FROM dvd, resource WHERE dvd.rID = resource.rID");

            while (rs.next()) {
                String thumbnailPath = rs.getString("thumbnail");
                Image resourceImage = new Image(thumbnailPath, true);
                
                resources.add(new DVD(rs.getInt("rID"), rs.getString("title"), 
                    rs.getInt("year"), resourceImage, thumbnailPath,
                    rs.getString("timestamp"), rs.getString("director"), rs.getInt("runTime"),
                    rs.getString("language"), null));

                System.out.println("New DVD added!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for the director of the movie on the DVD.
     * @return The director of the movie on the DVD.
     */
    public String getDirector() {
        return director;
    }

    /**
     * Sets the director of this DVD and updates the database accordingly.
     * @param director The new director value of the movie.
     */
    public void setDirector(String director) {
        this.director = director;
        updateDBvalue("dvd", this.uniqueID, "director", director);
    }

    /**
     * Getter for the run time of the movie on the DVD.
     * @return The run time of the movie on the DVD.
     */
    public int getRuntime() {
        return runTime;
    }

    /**
     * Sets the run time of this DVD and updates the database accordingly.
     * @param runtime The new run time value of the movie.
     */
    public void setRuntime(int runtime) {
        this.runTime = runtime;
        updateDBvalue("dvd", this.uniqueID, "runtime", Integer.toString(runtime));
    }

    /**
     * Getter for the language of the movie on the DVD.
     * @return The language of the movie on the DVD.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language of this DVD and updates the database accordingly.
     * @param language The new language value of the movie.
     */
    public void setLanguage(String language) {
        this.language = language;
        updateDBvalue("dvd", this.uniqueID, "language", language);
    }

    /**
     * Getter for the list of subtitle languages.
     * @return The list of all subtitles.
     */
    public ArrayList<String> getSubtitleLanguages() {

        return subtitleLanguages;
    }

    /**
     * Adds the given subtitle language to the list of subtitles and updates
     * the data base accordingly. If the subtitle is already in the list, 
     * it does nothing.
     * @param subtitleLanguage The subtitle to be added.
     */
    public void addSubtitle(String subtitleLanguage) {
        // go through the list and make sure the language is not in there already
        for (String s : subtitleLanguages) {
            if (s.equals(subtitleLanguage)) {
                return;
            }
        }

        subtitleLanguages.add(subtitleLanguage);
        try {
            Connection connectionToDB = DBHelper.getConnection();
            PreparedStatement sqlStatement = 
            		connectionToDB.prepareStatement("INSERT INTO subtitles "
            				+ "VALUES (?,?)");

            sqlStatement.setInt(1, uniqueID);
            sqlStatement.setString(2, subtitleLanguage);
            sqlStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the given subtitle from the list of subtitles and updates the data 
     * base accordingly. If the subtitle is already not in the list, nothing is changed.
     * @param subtitleLanguage The subtitle to be removed.
     */
    public void deleteSubtitle(String subtitleLanguage) {
        int languageIndex = -1;

        for (int i = 0; i < subtitleLanguages.size(); i++) {
            if (subtitleLanguages.get(i).equals(subtitleLanguage)) {
                languageIndex = i;
            }
        }

        if (languageIndex != -1) {
            subtitleLanguages.remove(languageIndex);

            try {
                Connection connectionToDB = DBHelper.getConnection(); // get the connection
                PreparedStatement sqlStatement = connectionToDB
                    .prepareStatement("DELETE FROM subtitles WHERE dvdID=? and " +
                    "subtitleLanguage=?");

                sqlStatement.setInt(1, uniqueID);
                sqlStatement.setString(2, subtitleLanguage);
                sqlStatement.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
     * given resource. Takes into account if the other resource is also a DVD 
     * and compares their attributes.
     * @param otherResource The resource this resource is compared with.
     * @return an integer representing how similar this resource is to the
     * given resource.
     */
    public int getLikenessScore(Resource otherResource) {
        int score = 0;

        if (otherResource.getClass() == DVD.class) {

            DVD otherDVD = (DVD) otherResource;

            if (director.equals(otherDVD.getDirector())) {
                score += 4;
            }

            if (language != null) {
                /*Language only contributes if it is not english since most of 
                 * the dvds are in english anyway. If it is in another language
                 * then maybe the user wants to see movies in that language. 
                 * This change is a suggestion from one of the participants 
                 * in my study.*/
                if (!language.equalsIgnoreCase("egnlish") && 
                        language.equals(otherDVD.getLanguage())) {
                    score++;
                }
            }
        }

        score += super.getLikenessScore(otherResource);
        return score;
    }
    
    /**
     * Says if this dvd is possibly in the same series as another dvd. 
     * It calculates it by seeing if they have the same director, as 
     * well the result of the Resource method of the same name.
     * @param otherResource Another dvd we want to see if it can be of the 
     * same series.
     * @return True if it is possibly of the same series, false if not.
     */
    public boolean isPossiblySameSeries(Resource otherResource) {
        if(!super.isPossiblySameSeries(otherResource)) {
            return false;
        }
        
        DVD otherDVD = (DVD) otherResource;
        if(director.equals(otherDVD.getDirector())) {
                return true;
        }
        
        return false;
    }
    
    /**
     * Return the default limit number from resource to restrict user over requesting
     */
    public int getLimitAmount() {
    	return super.getLimitAmount();
    }

    /**
     * Loads the subtitle languages of this dvd from the database and populates
     *  the list of subtitles.
     */
    private void loadSubtitles() {
        if (subtitleLanguages != null) {
            subtitleLanguages.clear();
        }
        else {
            subtitleLanguages = new ArrayList<String>();
        }

        try {
            Connection conn = DBHelper.getConnection(); // get the connection
            Statement stmt = conn.createStatement(); // prep a statement
            ResultSet subtitles = stmt.executeQuery("SELECT * FROM SUBTITLES "
            		+ "WHERE dvdID=" + uniqueID);
            while (subtitles.next()) {
                subtitleLanguages.add(subtitles.getString("subtitleLanguage"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
