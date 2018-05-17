package com.example.twinster.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etPassword;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPassword = findViewById(R.id.etPassword);

        email = getIntent().getStringExtra(MainActivity.email_key);
    }


    public void signIn(View view) {
        String password = etPassword.getText().toString();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent mainPage = new Intent(LoginActivity.this, MainPageActivity.class);
                    startActivity(mainPage);
                    finish();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Error During Registration Please input correct data",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
