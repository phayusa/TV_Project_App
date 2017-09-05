package com.example.msrouji.tv_app.Controller;

import com.example.msrouji.tv_app.Model.Category;
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
    public void received_datas(HashMap<Type, HashMap<Tag, HashMap<Category, List<Stream>>>> data);
    public void request_data();
}
