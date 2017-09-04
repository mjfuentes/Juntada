package com.nedelu.juntada.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.manager.GroupManager;
import com.nedelu.juntada.model.User;
import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.SimpleViewHolder> {

    private final Context mContext;
    private final RecyclerView mRecyclerView;
    private final List<User> mItems;
    private int mCurrentItemId = 0;
    private ClickListener mClickListener;

    public interface ClickListener {
        void onUserClicked(int position, View v);
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView userImage;

        public SimpleViewHolder(View view) {
            super(view);
            userImage = (ImageView) view.findViewById(R.id.userImage);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onUserClicked(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(UserAdapter.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public UserAdapter(Context context,List<User> users, RecyclerView recyclerView) {
        mContext = context;
        mItems = new ArrayList<User>(users.size());
        for (int i = 0; i < users.size(); i++) {
            addItem(i,users.get(i));
        }

        mRecyclerView = recyclerView;
    }

    public void addItem(int position, User user) {
        mItems.add(position, user);
        notifyItemInserted(position);
    }

    public void setItems(List<User> users) {
        mItems.clear();
        mItems.addAll(users);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        try {
            String url = mItems.get(position).getImageUrl();
            Picasso.with(mContext).load(url).into(holder.userImage);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}