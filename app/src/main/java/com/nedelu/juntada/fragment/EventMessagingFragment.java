package com.nedelu.juntada.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.Message;
import com.nedelu.juntada.model.MessageType;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.MessageService;
import com.nedelu.juntada.service.UserService;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class EventMessagingFragment extends Fragment implements MessageService.MessageSentListener, MessageService.MessagesLoadedListener {
    private Long eventId;
    private Long userId;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private MessageService messageService;
    private UserService userService;
    private FloatingActionButton sendMessageButton;
    private EditText messageText;
    private BroadcastReceiver mNotificationsReceiver;
    private ProgressBar progressBar;

    public EventMessagingFragment() {
    }

    public static EventMessagingFragment newInstance(Long eventId, Long userId) {
        EventMessagingFragment fragment = new EventMessagingFragment();
        Bundle args = new Bundle();
        args.putLong("eventId", eventId);
        args.putLong("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getLong("eventId");
            userId = getArguments().getLong("userId");
        }
        this.messageService = new MessageService(getActivity());
        this.userService = new UserService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messaging, container, false);

        Context context = view.getContext();
        messageAdapter = new MessageAdapter(messageService.getMessages(eventId));
        messageText = (EditText) view.findViewById(R.id.input);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_of_messages);
        progressBar = (ProgressBar) view.findViewById(R.id.button_progress_bar);
        sendMessageButton = (FloatingActionButton) view.findViewById(R.id.send_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(messageAdapter);
        scrollToBottom();
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollToBottom();
                        }
                    }, 100);
                }
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isNotBlank(messageText.getText().toString())) {
                    progressBar.setVisibility(View.VISIBLE);
                    User user = userService.getUser(userId);
                    messageService.sendMessage(user, messageText.getText().toString(), MessageType.EVENT, eventId, EventMessagingFragment.this);
                }
            }
        });

        mNotificationsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message message = new Message();
                if (intent.getStringExtra("type").equals("EVENT") && eventId.equals(intent.getLongExtra("type_id",0L)) && !userId.equals(intent.getLongExtra("creator_id", 0L))) {
                    message.setId(intent.getLongExtra("message_id", 0L));
                    message.setMessage(intent.getStringExtra("message"));
                    message.setCreatorId(intent.getLongExtra("creator_id", 0L));
                    message.setType(MessageType.EVENT);
                    message.setTypeId(eventId);
                    User user = userService.getUser(message.getCreatorId());
                    message.userImage = user.getImageUrl();
                    message.userName = user.getFirstName() + " " + user.getLastName();
                    messageAdapter.addItem(message);
                    scrollToBottom();
                }
            }
        };
        messageService.loadMessages(eventId, this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void refresh() {
    }

    @Override
    public void messageSent(Message message) {
        messageText.setText("");
        message.userImage = userService.getUser(message.getCreatorId()).getImageUrl();
        messageAdapter.addItem(message);
        scrollToBottom();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void messageFailed() {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getActivity(),"El mensaje no pudo ser enviado", Toast.LENGTH_SHORT);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mNotificationsReceiver, new IntentFilter("MESSAGE"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mNotificationsReceiver);
    }

    @Override
    public void messagesLoaded(List<Message> messages) {
        messageAdapter.setItems(messages);
        scrollToBottom();
    }

    private void scrollToBottom(){
        if (recyclerView.getAdapter().getItemCount() > 0){
            recyclerView.smoothScrollToPosition(
                    recyclerView.getAdapter().getItemCount() - 1);
        }
    }
    @Override
    public void messagesNotLoaded() {

    }
}
