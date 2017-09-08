package com.example.msrouji.tv_app.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by msrouji on 01/09/2017.
 */

public class Stream implements Serializable, NameInfo {
    static final long serialVersionUID = 727523134075960653L;


    private long id;
    private String name;
    private String url;
    private String image_url=null;
    private String summary;

    public Stream(){}

    public Stream(JSONObject object){
        this(object,false);
    }

    // Builder of the class
    public Stream(JSONObject receiveObj, boolean image){
        try {
            id = receiveObj.getLong("id");
            name = receiveObj.getString("name");
            if (receiveObj.has("url"))
                url = receiveObj.getString("url").replace(" ","");

            if (receiveObj.has("image_url"))
                image_url = receiveObj.getString("image_url");

            if (receiveObj.has("summary"))
                summary = receiveObj.getString("summary");

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Stream(String name, String url, long category_id) {
        this.name = name;
        this.url = url;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getUrl() {
        if (!url.startsWith("http"))
            return url.substring(1);
        return url;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return "Stream{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    /*
    public void buildFromJson(JSONObject object) {
        try {
            id = object.getLong("id");
            name = object.getString("name");
            url = object.getString("url").replace(" ","");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}
