package com.example.msrouji.tv_app.View;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.msrouji.tv_app.Model.Category;
import com.example.msrouji.tv_app.Model.Stream;
import com.example.msrouji.tv_app.Model.Type;
import com.example.msrouji.tv_app.R;

public class GridActivity extends Activity {
    private String title_view;
    private String url_data;
    private boolean is_stream_view;
    private boolean has_image;
    private String extra_label;

    public final static String keyTitle = "title_choose";
    public final static String keyUrl = "utld kl, d,s";
    public final static String keyType = "tyê jsisj ";
    public final static String keyLabel = "dspsdopdkdksopskopk";
    public final static String keyImage = "ssospsp, osps,opp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        title_view = intent.getStringExtra(keyTitle);
        url_data = intent.getStringExtra(keyUrl);
        is_stream_view = intent.getBooleanExtra(keyType, false);
        if (intent.hasExtra(keyLabel))
            extra_label = intent.getStringExtra(keyLabel);
        if (intent.hasExtra(keyImage))
            has_image = intent.getBooleanExtra(keyImage, false);

        setContentView(R.layout.activity_category);

    }


    public String getTitle_view() {
        return title_view;
    }

    public String getUrl_data() {
        return url_data;
    }

    public boolean is_stream_view() {
        return is_stream_view;
    }

    public String getExtra_label() {
        return extra_label;
    }

    public boolean isHas_image() {
        return has_image;
    }
}
