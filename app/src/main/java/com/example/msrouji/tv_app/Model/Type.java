package com.example.msrouji.tv_app.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by msrouji on 04/09/2017.
 */

public class Type implements Serializable, NameInfo {
    private String name;
    private long id;

    // Builder of the class
    public Type(JSONObject receiveObj){
        try {
            name = receiveObj.getString("name");
            id = receiveObj.getInt("id");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Type(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Type{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    public void buildFromJson(JSONObject object) {
        try {
            name = object.getString("name");
            id = object.getInt("id");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
