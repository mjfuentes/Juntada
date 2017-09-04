package com.nedelu.juntada.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.Event;
import com.nedelu.juntada.service.GroupService;

import org.lucasr.twowayview.widget.SpacingItemDecoration;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EventFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private Context mContext;
    private GroupService groupService;
    private Long groupId;
    private MyEventRecyclerViewAdapter eventRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private View mNoMessagesView;
    public EventFragment() {
    }

    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupService = GroupService.getInstance(getActivity());
        mContext = getActivity();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        mNoMessagesView = view.findViewById(R.id.noMessages);
        // Set the adapter
        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration spaceItemDecoration = new VerticalSpaceItemDecoration(15);
        recyclerView.addItemDecoration(spaceItemDecoration);

        SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        groupId = userPref.getLong("groupId", 0L);
        List<Event> events = groupService.getEvents(groupId);
        eventRecyclerViewAdapter = new MyEventRecyclerViewAdapter(events, mListener);
        recyclerView.setAdapter(eventRecyclerViewAdapter);
        recyclerView.setHasFixedSize(true);

        if (events.size() > 0) {
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
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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
        List<Event> events = this.groupService.getEvents(groupId);
        if (events.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            mNoMessagesView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            mNoMessagesView.setVisibility(View.VISIBLE);
        }
        eventRecyclerViewAdapter.setItems(events);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Event item);
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
