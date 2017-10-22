package com.example.msrouji.tv_app.controller;

import android.support.v17.leanback.widget.ArrayObjectAdapter;

import com.example.msrouji.tv_app.model.HeaderInfo;

import java.util.HashMap;

/**
 * Created by msrouji on 01/09/2017.
 */

public interface DataLoadingInterface {
    void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter>  data);
    void request_data();
    void on_error();
}
