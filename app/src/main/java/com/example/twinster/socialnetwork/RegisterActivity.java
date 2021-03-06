package com.example.twinster.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUserName, etEmail, etPassword;
    private Button btSignUp;
    private FirebaseAuth mAuth;

    private Toolbar myToolBar;
    private ProgressDialog registerDialog;

    private DatabaseReference appdatabase;

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
        getSupportActionBar().setTitle(R.string.create_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerDialog = new ProgressDialog(this);


        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    registerDialog.setTitle(getString(R.string.registering_user));
                    registerDialog.setMessage(getString(R.string.wait_while_creating_account));
                    registerDialog.setCanceledOnTouchOutside(false);
                    registerDialog.show();
                    user_registration(userName, email, password);
                }
                else{
                    Toast.makeText(RegisterActivity.this, R.string.fill_all_inputs,Toast.LENGTH_LONG).show();
                }

            }
        });



    }

    private void user_registration(final String userName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                    String UserId = currUser.getUid();
                    appdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UserId);
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name",userName);
                    userMap.put("image","Default");
                    userMap.put("thumb_image","Default");
                    userMap.put("device_token", deviceToken);
                    appdatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                registerDialog.dismiss();
                                Intent mainPage = new Intent(RegisterActivity.this, MainPageActivity.class);
                                mainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainPage);
                                finish();
                            }
                            else{
                                registerDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, R.string.error_while_registering, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else {
                    registerDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, R.string.error_while_registration,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
