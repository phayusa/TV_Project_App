package com.example.msrouji.tv_app.Controller;

import android.os.AsyncTask;
import android.support.v17.leanback.widget.ArrayObjectAdapter;

import com.example.msrouji.tv_app.Model.HeaderInfo;
import com.example.msrouji.tv_app.View.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by msrouji on 17/09/2017.
 */

public class RegisterDevice extends AsyncTask<String, Void, Void> {
    private DataLoadingInterface dataLoader;
    private String token;

    public RegisterDevice(DataLoadingInterface dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dataLoader.request_data();
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0] + "TV/create/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject toSendData = new JSONObject();
            toSendData.accumulate("id", strings[1]);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(toSendData.toString());
            writer.flush();

            if (!conn.getResponseMessage().equals("Created"))
                dataLoader.on_error();
        } catch (JSONException e) {
            e.printStackTrace();
            dataLoader.on_error();
        } catch (IOException e) {
            e.printStackTrace();
            dataLoader.on_error();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dataLoader.received_datas(null);
    }
}
