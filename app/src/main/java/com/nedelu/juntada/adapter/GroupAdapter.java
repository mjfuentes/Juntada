package com.nedelu.juntada.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nedelu.juntada.R;

import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.service.GroupService;
import com.squareup.picasso.Callback;
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
    private boolean lockedAnimations = false;
    private long profileHeaderAnimationStartTime = 0;
    private int lastAnimatedItem = 0;
    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final int MAX_PHOTO_ANIMATION_DELAY = 600;
    private static final int MIN_ITEMS_COUNT = 2;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

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
        Integer unread = mData.get(position).unansweredEventsAndPolls;
        if (unread > 0){
            holder.notificationsAmount.setText(String.valueOf(unread));
            holder.notificationsCircle.setVisibility(View.VISIBLE);
        } else {
            holder.notificationsCircle.setVisibility(View.GONE);
            holder.notificationsAmount.setVisibility(View.GONE);
        }
        bindPhoto(holder, position);
//        Picasso.with(mContext).load(groupImage).into(holder.imageView);
        System.out.println("Group image requested");
    }

    private void bindPhoto(final ViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(mData.get(position).getImageUrl())
                .error(R.drawable.no_picture)
                .resize(300, 240)
                .centerCrop()
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhoto(holder);
                    }

                    @Override
                    public void onError() {

                    }
                });
        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    private void animatePhoto(ViewHolder viewHolder) {
        if (!lockedAnimations) {
            if (lastAnimatedItem == viewHolder.getPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = profileHeaderAnimationStartTime + MAX_PHOTO_ANIMATION_DELAY - System.currentTimeMillis();
            if (profileHeaderAnimationStartTime == 0) {
                animationDelay = viewHolder.getPosition() * 30 + MAX_PHOTO_ANIMATION_DELAY;
            } else if (animationDelay < 0) {
                animationDelay = viewHolder.getPosition() * 30;
            } else {
                animationDelay += viewHolder.getPosition() * 30;
            }

            viewHolder.root.setScaleY(0);
            viewHolder.root.setScaleX(0);
            viewHolder.root.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
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
        public FrameLayout root;
        public TextView notificationsAmount;
        public ImageView notificationsCircle;

        public ViewHolder(View itemView) {
            super(itemView);
            root = (FrameLayout) itemView.findViewById(R.id.group_item);
            notificationsAmount = (TextView) itemView.findViewById(R.id.notifications_amount);
            groupName = (TextView) itemView.findViewById(R.id.group_name_text);
            imageView = (ImageView) itemView.findViewById(R.id.group_image);
            notificationsCircle = (ImageView) itemView.findViewById(R.id.notifications);
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