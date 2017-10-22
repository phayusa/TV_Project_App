package com.example.msrouji.tv_app.controller;

import android.os.AsyncTask;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.Presenter;

import com.example.msrouji.tv_app.model.HeaderInfo;
import com.example.msrouji.tv_app.model.Stream;
import com.example.msrouji.tv_app.view.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by msrouji on 01/09/2017.
 */

public class GridFactory extends AsyncTask<String, Void, Void> {
    private ArrayObjectAdapter list_objects;
    private DataLoadingInterface data_loader;
    private String extra_label;


    public GridFactory(DataLoadingInterface data_loader, Presenter object_presenter) {
        this.data_loader = data_loader;
        list_objects = new ArrayObjectAdapter(object_presenter);
    }

    public GridFactory(DataLoadingInterface data_loader, Presenter object_presenter, String extra_label) {
        this(data_loader,object_presenter);
        this.extra_label = extra_label;
    }

    private JSONArray requestData(String adress) {
        try {
            URL url = new URL(adress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("Authorisation", MainActivity.getId_device());
            conn.setRequestProperty("Authorization", "JWT " + MainActivity.getToken());

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
            String base_url = params[0];
            boolean tags_only = false;

            // Generally for filters
            if (params.length == 2){
                base_url += params[1];
            }

            JSONArray tags = requestData(base_url);
            int nb_tags = tags.length();
            for (int nth_tag = 0; nth_tag < nb_tags; nth_tag++) {
                if (params.length == 2)
                    list_objects.add(new Stream(tags.getJSONObject(nth_tag)));
                else
                    list_objects.add(new HeaderInfo(tags.getJSONObject(nth_tag), extra_label));
            }


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        HashMap<HeaderInfo, ArrayObjectAdapter> format_data = new HashMap<>();
        format_data.put(null,list_objects);

        data_loader.received_datas(format_data);
    }

}
