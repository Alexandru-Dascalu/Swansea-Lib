
package application;

import java.util.ArrayList;

import model.Book;
import model.Copy;
import model.DVD;
import model.Game;
import model.Laptop;
import model.Librarian;
import model.Person;
import model.Resource;

/**
* ScreenManager is a class that helps us get variables from the database to use in our scenes.
* @author Kane.
*
*/
public class ScreenManager {

	private static Person currentUser;
	private static ArrayList<Resource> resources;
	private static Librarian currentLibrarian;

	public static Resource currentResource;
	public static Copy currentCopy;
	
	public static Book currentBook;
	public static Laptop currentLaptop;
	public static DVD currentDVD;
	public static Game currentGame;
	

	/**
	* Getter method for getting currentUser.
	* @return currentUser user currently logged in.
	*/
	public static Person getCurrentUser() {
		return currentUser;
	}

	/**
	* Getter method for getting currentResource.
	* @return currentResource the resource being looked at.
	*/
	public static Resource getCurrentResource() {
		return currentResource;
	}

	/**
	* Setter method for currentResource.
	* @param resource the instance of the resource.
	*/
	public static void setCurrentResource(Resource resource) {
		currentResource = resource;
	}

	/**
	* Setter method for currentUser.
	* @param currentUser user currently using the program.
	*/
	public static void setCurrentUser(Person currentUser) {
		ScreenManager.currentUser = currentUser;
	}

	/**
	* Getter method that gets the list of resources.
	* @return resources a list of resources.
	*/
	public static ArrayList<Resource> getResources() {
		return (ArrayList<Resource>) resources.clone();
	}

	/**
	* Setter method for the list of resources.
	* @param resources a list of resources.
	*/
	public static void setResources(ArrayList<Resource> resources) {
		ScreenManager.resources = resources;
	}

}
