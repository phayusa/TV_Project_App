package com.example.msrouji.tv_app.Controller;

import android.os.AsyncTask;

import com.example.msrouji.tv_app.Model.Category;
import com.example.msrouji.tv_app.Model.Stream;
import com.example.msrouji.tv_app.Model.Tag;
import com.example.msrouji.tv_app.Model.Type;
import com.example.msrouji.tv_app.View.SpinnerFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by msrouji on 01/09/2017.
 */

public class StreamFactory extends AsyncTask<String, Void, Void> {
    public HashMap<Type, HashMap<Tag, HashMap<Category, List<Stream>>>> map_categ_channel;
    private DataLoadingInterface data_loader;

    public StreamFactory(DataLoadingInterface data_loader) {
        this.data_loader = data_loader;
        data_loader.request_data();
        map_categ_channel = new HashMap<Type, HashMap<Tag, HashMap<Category, List<Stream>>>>();
    }

    private JSONArray requestData(String adress) {
        try {
            URL url = new URL(adress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            BufferedReader buff = new BufferedReader(reader);

            if (conn.getResponseMessage().equals("OK")) {
                return new JSONArray(buff.readLine());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            /*JSONArray type = requestData(params[2]);
            int nb_type = type.length();
            for (int type_id = 0; type_id < nb_type; type_id++) {
                map_categ_channel.put(new Type(type.getJSONObject(type_id)), new HashMap<Tag, HashMap<Category, List<Stream>>>());
                //listCategories.add(new Category(categories.getJSONObject(category_id)));
            }

            JSONArray tags = requestData(params[3]);
            int nb_tags = tags.length();
            for (int tag_id = 0; tag_id < nb_tags; tag_id++) {
                Tag tag = new Tag(tags.getJSONObject(tag_id));
                for (Type type_tag : map_categ_channel.keySet()) {
                    if (type_tag.getId() == tag.getType())
                        map_categ_channel.get(type_tag).put(tag, new HashMap<Category, List<Stream>>());
                }
            }

            JSONArray categories = requestData(params[1]);
            int nb_categories = categories.length();
            for (int category_id = 0; category_id < nb_categories; category_id++) {
                Category object_Category = new Category(categories.getJSONObject(category_id));
                for (Type type_categ : map_categ_channel.keySet()) {
                    for (Tag tag_categ : map_categ_channel.get(type_categ).keySet()) {
                        if (object_Category.getTag() == tag_categ.getId()) {
                            map_categ_channel.get(type_categ).get(tag_categ).put(object_Category, new ArrayList<Stream>());
                            break;
                        }
                    }
                }
            }

            JSONArray datas = requestData(params[0]);
            int data_size = datas.length();
            for (int index = 0; index < data_size; index++) {
                Stream received_stream = new Stream(datas.getJSONObject(index));
                for (Type type_categ : map_categ_channel.keySet()) {
                    for (Tag tag_categ : map_categ_channel.get(type_categ).keySet()) {
                        for (Category category : map_categ_channel.get(type_categ).get(tag_categ).keySet()) {
                            if (received_stream.getCategory_id() == category.getId()) {
                                // List are passed by reference
                                map_categ_channel.get(type_categ).get(tag_categ).get(category).add(received_stream);
                                break;
                            }
                        }
                    }
                }
            }*/


        } catch (NullPointerException e) {
            e.printStackTrace();
        } /*catch (JSONException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //data_loader.received_datas(map_categ_channel);
    }

}
