package com.example.twinster.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login2Activity extends AppCompatActivity {


    private EditText etPassword;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        email = getIntent().getStringExtra(LoginActivity.email_key);
        etPassword = findViewById(R.id.etPassword);

    }

    public void signIn(View view) {
        String password = etPassword.getText().toString();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent mainPage = new Intent(Login2Activity.this, MainPageActivity.class);
                    startActivity(mainPage);
                    finish();
                }
                else {
                    Toast.makeText(Login2Activity.this, "Error During Registration Please input correct data",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void recoverPassword(View view) {
        Intent forgot = new Intent(Login2Activity.this, LoginActivity.class);
        forgot.putExtra(LoginActivity.email_key, email);
        startActivity(forgot);
        finish();
    }
}
