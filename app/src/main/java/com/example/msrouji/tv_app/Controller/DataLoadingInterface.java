package com.example.msrouji.tv_app.Controller;

import android.support.v17.leanback.widget.ArrayObjectAdapter;

import com.example.msrouji.tv_app.Model.Category;
import com.example.msrouji.tv_app.Model.HeaderInfo;
import com.example.msrouji.tv_app.Model.Stream;
import com.example.msrouji.tv_app.Model.Tag;
import com.example.msrouji.tv_app.Model.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by msrouji on 01/09/2017.
 */

public interface DataLoadingInterface {
    void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter>  data);
    void request_data();
    void on_error();
}
