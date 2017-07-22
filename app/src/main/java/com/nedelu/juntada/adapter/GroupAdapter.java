package com.nedelu.juntada.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.activity.GroupActivity;
import com.nedelu.juntada.activity.GroupsActivity;
import com.nedelu.juntada.activity.LoginActivity;
import com.nedelu.juntada.model.Group;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> implements Observer{
    private Context mContext;
    private List<Group> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private static ClickListener mClickListener;

    // data is passed into the constructor
    public GroupAdapter(Context context, List<Group> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.group_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String groupName = mData.get(position).getName();
        String groupImage = mData.get(position).getImageUrl();
        holder.groupName.setText(StringEscapeUtils.unescapeJava(groupName));
        Picasso.with(mContext).load(groupImage).into(holder.imageView);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void update(Observable observable, Object o) {
        this.notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView groupName;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            groupName = (TextView) itemView.findViewById(R.id.group_name_text);
            imageView = (ImageView) itemView.findViewById(R.id.group_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setData(List<Group> groups){
        mData = groups;
    }

    // convenience method for getting data at click position
    public Group getItem(int position) {
        return mData.get(position);
    }

}