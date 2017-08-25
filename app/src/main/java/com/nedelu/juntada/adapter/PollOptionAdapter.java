package com.nedelu.juntada.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.model.VotingItem;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class PollOptionAdapter extends RecyclerView.Adapter<PollOptionAdapter.ViewHolder> {

    private final Context mContext;
    private final RecyclerView mRecyclerView;
    private final List<VotingItem> mItems;
    private int mCurrentItemId = 0;
    private VoteListener mListener;
    private boolean admin;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View view) {
            super(view);
        }
    }

    public PollOptionAdapter(Context context, List<VotingItem> options, RecyclerView recyclerView, VoteListener listener, boolean creator) {
        mContext = context;
        mListener = listener;
        admin = creator;
        mItems = new ArrayList<VotingItem>(options.size());
        for (int i = 0; i < options.size(); i++) {
            addItem(i,options.get(i));
        }

        mRecyclerView = recyclerView;
    }

    public void addItem(int position, VotingItem option) {
        mItems.add(position, option);
        notifyItemInserted(position);
    }

    public void setItems(List<VotingItem> options) {
        mItems.clear();
        mItems.addAll(options);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public List<VotingItem> getItems(){
        return mItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.vote_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);
        SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
        SimpleDateFormat completeFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("es", "ES"));
        TextView weekday= (TextView) holder.mView.findViewById(R.id.option_weekday);
        TextView date = (TextView) holder.mView.findViewById(R.id.option_date);
        TextView time = (TextView) holder.mView.findViewById(R.id.option_time);
        TextView votes = (TextView) holder.mView.findViewById(R.id.option_votes);

        ImageView checkbox = (ImageView) holder.mView.findViewById(R.id.checkbox);

        if (holder.mItem.getVoted()) {
            checkbox.setImageDrawable(mContext.getResources().getDrawable(R.drawable.checked_green));
        } else {
            checkbox.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check_green));
        }

        try {
            Date optionDate = completeFormat.parse(holder.mItem.getOption().getDate());
            weekday.setText(StringUtils.upperCase(dayFormat.format(optionDate).substring(0,3)));
            date.setText(dayMonthFormat.format(optionDate));
            time.setText(holder.mItem.getOption().getTime());
            votes.setText(String.valueOf(holder.mItem.getOption().getVotes().size()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView checkbox;
                if (admin) {
                    for (int i = 0; i < mItems.size(); i++) {
                        View view = mRecyclerView.getChildAt(i);
                        checkbox = (ImageView) view.findViewById(R.id.checkbox);
                        checkbox.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check_green));
                    }
                }
                checkbox = (ImageView) holder.mView.findViewById(R.id.checkbox);

                if (holder.mItem.getVoted()) {
                    holder.mItem.setVoted(false);
                    checkbox.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check_green));
                } else {
                    for (VotingItem item :mItems){
                        item.setVoted(false);
                    }
                    holder.mItem.setVoted(true);
                    checkbox.setImageDrawable(mContext.getResources().getDrawable(R.drawable.checked_green));
                }

                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.itemVoted(holder.mItem);
                }

            }
        });
    }



    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public VotingItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public interface VoteListener{
        void itemVoted(VotingItem item);
    }
}