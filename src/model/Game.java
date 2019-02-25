package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.image.Image;

/**
 * TODO: Update all Javadocs
 * This class represents a resource of type Game that the library has to offer.
 * It has an author, publisher, genre, ISBN and language. It consists of
 * multiple copies that can be borrowed or requested.
 * @author Charles Day
 *
 */
public class Game extends Resource {
	
	//TODO: Update attributes
    /**The daily fine amount for over due copies of this type of resource.*/
    private static final int MAX_FINE_AMOUNT = 25;
    
    /**The maximum fine amount for over due copies of this type of resource.*/
    private static final int DAILY_FINE_AMOUNT = 2;
    
    /**Publisher of the book.*/
    private String publisher;
    
    /**Genre of the book.*/
    private String genre;
    
    /**ISBN code of the book.*/
    private String rating;
    
    /**Language of the book.*/
    private Boolean multiplayerSupport;

    /**
     * Makes a new book with the given data, representing all the fields of this book.
     * 
     * @param uniqueID The unique number that identifies this resource.
     * @param title The title of this resource.
     * @param year The year this resource appeared.
     * @param thumbnail A small image of this resource.
     * @param author The author of the book.
     * @param publisher The publisher of the book.
     * @param genre The genre of the book.
     * @param isbn The ISBN code of the book.
     * @param language The language of the book.
     */
    public Game(int uniqueID, String title, int year, Image thumbnail, 
    		String author, String publisher, String genre,
            String rating, Boolean multiplayerSupport) {
        super(uniqueID, title, year, thumbnail);
        this.publisher = publisher;
        this.genre = genre;
        this.rating = rating;
        this.multiplayerSupport = multiplayerSupport;
    }

//---------------------------------------------------------
//TODO: Ask if the Games attributes are all required
/*    *//**
     *  Makes a new book with the given data.
     * @param uniqueID The unique number that identifies this resource.
     * @param title The title of this resource.
     * @param year The year this resource appeared.
     * @param thumbnail A small image of this resource.
    * @param author The author of the book.
     * @param publisher The publisher of the book.
     *//*
    public Game(int uniqueID, String title, int year, Image thumbnail, 
    		String author, String publisher) {
        super(uniqueID, title, year, thumbnail);
        this.author = author;
        this.publisher = publisher;
    }
    
//TODO: Make database tables and make new loader
    *//**
     * Method that loads the details of all book resources from the book database table and
     * adds them to the list of all resources.
     *//*
    public static void loadDatabaseBooks() {
        try {

            Connection conn = DBHelper.getConnection(); // get the connection
            Statement stmt = conn.createStatement(); // prep a statement
            ResultSet rs = stmt.executeQuery(
                "SELECT resource.rID, resource.year, resource.title, "
                + "resource.thumbnail, author, publisher," +
                    "genre, ISBN, language FROM book, resource WHERE book.rID "
                    + "= resource.rID"); 

            while (rs.next()) {
                Image resourceImage = new Image(rs.getString("thumbnail"), true);
                
                resources.add(new Game(rs.getInt("rID"), rs.getString("title"), 
                		rs.getInt("year"), resourceImage,
                    rs.getString("author"), rs.getString("publisher"), 
                    rs.getString("genre"), rs.getString("ISBN"),
                    rs.getString("language")));

                System.out.println("New book added!");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
    
//---------------------------------------------------------    
    
    
    /**
     * Gets the genre of the book.
     * @return The genre of book
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre variable of this resource and updates the database.
     * @param genre New genre of the book.
     */
    public void setGenre(String genre) {
        this.genre = genre;
        //updateDbValue("book", this.uniqueID, "genre", genre);
    }

    /**
     * Gets the publisher of the book.
     * @return The publisher of book
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher variable of this resource and updates the database.
     * @param publisher New publisher of the book.
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
        //updateDbValue("book", this.uniqueID, "publisher", publisher);
    }
    
    
    /**
     * Gets the rating of the Game.
     * @return The rating of book
     */
    public String getRating() {
		return rating;
	}
    
    /**
     * Sets the rating variable of this resource and updates the database.
     * @param publisher New publisher of the book.
     */
	public void setRating(String rating) {
		this.rating = rating;
		//updateDbValue("book", this.uniqueID, "publisher", publisher);
	}
	
    /**
     * Returns whether or not the game has multiplayer support
     * @return The publisher of book
     */
	public Boolean getMultiplayerSupport() {
		return multiplayerSupport;
	}
	
	/**
     * Sets the publisher variable of this resource and updates the database.
     * @param publisher New publisher of the book.
     */
	public void setMultiplayerSupport(Boolean multiplayerSupport) {
		this.multiplayerSupport = multiplayerSupport;
		//updateDbValue("book", this.uniqueID, "publisher", publisher);
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

//---------------------------------------------------------
//TODO: Update with new attributes
    /**
     * Calculates an integer representing how similar this resource is to the
     *  given resource, taking into account if the other resource is a book.
     *  @param otherResource The resource this resource is compared with.
     *  @return an integer representing how similar this resource is to the
     *   given resource.
     */
    /*
    public int getLikenessScore(Resource otherResource) {
        int score = 0;

        if (otherResource.getClass() == Game.class) {

            Game otherBook = (Game) otherResource;

            if (author.equals(otherBook.getAuthor())) {
                score++;
            }

            if (publisher.equals(otherBook.getPublisher())) {
                score++;
            }

            if (genre != null) {
                if (genre.equals(otherBook.getGenre())) {
                    score++;
                }
            }

            if (isbn != null) {
                if (isbn.equals(otherBook.getISBN())) {
                    score++;
                }
            }

            if (language != null) {
                if (language.equals(otherBook.getLanguage())) {
                    score++;
                }
            }
        }

        score += super.getLikenessScore(otherResource);
        return score;
    }
    */
//---------------------------------------------------------
}