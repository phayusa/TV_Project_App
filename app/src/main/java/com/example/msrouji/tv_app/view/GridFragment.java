package com.example.msrouji.tv_app.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.view.View;
import android.widget.Toast;

import com.example.msrouji.tv_app.controller.DataLoadingInterface;
import com.example.msrouji.tv_app.controller.GridFactory;
import com.example.msrouji.tv_app.model.HeaderInfo;
import com.example.msrouji.tv_app.model.Stream;
import com.example.msrouji.tv_app.R;
import com.example.msrouji.tv_app.view.presenter.CardPresenter;
import com.example.msrouji.tv_app.view.presenter.GridItemPresenter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by msrouji on 04/09/2017.
 */

public class GridFragment extends android.support.v17.leanback.app.VerticalGridFragment {
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

        GridActivity activity = ((GridActivity) getActivity());

        if (activity.is_stream_view())
            if (activity.isHas_image())
                new GridFactory(new Data_listener(), new CardPresenter()).execute(getString(R.string.ip) + "TV/" + activity.getUrl_data(), "");
            else
                new GridFactory(new Data_listener(), new GridItemPresenter(400, 400)).execute(getString(R.string.ip) + "TV/" + activity.getUrl_data(), "");
        else if (((GridActivity) getActivity()).getExtra_label() != null)
            new GridFactory(new Data_listener(), new GridItemPresenter(400, 400), activity.getExtra_label()).execute(getString(R.string.ip) + "TV/" + activity.getUrl_data());
        else
            new GridFactory(new Data_listener(), new GridItemPresenter(400, 400)).execute(getString(R.string.ip) + "TV/" + activity.getUrl_data());


        //String ip = "10.53.8.123:8000";
        //new StreamFactory(this).execute("http://" + ip + "/TV/channels/", "http://" + ip + "/TV/categories/", "http://" + ip + "/TV/types/");


    }


    private class Data_listener implements DataLoadingInterface {
        @Override
        public void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter> data) {
            //getFragmentManager().beginTransaction().remove(spinnerFragment).commit();

            setTitle(((GridActivity) getActivity()).getTitle_view());
            System.err.println(data.get(null).size());
            mAdapter = data.get(null);
            setAdapter(mAdapter);

            setupEventListeners();
        }


        @Override
        public void request_data() {
            spinnerFragment = new SpinnerFragment();
            getFragmentManager().beginTransaction().add(R.id.vertical_grid_fragment, spinnerFragment).commit();

        }

        @Override
        public void on_error() {

        }

    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), getString(R.string.search_teaser), Toast.LENGTH_LONG)
                        .show();
            }
        });

        setOnItemViewClickedListener(new ItemListener());
        setOnItemViewSelectedListener(new ItemListener());
    }

    private class ItemListener implements OnItemViewClickedListener, OnItemViewSelectedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof HeaderInfo) {
                HeaderInfo header = ((HeaderInfo) item);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(MainActivity.key_extra_title, header.getName());
                intent.putExtra(MainActivity.key_extra_header_url, "channels/categories/?tag=" + header.getId());
                intent.putExtra(MainActivity.key_extra_data_url, "channels/?category=");
                intent.putExtra(MainActivity.key_extra_columns, 5);

                getActivity().startActivity(intent);
            }else{
                Stream movie = ((Stream) item);
                if(movie.getUrl().endsWith(".avi")) {
                    Intent intent = new Intent(getActivity(), VideoAviAct.class);
                    intent.putExtra(VideoAviAct.key_url, movie.getUrl());
                    getActivity().startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), VideoActivity.class);
                    intent.putExtra(DetailsActivity.STREAM, movie);
                    getActivity().startActivity(intent);
                }
                System.err.println(item);
            }
        }

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {

        }
    }


}
