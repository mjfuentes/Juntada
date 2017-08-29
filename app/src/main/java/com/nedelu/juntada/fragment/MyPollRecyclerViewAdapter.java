package com.nedelu.juntada.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.model.PollOption;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyPollRecyclerViewAdapter extends RecyclerView.Adapter<MyPollRecyclerViewAdapter.ViewHolder> {

    private List<Poll> mValues;
    private final PollFragment.OnListFragmentInteractionListener mListener;
    private Long mUserId;

    public MyPollRecyclerViewAdapter(List<Poll> items,Long userId, PollFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        Collections.sort(items, new Comparator<Poll>() {
            @Override
            public int compare(Poll poll, Poll t1) {
                return t1.getId().compareTo(poll.getId());
            }
        });
        mUserId = userId;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event_unconfirmed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Poll poll = mValues.get(position);
        List<PollOption> options = new ArrayList<>(poll.getOptions());

        Collections.sort(options, new Comparator<PollOption>() {
            @Override
            public int compare(PollOption pollOption, PollOption t1) {
                return Integer.valueOf(t1.getVotes().size()).compareTo(Integer.valueOf(pollOption.getVotes().size()));
            }
        });

        String dayMonth = "dd/MM";
        SimpleDateFormat dayMonthFormat = new SimpleDateFormat(dayMonth, Locale.ENGLISH);

        SimpleDateFormat completeFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("es", "ES"));

        TextView pollname = (TextView) holder.mView.findViewById(R.id.poll_name);
        pollname.setText(poll.getTitle());
        TextView vote = (TextView) holder.mView.findViewById(R.id.vote_title);
        ImageView voteImage = (ImageView) holder.mView.findViewById(R.id.vote_image);

        if (poll.getCreator().getId().equals(mUserId)){
            voteImage.setVisibility(View.GONE);
            vote.setText("CONFIRMAR");
        }

        try {
            if (options.size() > 0) {
                LinearLayout option1 = (LinearLayout) holder.mView.findViewById(R.id.option1);
                option1.setVisibility(View.VISIBLE);
                TextView optionDate1 = (TextView) holder.mView.findViewById(R.id.text_date1);
                TextView optionDay1 = (TextView) holder.mView.findViewById(R.id.text_day1);
                TextView optionVotes1 = (TextView) holder.mView.findViewById(R.id.text_votes1);

                Date date1 = completeFormat.parse(options.get(0).getDate());
                optionDate1.setText(dayMonthFormat.format(date1));
                optionDay1.setText(StringUtils.upperCase(dayFormat.format(date1).substring(0,3)));
                optionVotes1.setText(String.valueOf(options.get(0).getVotes().size()));
            }

            if (options.size() > 1) {
                LinearLayout option2 = (LinearLayout) holder.mView.findViewById(R.id.option2);
                option2.setVisibility(View.VISIBLE);
                TextView optionDate2 = (TextView) holder.mView.findViewById(R.id.text_date2);
                TextView optionDay2 = (TextView) holder.mView.findViewById(R.id.text_day2);
                TextView optionVotes2 = (TextView) holder.mView.findViewById(R.id.text_votes2);
                Date date2 = completeFormat.parse(options.get(1).getDate());
                optionDate2.setText(dayMonthFormat.format(date2));
                optionDay2.setText(StringUtils.upperCase(dayFormat.format(date2).substring(0,3)));
                optionVotes2.setText(String.valueOf(options.get(1).getVotes().size()));
            }

            if (options.size() > 2) {
                LinearLayout option3 = (LinearLayout) holder.mView.findViewById(R.id.option3);
                option3.setVisibility(View.VISIBLE);
                TextView optionDate3 = (TextView) holder.mView.findViewById(R.id.text_date3);
                TextView optionDay3 = (TextView) holder.mView.findViewById(R.id.text_day3);
                TextView optionVotes3 = (TextView) holder.mView.findViewById(R.id.text_votes3);

                Date date3 = completeFormat.parse(options.get(2).getDate());
                optionDate3.setText(dayMonthFormat.format(date3));
                optionDay3.setText(StringUtils.upperCase(dayFormat.format(date3).substring(0,3)));
                optionVotes3.setText(String.valueOf(options.get(2).getVotes().size()));
            }

            if (options.size() > 3) {
                LinearLayout option4 = (LinearLayout) holder.mView.findViewById(R.id.option4);
                option4.setVisibility(View.VISIBLE);
                TextView optionDate4 = (TextView) holder.mView.findViewById(R.id.text_date4);
                TextView optionDay4 = (TextView) holder.mView.findViewById(R.id.text_day4);
                TextView optionVotes4 = (TextView) holder.mView.findViewById(R.id.text_votes4);

                Date date4 = completeFormat.parse(options.get(3).getDate());
                optionDate4.setText(dayMonthFormat.format(date4));
                optionDay4.setText(StringUtils.upperCase(dayFormat.format(date4).substring(0,3)));
                optionVotes4.setText(String.valueOf(options.get(3).getVotes().size()));
            }
        } catch (ParseException e){
            e.printStackTrace();
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setItems(List<Poll> items) {
        this.mValues = items;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Poll mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
