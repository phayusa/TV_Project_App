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
import android.os.Bundle;

import com.example.msrouji.tv_app.R;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    public final static String key_extra_header_url = "RNZLsq,klq";
    public final static String key_extra_data_url = "djkdsnjsd,nlsd,lq,klq";
    public final static String key_extra_title = "sjsi,qd,spi,sozo";
    public final static String key_extra_columns = "sjsi,qd,s columns";

    private String header_url;
    private String data_url;
    private String title_view;
    private int nb_columns;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent old_intent = getIntent();
        if (old_intent.hasExtra(key_extra_header_url)) {
            header_url = old_intent.getStringExtra(key_extra_header_url);
            data_url = old_intent.getStringExtra(key_extra_data_url);

            if (old_intent.hasExtra(key_extra_title))
                title_view = old_intent.getStringExtra(key_extra_title);

            nb_columns = old_intent.getIntExtra(key_extra_columns, 5);
        }

        setContentView(R.layout.activity_main);
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
}
