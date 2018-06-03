package com.example.twinster.socialnetwork;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainPageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ViewPager mainviewpager;
    private SectionsPagerAdapter mainpagesectionsadapter;
    private TabLayout mainpagetablayout;

    private DatabaseReference dbUser;

    private Toolbar myToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mAuth = FirebaseAuth.getInstance();
        dbUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        mainviewpager = (ViewPager) findViewById(R.id.mainpage_viewpager);
        mainpagesectionsadapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mainviewpager.setAdapter(mainpagesectionsadapter);

        mainpagetablayout = (TabLayout) findViewById(R.id.mainpage_tabs);
        mainpagetablayout.setupWithViewPager(mainviewpager);


        myToolBar = (Toolbar) findViewById(R.id.mainpage_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle(R.string.main_page);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbUser.child("online").setValue("true");
    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        dbUser.child("online").setValue(false);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_button){
            FirebaseAuth.getInstance().signOut();
            dbUser.child("online").setValue(ServerValue.TIMESTAMP);
            Intent mainIntent = new Intent(MainPageActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }

        if (item.getItemId() == R.id.main_settings_button){
            Intent settingsIntent = new Intent(MainPageActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.main_all_button){
            Intent usersIntent = new Intent(MainPageActivity.this, UsersActivity.class);
            startActivity(usersIntent);
        }

        return true;
    }
}
