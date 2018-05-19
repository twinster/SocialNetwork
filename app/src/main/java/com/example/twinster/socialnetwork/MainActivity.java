package com.example.twinster.socialnetwork;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar myToolBar;

    Button btLogin, btGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btLogin = findViewById(R.id.btLogin);
        btGetStarted = findViewById(R.id.btGetStarted);

        mAuth = FirebaseAuth.getInstance();

        myToolBar = (Toolbar) findViewById(R.id.mainactivity_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("Bruco");

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            btLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(nextIntent);
                    finish();
                }
            });


            btGetStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(nextIntent);
                    finish();
                }
            });
        }
        else {
            Intent nextIntent = new Intent(MainActivity.this, MainPageActivity.class);
            startActivity(nextIntent);
            finish();
        }
    }
}
