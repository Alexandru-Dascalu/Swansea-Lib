package application;

import model.MovieDescription;
import model.VideoDescription;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.scene.web.WebView;
import java.io.IOException;
import java.util.List;

/**
 * This class is a view of a movie trailer which is dynamically loaded from 
 * the Web. This class contains all the code that finds out the Youtube URL 
 * of a trailer for a movie with the given name. The trailer is loaded as 
 * an embedded Youtube video into the WebView object of this 
 * MovieTrailerView.
 * @author Alexandru Dascalu
 * @version 1.1
 */
public class MovieTrailerView {
    /**
     * The API key needed to use the TMDB api to get details of the trailer
     * for a movie.
     */
    private static final String API_KEY = "fde767385d9021cca4adc2853f21a53f";
    
    /**
     * The URL to the REST web service API used to search for the IMDB ID of a movie
     *  based on its name.
     */
    private static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie?";
    
    /**
     * The URL to the REST web service API to get details of a trailer for the 
     * movie with a given ID.
     */
    private static final String GET_VIDEOS_URL = "https://api.themoviedb.org/3/movie/{movieID}/videos?";
    
    /**
     * The first part of the URL of the trailer, which is always the same for 
     * all embedded youtube videos.
     */
    private static final String YOUTUBE_URL = "https://www.youtube.com/embed/";
    
    /**
     * The default width of the WebView associated with this object. Its 
     * value is {@value}.
     */
    private static final int TRAILER_VIEW_WIDTH = 1600;
    
    /**
     * The default width of the WebView associated with this object. Its 
     * value is {@value}.
     */
    private static final int TRAILER_VIEW_HEIGHT = 900;
    
    /**
     * The ObjectMapper used to translate strings into JSON objects from which 
     * we will read data about movies and trailers.
     */
    private ObjectMapper jsonMapper;
    
    /**
     * The movie description from the TMDB database of the movie with the given
     * name.
     */
    private MovieDescription movieDescription;
    
    /**
     * The trailer description from the TMDB database of the trailer for the 
     * movie with the given name.
     */
    private VideoDescription trailerDescription;
    
    /**
     * The WebView that loads the trailer of the given movie from Youtube.
     */
    private WebView youtubeView;
    
    /**
     * Makes a new MovieTrailerView object for the movie with the given name.
     * @param movieName The name of the movie whose trailer we want to view.
     */
    public MovieTrailerView(String movieName) {
        jsonMapper = new ObjectMapper();
        
        /*Get the ID of the movie on the TMDB database, get a description for
         * a trailer of that movie, then load the URL of that video into 
         * a WebView.*/
        movieDescription = getMovieFromTMDB(movieName);
        trailerDescription = null;
        youtubeView = null;
        
        if(movieDescription != null) {
            trailerDescription = getTrailerFromTMDB(movieDescription);
        } else {
        	AlertBox.showErrorAlert("The movie could not be found in the "
        			+ "TMDB database.");
        }
        
        if(trailerDescription !=null) {
            youtubeView = new WebView();
            youtubeView.getEngine().load(YOUTUBE_URL + trailerDescription.getKey());
            youtubeView.setPrefSize(TRAILER_VIEW_WIDTH, TRAILER_VIEW_HEIGHT);
        } else {
	    	AlertBox.showErrorAlert("There is no trailer for this a movie with"
	    			+ " this exact name.");
	    }
    }
    
    /**
     * Makes a request to the TMDB web API to get details of a movie with the 
     * given name and returns those details.
     * @param movieName The name of the movie whose details we want to find.
     * @return A MovieDescription object representing details of the movie we 
     * searched for.
     */
    public MovieDescription getMovieFromTMDB(String movieName) {
        HttpResponse<String> response = null;
        
        /*Make a request to the web API for movies with names similar to the 
         * movie name we are searching for.*/
        try {
            response = Unirest.get(SEARCH_URL).queryString("api_key", API_KEY)
                    .queryString("query", movieName).queryString("page", 1)
                    .queryString("include_adult", false).asString();
        }
        catch (UnirestException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert("Connection to TMDB web database failed.");
            return null;
        }
        
        /*Get the string representing just the list of results, and parse that
         * into a list of java objects.*/
        String resultList = getResultList(response.getBody());
        List<MovieDescription> movieDescriptions = null;
        
        try {
            movieDescriptions = jsonMapper.readValue(resultList, new TypeReference<List<MovieDescription>>() {});
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            AlertBox.showErrorAlert("The result from the TMDB web database is "
            		+ "not in correct JSON format.");
            return null;
        }
      
        /*Search through the list for a movie description with the same name 
         * as the given movie name, and return that description.*/
        MovieDescription result = null;
        boolean search = true;
        int i = 0;
        while(search && i < movieDescriptions.size())
        {
            if(movieDescriptions.get(i).getTitle().equals(movieName))
            {
                result = movieDescriptions.get(i);
                search = false;
            }
            
            i++;
        }
        
        return result;
    }
    
    /**
     * Get the video description of the first video search result for a movie 
     * with the given movie description.
     * @param movieDescription The description of the movie for which we want 
     * a trailer description.
     * @return A video description for the first trailer in the list of results
     * for a video associated with the given movie.
     */
    public VideoDescription getTrailerFromTMDB(MovieDescription movieDescription) {
        /*Send a request to the TMDB web api for details of videos of the 
         * movie from the given description.*/
        HttpResponse<String> response = null;
        try {
            response = Unirest.get(GET_VIDEOS_URL).routeParam("movieID", movieDescription.getID() + "")
                    .queryString("api_key", API_KEY).asString();
        }
        catch (UnirestException e) 
        {
            e.printStackTrace();
            AlertBox.showErrorAlert("Connection to TMDB web database failed.");
            return null;
        }
        
        /*Get the string representing just the list of results, and parse that
         * into a list of java objects.*/
        String resultList = getResultList(response.getBody());
        
        List<VideoDescription> videoDescriptions = null;
        try {
            videoDescriptions = jsonMapper.readValue(resultList, 
                new TypeReference<List<VideoDescription>>() {});
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            AlertBox.showErrorAlert("Reading the result from the TMDB web database failed.");
            return null;
        }
        
        /*Search through the list for a video description of a trailer of 
         * Youtube and return the first such video description.*/
        VideoDescription video = null;
        boolean search = true;
        int i = 0;
        while(search && i < videoDescriptions.size())
        {
            VideoDescription currentVideo = videoDescriptions.get(i);
            if(currentVideo.getType().equalsIgnoreCase("Trailer") && 
                    currentVideo.getSite().equalsIgnoreCase("YouTube"))
            {
                video = currentVideo;
                break;
            }
            
            i++;
        }
        
        return video;
    }
    
    /**
     * Stops any web content inside the WebView associated wih this object.
     */
    public void stop() {
        youtubeView.getEngine().load(null);
    }
    
    /**
     * Gets the WebView containing a web page with a trailer for the given 
     * movie.
     * @return The WebView associated with this object.
     */
    public WebView getWebView() {
        return youtubeView;
    }
    
    /**
     * Gets the description from TMDB of the movie with the given name.
     * @return the description associated with the given movie.
     */
    public MovieDescription getMovieDescription() {
        return movieDescription;
    }
    
    /**
     * Gets the description from TMDB of the movie with the given name.
     * @return the description associated with the given movie.
     */
    public VideoDescription getTrailerDescription() {
        return trailerDescription;
    }
    
    /**
     * Gets the default width of the WebView associated with this object.
     * @return the default width of the WebView associated with this object.
     */
    public int getPrefViewWidth() {
        return TRAILER_VIEW_WIDTH;
    }
    
    /**
     * Gets the default height of the WebView associated with this object.
     * @return the default height of the WebView associated with this object.
     */
    public int getPrefViewHeight() {
        return TRAILER_VIEW_HEIGHT;
    }
    
    /**
     * Returns the substring of the given string in between the first "[" and
     * the last "]". Used to extract a subtring representing an array from a 
     * string representing a JSON object with an array field.
     * @param jsonSearchResult The string from which we will extract a subtring.
     * @return The substring representing a JSON array.
     */
    private static String getResultList(String jsonSearchResult) {
        int resultStart = jsonSearchResult.indexOf("[");
        int resultEnd = jsonSearchResult.lastIndexOf("]");
        
        return jsonSearchResult.substring(resultStart, resultEnd + 1);
    }
}
