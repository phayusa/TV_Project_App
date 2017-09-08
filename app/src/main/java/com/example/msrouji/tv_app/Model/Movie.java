package com.example.msrouji.tv_app.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by msrouji on 07/09/2017.
 */

public class Movie extends Stream{
    private String image_url;

    public Movie(){}

    public Movie(JSONObject object) {
        super(object);
        try {
            image_url = object.getString("image_url");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getImage_url() {
        return image_url;
    }
}
