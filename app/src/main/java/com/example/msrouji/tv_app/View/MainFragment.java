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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Color;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.msrouji.tv_app.CategoryActivity;
import com.example.msrouji.tv_app.Controller.StreamFactory;
import com.example.msrouji.tv_app.Controller.DataLoadingInterface;
import com.example.msrouji.tv_app.Model.Category;
import com.example.msrouji.tv_app.Model.Stream;
import com.example.msrouji.tv_app.Model.Tag;
import com.example.msrouji.tv_app.Model.Type;
import com.example.msrouji.tv_app.Movie;
import com.example.msrouji.tv_app.R;

public class MainFragment extends BrowseFragment implements DataLoadingInterface {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final int NUM_COLS = 15;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private URI mBackgroundURI;
    private BackgroundManager mBackgroundManager;
    private SpinnerFragment spinnerFragment;

    // Data getting by the request
    private HashMap<Type, HashMap<Tag, HashMap<Category, List<Stream>>>> loaded_data;
    private Type last_type_choosed;
    private Tag last_tag_choosed;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        String ip = "10.53.8.178:8000";
        new StreamFactory(this).execute("http://" + ip + "/TV/channels/", "http://" + ip + "/TV/categories/", "http://" + ip + "/TV/types/", "http://" + ip + "/TV/tags/");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }


    private void loadRows(String choose_type, String choose_tag) {
        //last_type_choosed = choose_type;

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();
        int i = 0;

        HeaderItem gridHeaderCateg = new HeaderItem(i++, "TYPE");
        GridItemPresenter mGridPresenterCateg = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapterCateg = new ArrayObjectAdapter(mGridPresenterCateg);
        for (Type type_stream : loaded_data.keySet())
            gridRowAdapterCateg.add(type_stream.getName());
        mRowsAdapter.add(new ListRow(gridHeaderCateg, gridRowAdapterCateg));

        if (!choose_type.equals("")) {
            HeaderItem gridHeaderTag = new HeaderItem(i++, "TAG");
            GridItemPresenter mGridPresenterTag = new GridItemPresenter();
            ArrayObjectAdapter gridRowAdapterTag = new ArrayObjectAdapter(mGridPresenterTag);
            for (Type type_stream : loaded_data.keySet())
                if (type_stream.getName().equals(choose_type))
                    for (Tag tag : loaded_data.get(type_stream).keySet()) {
                        if (tag.getType() == type_stream.getId())
                            gridRowAdapterTag.add(tag.getName());
                    }
            mRowsAdapter.add(new ListRow(gridHeaderTag, gridRowAdapterTag));

        }


        for (Type type_stream : loaded_data.keySet())
            if (choose_type.equals(type_stream.getName())) {
                last_type_choosed = type_stream;
                for (Tag tag : loaded_data.get(type_stream).keySet())
                    if (tag.getName().equals(choose_tag)) {
                        last_tag_choosed = tag;
                        System.err.println(tag);
                        System.err.println(last_tag_choosed);
                        for (Category category : loaded_data.get(type_stream).get(tag).keySet()) {
                            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                            int size_stream_category = loaded_data.get(type_stream).get(tag).get(category).size();
                            boolean max_channel = false;
                            for (int j = 0; j < NUM_COLS; j++) {
                                //listRowAdapter.add(list.get(j % 5));
                                if (j >= size_stream_category) {
                                    max_channel = true;
                                    break;
                                }
                                listRowAdapter.add(loaded_data.get(type_stream).get(tag).get(category).get(j));
                            }
                            listRowAdapter.add("More");
                            HeaderItem header = new HeaderItem(i++, category.getName());
                            mRowsAdapter.add(new ListRow(header, listRowAdapter));
                        }
                    }
            }

        HeaderItem gridHeader = new HeaderItem(i++, "PREFERENCES");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.grid_view));
        gridRowAdapter.add(getString(R.string.error_fragment));
        gridRowAdapter.add(getResources().getString(R.string.personal_settings));
        mRowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        setAdapter(mRowsAdapter);

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
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
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
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            } else if (item instanceof String) {
                switch (row.getHeaderItem().getName()) {
                    case "PREFERENCES":
                        if (((String) item).indexOf(getString(R.string.error_fragment)) >= 0) {
                            Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                                    .show();
                        }
                        break;
                    case "TYPE":
                        loadRows(((String) item), "");
                        setTitle(((String) item).toUpperCase());
                        setupEventListeners();
                        break;

                    case "TAG":
                        loadRows(last_type_choosed.getName(), ((String) item));
                        setTitle(last_type_choosed.getName().toUpperCase() + " (" + ((String) item).toUpperCase() + ")");
                        setupEventListeners();
                        break;


                    default:
                        Intent intent = new Intent(getActivity(), CategoryActivity.class);
                        intent.putExtra(CategoryActivity.keyType, last_type_choosed.getName());
                        intent.putExtra(CategoryActivity.keyTag, last_tag_choosed.getName());
                        intent.putExtra(CategoryActivity.keyCateg, row.getHeaderItem().getName());

                        startActivity(intent);
                        //loadRows(last_type_choosed);
                        //setupEventListeners();


                }
            }
        }
    }

    //TODO : changed with the API
    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                mBackgroundURI = ((Movie) item).getBackgroundImageURI();
                startBackgroundTimer();
            }

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

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }


    @Override
    public void received_datas(HashMap<Type, HashMap<Tag, HashMap<Category, List<Stream>>>> data) {
        getFragmentManager().beginTransaction().remove(spinnerFragment).commit();
        loaded_data = new HashMap<Type, HashMap<Tag, HashMap<Category, List<Stream>>>>(data);

        loadRows("", "");

        setupEventListeners();
    }


    @Override
    public void request_data() {
        spinnerFragment = new SpinnerFragment();
        getFragmentManager().beginTransaction().add(R.id.main_browse_fragment, spinnerFragment).commit();
    }
}
