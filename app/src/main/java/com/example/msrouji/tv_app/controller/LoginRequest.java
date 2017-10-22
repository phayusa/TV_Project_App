package com.example.msrouji.tv_app.controller;

import android.os.AsyncTask;
import android.support.v17.leanback.widget.ArrayObjectAdapter;

import com.example.msrouji.tv_app.model.HeaderInfo;

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
 * Created by msrouji on 19/09/2017.
 */

public class LoginRequest extends AsyncTask<String, Void, Void> {

    private DataLoadingInterface dataLoader;
    private String token;

    public LoginRequest(DataLoadingInterface dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0] + "TV/login/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject toSendData = new JSONObject();
            toSendData.accumulate("id", strings[1]);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(toSendData.toString());
            writer.flush();

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            BufferedReader buff = new BufferedReader(reader);


            if (!conn.getResponseMessage().equals("Accepted"))
                dataLoader.on_error();
            else {
                JSONObject receiveJson = new JSONObject(buff.readLine());
                token = receiveJson.getString("token");
            }
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
    protected void onPreExecute() {
        super.onPreExecute();
        dataLoader.request_data();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        HashMap<HeaderInfo,ArrayObjectAdapter> data = new HashMap<>();
        data.put(new HeaderInfo(token,1),null);
        dataLoader.received_datas(data);
    }
}
