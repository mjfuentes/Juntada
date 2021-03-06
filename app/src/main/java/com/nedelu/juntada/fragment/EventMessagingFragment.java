package com.nedelu.juntada.fragment;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.apache.commons.lang3.StringEscapeUtils;
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
    private KeyListener keyListener;
    private NotificationManager notificationManager;

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

        notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messaging, container, false);

        Context context = view.getContext();
        messageAdapter = new MessageAdapter(messageService.getMessages(eventId, userId));
        messageText = (EditText) view.findViewById(R.id.edittext_chatbox);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_of_messages);
        progressBar = (ProgressBar) view.findViewById(R.id.button_progress_bar);
        sendMessageButton = (FloatingActionButton) view.findViewById(R.id.button_chatbox_send);
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
                    sendMessageButton.setClickable(false);
                    keyListener = messageText.getKeyListener();
                    messageText.setKeyListener(null);
                    progressBar.setVisibility(View.VISIBLE);
                    User user = userService.getUser(userId);
                    messageService.sendMessage(user, StringEscapeUtils.escapeJava(messageText.getText().toString()), MessageType.EVENT, eventId, EventMessagingFragment.this);
                }
            }
        });

        mNotificationsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message message = new Message();
                notificationManager.cancel(Integer.valueOf(String.valueOf(MessageType.EVENT.ordinal()) + String.valueOf(eventId)));
                if (intent.getStringExtra("type").equals("EVENT") && eventId.equals(intent.getLongExtra("type_id",0L)) && !userId.equals(intent.getLongExtra("creator_id", 0L))) {
                    message.setId(intent.getLongExtra("message_id", 0L));
                    message.setMessage(intent.getStringExtra("message"));
                    message.setCreatorId(intent.getLongExtra("creator_id", 0L));
                    message.setTime(intent.getStringExtra("time"));
                    message.setType(MessageType.EVENT);
                    message.setTypeId(eventId);
                    if (message.getCreatorId() != userId) {
                        User user = userService.getUser(message.getCreatorId());
                        message.userImage = user.getImageUrl();
                        message.userName = user.getFirstName() + " " + user.getLastName();
                        message.mine = false;
                    } else {
                        message.mine =true;
                    }
                    messageAdapter.addItem(message);
                    scrollToBottom();
                }
            }
        };
        messageService.loadMessages(eventId, userId, this);

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
        messageText.setKeyListener(keyListener);
        sendMessageButton.setClickable(true);
        messageText.setEnabled(true);
        message.userImage = userService.getUser(message.getCreatorId()).getImageUrl();
        messageAdapter.addItem(message);
        scrollToBottom();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void messageFailed() {
        sendMessageButton.setClickable(true);
        messageText.setKeyListener(keyListener);
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
            recyclerView.scrollToPosition(
                    recyclerView.getAdapter().getItemCount() - 1);
        }
    }
    @Override
    public void messagesNotLoaded() {

    }
}
