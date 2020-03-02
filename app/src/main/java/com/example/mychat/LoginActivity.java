package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private TextInputLayout email,password;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.main_activity_toolbar);
        progressDialog=new ProgressDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email=findViewById(R.id.loginemailid);
        password=findViewById(R.id.loginpassid);
        loginButton=findViewById(R.id.loginButtonid);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e=email.getEditText().getText().toString();
                String p=password.getEditText().getText().toString();
                if(TextUtils.isEmpty(e) || TextUtils.isEmpty(p)){
                    Toast.makeText(LoginActivity.this,"Anything is Empty.",Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.setTitle("Logging in user");
                    progressDialog.setMessage("Please wait while we enter your account.");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    login(e,p);
                }
            }
        });

    }
    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent i=new Intent(LoginActivity.this,MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            progressDialog.hide();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
