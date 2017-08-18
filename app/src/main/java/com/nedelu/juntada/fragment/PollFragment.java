package com.nedelu.juntada.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nedelu.juntada.R;
import com.nedelu.juntada.activity.GroupActivity;
import com.nedelu.juntada.model.Poll;
import com.nedelu.juntada.service.EventService;
import com.nedelu.juntada.service.GroupService;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PollFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private PollFragment.OnListFragmentInteractionListener mListener;
    private GroupService groupService;
    private Context mContext;

    public PollFragment() {
    }

    public static PollFragment newInstance(Context context) {
        PollFragment fragment = new PollFragment();
        fragment.setContext(context);
        fragment.setGroupService(GroupService.getInstance(context));

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_unconfirmed_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            SharedPreferences userPref = context.getSharedPreferences("user", 0);
            Long groupId =  userPref.getLong("groupId", 0L);

            recyclerView.setAdapter(new MyPollRecyclerViewAdapter(groupService.getPolls(groupId), mListener));
            recyclerView.setHasFixedSize(true);
            recyclerView .setNestedScrollingEnabled(false);

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EventFragment.OnListFragmentInteractionListener) {
            mListener = (PollFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void setContext(Context context) {this.mContext = context; }
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Poll item);
    }
}
