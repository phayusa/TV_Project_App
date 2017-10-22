package com.example.msrouji.tv_app.view.presenter;

import android.graphics.Color;
import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.msrouji.tv_app.model.NameInfo;
import com.example.msrouji.tv_app.R;

/**
 * Created by msrouji on 07/09/2017.
 */

public class GridItemPresenter extends Presenter {

    private int GRID_ITEM_WIDTH;
    private int GRID_ITEM_HEIGHT;

    public GridItemPresenter(int GRID_ITEM_WIDTH, int GRID_ITEM_HEIGHT) {
        this.GRID_ITEM_WIDTH = GRID_ITEM_WIDTH;
        this.GRID_ITEM_HEIGHT = GRID_ITEM_HEIGHT;
    }

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {

        TextView view = new TextView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setBackgroundColor(parent.getResources().getColor(R.color.default_background));
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER);
        return new Presenter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        if (item instanceof String)
            ((TextView) viewHolder.view).setText((String) item);
        else
            ((TextView) viewHolder.view).setText(((NameInfo) item).getName());
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }
}
