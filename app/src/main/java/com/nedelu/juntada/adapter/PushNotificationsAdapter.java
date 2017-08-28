package com.nedelu.juntada.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.PushNotification;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de notificaciones
 */
public class PushNotificationsAdapter
        extends RecyclerView.Adapter<PushNotificationsAdapter.ViewHolder> {

    List<PushNotification> pushNotifications = new ArrayList<>();

    public PushNotificationsAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_list_notification, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PushNotification newNotification = pushNotifications.get(position);

        holder.title.setText(newNotification.getTitle());
        holder.description.setText(newNotification.getDescription());

    }

    @Override
    public int getItemCount() {
        return pushNotifications.size();
    }

    public void replaceData(List<PushNotification> items) {
        setList(items);
        notifyDataSetChanged();
    }

    public void setList(List<PushNotification> list) {
        this.pushNotifications = list;
    }

    public void addItem(PushNotification pushMessage) {
        pushNotifications.add(0, pushMessage);
        notifyItemInserted(0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView expiryDate;
        public TextView discount;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            description = (TextView) itemView.findViewById(R.id.tv_description);
        }
    }
}