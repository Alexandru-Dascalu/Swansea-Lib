package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import application.AlertBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * This class represents a resource that the library has to offer. A resource is
 * a certain book, DVD, type of laptop, etc. and consists of multiple copies
 * that can be borrowed or requested. It has a unique ID, a title, the year it
 * came out and a thumbnail image.
 * 
 * @author Kane Miles
 * @author Alexandru Dascalu
 * @version 1.5
 */
public abstract class Resource {
    /**A list of all resources in the application.*/
    protected static ArrayList<Resource> resources = new ArrayList<>();
    
    /**
     * A default number for each resource used to limit the number of items a user
     * can checkout.
     */
    protected static final int LIMIT_AMOUNT = 1;
    
    /**Number of milliseconds in a day. Used when calculating the number of 
     * days a copy is overdue. Its value is {@value}.*/
    private static final long MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;
    
    /** A unique number that identifies this resource. */
    protected final int uniqueID;

    /** The title of this resource. */
    protected String title;

    /** The year this resource came out. */
    protected int year;

    /** The thumbnail Image of this resource. */
    protected Image thumbnail;

    /**The file path to the thumbnail of this image.*/
    protected String thumbnailPath;
    
    /**
     * A time stamp for resource, so that each time a user logs in the user can view any
     * new additions.
     */
    protected String timestamp;
    
    /** A list of all the copies of this resource. */
    private ArrayList<Copy> copyList;

    /** A list of all copies of this resource that are not currently borrowed.*/
    private LinkedList<Copy> freeCopies;

    /** A queue of all borrowed copies who have no due date set. */
    private PriorityQueue<Copy> noDueDateCopies;

    /** A queue of user who have requested a copy of this resource but have not
     * gotten one because there is no free copy.*/
    private Queue<User> userRequestQueue;

    /**A list of users with pending requests to borrow a copy of this resource 
     * that need to be authorised by a librarian.*/
    private ArrayList<User> pendingRequests;
    
    /**The unique IDs of other resources that are set as part of the same series
     * in the database.*/
    private LinkedList<Integer> sameSeriesResources;
    
    /**The unique IDs of other resources that are set as being related in the
     * database.*/
    private LinkedList<Integer> otherRelatedResources;
    
    /**
     * Makes a new resource whose details are the given arguments.
     * 
     * @param uniqueID The unique number that identifies this resource.
     * @param title The title of this resource.
     * @param year The year this resource appeared.
     * @param thumbnail A small image of this resource.
     * @param thumbnailPath The path to the thumbnail of the image.
     * @param timestamp The time when this resource was added.
     */
    public Resource(int uniqueID, String title, int year, Image thumbnail, String thumbnailPath, String timestamp) {
        this.uniqueID = uniqueID;
        this.title = title;
        this.year = year;
        this.thumbnail = thumbnail;
        this.thumbnailPath = thumbnailPath;
        this.timestamp = timestamp;

        /*
         * just make new empty instances of the containers used for tracking
         * information about borrowing and returning copies
         */
        copyList = new ArrayList<Copy>();
        freeCopies = new LinkedList<Copy>();
        noDueDateCopies = new PriorityQueue<Copy>();
        userRequestQueue = new Queue<User>();
        pendingRequests = new ArrayList<User>();
        sameSeriesResources = new LinkedList<Integer>();
        otherRelatedResources = new LinkedList<Integer>();

        loadRelatedResources();
        loadCopyList();
        loadCopyPriorityQueue();
        loadUserQueue();
        loadPendingRequests();
    }
    
    /**
     * Loads all the resources from the database and adds them to the list of
     *  all resources.
     */
    public static void loadDatabaseResources() {
        resources = new ArrayList<>();
        Book.loadDatabaseBooks();
        Laptop.loadDatabaseLaptops();
        DVD.loadDatabaseDVDs();
        Game.loadDatabaseGames();
    }
    
    /**
     * Gets a reference to the resource with the given resource ID.
     * @param resourceID Unique ID of the resource we want.
     * @return The resource with the given ID, or null if it does not exist.
     */
    public static Resource getResource(int resourceID) {
        for (Resource r : resources) {
            if (r.getUniqueID() == resourceID) {
                return r;
            }
        }

        return null;
    }
    
    /**
     * Getter for list of all resources of the application.
     * @return The list of all resources.
     */
    public static ArrayList<Resource> getResources() {
        return resources;
    }

    /**
     * Gets the list of all copies of this resource.
     * @return An array list of all copies of the resource.
     */
    public ArrayList<Copy> getCopies() {
        return copyList;
    }

    /**
     * Adds a copy to the copy list and updates the data base.
     * @param copy Copy to be added.
     */
    public void addCopy(Copy copy) {
        copyList.add(copy);
        
        if (copy.getBorrower() == null) {
            freeCopies.addFirst(copy);
        }

        saveCopyToDB(copy);
    }

    /**
     * Addes multiple copies to the copy and list and updates the database.
     * @param copies Copies to be added.
     */
    public void addCopies(Collection<Copy> copies) {
        copyList.addAll(copies);
        freeCopies.addAll(copies);

        for (Copy copy : copies) {
            if (copy.getBorrower() == null) {
                freeCopies.addFirst(copy);
            }
            saveCopyToDB(copy);
        }
    }
    
    /**
     * Returns to number of free copies available for this resource.
     * @return int No. of free copies.
     */
    public int freeCopiesNo() {
    	return freeCopies.size();
    }

    /**
     * Removes a copy from the list of copies and updates the database.
     * @param copy Copy to be removed.
     */
    public void removeCopy(Copy copy) {
        if (copy.getResource().equals(this)) {
            copyList.remove(copy);
            freeCopies.remove(copy);
            noDueDateCopies.remove(copy);

            Connection dbConnection = null;
            PreparedStatement sqlStatement = null;
            try {
                dbConnection = DBHelper.getConnection();
                sqlStatement = dbConnection.prepareStatement(
                    "DELETE FROM copies WHERE copyID=" + copy.getCopyID());
                sqlStatement.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if(sqlStatement != null) {
                    try {
                        sqlStatement.close();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                
                if(dbConnection != null) {
                    try {
                        dbConnection.close();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else {
            throw new IllegalArgumentException(
                "Copy argument is not a copy of this resource!");
        }
    }

    /**
     * This method ensures a returned copy is marked a free copy or that it is
     * reserved for the user at the front of the request queue. It also ensures
     * that any fines that need to be applied are added to the database.
     * 
     * @param returnedCopy The copy being returned.
     */
    public void processReturn(Copy returnedCopy) {
        applyFines(returnedCopy);
        Resource.insertReturnDate(returnedCopy.getBorrower(), returnedCopy);
        
        /*
         * If the user request queue is empty, add the copy to the list of free
         * copies and mark it as free.
         */
        if (userRequestQueue.isEmpty()) {
            freeCopies.add(returnedCopy);

            // Gets the user that returned the copy and removes it from
            // there withdrawn copies.
            returnedCopy.getBorrower().removeBorrowedCopy(returnedCopy);
            returnedCopy.setBorrower(null);
            returnedCopy.resetDates();

            saveCopyToDB(returnedCopy);
        }
        /*
         * If the are user in the queue, reserve this copy for the first user in
         * the queue and take that person out of the queue.
         */
        else {
            User firstRequest = userRequestQueue.peek();
            
            returnedCopy.resetDates();
            returnedCopy.setBorrower(firstRequest);
            returnedCopy.setBorrowDate(new Date());
            saveCopyToDB(returnedCopy);
            noDueDateCopies.add(returnedCopy);
            
            try {
                Connection conn = DBHelper.getConnection();

                PreparedStatement sqlStatement = conn.prepareStatement("DELETE" +
                        " FROM requestsToApprove WHERE rID = ? AND userName = ?");
                sqlStatement.setInt(1, uniqueID);
                sqlStatement.setString(2, firstRequest.getUsername());
                sqlStatement.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            
            firstRequest.addBorrowedCopy(returnedCopy);
            userRequestQueue.dequeue();
            
            Resource.insertBorrowRecord(firstRequest, returnedCopy);
        }
    }

    /**
     * Ensures a copy is loaned to the given user if there are available copies,
     * else it adds the user to the request queue.
     * 
     * @param user The user that wants to borrow a copy of this resource.
     * @return If the user has the copy or is in the queue (false)
     */
    public boolean loanToUser(User user) {
        /*
         * If there are free copies, mark a copy as borrowed and reserve it for
         * the user.
         */
        if (!freeCopies.isEmpty()) {
        
            Copy copyToBorrow = freeCopies.removeFirst();

            copyToBorrow.resetDates();
            copyToBorrow.setBorrower(user);
            copyToBorrow.setBorrowDate(new Date());

            noDueDateCopies.add(copyToBorrow);
            user.addBorrowedCopy(copyToBorrow);

            saveCopyToDB(copyToBorrow);
            insertBorrowRecord(user, copyToBorrow);
            return true;
        }
        /*
         * Else, add the user to the request queue and set the due date of the
         * borrowed copy with no due date that has been borrowed the longest.
         */
        else {
        	
            userRequestQueue.enqueue(user);
            try {
                Copy firstCopy = noDueDateCopies.poll();
                
                firstCopy.setDueDate();
                saveCopyToDB(firstCopy);
            } catch(NullPointerException e) {
                e.printStackTrace();
                AlertBox.showErrorAlert("No copies in the database!");
            }
               
            return false;
        }
    }

    /**
     * Gets the unique ID of this resource.
     * @return The unique ID of this resource.
     */
    public int getUniqueID() {
        return uniqueID;
    }

    /**
     * Gets the thumbnail image of this resource.
     * @return The thumbnail image of this resource.
     */
    public Image getThumbnail() {
        return thumbnail;
    }

    /**
     * Gets the path of the thumbnail of this resource, as it is in the database
     * @return the path to the thumbnail of this resource.
     */
    public String getThumbnailPath() {
        return thumbnailPath;
    }
    
    /**
     * Sets the path to the thumbnail of this resource to a new value.
     * @param path new path to the thumbnail of the resource.
     */
    public void setThumnailPath(String path) {
        thumbnailPath = path;
    }

    /**
     * Sets the thumbnail to a new image.
     * @param thumbnail New Thumbnail Image.
     */
    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    /**
     * Sets the database value
     * @param thumbnail New location.
     */
    public void setThumbnailDatabase(String thumbnail) {
    	updateDBvalue("resource",uniqueID,"thumbnail",thumbnail);

    }

    /**
     * Gets the title of this resource.
     * @return The title of this resource.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this resource.
     * @param title New title value.
     */
    public void setTitle(String title) {
        updateDBvalue("resource", uniqueID, "title", title);
        this.title = title;
    }

    /**
     * Getter of the year this resource came out.
     * @return The year this resource came out.
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the year of this resource to a new value.
     * @param year New value for year.
     */
    public void setYear(int year) {
        updateDBvalue("resource", uniqueID, "year", year);
        this.year = year;
    }

    /**
     * Gets a copy of this resource with the given copy ID.
     * @param copyID The unique ID of this resource.
     * @return The copy of this resource with that ID, or null if there is no
     *  copy with that ID of this resource.
     */
    public Copy getCopy(int copyID) {
        for (Copy c : copyList) {
            if (c.getCopyID() == copyID) {
                return c;
            }
        }

        return null;
    }

    /**
     * Getter for the daily fine amount for over due copies of this type of
     * resource.
     * @return The daily fine amount for over due copies of this type of resource.
     */
    public abstract int getDailyFineAmount();

    /**
     * Getter for the maximum fine amount for over due copies of this type of
     * resource.
     * @return The maximum fine amount for over due copies of this type of resource.
     */
    public abstract int getMaxFineAmount();

    /**
     * Builds and returns a string representation of this resource.
     * @return A string representation of this resource.
     */
    @Override
    public String toString() {
        return "Title: " + title + "\nID: " + uniqueID + "\nYear: " + year;
    }

    /**
     * Returns a string with the unique ID and availability of each copy of this
     * resource. The information of each copy is on each separate line.
     * 
     * @return A string where each line of the unique ID and availability of a
     *         copy of this resource.
     */
    public String getCopyInformation() {
        String copieInfo = "";

        for (Copy c : copyList) {
            copieInfo += c.toString();
        }

        return copieInfo;
    }

    /**
     * Loads the copies of this resource from the database and loads them into
     *  the list of copies of this resource object.
     */
    public void loadCopyList() {
        Connection dbConnection = null; 
        Statement sqlStatement = null; 
        ResultSet savedCopies = null; 
        try {
            dbConnection = DBHelper.getConnection(); 
            sqlStatement = dbConnection.createStatement(); 
            savedCopies = sqlStatement.executeQuery("SELECT * FROM " +
               "copies WHERE rID=" + uniqueID);

            copyList.clear();
            freeCopies.clear();

            while (savedCopies.next()) {
                String userName = savedCopies.getString("keeper");

                if (userName != null) {
                    User borrower;
                    borrower = (User) Person.loadPerson(userName);

                    Date borrowDate = null;
                    Date lastRenewalDate = null;
                    Date dueDate = null;

                    try {
                        SimpleDateFormat normalDateFormat = new SimpleDateFormat(
                            "dd/MM/yyyy");
                        borrowDate = normalDateFormat.parse(savedCopies.getString("borrowDate"));

                        String dbLastRenewal = savedCopies.getString("lastRenewal");
                        if (dbLastRenewal != null) {
                            
                            lastRenewalDate = normalDateFormat.parse(dbLastRenewal);
                        }

                        String dbDueDate = savedCopies.getString("dueDate");
                        if (dbDueDate != null) {
                            
                            dueDate = normalDateFormat.parse(dbDueDate);
                        }
                    }
                    catch (ParseException e) {
                        System.err.println(
                            "Failed to load a date for a copy from the database.");
                    }

                    Copy c= new Copy(this, savedCopies.getInt("copyID"),
                        borrower, savedCopies.getInt("loanDuration"),
                        borrowDate, lastRenewalDate, dueDate);
                    copyList.add(c);
                }
                else {
                    Copy freeCopy = new Copy(this, savedCopies.getInt("copyID"),
                        null, savedCopies.getInt("loanDuration"));
                    copyList.add(freeCopy);
                    freeCopies.add(freeCopy);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(savedCopies != null) {
                try {
                    savedCopies.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if(sqlStatement != null) {
                try {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if(dbConnection != null) {
                try {
                    dbConnection.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Saves the user queue or users waiting to get a copy of this resource 
     * to the database.
     */
    public void saveUserQueue() {
        LinkedList<User> orderedUsers = userRequestQueue.getOrderedList();
        Connection dbConnection = null;
        PreparedStatement sqlStatement = null;
        
        try {
            dbConnection = DBHelper.getConnection();
            sqlStatement = dbConnection
                .prepareStatement("DELETE FROM userRequests");
            sqlStatement.executeUpdate();
            sqlStatement = dbConnection.prepareStatement(
                "INSERT INTO userRequests VALUES (" + uniqueID + ",?,?)");

            int orderNr = 1;
            User current = orderedUsers.pollFirst();
            while (current != null) {
                sqlStatement.setString(1, current.getUsername());
                sqlStatement.setInt(2, orderNr);
                sqlStatement.executeUpdate();
                current = orderedUsers.pollFirst();
                orderNr++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (dbConnection != null) {
                try {
                    dbConnection.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets the number of copies that this resource has, whether free or
     * borrowed.
     * 
     * @return The number of copies of this resource.
     */
    public int getNrOfCopies() {
        return copyList.size();
    }

    /**
     * Getter for the list of users waiting to have their borrow request 
     * approved by a librarian.
     * @return The list of users waiting to have their borrow request 
     * approved by a librarian.
     */
    public ArrayList<User> getPendingRequests() {
        return pendingRequests;
    }

    /**
     * Adds the given user to the list of borrow requests waiting to be
     * approved by a librarian. Updates the data base as well.
     * @param requester The user wanting to borrow a copy of this resource.
     */
    public void addPendingRequest(User requester) {
        pendingRequests.add(requester);
        Connection dbConnection = null;
        PreparedStatement sqlStatement = null;
        try {
            dbConnection = DBHelper.getConnection();
            sqlStatement = dbConnection.prepareStatement(
                "INSERT INTO requestsToApprove('rID','userName') VALUES (?,?)");
            sqlStatement.setInt(1, uniqueID);
            sqlStatement.setString(2, requester.getUsername());
            sqlStatement.executeUpdate();
            
           PreparedStatement sqlStatement2 = dbConnection.prepareStatement(
                    "INSERT INTO majorStat('resource') VALUES (?)");
                sqlStatement2.setInt(1, getUniqueID());
                sqlStatement2.executeUpdate();
          
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (dbConnection != null) {
                try {
                    dbConnection.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Calculates an integer representing how similar this resource is to the
     * given resource.
     * @param otherResource The resource this resource is compared with.
     * @return an integer representing how similar this resource is to the
     * given resource.
     */
    public int getLikenessScore(Resource otherResource) {
        int score = 0;

        if (title.equals(otherResource.getTitle())) {
            score += 4;
        }

        if (year == otherResource.getYear()) {
            score++;
        }

        return score;
    }

    /**
     * Determines if the title of this resource contains the given string.
     * @param search The substring that will be searched for in the title.
     * @return True if the given string is a substring of the title of this
     * resource, false if not.
     */
    public boolean contains(String search) {
        if (title != null &&
            title.toUpperCase().contains(search.toUpperCase())) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Computes a list of other resources that could possibly be part of the 
     * same series, based on the implementation of the 
     * isPossiblySameSeries(Resource) method.
     * @return a list of other resources that could possibly be part of the 
     * same series.
     */
    public List<Resource> getSameSeriesSuggestions() {
        List<Resource> possiblySameSeries = new LinkedList<>();
        
        for(Resource resource: resources) {
            if(isPossiblySameSeries(resource) && (this != resource)) {
                possiblySameSeries.add(resource);
            }
        }
        
        return possiblySameSeries;
    }
    
    /**
     * Says if this resource is possibly in the same series as another resource. 
     * It calculates it by just seeing if it is of the same type and if the first 
     * word of both titles is the same. Should be overridden to provide a better 
     * implementation for different resource types.
     * @param otherResource Another resource we want to see if it can be of the 
     * same series.
     * @return True if it is possibly of the same series, false if not.
     */
    public boolean isPossiblySameSeries(Resource otherResource) {
        if(getClass() != otherResource.getClass()) {
            return false;
        }
        
        String thisFirstWord = title.split(" ")[0];
        String otherFirstWord = otherResource.getTitle().split(" ")[0];
        
        if(thisFirstWord.equals(otherFirstWord)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Gets the limit amount used to restrict number of items a user can borrow.
     * @return the default amount of the resource.
     */
    public int getLimitAmount() {
    	return LIMIT_AMOUNT;
    }
    
    /**
     * time of the resource added.
     * @return the time added
     */
    public String getTimeStamp() {
        return this.timestamp;
    }
    
    /**
     * Gets a list of unique ids of resources which are considered to be in 
     * the same series as this resource.
     * @return a list of unique ids of resources which are considered to be in 
     * the same series as this resource.
     */
    public List<Integer> getSameSeriesResources() {
        return sameSeriesResources;
    }
    
    /**
     * Gets a list of the unique ids of resources which are considered related 
     * to this one, but not part of the same series.
     * @return a list of the unique ids of resources which are considered related 
     * to this one, but not part of the same series.
     */
    public List<Integer> getOtherRelatedResources() {
        return otherRelatedResources;
    }
    
    /**
     * Method to check if user last login compared to resource added timestamp
     * @param person current user
     * @return true if user last login was before resource added time, false otherwise.
     * @throws ParseException
     */
    public boolean compareTimeDifference(Person person) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resourceDate = formatter.parse(this.timestamp);
        long timeDifference = 0;
        try {
	        Date loginDate = formatter.parse(person.getLastLogin());
	        timeDifference = loginDate.getTime() - resourceDate.getTime();
        }
        catch (NullPointerException | ParseException e) {
        	timeDifference = Long.MIN_VALUE;
        }
        
        if (timeDifference < 0) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Updates the database value in a resource table.
     * @param tableName The table where the change will be made.
     * @param resourceID The id of the resource to be changed.
     * @param field The field that will be changed.
     * @param data The new value of the field.
     */
    protected static void updateDBvalue(String tableName, int resourceID,
            String field, String data) {
        Connection connectionToDB = null;
        PreparedStatement sqlStatement = null;
        try {
            connectionToDB = DBHelper.getConnection();
            sqlStatement = connectionToDB.prepareStatement(
                "update " + tableName + " set " + field + "=? where rID=?");
            sqlStatement.setString(1, data);
            sqlStatement.setInt(2, resourceID);
            sqlStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connectionToDB != null) {
                try {
                    connectionToDB.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Updates the database value in a resource table.
     * @param tableName The table where the change will be made.
     * @param resourceID The id of the resource to be changed.
     * @param field The field that will be changed.
     * @param data The new value of the field.
     */
    protected static void updateDBvalue(String tableName, int resourceID,
            String field, int data) {
        
        try (Connection connectionToDB = DBHelper.getConnection();
                PreparedStatement sqlStatement = connectionToDB.prepareStatement(
                "update " + tableName + " set " + field + "=? where rID=?")) {
            
            sqlStatement.setInt(1, data);
            sqlStatement.setInt(2, resourceID);
            sqlStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void insertBorrowRecord(User borrower, Copy copyToBorrow) {
        Connection connectionToDB = null;
        PreparedStatement sqlStatement = null;
        try {
            connectionToDB = DBHelper.getConnection();
            sqlStatement = connectionToDB.prepareStatement(
                "INSERT INTO borrowRecords (copyID,username,description) VALUES (?,?,?)");
            
            sqlStatement.setInt(1, copyToBorrow.getCopyID());
            sqlStatement.setString(2, borrower.getUsername());
            
            SimpleDateFormat normalDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            sqlStatement.setString(3, "Borrow Date:" + normalDateFormat.format(
                copyToBorrow.getBorrowDate()));
            sqlStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connectionToDB != null) {
                try {
                    connectionToDB.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static void insertReturnDate(User borrower, Copy copyToBorrow) {
        Connection connectionToDB = null;
        PreparedStatement sqlStatement = null;
        try {
            connectionToDB = DBHelper.getConnection();
            sqlStatement = connectionToDB.prepareStatement(
                "UPDATE borrowRecords SET description=? WHERE copyID=? AND " + 
                    "username=? AND description=?");
            
            SimpleDateFormat normalDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            String oldDescription = "Borrow Date:" + normalDateFormat.format(
                copyToBorrow.getBorrowDate());
            
            String newDescription = oldDescription + " Return Date: " + 
                normalDateFormat.format(new Date());
            
            sqlStatement.setString(1, newDescription);
            sqlStatement.setInt(2, copyToBorrow.getCopyID());
            sqlStatement.setString(3, borrower.getUsername());
            sqlStatement.setString(4, oldDescription);
            sqlStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connectionToDB != null) {
                try {
                    connectionToDB.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Applies fines to the user of the copy being returned, if that is the case,
     *  based on the due date and the current date.
     * @param copyToBeReturned The copy being returned.
     */
    private void applyFines(Copy copyToBeReturned) {
        Date dueDate = copyToBeReturned.getDueDate();
        if (dueDate == null) {
            return;
        }
        
        Date today = new Date();
        int daysOverDue = (int) ((today.getTime() - dueDate.getTime()) /
            MILLISECONDS_IN_DAY);
        if (daysOverDue > 0) {
            int amount = daysOverDue * getDailyFineAmount();

            if (amount > getMaxFineAmount()) {
                amount = getMaxFineAmount();
            }

            Connection dbConnection = null;
            PreparedStatement sqlStatement = null;
            try {
                dbConnection = DBHelper.getConnection();
                sqlStatement = dbConnection.prepareStatement(
                    "INSERT INTO fines (userName,rID,daysOver,amount,dateTime,"
                     + "paid) VALUES (?,?,?,?,?,?)");

                sqlStatement.setString(1,
                    copyToBeReturned.getBorrower().getUsername());
                sqlStatement.setInt(2, uniqueID);
                sqlStatement.setInt(3, daysOverDue);
                sqlStatement.setDouble(4, amount);

                SimpleDateFormat normalDateFormat = new SimpleDateFormat(
                    "dd/MM/yyyy");
                sqlStatement.setString(5, normalDateFormat.format(today));
                sqlStatement.setInt(6, 0);

                sqlStatement.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (sqlStatement != null) {
                    try {
                        sqlStatement.close();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (dbConnection != null) {
                    try {
                        dbConnection.close();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * Rebuilds the queue of borrowed copies with no due date set from the list
     * of all copies.
     */
    private void loadCopyPriorityQueue() {
        noDueDateCopies.clear();

        for (Copy c : copyList) {
            if (c.getDueDate() == null && c.getBorrower() != null) {
                noDueDateCopies.add(c);
            }
        }
    }

    /**
     * Loads the queue of users who want a copy of this resource but can not 
     * because there are no free copies from the database from the database
     * into the queue of this resource.
     */
    private void loadUserQueue() {
        userRequestQueue.clean();
        
        Connection dbConnection = null;
        Statement sqlStatement = null;
        try {
            dbConnection = DBHelper.getConnection();
            sqlStatement = dbConnection.createStatement();
            ResultSet userRequests = sqlStatement
                .executeQuery("SELECT * FROM userRequests " + "WHERE rID=" +
                uniqueID + " ORDER BY orderNumber ASC");

            while (userRequests.next()) {
                String userName = userRequests.getString("userName");
                User userWithRequest = (User) Person.loadPerson(userName);
                userRequestQueue.enqueue(userWithRequest);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (dbConnection != null) {
                try {
                    dbConnection.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Loads the queue of users who want a copy of this resource and whose 
     * requests need to be approved by a librarian from the database this 
     * resource.
     */
    private void loadPendingRequests() {
        pendingRequests.clear();
        Connection dbConnection = null;
        Statement sqlStatement = null;
        
        try {
            dbConnection = DBHelper.getConnection();
            sqlStatement = dbConnection.createStatement();
            ResultSet userRequests = sqlStatement.executeQuery(
                "SELECT * FROM requestsToApprove WHERE rID=" + uniqueID);

            while (userRequests.next()) {
                String userName = userRequests.getString("userName");
                User userWithRequest = (User) Person.loadPerson(userName);
                userRequestQueue.enqueue(userWithRequest);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (dbConnection != null) {
                try {
                    dbConnection.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadRelatedResources() {
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement selectStatement = dbConnection.prepareStatement(
                "SELECT seriesResource FROM resourceSeries WHERE rID = " + uniqueID);
                ResultSet inSameSeries = selectStatement.executeQuery()) {
            
            while(inSameSeries.next()) {
                sameSeriesResources.add(Integer.valueOf(inSameSeries.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
        }
        
        try (Connection dbConnection = DBHelper.getConnection();
                PreparedStatement selectStatement = dbConnection.prepareStatement(
                "SELECT relatedRsrc FROM related WHERE rID = " + uniqueID);
                ResultSet related = selectStatement.executeQuery()) {
            
            while(related.next()) {
                otherRelatedResources.add(Integer.valueOf(related.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert(e.getMessage());
        }
    }
    
    /**
     * Saves the given copy to the database.
     * @param copy The copy being saved.
     */
    private void saveCopyToDB(Copy copy) {
        Connection dbConnection = null;
        PreparedStatement statement = null;
        PreparedStatement sqlStatement = null;
        try {
            dbConnection = DBHelper.getConnection();
            // PreparedStatement sqlStatement = dbConnection.prepareStatement(
            // "DELETE FROM copies WHERE copyID=" + copy.getCopyID());
            // sqlStatement.executeUpdate();

            statement = dbConnection
                .prepareStatement("SELECT * FROM copies WHERE copyID=?");
                statement.setInt(1, copy.getCopyID());
            ResultSet results = statement.executeQuery();

            if (!results.next()) {
                results.close();
                SimpleDateFormat normalDateFormat = new SimpleDateFormat(
                    "dd/MM/yyyy");
                sqlStatement = dbConnection.prepareStatement(
                    "INSERT INTO copies " + "VALUES(?,?,?,?,?,?,?)");

                sqlStatement.setInt(1, copy.getCopyID());
                sqlStatement.setInt(2, uniqueID);
                sqlStatement.setInt(4, copy.getLoanDuration());
                sqlStatement.setString(5,
                    formatDate(copy.getBorrowDate(), normalDateFormat));
                sqlStatement.setString(6,
                    formatDate(copy.getLastRenewal(), normalDateFormat));
                sqlStatement.setString(7,
                    formatDate(copy.getDueDate(), normalDateFormat));

                String userName = null;
                if (copy.getBorrower() != null) {
                    userName = copy.getBorrower().getUsername();
                    sqlStatement.setString(3, userName);
                }
                else {
                    sqlStatement.setString(3, null);
                }
                sqlStatement.executeUpdate();
                dbConnection.close();
            } else {

                SimpleDateFormat normalDateFormat = new SimpleDateFormat(
                    "dd/MM/yyyy");

                sqlStatement = dbConnection.prepareStatement(
                    "UPDATE copies SET rID=?,keeper=?," +
                        "loanDuration=?,borrowDate=?,lastRenewal=?," +
                        "dueDate=? WHERE copyID=?");
                copy.getCopyID();
                sqlStatement.setInt(7, copy.getCopyID());
                sqlStatement.setInt(1, uniqueID);
                sqlStatement.setInt(3, copy.getLoanDuration());

                sqlStatement.setString(4,
                    formatDate(copy.getBorrowDate(), normalDateFormat));
                sqlStatement.setString(5,
                    formatDate(copy.getLastRenewal(), normalDateFormat));
                sqlStatement.setString(6,
                    formatDate(copy.getDueDate(), normalDateFormat));

                String userName = null;
                if (copy.getBorrower() != null) {
                    userName = copy.getBorrower().getUsername();
                    sqlStatement.setString(2, userName);
                }
                else {
                    sqlStatement.setString(2, null);
                }

                sqlStatement.executeUpdate();
            }
            dbConnection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } 
    }

    /**
     * A wrapper function that returns a string representing a date, or null if 
     * the given date is null. Necessary because format method for DateFormat
     *  would throw an exception when given null.
     * @param date The date to be converted to a string.
     * @param dateFormater The date formater to be used.
     * @return A string representation of the date, or null if the date is null.
     */
    private String formatDate(Date date, SimpleDateFormat dateFormater) {
        if (date == null) {
            return null;
        }
        else {
            return dateFormater.format(date);
        }
    }
}
