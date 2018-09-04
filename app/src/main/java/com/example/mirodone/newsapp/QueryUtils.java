package com.example.mirodone.newsapp;

// Helper methods needed for request/receive news data from Guardian website.

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.mirodone.newsapp.MainActivity.LOG_TAG;

public class QueryUtils {

    private QueryUtils() {

    }

    // ** 1 ** Returns new URL object from the given string URL.

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    // ** 2 ** Make an HTTP request to the given URL and return a String as the response.

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // ** 3 ** Convert the {@link InputStream} into a String which contains the whole JSON response from the server.

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //  ** 4 **    Return a list of  News objects that has been built up from
    //      parsing the JSON response.

    private static List<News> extractFeatureFromJson(String newsJSON) {

        //if JSON string is empty or null, return early

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        //Create an empty ArrayList where we can add earthquakes

        List<News> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // create a JSONobj from JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results ( news).
            JSONArray newsArray = baseJsonResponse.optJSONObject("response").getJSONArray("results");

            // for each news in the newsArray, create an NEWS object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news at position i within the list of news
                JSONObject currentNews = newsArray.getJSONObject(i);


                // Extract the value for the key called "webTitle"
                String title = currentNews.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String sectionName = currentNews.getString("sectionName");

                // Extract the value for the key called "url"
                String url = currentNews.getString("webUrl");

                // "tags" array element

                JSONArray tagsArray = currentNews.getJSONArray("tags");

                String authorFullName = "";
                if (tagsArray.length() > 0) {
                    JSONObject currentTag = tagsArray.optJSONObject(0);

                    // Extract the value for the key called "webTitle" from array tags, that is actually the author name
                    authorFullName = currentTag.getString("webTitle");

                    if (authorFullName.equals("")) {
                        authorFullName = "";
                    } else {
                        authorFullName = ("Author: ").concat(authorFullName);
                    }
                }


                // Extract the value for the key called "webPublicationDate"
                String originalPublicationDate = currentNews.getString("webPublicationDate");

                //Format publication date
                Date publicationDate = null;
                try {
                    publicationDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(originalPublicationDate);
                } catch (Exception e) {
                    // If an error is thrown when executing the above statement in the "try" block,
                    // catch the exception here, so the app doesn't crash. Print a log message
                    // with the message from the exception.
                    Log.e("QueryUtils", "Problem parsing the news date", e);
                }


                // Create a new  NEWS object with the title, sectionName, authorFullName,
                // date and url from the JSON response.
                News news = new News(title, sectionName, authorFullName, publicationDate, url);
                // add the new EQ to the list of eqs
                newsList.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return newsList;
    }

    // ** 5 **   method that ties all the steps together - creating a URL, sending the request,
    // processing the response. Since this is the only “public” QueryUtils method that the
    // EarthquakeAsyncTask needs to interact with, make all other helper methods in QueryUtils “private”.

    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making the HTTP request", e);
        }


        // Extract relevant fields from the JSON response and create a list of Earthquakes
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of Earthquakes
        return news;
    }


}
