package com.example.twinster.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private EditText etEmail;
    public static final String email_key = "Email";
    private Button btNext, btSend;
    private String email;

    private Toolbar myToolBar;

    public LoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        btNext = findViewById(R.id.btNext);
        btSend = findViewById(R.id.btSend);
        email = getIntent().getStringExtra(email_key);

        if (email != null){
            forgotPassword();
        }

        myToolBar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void forgotPassword() {
        btNext.setVisibility(View.GONE);
        btSend.setVisibility(View.VISIBLE);
        etEmail.setText(email);
    }


    public void next(View view) {
        String regEmail = etEmail.getText().toString();
        if (!TextUtils.isEmpty(regEmail)) {
            Intent login2 = new Intent(LoginActivity.this, Login2Activity.class);
            login2.putExtra(email_key,etEmail.getText().toString());
            startActivity(login2);
            finish();
        }
        else {
            Toast.makeText(LoginActivity.this, "Please fill email field",Toast.LENGTH_LONG).show();
        }

    }

    public void sendRecover(View view) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Recovery Message Sent To Email",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(LoginActivity.this, "You tried many times, Try again later",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
