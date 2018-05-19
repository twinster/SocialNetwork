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

public class MainPageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ViewPager mainviewpager;
    private SectionsPagerAdapter mainpagesectionsadapter;
    private TabLayout mainpagetablayout;

    private Toolbar myToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mAuth = FirebaseAuth.getInstance();
        mainviewpager = (ViewPager) findViewById(R.id.mainpage_viewpager);
        mainpagesectionsadapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mainviewpager.setAdapter(mainpagesectionsadapter);

        mainpagetablayout = (TabLayout) findViewById(R.id.mainpage_tabs);
        mainpagetablayout.setupWithViewPager(mainviewpager);


        myToolBar = (Toolbar) findViewById(R.id.mainpage_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("Main Page");
    }


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
            Intent mainIntent = new Intent(MainPageActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }

        else {
            Intent settingsIntent = new Intent(MainPageActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return true;
    }
}
