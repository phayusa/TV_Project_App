package com.example.msrouji.tv_app.View;

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

import com.example.msrouji.tv_app.Controller.DataLoadingInterface;
import com.example.msrouji.tv_app.Controller.GridFactory;
import com.example.msrouji.tv_app.Model.HeaderInfo;
import com.example.msrouji.tv_app.Model.Stream;
import com.example.msrouji.tv_app.R;
import com.example.msrouji.tv_app.View.presenter.CardPresenter;

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

        //new GridFactory(new Data_listener(), new GridItemPresenter(400,400)).execute("http://10.53.8.144:8000/TV/channels/tags/");
        //new GridFactory(new Data_listener(), new GridItemPresenter(400,400)).execute("http://10.53.8.144:8000/TV/"+ ((GridActivity) getActivity()).getUrl_data());
        new GridFactory(new Data_listener(), new CardPresenter()).execute("http://10.53.8.144:8000/TV/"+ ((GridActivity) getActivity()).getUrl_data(),"");


        //String ip = "10.53.8.123:8000";
        //new StreamFactory(this).execute("http://" + ip + "/TV/channels/", "http://" + ip + "/TV/categories/", "http://" + ip + "/TV/types/");


    }


   /* private void setupFragment() {

        mAdapter = new ArrayObjectAdapter(new CardPresenter());


        for (Stream item : streams) {
            mAdapter.add(item);
        }
        setAdapter(mAdapter);
    }*/

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
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
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
            if (item instanceof HeaderInfo){
                HeaderInfo header = ((HeaderInfo) item);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(MainActivity.key_extra_title, header.getName());
                intent.putExtra(MainActivity.key_extra_header_url, "channels/categories/?tag="+ header.getId());
                intent.putExtra(MainActivity.key_extra_data_url, "channels/?category=");
                intent.putExtra(MainActivity.key_extra_columns, 5);

                getActivity().startActivity(intent);
            }
        }

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {

        }
    }


}
