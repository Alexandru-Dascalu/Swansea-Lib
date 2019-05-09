package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This class represents a resource of type book that the library has to offer.
 * It has an author, publisher, genre, ISBN and language. It consists of
 * multiple copies that can be borrowed or requested.
 * @author Alexandru Dascalu
 * @author Kane Miles
 *
 */
public class Book extends Resource {

    /**The daily fine amount for over due copies of this type of resource.*/
    private static final int MAX_FINE_AMOUNT = 25;
    
    /**The maximum fine amount for over due copies of this type of resource.*/
    private static final int DAILY_FINE_AMOUNT = 2;

    /**Author of the book.*/
    private String author;
    
    /**Publisher of the book.*/
    private String publisher;
    
    /**Genre of the book.*/
    private String genre;
    
    /**ISBN code of the book.*/
    private String isbn;
    
    /**Language of the book.*/
    private String language;

    /**
     * Makes a new book with the given data, representing all the fields of this book.
     * 
     * @param uniqueID The unique number that identifies this resource.
     * @param title The title of this resource.
     * @param year The year this resource appeared.
     * @param thumbnail A small image of this resource.
     * @param thumbnailPath The path to the thumbnail of the image.
     * @param author The author of the book.
     * @param publisher The publisher of the book.
     * @param genre The genre of the book.
     * @param isbn The ISBN code of the book.
     * @param language The language of the book.
     * @param timestamp The time when this resource was added.
     */
    public Book(int uniqueID, String title, int year, Image thumbnail, 
            String thumbnailPath, String timestamp,String author, String publisher,
            String genre, String isbn, String language) {
        super(uniqueID, title, year, thumbnail, thumbnailPath, timestamp);
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.isbn = isbn;
        this.language = language;
    }

    /**
     *  Makes a new book with the given data.
     * @param uniqueID The unique number that identifies this resource.
     * @param title The title of this resource.
     * @param year The year this resource appeared.
     * @param thumbnail A small image of this resource.
     * @param thumbnailPath The path to the thumbnail of the image.
     * @param author The author of the book.
     * @param publisher The publisher of the book.
     * @param timestamp The time when this resource was added.
     */
    public Book(int uniqueID, String title, int year, Image thumbnail, 
    		String thumbnailPath, String timestamp, String author, 
    		String publisher) {
        super(uniqueID, title, year, thumbnail, thumbnailPath, timestamp);
        this.author = author;
        this.publisher = publisher;
    }
    
    /**
     * Method that loads the details of all book resources from the book database table and
     * adds them to the list of all resources.
     */
    public static void loadDatabaseBooks() {
        try {

            Connection conn = DBHelper.getConnection(); // get the connection
            Statement stmt = conn.createStatement(); // prep a statement
            ResultSet rs = stmt.executeQuery(
                "SELECT resource.rID, resource.year, resource.title, "
                + "resource.thumbnail, author, publisher," +
                    "genre, ISBN, language, timestamp FROM book, resource WHERE book.rID "
                    + "= resource.rID"); 

            while (rs.next()) {
                String thumbnailPath = rs.getString("thumbnail");
                Image resourceImage = new Image(thumbnailPath, true);
                
                resources.add(new Book(rs.getInt("rID"), rs.getString("title"), 
                		rs.getInt("year"), resourceImage, thumbnailPath, rs.getString("timestamp"),
                    rs.getString("author"), rs.getString("publisher"), 
                    rs.getString("genre"), rs.getString("ISBN"),
                    rs.getString("language")));

                System.out.println("New book added!");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
        updateDBvalue("book", this.uniqueID, "genre", genre);
    }

    /**
     * Gets the author of the book.
     * @return The author of book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author variable of this resource and updates the database.
     * @param author New author of the book.
     */
    public void setAuthor(String author) {
        this.author = author;
        updateDBvalue("book", this.uniqueID, "author", author);
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
        updateDBvalue("book", this.uniqueID, "publisher", publisher);
    }

    /**
     * Gets the ISBN of the book.
     * @return The ISBN of book.
     */
    public String getISBN() {
        return isbn;
    }

    /**
     * Sets the ISBN variable of this resource and updates the database.
     * @param isbn New ISBN of the book.
     */
    public void setISBN(String isbn) {
        this.isbn = isbn;
        updateDBvalue("book", this.uniqueID, "ISBN", isbn);
    }

    /**
     * Gets the language of the book.
     * @return The language of book.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language variable of this resource and updates the database.
     * @param language New language of the book.
     */
    public void setLanguage(String language) {
        this.language = language;
        updateDBvalue("book", this.uniqueID, "language", language);
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
     *  given resource, taking into account if the other resource is a book.
     *  @param otherResource The resource this resource is compared with.
     *  @return an integer representing how similar this resource is to the
     *   given resource.
     */
    public int getLikenessScore(Resource otherResource) {
        int score = 0;

        if (otherResource.getClass() == Book.class) {

            Book otherBook = (Book) otherResource;

            if (author.equals(otherBook.getAuthor())) {
                score += 4;
            }

            if (publisher.equals(otherBook.getPublisher())) {
                score++;
            }

            if (genre != null) {
                if (genre.equals(otherBook.getGenre())) {
                    score += 3;
                }
            }

            if (isbn != null) {
                if (isbn.equals(otherBook.getISBN())) {
                    score++;
                }
            }

            if (language != null) {
                /*Language only contributes if it is not english since most of 
                 * the books are in english anyway. If it is in another language
                 * then maybe the user wants to read books in that language. 
                 * This change is a suggestion from one of the participants 
                 * in my study.*/
                if (!language.equalsIgnoreCase("english") && 
                        language.equals(otherBook.getLanguage())) {
                    score += 2;
                }
            }
        }

        score += super.getLikenessScore(otherResource);
        return score;
    }
    
    /**
     * Says if this book is possibly in the same series as another book. 
     * It calculates it by seeing if they have the same author and genre, as 
     * well the result of the Resource method of the same name.
     * @param otherBook Another book we want to see if it can be of the 
     * same series.
     * @return True if it is possibly of the same series, false if not.
     */
    protected boolean isPossiblySameSeries(Book otherBook) {
        if(!super.isPossiblySameSeries(otherBook)) {
            return false;
        }
        
        if(author.equals(otherBook.getAuthor())) {
            if(genre != null && genre.equalsIgnoreCase(otherBook.getGenre())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Return the default limit number from resource to restrict user over requesting
     */
    public int getLimitAmount() {
    	return super.getLimitAmount();
    }
}
