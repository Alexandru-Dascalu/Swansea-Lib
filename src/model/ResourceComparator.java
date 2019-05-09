package model;

import java.util.Comparator;

/**
 * A comparator used to order resources in descending order based on their 
 * likeness to a resource given to the constructor of this class.
 * @author Alexandru Dascalu
 */
public class ResourceComparator implements Comparator<Resource> {
    
    /**The resource that other resources given to the compare method will be
     *  compared against.*/
    private final Resource resourceToCompareAgainst;
    
    /**
     * Makes a new comparator that will compare resources against the given
     * resources.
     * @param resourceToCompareAgainst The resource other resources will be 
     * compared against.
     */
    public ResourceComparator(Resource resourceToCompareAgainst) {
        this.resourceToCompareAgainst = resourceToCompareAgainst;
    }
    
    /**
     * Compares two resources by comparing their likeness to the resource of 
     * this comparator, so that the resources are ordered descendingly.
     * @param r1 a resource to compare.
     * @param r2 the second resource to compare.
     * @return a negative integer, zero, or a positive integer as the first 
     * argument is less than, equal to, or greater than the second, based on 
     * their likeness to the resource of this comparator.
     */
    public int compare(Resource r1, Resource r2) {
        return (r2.getLikenessScore(resourceToCompareAgainst) - 
                r1.getLikenessScore(resourceToCompareAgainst));
    }
}
