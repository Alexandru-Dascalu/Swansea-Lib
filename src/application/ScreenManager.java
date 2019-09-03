
package application;

import java.util.ArrayList;
import java.util.LinkedList;

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

	public static Copy currentCopy;

	/**
	* Getter method for getting currentUser.
	* @return currentUser user currently logged in.
	*/
	public static Person getCurrentUser() {
		return currentUser;
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
	@SuppressWarnings("unchecked")
	public static ArrayList<Resource> getResources() {
		if(resources != null) 
		{
			return (ArrayList<Resource>) resources.clone();
		} 
		else 
		{
			return null;
		}
	}

	/**
	* Setter method for the list of resources.
	* @param resources a list of resources.
	*/
	public static void setResources(ArrayList<Resource> resources) {
		ScreenManager.resources = resources;
	}

}
