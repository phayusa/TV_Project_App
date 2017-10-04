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

package com.example.msrouji.tv_app.View;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;

import com.example.msrouji.tv_app.Controller.DataLoadingInterface;
import com.example.msrouji.tv_app.Controller.LoginRequest;
import com.example.msrouji.tv_app.Controller.RegisterDevice;
import com.example.msrouji.tv_app.Model.HeaderInfo;
import com.example.msrouji.tv_app.R;

import java.util.HashMap;
import java.util.UUID;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity implements DataLoadingInterface{
    /**
     * Called when the activity is first created.
     */
    public final static String key_extra_header_url = "RNZLsq,klq";
    public final static String key_extra_data_url = "djkdsnjsd,nlsd,lq,klq";
    public final static String key_extra_title = "sjsi,qd,spi,sozo";
    public final static String key_extra_columns = "sjsi,qd,s columns";
    public final static String key_extra_label = "sdipdspkdsoskop";

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

        }else {

            SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE);
            // First time launch application
            if (preferences.getString(getString(R.string.pref_name_id), null) == null) {
                String id = UUID.randomUUID().toString();

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

    private class LoginSend implements DataLoadingInterface{
        private Activity instance;

        public LoginSend(Activity instance) {
            this.instance = instance;
        }

        @Override
        public void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter> data) {
            System.err.println(token);
            token = ((HeaderInfo) data.keySet().toArray()[0]).getName();
            instance.setContentView(R.layout.activity_main);
        }

        @Override
        public void request_data() {

        }

        @Override
        public void on_error() {

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
