package com.example.msrouji.tv_app.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by msrouji on 07/09/2017.
 */

public class HeaderInfo implements NameInfo {
    private String name;
    private long id;
    private String extra_label;

    public HeaderInfo(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public HeaderInfo(JSONObject object, String extra_label) {
        try {
            id = object.getLong("id");
            if (object.has("number"))
                name = extra_label + " " + object.getString("number");
            else
                name = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public HeaderInfo(JSONObject object) {
        try {
            id = object.getLong("id");
            name = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }
}
