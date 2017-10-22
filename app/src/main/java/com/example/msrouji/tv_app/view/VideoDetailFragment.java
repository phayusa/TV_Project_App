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

package com.example.msrouji.tv_app.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.msrouji.tv_app.controller.DataLoadingInterface;
import com.example.msrouji.tv_app.controller.GridFactory;
import com.example.msrouji.tv_app.model.HeaderInfo;
import com.example.msrouji.tv_app.model.Stream;
import com.example.msrouji.tv_app.R;
import com.example.msrouji.tv_app.Utils;
import com.example.msrouji.tv_app.view.presenter.GridItemPresenter;

import java.io.Serializable;
import java.util.HashMap;

/*
 * LeanbackDetailsFragment extends VideoDetailFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class VideoDetailFragment extends android.support.v17.leanback.app.DetailsFragment {
    private static final String TAG = "VideoDetailFragment";

    private static final int ACTION_WATCH_TRAILER = 1;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 400;

    private static final int NUM_COLS = 10;

    private Stream mSelectedStream;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreate VideoDetailFragment");
        super.onCreate(savedInstanceState);

        prepareBackgroundManager();

        mSelectedStream = (Stream) getActivity().getIntent()
                .getSerializableExtra(DetailsActivity.STREAM);
        if (mSelectedStream != null) {
            new GridFactory(new DataListener(), new GridItemPresenter(200,100), "Season").execute(getString(R.string.ip)+"TV/"+ ((DetailsActivity) getActivity()).getUrl_data());

            setupAdapter();
            setupDetailsOverviewRowPresenter();

            //updateBackground(mSelectedStream.getImage_url());
            setOnItemViewClickedListener(new ItemViewClickedListener());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    protected void updateBackground(String uri) {
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(mMetrics.widthPixels, mMetrics.heightPixels) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
    }

    private void setupAdapter() {
        mPresenterSelector = new ClassPresenterSelector();
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    private void setupDetailsOverviewRow(ArrayObjectAdapter list_seasons) {
        //Log.d(TAG, "doInBackground: " + mSelectedStream.toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedStream);
        row.setImageDrawable(getActivity().getDrawable(R.drawable.default_background));


        int width = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_HEIGHT);
        Glide.with(getActivity())
                .load(mSelectedStream.getImage_url())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        Log.d(TAG, "details overview card image url ready: " + resource);
                        row.setImageDrawable(resource);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });

        int size_data = list_seasons.size();
        System.err.println(size_data);
        for (int nth_data = 0; nth_data < size_data; nth_data++) {
            HeaderInfo info = ((HeaderInfo) list_seasons.get(nth_data));
            row.addAction(new Action (info.getId(), info.getName()));
        }
        //row.setActionsAdapter(list_seasons);
        //row.setActionsAdapter(list_seasons);

        mAdapter.add(row);
    }


    private void setupDetailsOverviewRowPresenter() {
        // Set detail background and style.
        DetailsOverviewRowPresenter detailsPresenter =
                new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background));
        detailsPresenter.setStyleLarge(true);

        // Hook up transition element.
        detailsPresenter.setSharedElementEnterTransition(getActivity(),
                DetailsActivity.SHARED_ELEMENT_NAME);


        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {

                Intent intent = new Intent(getActivity(), GridActivity.class);
                intent.putExtra(GridActivity.keyUrl, "series/episodes/?season="+action.getId());
                intent.putExtra(GridActivity.keyType, true);
                intent.putExtra(GridActivity.keyImage, false);
                intent.putExtra(GridActivity.keyTitle, action.getLabel1());
                intent.putExtra(GridActivity.keyLabel,"Episode");


                //intent.putExtra(DetailsActivity.STREAM, (Serializable) mSelectedStream);
                startActivity(intent);
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }


    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Stream) {
                Stream movie = (Stream) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(getResources().getString(R.string.movie), (Serializable) mSelectedStream);
                intent.putExtra(getResources().getString(R.string.should_start), true);
                startActivity(intent);


                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }
    }

    private final class DataListener implements DataLoadingInterface{
        @Override
        public void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter> data) {

            //setAdapter(data.get(null));

            setupDetailsOverviewRow(data.get(null));

            System.err.println(((HeaderInfo) ((ArrayObjectAdapter) data.get(null)).get(0)).getName());
        }

        @Override
        public void request_data() {

        }

        @Override
        public void on_error() {

        }
    }
}
