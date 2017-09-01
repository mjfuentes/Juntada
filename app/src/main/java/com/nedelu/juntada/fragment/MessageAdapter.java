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

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = messages.get(position);

        TextView message = (TextView) holder.mView.findViewById(R.id.message);
        ImageView imageView = (ImageView) holder.mView.findViewById(R.id.user_image);

        message.setText(holder.mItem.getMessage());
        Picasso.with(holder.mView.getContext()).load(holder.mItem.getCreatorImageUrl()).into(imageView);

    }

    public void addItem(Message message){
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messages.size();
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
