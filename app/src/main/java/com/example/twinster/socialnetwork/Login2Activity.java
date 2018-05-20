package com.example.twinster.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login2Activity extends AppCompatActivity {


    private EditText etPassword;
    private String email;

    private ProgressDialog loginDialog;

    private Toolbar myToolBar;
    private DatabaseReference userDatabase;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        email = getIntent().getStringExtra(LoginActivity.email_key);
        etPassword = findViewById(R.id.etPassword);

        myAuth = FirebaseAuth.getInstance();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        loginDialog = new ProgressDialog(this);


        myToolBar = (Toolbar) findViewById(R.id.login2_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void signIn(View view) {
        String password = etPassword.getText().toString();
        if (!TextUtils.isEmpty(password)){
            loginDialog.setTitle("Signing in ");
            loginDialog.setMessage("Please wait while we are checking ur credentials");
            loginDialog.setCanceledOnTouchOutside(false);
            loginDialog.show();
            myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        loginDialog.dismiss();

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                        String currentUserId = myAuth.getCurrentUser().getUid();

                        userDatabase.child(currentUserId).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent mainPage = new Intent(Login2Activity.this, MainPageActivity.class);
                                mainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainPage);
                                finish();
                            }
                        });
                    }
                    else {
                        loginDialog.dismiss();
                        Toast.makeText(Login2Activity.this, "Error During Registration Please input correct data",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        else {
            Toast.makeText(Login2Activity.this, "Plase fill password field",Toast.LENGTH_LONG).show();
        }

    }

    public void recoverPassword(View view) {
        Intent forgot = new Intent(Login2Activity.this, LoginActivity.class);
        forgot.putExtra(LoginActivity.email_key, email);
        startActivity(forgot);
        finish();
    }
}
