package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.utils.GlideApp;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InGroupActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Group group;
    private SessionManager sessionManager;

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_group);
        init();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        navigationView.setNavigationItemSelectedListener(this);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setHeaderInfo();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Fragment_GroupChat()).commit();
            navigationView.setCheckedItem(R.id.nav_groupchat);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_groupchat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment_GroupChat()).commit();
                break;
            case R.id.nav_reminder:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment_Reminder()).commit();
                break;
            case R.id.nav_suggestion:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment_SuggestionList()).commit();
                break;
            case R.id.nav_plan:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment_GroupPlan()).commit();
                break;
            case R.id.nav_close_group:
                Intent intent = new Intent(InGroupActivity.this, InHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer((GravityCompat.START));
        } else {
            Intent intent = new Intent(InGroupActivity.this, InHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void setHeaderInfo() {
        View headerView = navigationView.getHeaderView(0);

        ImageView header_photo = headerView.findViewById(R.id.nav_profile);
        TextView header_fullName = headerView.findViewById(R.id.user_fullname);
        TextView header_groupName = headerView.findViewById(R.id.group_name);
        TextView header_userRole = headerView.findViewById(R.id.user_role);

        setProfilePicture(header_photo);
        setUserFullName(header_fullName);
        setGroupName(header_groupName);
        setUserRole(header_userRole);
    }

    private void setProfilePicture(ImageView view) {
        String photoUrl = sessionManager.getUserDetails().get(sessionManager.KEY_PHOTO_URL);
        Uri uri = Uri.parse(photoUrl);
        GlideApp.with(this)
                .load(uri)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .centerCrop()
                .apply(new RequestOptions().override(120, 120))
                .apply(RequestOptions.circleCropTransform())
                .into(view);
    }

    private void setUserFullName(TextView view) {
        view.setText(sessionManager.getUserDetails().get(sessionManager.KEY_FULLNAME));
    }

    private void setGroupName(TextView view) {
        String groupText = view.getText().toString();
        groupText = groupText + group.getGroup_name();
        view.setText(groupText);
    }

    private void setUserRole(TextView view) {
        String role = view.getText().toString();
        if (isGroupCreator()) {
            role = role + " Leader";
        } else {
            role = role + " Member";
        }
        view.setText(role);
    }

    private boolean isGroupCreator() {
        return firebaseUser.getUid().equals(group.getCreator_id());
    }

    private void init() {
        sessionManager = new SessionManager(getApplicationContext());
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        group = getIntent().getParcelableExtra("group");
        toolbar.setTitle(group.getGroup_name());
    }
}
