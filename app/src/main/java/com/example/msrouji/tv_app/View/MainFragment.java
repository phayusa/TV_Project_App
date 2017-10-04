/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.msrouji.tv_app.View;

import java.net.URI;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.msrouji.tv_app.Controller.MainFactory;
import com.example.msrouji.tv_app.Controller.DataLoadingInterface;
import com.example.msrouji.tv_app.Model.HeaderInfo;
import com.example.msrouji.tv_app.Model.Stream;
import com.example.msrouji.tv_app.Model.Tag;
import com.example.msrouji.tv_app.Model.Type;
import com.example.msrouji.tv_app.R;
import com.example.msrouji.tv_app.View.presenter.CardPresenter;
import com.example.msrouji.tv_app.View.presenter.GridItemPresenter;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 0;
    private static final int GRID_ITEM_WIDTH = 500;
    private static final int GRID_ITEM_HEIGHT = 500;
    private static final int NUM_COLS = 15;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private URI mBackgroundURI;
    private BackgroundManager mBackgroundManager;
    private SpinnerFragment spinnerFragment;

    private boolean error_occured;

    // Data getting by the request
    private Type last_type_choosed;
    private Tag last_tag_choosed;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        if (getActivity().getClass().getSimpleName().equals("MainActivity")) {
            MainActivity activity = ((MainActivity) getActivity());
            if (activity.getHeader_url() == null)
                loadDefaultRows();
            else {
                setTitle(activity.getTitle_view());
                if (activity.getTitle_view().equals("Series"))
                    new MainFactory(new Data_receiver(), new CardPresenter(), activity.getNb_columns())
                            .execute(getString(R.string.ip), activity.getHeader_url(), activity.getData_url());
                else
                    new MainFactory(new Data_receiver(), new GridItemPresenter(300, 300), activity.getNb_columns())
                            .execute(getString(R.string.ip), activity.getHeader_url(), activity.getData_url());
            }
        }


        //String ip = "10.53.8.149:8000";
        //new StreamFactory(this).execute("http://" + ip + "/TV/channels/", "http://" + ip + "/TV/categories/", "http://" + ip + "/TV/types/", "http://" + ip + "/TV/tags/");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }


    private void loadDefaultRows() {

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        HeaderItem gridHeaderCateg = new HeaderItem(0, "TYPE");
        GridItemPresenter mGridPresenterCateg = new GridItemPresenter(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT);
        ArrayObjectAdapter gridRowAdapterCateg = new ArrayObjectAdapter(mGridPresenterCateg);
        gridRowAdapterCateg.add("Channels");
        gridRowAdapterCateg.add("Movies");
        gridRowAdapterCateg.add("Series");
        mRowsAdapter.add(new ListRow(gridHeaderCateg, gridRowAdapterCateg));


        HeaderItem gridHeader = new HeaderItem(1, "Subscription");

        GridItemPresenter mGridPresenter = new GridItemPresenter(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT);
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.grid_view));
        gridRowAdapter.add(getString(R.string.error_fragment));
        gridRowAdapter.add(getResources().getString(R.string.personal_settings));
        mRowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        setAdapter(mRowsAdapter);

        setupEventListeners();
    }

    private void loadRowFromWeb(HashMap<HeaderInfo, ArrayObjectAdapter> data) {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        //int i = 0;


        for (HeaderInfo head_info : data.keySet()) {
            HeaderItem head_label = new HeaderItem(head_info.getId(), head_info.getName());
            mRowsAdapter.add(new ListRow(head_label, data.get(head_info)));
        }

        setAdapter(mRowsAdapter);

        setupEventListeners();
    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), getString(R.string.search_teaser), Toast.LENGTH_LONG)
                        .show();
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    protected void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Stream) {
                Stream movie = (Stream) item;
                Log.d(TAG, "Item: " + item.toString());

                if (movie.getUrl() == null) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(DetailsActivity.STREAM, movie);
                    intent.putExtra(DetailsActivity.key_url, "series/seasons/?serie=" + movie.getId());

                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            ((ImageCardView) itemViewHolder.view).getMainImageView(),
                            DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                    getActivity().startActivity(intent, bundle);
                } else {
                    Intent intent = new Intent(getActivity(), VideoActivity.class);
                    intent.putExtra(DetailsActivity.STREAM, movie);
                    getActivity().startActivity(intent);
                    //Toast.makeText(getActivity().getApplicationContext(), ((Stream) item).getUrl(), Toast.LENGTH_LONG);
                }
            } else if (item instanceof String) {
                switch (row.getHeaderItem().getName()) {
                    case "Subscription":
                        if (((String) item).indexOf(getString(R.string.error_fragment)) >= 0) {
                            Intent intent = new Intent(getActivity(), Paid_Activity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                                    .show();
                        }
                        break;
                    case "TYPE":
                        switch (((String) item)) {
                            case "Movies":
                                Intent intent2 = new Intent(getActivity(), MainActivity.class);
                                intent2.putExtra(MainActivity.key_extra_header_url, "movies/categories/");
                                intent2.putExtra(MainActivity.key_extra_data_url, "movies/?category=");
                                intent2.putExtra(MainActivity.key_extra_title, "Movies");
                                startActivity(intent2);
                                break;
                            case "Channels":
                                Intent intent_chan = new Intent(getActivity(), GridActivity.class);
                                intent_chan.putExtra(GridActivity.keyTitle, "Tags");
                                intent_chan.putExtra(GridActivity.keyUrl, "channels/tags/");
                                startActivity(intent_chan);
                                break;
                            case "Series":
                                Intent intent_ser = new Intent(getActivity(), MainActivity.class);
                                intent_ser.putExtra(MainActivity.key_extra_header_url, "series/categories/");
                                intent_ser.putExtra(MainActivity.key_extra_data_url, "series/?category=");
                                intent_ser.putExtra(MainActivity.key_extra_title, "Series");
                                intent_ser.putExtra(MainActivity.key_extra_label, "Season");
                                startActivity(intent_ser);
                                break;
                        }

                        //setupEventListeners();
                        break;

                    default:

                        Intent intent_chan = new Intent(getActivity(), GridActivity.class);
                        intent_chan.putExtra(GridActivity.keyTitle, row.getHeaderItem().getName());
                        if (getTitle().equals("Movies")) {
                            intent_chan.putExtra(GridActivity.keyUrl, "movies/?category=" + row.getHeaderItem().getId());
                            intent_chan.putExtra(GridActivity.keyType, true);
                            intent_chan.putExtra(GridActivity.keyImage, false);
                            //intent_chan.putExtra(GridActivity.keyType, true);
                        } else if (getTitle().equals("Series")) {
                            intent_chan.putExtra(GridActivity.keyUrl, "series/?category=" + row.getHeaderItem().getId());
                            intent_chan.putExtra(GridActivity.keyType, true);
                            intent_chan.putExtra(GridActivity.keyImage, true);
                        } else {
                            intent_chan.putExtra(GridActivity.keyUrl, "channels/?category=" + row.getHeaderItem().getId());
                            intent_chan.putExtra(GridActivity.keyType, true);
                            intent_chan.putExtra(GridActivity.keyImage, false);
                        }
                        startActivity(intent_chan);


                }
            }
        }
    }


    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            /*if (item instanceof Stream) {
                try {
                    Stream stream = ((Stream) item);
                    if (stream.getImage_url() != null) {
                        mBackgroundURI = new URI(stream.getImage_url());
                        startBackgroundTimer();

                    }
                }catch (java.net.URISyntaxException e){
                    e.printStackTrace();
                }

            }*/

        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI.toString());
                    }
                }
            });

        }
    }


    private final class Data_receiver implements DataLoadingInterface {
        @Override
        public void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter> data) {
            getFragmentManager().beginTransaction().remove(spinnerFragment).commit();

            loadRowFromWeb(data);
            setupEventListeners();
        }


        @Override
        public void request_data() {
            spinnerFragment = new SpinnerFragment();
            getFragmentManager().beginTransaction().add(R.id.main_browse_fragment, spinnerFragment).commit();
        }

        @Override
        public void on_error() {
            getActivity().finish();
            //error_occured = true;
            Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
            startActivity(intent);

        }
    }

}
