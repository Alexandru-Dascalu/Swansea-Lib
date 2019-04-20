package application;

import java.io.IOException;
import javafx.scene.web.WebView;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * This class is a view of a video game trailer which is dynamically loaded 
 * from the Web. This class contains all the code that finds out the Youtube 
 * URL of a trailer for a movie with the given name. The trailer is loaded as 
 * an embedded Youtube video into the WebView object of this 
 * GameTrailerView.
 * @author Alexandru Dascalu
 * @version 1.1
 */
public class GameTrailerView {
    
    /**The first part of the URL of the trailer, which is always the same for 
     * all embedded youtube videos.*/
    private static final String YOUTUBE_URL = "https://www.youtube.com/embed/";
    
    /**The URL to the Youtube Web API method with parameters needed to search 
     * for a video based on some keywords.*/
    private static final String YOUTUBE_API_SEARCH_URL = "https://www.google" + 
            "apis.com/youtube/v3/search?part=snippet&maxResults=1&order=rating&q=";
    
    /**The Youtube API key needed to access the API, written as an argument in
     * a URL.*/
    private static final String YOUTUBE_API_KEY = "&key=AIzaSyBLyeidWvIxNFdvK" + 
            "0Bl7fPZ_WqeXrI8cac";
    
    /**The string added to the name of the video game so we will get a launch 
     * trailer of this game.*/
    private static final String KEYWORD_ADD_ON = " Launch Trailer";
    
    /**The timeout period of the connection until an exception is thrown if 
     * the server has not responded.*/
    private static final int REQUEST_TIMEOUT = 10000;
    
    /**The default width of the WebView the trailer will be displayed in.*/
    private static final int TRAILER_VIEW_WIDTH = 1600;
    
    /**The default height of the WebView the trailer will be displayed in.*/
    private static final int TRAILER_VIEW_HEIGHT = 900;
    
    /**The name of the video displayed by the WebView of this object.*/
    private String videoName;
    
    /**The key of the youtube URL of the video this object displays.*/
    private String youtubeKey;
    
    /**The WebView object used to display the video from youtube Tawe Lib.*/
    private WebView youtubeView;
    
    /**
     * Makes a new GameTrailerView object for the game with the given name.
     * @param gameName The name of the game whose trailer we want to view.
     */
    public GameTrailerView(String gameName) {
        JSONObject firstResult = getFirstResult(gameName);
        videoName = null;
        youtubeKey = null;
        youtubeView = null;
        
        /*FirstResult wil be null if the connection to the Youtube API failed 
         * or it returned invalid data, so we check it is not to avoid an
         * exception.*/
        if(firstResult != null) {
            videoName = firstResult.getJSONObject("snippet").getString("title");
            youtubeKey = firstResult.getJSONObject("id").getString("videoId");
            
            youtubeView = new WebView();
            youtubeView.setPrefSize(TRAILER_VIEW_WIDTH, TRAILER_VIEW_HEIGHT);
            youtubeView.getEngine().load(YOUTUBE_URL + youtubeKey);
        }
    }
    
    /**
     * Returns a JSON object representing the first video in the list of 
     * results from a Youtube search for a trailer for the game with given name.
     * 
     * This code was initially copied from http://www.joe0.com/2016/03/05/
     * youtube-data-api-v3-how-to-search-youtube-using-java-and-extract-video-
     * id-of-the-most-relevant-result/ . However, that code failed because the 
     * JSON object did not have the field videoID, so I had to heavily modify 
     * the code to make it work. The code after line 110 is all done by me. I 
     * also added the try catch block.
     * @param gameName The name of the game for which we want a video.
     * @return A JSON object representing the first result from Youtube.
     */
    public JSONObject getFirstResult(String gameName) {
        String keyword = gameName + KEYWORD_ADD_ON;
        keyword = keyword.replace(" ", "+");
 
        String searchUrl = YOUTUBE_API_SEARCH_URL + keyword + YOUTUBE_API_KEY;
 
        Document resultsDocument = null;
        try {
            Connection apiConnection = Jsoup.connect(searchUrl).timeout(REQUEST_TIMEOUT);
            resultsDocument = apiConnection.ignoreContentType(true).get();
        }
        catch (IOException e) {
            e.printStackTrace();
            AlertBox.showErrorAlert("Connection to youtube web API failed.");
            return null;
        }
 
        String jsonResultString = resultsDocument.text();
        JSONObject resultJsonObject = (JSONObject) new JSONTokener(jsonResultString).nextValue();
        JSONObject firstResult = resultJsonObject.getJSONArray("items").getJSONObject(0);
        
        return firstResult;
    }
    
    /**
     * Gets the WebView used to display the game trailer.
     * @return the WebView used to display the game trailer.
     */
    public WebView getWebView() {
        return youtubeView;
    }
    
    /**
     * Gets the name of the video for which a trailer will be displayed.
     * @return the name of the video for which a trailer will be displayed.
     */
    public String getVideoName() {
        return videoName;
    }
    
    /**
     * Gets the key of the youtube URL of the video this object displays.
     * @return the key of the youtube URL of the video this object displays.
     */
    public String getYoutubeKey() {
        return youtubeKey;
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
     * Stops any web content inside the WebView associated wih this object.
     */
    public void stop() {
        youtubeView.getEngine().load(null);
    }
}
