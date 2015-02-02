package com.example.mvince.gridimagesearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mvince on 1/31/15.
 */
public class ImageResult implements Serializable {
    public String fullUrl;
    public String thumbUrl;
    public String title;

    // new ImageResult(..raw item json..)
    public ImageResult(JSONObject json) {
        try {
            this.fullUrl = json.getString("url");
            this.thumbUrl = json.getString("tbUrl");
            this.title = json.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Take an array of json images and return arraylist of image results
    // ImageResult.fromJsonArray([..., ...]);
    public static ArrayList<ImageResult> fromJsonArray(JSONArray array) {
        ArrayList<ImageResult> results = new ArrayList<ImageResult>();
        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new ImageResult(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
