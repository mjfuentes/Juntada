package com.nedelu.juntada.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.fragment.EventFragment.OnListFragmentInteractionListener;
import com.nedelu.juntada.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {

    private final List<Event> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyEventRecyclerViewAdapter(List<Event> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        final SimpleDateFormat completeFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        final SimpleDateFormat time = new SimpleDateFormat("HH:mm");

        Collections.sort(items, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                try {
                    Date optionDate1 = completeFormat.parse(event.getDate());
                    Date optionDate2 = completeFormat.parse(t1.getDate());

                    int result = optionDate1.compareTo(optionDate2);
                    if (result == 0){
                        optionDate1 = time.parse(event.getTime());
                        optionDate2 = time.parse(t1.getTime());
                        return optionDate1.compareTo(optionDate2);
                    }
                    return result;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);


        TextView eventName = (TextView) holder.mView.findViewById(R.id.event_name);
        TextView eventDate = (TextView) holder.mView.findViewById(R.id.event_date);
        TextView eventTime = (TextView) holder.mView.findViewById(R.id.event_time);
        TextView eventLocation = (TextView) holder.mView.findViewById(R.id.event_location);
        TextView eventParticipants = (TextView) holder.mView.findViewById(R.id.users);

        eventName.setText(mValues.get(position).getTitle());
        eventDate.setText(mValues.get(position).getDate());
        eventTime.setText(mValues.get(position).getTime());
        eventLocation.setText(mValues.get(position).getLocation());
        eventParticipants.setText(String.valueOf(mValues.get(position).getConfirmedUsers().size()));

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Event mItem;

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
