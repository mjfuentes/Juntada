package com.nedelu.juntada.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollOptionAdapter extends RecyclerView.Adapter<PollOptionAdapter.SimpleViewHolder> {

    private final Context mContext;
    private final RecyclerView mRecyclerView;
    private final List<PollOption> mItems;
    private int mCurrentItemId = 0;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View view) {
            super(view);
        }
    }

    public PollOptionAdapter(Context context, List<PollOption> options, RecyclerView recyclerView) {
        mContext = context;
        mItems = new ArrayList<PollOption>(options.size());
        for (int i = 0; i < options.size(); i++) {
            addItem(i,options.get(i));
        }

        mRecyclerView = recyclerView;
    }

    public void addItem(int position, PollOption option) {
        mItems.add(position, option);
        notifyItemInserted(position);
    }

    public void setItems(List<PollOption> options) {
        mItems.clear();
        mItems.addAll(options);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.vote_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        //TODO POPULATE VIEW
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}