package com.example.msrouji.tv_app.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by msrouji on 01/09/2017.
 */

public class Category implements Serializable, NameInfo {
    static final long serialVersionUID = 727566134075960653L;

    private long id;
    private String name;
    private long tag;

    // Builder of the class
    public Category(JSONObject receiveObj){
        try {
            id = receiveObj.getLong("id");
            name = receiveObj.getString("name");
            tag = receiveObj.getLong("tag");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Category(long id, String name, long tag) {
        this.id = id;
        this.name = name;
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public long getTag() {
        return tag;
    }

    public void buildFromJson(JSONObject object) {
        try {
            id = object.getLong("id");
            name = object.getString("name");
            tag = object.getLong("tag");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
