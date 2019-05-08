package model;

import java.util.ArrayList;

/**
 * A class that represents a score which shows how similar a resource is to
 * other resources whose copies have been borrowed in the past by a user.
 * It is used to implement the extra feature of showing recommended resources
 * to the user.
 * @author Alexandru Dascalu
 *
 */
public class ResourceRecommender
        implements Comparable<ResourceRecommender> {

    private Resource resource;

    private ArrayList<Resource> borrowedResources;

    /**
     * Makes a new ResourceRecommendScore object. It just initialises the 
     * resource as null and the list of resources as an empty list.
     */
    public ResourceRecommender() {
        resource = null;
        borrowedResources = new ArrayList<Resource>();
    }

    /**
     * Calculates how similar the resource is to the overall list of 
     * borrowedResources.
     * @return A score representing how the resource of this object is similar 
     * to the overall list of resources of this object
     */
    public int calculateLikeness() {
        int likeness = 0;

        for (Resource borrowedResource : borrowedResources) {
            likeness += borrowedResource.getLikenessScore(resource);
        }

        return likeness;
    }

    /**
     * Gets the resource this object calculates the score for.
     * @return The resource of this object.
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Sets the resource of this object to a new value.
     * @param resource New value of the resource to be compared.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Gets the list of resources which have ever been borrowed by a user and 
     * that the resource of this object is compared to.
     * @return The list of resources which have ever been borrowed by a user and 
     * that the resource of this object is compared to.
     */
    public ArrayList<Resource> getBorrowedResources() {
        return borrowedResources;
    }

    /**
     * Sets the list of resources which have ever been borrowed by a user and 
     * that the resource of this object is compared to.
     * @param borrowedResources New value of the list of borrowed resources.
     */
    public void setBorrowedResources(ArrayList<Resource> borrowedResources) {
        this.borrowedResources = borrowedResources;
    }

    /**
     * A method that compares object of this class and establishes and order. 
     * Objects that have a higher likeness score are considered smaller, so 
     * their natural ordering is descending.
     * @param other The other ResourceRecommendScore object that this is compared against.
     * @return -1 if the other object has a lower score, 1 of the other object 
     * has a higher score, 0 if their scores are the same.
     */
    public int compareTo(ResourceRecommender other) {
        int likenessDifference = calculateLikeness() -
            other.calculateLikeness();
        if (likenessDifference > 0) {
            return -1;
        }
        else if (likenessDifference < 0) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * Returns a string represantation of this object.
     * @return A string with the details of the resource of this object and
     * the score.
     */
    public String toString() {
        String result = resource.toString() + " score: " + calculateLikeness();
        return result;
    }
}
