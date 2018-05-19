package com.example.twinster.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUserName, etEmail, etPassword;
    private Button btSignUp;
    private FirebaseAuth mAuth;

    private Toolbar myToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btSignUp = findViewById(R.id.btSignUp);

        myToolBar = findViewById(R.id.registration_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("Create Account");

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                
                user_registration(userName, email, password);
            }
        });



    }

    private void user_registration(String userName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent mainPage = new Intent(RegisterActivity.this, MainPageActivity.class);
                    startActivity(mainPage);
                    finish();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Error During Registration Please input correct data",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
