/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.msrouji.tv_app.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;

import com.example.msrouji.tv_app.Utils;
import com.example.msrouji.tv_app.controller.DataLoadingInterface;
import com.example.msrouji.tv_app.controller.LoginRequest;
import com.example.msrouji.tv_app.controller.RegisterDevice;
import com.example.msrouji.tv_app.controller.SimpleLoaderInterface;
import com.example.msrouji.tv_app.controller.TokenRefresh;
import com.example.msrouji.tv_app.model.HeaderInfo;
import com.example.msrouji.tv_app.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity implements DataLoadingInterface {
    /**
     * Called when the activity is first created.
     */
    public final static String key_extra_header_url = "RNZLsq,klq";
    public final static String key_extra_data_url = "djkdsnjsd,nlsd,lq,klq";
    public final static String key_extra_title = "sjsi,qd,spi,sozo";
    public final static String key_extra_columns = "sjsi,qd,s columns";
    public final static String key_extra_label = "sdipdspkdsoskop";

    public static final int Request_Main = 46778289;


    private String header_url;
    private String data_url;
    private String title_view;
    private int nb_columns;
    private String extra_label;

    private static String id_device;
    private static String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent old_intent = getIntent();
        if (old_intent.hasExtra(key_extra_header_url)) {
            header_url = old_intent.getStringExtra(key_extra_header_url);
            data_url = old_intent.getStringExtra(key_extra_data_url);

            if (old_intent.hasExtra(key_extra_title))
                title_view = old_intent.getStringExtra(key_extra_title);

            if (old_intent.hasExtra(key_extra_label))
                extra_label = old_intent.getStringExtra(key_extra_label);

            nb_columns = old_intent.getIntExtra(key_extra_columns, 5);

            setContentView(R.layout.activity_main);

        } else {

            SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE);
            // First time launch application
            if (preferences.getString(getString(R.string.pref_name_id), null) == null) {
                //String id = UUID.randomUUID().toString();
                String id = Utils.getMACAddress("eth0");
                if (id.equals(""))
                    id = Utils.getMACAddress("wlan0");
                System.err.println(id);
                /*byte[] bytesOfMessage = id.getBytes("UTF-8");

                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hash_id = md.digest(bytesOfMessage);*/

                preferences.edit().putString(getString(R.string.pref_name_id), id).apply();
                id_device = id;

                new RegisterDevice(this).execute(getString(R.string.ip), id_device);

            } else {
                id_device = preferences.getString(getString(R.string.pref_name_id), null);
                //startActivity(new Intent(this, MainActivity.class));
                new LoginRequest(new LoginSend(this)).execute(getString(R.string.ip), id_device);

                //setContentView(R.layout.activity_main);

            }
            System.err.println(id_device);
        }

    }

    public String getHeader_url() {
        return header_url;
    }

    public String getData_url() {
        return data_url;
    }


    public String getTitle_view() {
        return title_view;
    }

    public int getNb_columns() {
        return nb_columns;
    }

    public String getExtra_label() {
        return extra_label;
    }

    public static String getId_device() {
        return id_device;
    }

    public static String getToken() {
        return token;
    }

    private class LoginSend implements DataLoadingInterface {
        private Activity instance;

        public LoginSend(Activity instance) {
            this.instance = instance;
        }

        @Override
        public void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter> data) {
            //System.err.println(token);
            token = ((HeaderInfo) data.keySet().toArray()[0]).getName();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(this, 30000); // every 5 minutes
                    new TokenRefresh(new ReceiveToken()).execute(getString(R.string.ip),token);

                }
            }, 30000);
            instance.setContentView(R.layout.activity_main);
        }

        @Override
        public void request_data() {

        }

        @Override
        public void on_error() {

        }
    }
    
    private class ReceiveToken implements SimpleLoaderInterface{
        @Override
        public void received_datas(Object data) {
             token = ((String) data);
        }

        @Override
        public void request_data() {

        }

        @Override
        public void on_error(@Nullable String message_error) {
            finish();
        }
    }

    @Override
    public void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter> data) {
        //setContentView(R.layout.activity_main);
        new LoginRequest(new LoginSend(this)).execute(getString(R.string.ip), id_device);

    }

    @Override
    public void request_data() {

    }

    @Override
    public void on_error() {
        getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE).edit().putString(getString(R.string.pref_name_id), null).apply();
    }
}
