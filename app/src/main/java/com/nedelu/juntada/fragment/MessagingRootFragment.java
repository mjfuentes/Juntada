package com.nedelu.juntada.fragment;

        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentTransaction;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.nedelu.juntada.R;

/**
 * Example about replacing fragments inside a ViewPager. I'm using
 * android-support-v7 to maximize the compatibility.
 *
 * @author Dani Lao (@dani_lao)
 *
 */
public class MessagingRootFragment extends Fragment {

    private static final String TAG = "RootFragment";
    private NoMessagingFragment noMessagingFragment;
    private EventMessagingFragment eventMessagingFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		/* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.messaging_root_fragment, container, false);

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
		/*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */
		noMessagingFragment = new NoMessagingFragment();
        transaction.replace(R.id.root_frame, noMessagingFragment);

        transaction.commit();

        return view;
    }

    public void showMessages(Long eventId, Long userId){
        if (eventMessagingFragment == null){
            eventMessagingFragment = EventMessagingFragment.newInstance(eventId,userId);
        }
        FragmentTransaction trans = getFragmentManager()
                .beginTransaction();
        trans.replace(R.id.root_frame, eventMessagingFragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);
        trans.commit();
    }

    public void hideMessages() {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.root_frame, noMessagingFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}