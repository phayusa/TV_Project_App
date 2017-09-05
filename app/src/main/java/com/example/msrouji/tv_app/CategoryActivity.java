package com.example.msrouji.tv_app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.msrouji.tv_app.Model.Category;
import com.example.msrouji.tv_app.Model.Stream;
import com.example.msrouji.tv_app.Model.Type;

public class CategoryActivity extends Activity {
    private String category;
    private String type;
    private String tag;

    public final static String keyCateg = "category_choose";
    public final static String keyType = "type_choose";
    public final static String keyTag = "tag_choose";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        category = intent.getStringExtra(keyCateg);
        type = intent.getStringExtra(keyType);
        tag = intent.getStringExtra(keyTag);

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
}
