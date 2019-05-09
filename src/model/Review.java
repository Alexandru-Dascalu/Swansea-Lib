package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Review object class.
 * 
 * @author Oliver Harris
 *
 */
public class Review {

    /**The index of the database column where the username of the review is 
     * stored.*/
    private static final int USERNAME_DB_COLUMN = 3;
    
    /**The index of the database column where the rating of the review is 
     * stored.*/
    private static final int RATING_DB_COLUMN = 4;
    
    /**The index of the database column where the text of the review is 
     * stored.*/
    private static final int REVIEW_TEXT_DB_COLUMN = 5;
    
    /**The index of the database column where the timestamp of the review is 
     * stored.*/
    private static final int TIMESTAMP_DB_COLUMN = 6;
    /**
     * Checks if a user has borrowed a resource.
     * 
     * @param username The username of borrower.
     * @param rId The resource ID.
     * @return True if they have borrowed, false otherwise.
     */
    public static boolean hasBorrowed(String username, int rId) {

        try {
            Connection connection = DBHelper.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM copies,borrowRecords,resource " +
                    "WHERE copies.rID = resource.rID AND " +
                    "borrowRecords.copyId = copies.copyID AND " +
                    "borrowRecords.username = ?" + " AND copies.rID=? " +
                    "AND borrowRecords.description LIKE 'Returned on %'");
            statement.setString(1, username);
            statement.setInt(2, rId);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                connection.close();
                return true;
            }
            else {
                connection.close();
                return false;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
    * Method that checks if the resource has been reviewed.
    * @param username username of the user
    * @param rId resource ID
    * @return true or false. depending if a resource has been reviewed
    */
    public static boolean hasReviewed(String username, int rId) {

        try {
            Connection connection = DBHelper.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM reviews WHERE" + " username=? AND resourceId=?");
            statement.setString(1, username);
            statement.setInt(2, rId);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                connection.close();
                return true;
            }
            else {
                connection.close();
                return false;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method that returns true or false whether a resource has reviews or not.
     * 
     * @param resourceId id of the resource
     * @return true or false
     */
    public static boolean hasReviews(int resourceId) {
        try {
            Connection connection = DBHelper.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT *" + " FROM reviews WHERE resourceId=?");
            statement.setInt(1, resourceId);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                connection.close();
                return true;
            }
            else {
                connection.close();
                return false;

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get method that gets the average rating/star of a resource.
     * 
     * @param resourceId id of resource
     * @return average rating/star of a resource
     */

    public static double getAvgStar(int resourceId) {
        try {
            Connection connection = DBHelper.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT avg(star) FROM reviews " + "WHERE resourceId=?");
            statement.setInt(1, resourceId);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                Double star = results.getDouble(1);
                connection.close();
                return star;
            }
            else {
                connection.close();
                return 0;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Add a review to the database.
     * 
     * @param username Username of reviewer.
     * @param resource Resource id the review is for.
     * @param star Star rating.
     * @param text Review text.
     */
    public static void addReview(String username, int resource, int star,
            String text) {
        try {
            Connection connection = DBHelper.getConnection();
            PreparedStatement statement = connection
                .prepareStatement("INSERT INTO reviews(resourceId," +
                    "username,star,review) VALUES(?,?,?,?)");
            statement.setInt(1, resource);
            statement.setString(2, username);
            statement.setInt(USERNAME_DB_COLUMN, star);
            statement.setString(RATING_DB_COLUMN, text);
            statement.execute();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get method for the written reviews people have left on a specific
     * resource.
     * 
     * @param resourceId id of resource
     * @return arraylist of reviews on a certain resource
     */
    public static ArrayList<String[]> getReviews(int resourceId) {
        try {
            Connection connection = DBHelper.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * " + "FROM reviews WHERE resourceId=?");
            statement.setInt(1, resourceId);
            ResultSet results = statement.executeQuery();
            ArrayList<String[]> reviews = new ArrayList<String[]>();
            while (results.next()) {

                // star, name,what,when
                String[] re = {String.valueOf(results.getInt(RATING_DB_COLUMN)),
                        results.getString(USERNAME_DB_COLUMN), 
                        results.getString(REVIEW_TEXT_DB_COLUMN),
                        results.getString(TIMESTAMP_DB_COLUMN),
                        String.valueOf(results.getInt(1)) };

                reviews.add(re);

            }
            connection.close();
            return reviews;

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get method of all reviews on all resources.
     * 
     * @return arralist of all reviews
     */
    public static ArrayList<String[]> getReviews() {

        try {
            Connection connection = DBHelper.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM reviews");

            ResultSet results = statement.executeQuery();
            ArrayList<String[]> reviews = new ArrayList<String[]>();
            while (results.next()) {
                String[] reviewFromDB = {String.valueOf(results.getInt(1)),
                        String.valueOf(results.getInt(USERNAME_DB_COLUMN)), 
                        results.getString(REVIEW_TEXT_DB_COLUMN),
                        results.getString(RATING_DB_COLUMN)};
                reviews.add(reviewFromDB);

            }

            return reviews;

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Void method that removes a review from the database.
     * 
     * @param reviewId id of a review
     */
    public static void removeReview(int reviewId) {

        try {
            Connection connection = DBHelper.getConnection();

            PreparedStatement statement = connection
                .prepareStatement("DELETE FROM reviews WHERE reviewId=?");
            statement.setInt(1, reviewId);
            statement.execute();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
