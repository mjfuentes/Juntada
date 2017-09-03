package com.nedelu.juntada.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = messages.get(position);
        TextView message = (TextView) holder.mView.findViewById(R.id.message);
        message.setText(holder.mItem.getMessage());

        if (!holder.mItem.mine) {
            ImageView imageView = (ImageView) holder.mView.findViewById(R.id.user_image);
            TextView userName = (TextView) holder.mView.findViewById(R.id.user_name);
            userName.setText(holder.mItem.userName);
            Picasso.with(holder.mView.getContext()).load(holder.mItem.userImage).into(imageView);
        }

    }

    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (message.mine) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    public void addItem(Message message){
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setItems(List<Message> items) {
        this.messages = items;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Message mItem;

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
