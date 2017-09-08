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
    private String category;
    private String type;
    private String tag;
    private String title_view;
    private String url_data;

    public final static String keyCateg = "category_choose";
    public final static String keyType = "type_choose";
    public final static String keyTag = "tag_choose";
    public final static String keyTitle = "title_choose";
    public final static String keyUrl = "utld kl, d,s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra(keyCateg)) {
            category = intent.getStringExtra(keyCateg);
            type = intent.getStringExtra(keyType);
            tag = intent.getStringExtra(keyTag);
        }
        title_view = intent.getStringExtra(keyTitle);
        url_data = intent.getStringExtra(keyUrl);

        setContentView(R.layout.activity_category);

    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle_view() {
        return title_view;
    }

    public String getUrl_data() {
        return url_data;
    }
}
