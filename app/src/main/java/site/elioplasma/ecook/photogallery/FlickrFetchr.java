package site.elioplasma.ecook.photogallery;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eli on 3/29/16.
 * Updated by eli on 11/10/16.
 */
class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";

    private static final String API_KEY = KeyHolder.getApiKey();
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final String GET_SIZES = "flickr.photos.getSizes";
    private static final String GET_USER_INFO = "flickr.people.getInfo";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    // urs_s --> small square 75x75
    // urs_q --> large square 150x150

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    List<GalleryItem> fetchRecentPhotos() {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }

    List<GalleryItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }

    List<GalleryItem> searchPhotos(Location location) {
        String url = buildUrl(location);
        return downloadGalleryItems(url);
    }

    String getPhotoUrl(String id) {
        String url = buildUrl(GET_SIZES, id);
        return downloadPhotoUrl(url);
    }

    String getOwnerName(String ownerId) {
        String url = buildUrl(GET_USER_INFO, ownerId);
        return downloadUserInfo(url);
    }

    private List<GalleryItem> downloadGalleryItems(String url) {

        List<GalleryItem> items = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;
    }

    private String downloadPhotoUrl(String url) {

        StringBuilder photoUrl = new StringBuilder();

        try {
            Log.i(TAG, "URL: " + url);
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parsePhotoUrl(photoUrl, jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch photo url", ioe);
        }

        return photoUrl.toString();
    }

    private String downloadUserInfo(String url) {

        String userInfo = "";

        try {
            Log.i(TAG, "URL: " + url);
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            userInfo = parseUserInfo(jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch user info", ioe);
        }

        return userInfo;
    }

    private String buildUrl(String method, String extra) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);

        if (method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("text", extra);
        }

        if (method.equals(GET_SIZES)) {
            uriBuilder.appendQueryParameter("photo_id", extra);
        }

        if (method.equals(GET_USER_INFO)) {
            uriBuilder.appendQueryParameter("user_id", extra);
        }

        return uriBuilder.build().toString();
    }

    private String buildUrl(Location location) {
        return ENDPOINT.buildUpon()
                .appendQueryParameter("method", SEARCH_METHOD)
                .appendQueryParameter("lat", "" + location.getLatitude())
                .appendQueryParameter("lon", "" + location.getLongitude())
                .build().toString();
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException {

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setOwnerId(photoJsonObject.getString("owner"));
            item.setTitle(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }

    private void parsePhotoUrl(StringBuilder photoUrl, JSONObject jsonBody)
            throws IOException, JSONException {

        JSONObject photosJsonObject = jsonBody.getJSONObject("sizes");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("size");

        String url = "";

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            String label = photoJsonObject.getString("label");
            if (label.equals("Small") || label.equals("Medium")) {
                url = photoJsonObject.getString("source");
            } else if (label.equals("Large")) {
                url = photoJsonObject.getString("source");
                break;
            }

        }

        photoUrl.append(url);
    }

    private String parseUserInfo(JSONObject jsonBody)
            throws IOException, JSONException {

        JSONObject userJsonObject = jsonBody.getJSONObject("person");
        JSONObject nameJsonObject = userJsonObject.getJSONObject("username");
        return nameJsonObject.getString("_content");
    }
}
