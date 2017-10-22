package com.example.msrouji.tv_app.controller;

import android.os.AsyncTask;

import com.example.msrouji.tv_app.view.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by msrouji on 23/09/2017.
 */

public class TokenSender extends AsyncTask<String,Void,Void> {
    private DataLoadingInterface dataLoader;

    public TokenSender(DataLoadingInterface dataLoader) {
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
            URL url = new URL(strings[0] + "TV/user/subscription/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "JWT " + MainActivity.getToken());
            conn.setDoOutput(true);

            JSONObject toSendData = new JSONObject();
            toSendData.accumulate("token", strings[1]);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(toSendData.toString());
            writer.flush();

            if (!conn.getResponseMessage().equals("Accepted"))
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
