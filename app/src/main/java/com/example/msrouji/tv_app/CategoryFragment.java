package com.example.msrouji.tv_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.telecom.Connection;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.msrouji.tv_app.Controller.DataLoadingInterface;
import com.example.msrouji.tv_app.Controller.StreamFactory;
import com.example.msrouji.tv_app.Model.Category;
import com.example.msrouji.tv_app.Model.Stream;
import com.example.msrouji.tv_app.Model.Tag;
import com.example.msrouji.tv_app.Model.Type;
import com.example.msrouji.tv_app.View.BrowseErrorActivity;
import com.example.msrouji.tv_app.View.CardPresenter;
import com.example.msrouji.tv_app.View.DetailsActivity;
import com.example.msrouji.tv_app.View.MainFragment;
import com.example.msrouji.tv_app.View.SpinnerFragment;

import org.json.JSONException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by msrouji on 04/09/2017.
 */

public class CategoryFragment extends android.support.v17.leanback.app.VerticalGridFragment implements OnItemViewSelectedListener, OnItemViewClickedListener, DataLoadingInterface {
    private static final String TAG = VerticalGridFragment.class.getSimpleName();
    private static final int NUM_COLUMNS = 4;

    private List<Stream> streams;
    private ArrayObjectAdapter mAdapter;

    private SpinnerFragment spinnerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(NUM_COLUMNS);
        setGridPresenter(gridPresenter);


        String ip = "10.53.8.123:8000";
        new StreamFactory(this).execute("http://" + ip + "/TV/channels/", "http://" + ip + "/TV/categories/", "http://" + ip + "/TV/types/");


    }


    private void setupFragment() {

        mAdapter = new ArrayObjectAdapter(new CardPresenter());


        for (Stream item : streams) {
            mAdapter.add(item);
        }
        setAdapter(mAdapter);
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

    }

    @Override
    public void received_datas(HashMap<Type, HashMap<Tag, HashMap<Category, List<Stream>>>> data) {
        getFragmentManager().beginTransaction().remove(spinnerFragment).commit();

        CategoryActivity activity = ((CategoryActivity) getActivity());

        setTitle(activity.getCategory());

        for (Type type : data.keySet()) {
            if (type.getName().equals(activity.getType()))
                for (Tag tag : data.get(type).keySet())
                    if (tag.getName().equals(activity.getTag()))
                        for (Category category : data.get(tag).get(type).keySet())
                            if (category.getName().equals(activity.getCategory()))
                                streams = data.get(tag).get(type).get(category);
        }

        setupFragment();
    }

    @Override
    public void request_data() {
        spinnerFragment = new SpinnerFragment();
        getFragmentManager().beginTransaction().add(R.id.vertical_grid_fragment, spinnerFragment).commit();

    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                        .show();
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {


        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {

        }
    }

}
