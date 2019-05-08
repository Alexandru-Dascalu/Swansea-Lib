package model;

import java.util.Comparator;

public class ResourceComparator implements Comparator<Resource> {
    
    private final Resource resourceToCompareAgainst;
    
    public ResourceComparator(Resource resourceToCompareAgainst) {
        this.resourceToCompareAgainst = resourceToCompareAgainst;
    }
    
    public int compare(Resource r1, Resource r2) {
        return (r1.getLikenessScore(resourceToCompareAgainst) - 
                r2.getLikenessScore(resourceToCompareAgainst));
    }
}
