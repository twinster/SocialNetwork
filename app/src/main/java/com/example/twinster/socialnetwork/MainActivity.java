package com.example.twinster.socialnetwork;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public static final String email_key = "Email";

    Button btNext, btCreateAccount;
    EditText etEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btNext = findViewById(R.id.btNext);
        btCreateAccount = findViewById(R.id.btCreateAccount);
        etEmail = findViewById(R.id.etEmail);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Todo main page login and create
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            btNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextIntent = new Intent(MainActivity.this, LoginActivity.class);
                    nextIntent.putExtra(email_key, etEmail.getText().toString());
                    startActivity(nextIntent);
                    finish();
                }
            });


            btCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(nextIntent);
                    finish();
                }
            });
        }
        else {
            //TODO call main activity after authorization
            Intent nextIntent = new Intent(MainActivity.this, MainPageActivity.class);
            startActivity(nextIntent);
            finish();
        }
    }
}
