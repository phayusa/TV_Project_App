package com.example.msrouji.tv_app.controller;

import android.os.AsyncTask;
import android.support.v17.leanback.widget.ArrayObjectAdapter;

import com.example.msrouji.tv_app.model.HeaderInfo;
import com.example.msrouji.tv_app.view.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by msrouji on 08/10/2017.
 */

public class RequestSubscriptionTime extends AsyncTask<String, Void, String> {

    private SimpleLoaderInterface data;
    private boolean error_occured;

    public RequestSubscriptionTime(SimpleLoaderInterface data) {
        this.data = data;
        error_occured = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        data.request_data();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "JWT " + MainActivity.getToken());
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            BufferedReader buff = new BufferedReader(reader);

            //return buff.readLine();

            //if (conn.getResponseMessage().equals("OK")) {
            return new JSONObject(buff.readLine()).getString("time");
            //}
        } catch (JSONException e) {
            e.printStackTrace();
            //data.on_error(null);
            error_occured = true;
        } catch (IOException e) {
            e.printStackTrace();
            //data.on_error(null);
            error_occured = true;
        }
        return "";
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        if (!aVoid.equals("")) {
            data.received_datas(aVoid);
        } else {
            data.on_error(null);
        }
    }
}
