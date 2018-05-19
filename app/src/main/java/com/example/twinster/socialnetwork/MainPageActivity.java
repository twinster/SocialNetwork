package com.example.twinster.socialnetwork;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainPageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ViewPager mainviewpager;
    private SectionsPagerAdapter mainpagesectionsadapter;
    private TabLayout mainpagetablayout;

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
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent mainIntent = new Intent(MainPageActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
