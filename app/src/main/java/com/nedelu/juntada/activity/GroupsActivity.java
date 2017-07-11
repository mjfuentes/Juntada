package com.nedelu.juntada.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nedelu.juntada.R;
import com.nedelu.juntada.manager.GroupManager;
import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.User;
import com.nedelu.juntada.service.UserService;
import com.nedelu.juntada.util.SpacesItemDecoration;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "This button will be used to create new groups.", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent main = new Intent(GroupsActivity.this, NewGroupActivity.class);
                startActivity(main);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        GroupManager.getInstance().setGroups(generateMockGroups());

        groupAdapter = new GroupAdapter(GroupsActivity.this, generateMockGroups());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.groups_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(groupAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle inBundle = getIntent().getExtras();
        String name = inBundle.get("name").toString();
        String surname = inBundle.get("surname").toString();
        String imageUrl = inBundle.get("imageUrl").toString();
        String userId = inBundle.get("id").toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupAdapter.notifyDataSetChanged();
    }

    /*
        This method mocks some groups for testing the ui in early stages
    */
    private List<Group> generateMockGroups() {
        //Mock group 1
        Group group1 = new Group();
        group1.setCreationDate(new Date());
        group1.setId(1l);
        group1.setName("Facu");
        group1.setImageUrl("http://estaticos.tonterias.com/wp-content/uploads/2009/10/20090929033537_borrachos-dinero-cartera-mano.jpg");

        //Mock group 2
        Group group2 = new Group();
        group2.setCreationDate(new Date());
        group2.setId(2l);
        group2.setName("Futbol");
        group2.setImageUrl("http://www.bocalista.com/wp-content/uploads/2017/02/vuelven-los-Supercampeones-en-2018.jpg");

        //Mock group 3
        Group group3 = new Group();
        group3.setCreationDate(new Date());
        group3.setId(3l);
        group3.setName("Barrio");
        group3.setImageUrl("http://cdn.glamour.mx/uploads/images/thumbs/201547/fiesta_1247_980x560.jpg");

        List<Group> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);
        groups.add(group3);
        groups.add(group3);
        groups.add(group3);
        groups.add(group3);
        groups.add(group3);
        groups.add(group3);


        return groups;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
        private Context mContext;
        private List<Group> mData = new ArrayList<>();
        private LayoutInflater mInflater;

        // data is passed into the constructor
        public GroupAdapter(Context context, List<Group> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.mContext = context;
        }

        // inflates the cell layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.group_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        // binds the data to the textview in each cell
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String groupName = mData.get(position).getName();
            String groupImage = mData.get(position).getImageUrl();
            holder.groupName.setText(groupName);
            Picasso.with(mContext).load(groupImage).into(holder.imageView);
        }

        // total number of cells
        @Override
        public int getItemCount() {
            return mData.size();
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView groupName;
            public ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                groupName = (TextView) itemView.findViewById(R.id.group_name_text);
                imageView = (ImageView) itemView.findViewById(R.id.group_image);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
//                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        // convenience method for getting data at click position
        public String getItem(int id) {
            return mData.get(id).getId().toString();
        }

    }
}
