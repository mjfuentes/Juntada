package com.nedelu.juntada.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.Poll;
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
    private Long userId;
    private Long groupId;
    private View mNoMessagesView;
    private MyPollRecyclerViewAdapter myPollRecyclerViewAdapter;
    private RecyclerView recyclerView;
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

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        groupId = userPref.getLong("groupId", 0L);
        userId = userPref.getLong("userId", 0L);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_unconfirmed_list, container, false);
        Context context = view.getContext();
        mNoMessagesView = view.findViewById(R.id.noMessages);
         recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        Long groupId =  userPref.getLong("groupId", 0L);

        List<Poll> polls = groupService.getPolls(groupId);
        myPollRecyclerViewAdapter = new MyPollRecyclerViewAdapter(polls, userId, mListener);
        recyclerView.setAdapter(myPollRecyclerViewAdapter);
        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration spaceItemDecoration = new VerticalSpaceItemDecoration(15);
        recyclerView.addItemDecoration(spaceItemDecoration);

        if (polls.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            mNoMessagesView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            mNoMessagesView.setVisibility(View.VISIBLE);
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

    public void refresh() {
        List<Poll> polls = groupService.getPolls(groupId);
        if (polls.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            mNoMessagesView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            mNoMessagesView.setVisibility(View.VISIBLE);
        }
        myPollRecyclerViewAdapter.setItems(polls);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Poll item);
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }
}
