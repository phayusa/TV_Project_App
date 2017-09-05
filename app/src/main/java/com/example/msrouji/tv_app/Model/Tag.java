package com.example.msrouji.tv_app.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by msrouji on 04/09/2017.
 */

public class Tag implements Serializable {
    private String name;
    private long id;
    private long type;

    // Builder of the class
    public Tag(JSONObject receiveObj){
        try {
            name = receiveObj.getString("name");
            id = receiveObj.getLong("id");
            type = receiveObj.getLong("type");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public long getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Type{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
