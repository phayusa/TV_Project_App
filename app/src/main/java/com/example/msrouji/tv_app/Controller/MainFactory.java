package com.example.msrouji.tv_app.Controller;

import android.os.AsyncTask;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.Presenter;

import com.example.msrouji.tv_app.Model.HeaderInfo;
import com.example.msrouji.tv_app.Model.Stream;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by msrouji on 07/09/2017.
 */

public class MainFactory extends AsyncTask<String, Void, Void> {
    private DataLoadingInterface data_loader;
    private Presenter presenterObject;

    private HashMap<HeaderInfo, ArrayObjectAdapter> data;
    private boolean error_occured;
    private int nb_columns;

    public MainFactory(DataLoadingInterface data_loader, Presenter presenterObject) {
        this.data_loader = data_loader;
        this.presenterObject = presenterObject;
        data = new HashMap<>();
        error_occured = false;

    }

    public MainFactory(DataLoadingInterface data_loader, Presenter presenterObject, int nb_columns) {
        this(data_loader,presenterObject);
        this.nb_columns = nb_columns;
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
            data_loader.on_error();
            error_occured = true;
        } catch (IOException e) {
            e.printStackTrace();
            data_loader.on_error();
            error_occured = true;
        }
        return null;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {

            String base_url = strings[0] + "TV/";
            JSONArray headers = requestData(base_url + strings[1]);
            int nb_headers = headers.length();
            for (int nth_head = 0; nth_head < nb_headers; nth_head++) {
                data.put(new HeaderInfo(headers.getJSONObject(nth_head)), new ArrayObjectAdapter(presenterObject));
            }

            for (HeaderInfo info : data.keySet()) {
                String url;
                if (strings.length == 4)
                    url = base_url + strings[2] + info.getId();
                else
                    url = base_url + strings[2] + info.getId();
                JSONArray columns_of_row = requestData(url);
                if (nb_columns != 0) {
                    int nb_columns_data = columns_of_row.length();
                    int limit_data = nb_columns_data < nb_columns ? nb_columns_data : nb_columns;
                    for (int column = 0; column < limit_data; column++) {
                        data.get(info).add(new Stream(columns_of_row.getJSONObject(column)));
                    }
                    if (limit_data == nb_columns)
                        data.get(info).add("More");
                }else{
                    int nb_columns_data = columns_of_row.length();
                    for (int column = 0; column < nb_columns_data; column++) {
                        data.get(info).add(new Stream(columns_of_row.getJSONObject(column)));
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        data_loader.request_data();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!error_occured)
            data_loader.received_datas(data);
    }
}
