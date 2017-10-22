package com.example.msrouji.tv_app.controller;

import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;

import com.example.msrouji.tv_app.model.HeaderInfo;

import java.util.HashMap;

/**
 * Created by msrouji on 08/10/2017.
 */

public interface SimpleLoaderInterface {
    void received_datas(Object data);
    void request_data();
    void on_error(@Nullable String message_error);
}
