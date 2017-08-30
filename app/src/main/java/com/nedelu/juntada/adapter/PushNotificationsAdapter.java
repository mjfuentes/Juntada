package com.nedelu.juntada.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nedelu.juntada.R;
import com.nedelu.juntada.model.PushNotification;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.UserService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de notificaciones
 */
public class PushNotificationsAdapter
        extends RecyclerView.Adapter<PushNotificationsAdapter.ViewHolder> {

    List<PushNotification> pushNotifications = new ArrayList<>();
    private OnItemClickListener listener;
    private Context mContext;
    private UserService userService;

    public PushNotificationsAdapter(OnItemClickListener listener, Context context) {
        this.listener = listener;
        this.mContext = context;
        this.userService = new UserService(mContext);
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
        final PushNotification newNotification = pushNotifications.get(position);
        User user = userService.getUser(newNotification.getCreatorId());
        holder.title.setText(newNotification.getTitle());
        holder.description.setText(newNotification.getDescription());
        Picasso.with(mContext).load(user.getImageUrl()).resize(150, 150).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 listener.itemClicked(newNotification.getmType(), Long.valueOf(newNotification.getmValue()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return pushNotifications.size();
    }

    public void replaceData(List<PushNotification> items) {
        setList(items);
        notifyDataSetChanged();
    }

    public PushNotification getItem(int index){
        return pushNotifications.get(index);
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
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public interface OnItemClickListener{
        void itemClicked(String type, Long value);
    }
}