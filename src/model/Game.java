package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.image.Image;

/**
 * This class represents a resource of type Game that the library has to offer.
 * It has an author, publisher, genre, rating and multi-player support. It consists of
 * multiple copies that can be borrowed or requested.
 * Based heavily on the Book class.
 * @author Charles Day
 * @version 1.0
 */
public class Game extends Resource {
    /**The daily fine amount for over due copies of this type of resource.*/
    private static final int MAX_FINE_AMOUNT = 25;
    
    /**The maximum fine amount for over due copies of this type of resource.*/
    private static final int DAILY_FINE_AMOUNT = 2;
    
    /**Publisher of the video game.*/
    private String publisher;
    
    /**Genre of the video game.*/
    private String genre;
    
    /**Rating of the game.*/
    private String rating;
    
    /**The multi-player support of the game.*/
    private String multiplayerSupport;

    /**
     * Makes a new Game object with the given data, representing all the fields of this video game.
     * 
     * @param uniqueID The unique number that identifies this resource.
     * @param title The title of this resource.
     * @param year The year this resource appeared.
     * @param thumbnail A small image of this resource.
     * @param thumbnailPath The path to the thumbnail of the image.
     * @param publisher The publisher of the game.
     * @param genre The genre of the game.
     * @param rating The rating of the game.
     * @param multiplayerSupport The multi-player support of the game.
     * @param timestamp The timestamp of when it was created.
     */
    public Game(int uniqueID, String title, int year, Image thumbnail,
    		String thumbnailPath, String timestamp, String publisher, String genre,
            String rating, String multiplayerSupport) {
        super(uniqueID, title, year, thumbnail, thumbnailPath, timestamp);
        this.publisher = publisher;
        this.genre = genre;
        this.rating = rating;
        this.multiplayerSupport = multiplayerSupport;
    }


    /**
     * Method that loads the details of all game resources from the game database table and
     * adds them to the list of all resources.
     */
    public static void loadDatabaseGames() {
        try {

            Connection conn = DBHelper.getConnection(); // get the connection
            Statement stmt = conn.createStatement(); // prep a statement
            ResultSet rs = stmt.executeQuery(
                "SELECT resource.rID, resource.year, resource.title, "
                + "resource.thumbnail, publisher,"
                + "genre, rating, multiplayer, timestamp FROM game, resource WHERE game.rID "
                + "= resource.rID"); 

            while (rs.next()) {
                String thumbnailPath = rs.getString("thumbnail");
                Image resourceImage = new Image(thumbnailPath, true);
                
                resources.add(new Game(	rs.getInt("rID"), 
                					   	rs.getString("title"), 
                					   	rs.getInt("year"), 
                					   	resourceImage,
                					   	thumbnailPath,
                					   	rs.getString("timestamp"),
					                    rs.getString("publisher"), 
					                    rs.getString("genre"), 
					                    rs.getString("rating"),
					                    rs.getString("multiplayer")));

                System.out.println("New game added!");
            }
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * Gets the genre of the game.
     * @return The genre of game.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre variable of this resource and updates the database.
     * @param genre New genre of the game.
     */
    public void setGenre(String genre) {
        this.genre = genre;
        updateDBvalue("game", this.uniqueID, "genre", genre);
    }

    /**
     * Gets the publisher of the game.
     * @return The publisher of game.
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher variable of this resource and updates the database.
     * @param publisher New publisher of the game.
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
        updateDBvalue("game", this.uniqueID, "publisher", publisher);
    }
    
    
    /**
     * Gets the rating of the game.
     * @return The rating of game.
     */
    public String getRating() {
    	return rating;
    }
    
    /**
     * Sets the rating variable of this resource and updates the database.
     * @param rating New Rating of the game.
     */
	public void setRating(String rating) {
		this.rating = rating;
		updateDBvalue("game", this.uniqueID, "rating", rating);
	}
	
    /**
     * Returns whether or not the game has multiplayer support.
     * @return The multiplayer support of game.
     */
	public String getMultiplayerSupport() {
		return multiplayerSupport;
	}
	
	/**
     * Sets the multi-player support variable of this resource and updates the database.
     * @param multiplayerSupport Set the multi-player support of the game.
     */
	public void setMultiplayerSupport(String multiplayerSupport) {
		this.multiplayerSupport = multiplayerSupport;
		updateDBvalue("game", this.uniqueID, "multiplayer", multiplayerSupport);
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
     *  given resource, taking into account if the other resource is a video game.
     *  @param otherResource The resource this resource is compared with.
     *  @return an integer representing how similar this resource is to the
     *  given resource.
     */
    public int getLikenessScore(Resource otherResource) {
        int score = 0;

        if (otherResource.getClass() == Game.class) {
            Game otherGame = (Game) otherResource;

            if (publisher.equals(otherGame.getPublisher())) {
                score++;
            }
            
            if (genre != null) {
                if (genre.equals(otherGame.getGenre())) {
                    score++;
                }
            }

            if (rating != null) {
                if (rating.equals(otherGame.getRating())) {
                    score++;
                }
            }

            if (multiplayerSupport != null) {
                if (multiplayerSupport.equals(otherGame.getMultiplayerSupport())) {
                    score++;
                }
            }
        }
        score += super.getLikenessScore(otherResource);
        return score;
    }
}
