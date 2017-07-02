package com.nedelu.juntada;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.nedelu.juntada.model.Group;
import com.nedelu.juntada.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
                Snackbar.make(view, "This button will be used to create new groups.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        GridView gridView = (GridView) findViewById(R.id.groupList);
        gridView.setAdapter(new GroupAdapter(generateMockGroups(), GroupsActivity.this));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        User user1 = new User("Matias", "Fuentes");
        User user2 = new User("Maria", "Mardon");
        User user3 = new User("Pedro", "Perez");
        User user4 = new User("Alejandro", "Gutierrez");

        group1.getUsers().add(user1);
        group1.getUsers().add(user2);
        group1.getUsers().add(user3);
        group1.getUsers().add(user4);

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

    private class GroupAdapter extends BaseAdapter{

        private List<Group> groups;
        private Context context;

        public GroupAdapter(List<Group> groups, Context context){
            this.groups = groups;
            this.context = context;
        }

        @Override
        public int getCount() {
            return groups.size();
        }

        @Override
        public Object getItem(int i) {
            return groups.get(i);
        }

        @Override
        public long getItemId(int i) {
            return groups.get(i).getId();
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v;
            if (convertView == null) {
                v = inflater.inflate(R.layout.group_item, null);
            } else {
                v = convertView;
            }

            ImageView image = v.findViewById(R.id.groupImage);
            String url = groups.get(i).getImageUrl();
            Picasso.with(context).load(url).into(image);

            TextView text = v.findViewById(R.id.groupName);
            text.setText(groups.get(i).getName());

            return v;
        }
    }
}
