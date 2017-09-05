package com.example.msrouji.tv_app.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by msrouji on 01/09/2017.
 */

public class Stream implements Serializable{
    static final long serialVersionUID = 727523134075960653L;


    private long id;
    private String name;
    private String url;
    private long category_id;
    private String extra_info;

    // Builder of the class
    public Stream(JSONObject receiveObj){
        try {
            id = receiveObj.getLong("id");
            name = receiveObj.getString("name");
            url = receiveObj.getString("url").replace(" ","");
            category_id = receiveObj.getInt("category");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Stream(String name, String url, long category_id, String extra_info) {
        this.name = name;
        this.url = url;
        this.category_id = category_id;
        this.extra_info = extra_info;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        if (!url.startsWith("http"))
            return url.substring(1);
        return url;
    }

    public long getCategory_id() {
        return category_id;
    }

    public String getExtra_info() {
        return extra_info;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Stream{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", category_id=" + category_id +
                ", extra_info='" + extra_info + '\'' +
                '}';
    }
}
