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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout displayName,email,password;
    private Button createAcc;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog=new ProgressDialog(this);
        toolbar=findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        displayName=findViewById(R.id.displaynameid);
        email=findViewById(R.id.emailid);
        password=findViewById(R.id.passwordid);
        createAcc=findViewById(R.id.createbtnid);

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dn=displayName.getEditText().getText().toString();
                String e=email.getEditText().getText().toString();
                String p=password.getEditText().getText().toString();
                if(TextUtils.isEmpty(dn) || TextUtils.isEmpty(e) || TextUtils.isEmpty(p)){
                    Toast.makeText(RegisterActivity.this,"Anything is Empty.",Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.setTitle("Registering user");
                    progressDialog.setMessage("Please wait while we create your account.");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    registerUser(dn,e,p);
                }


            }
        });
    }

    private void registerUser(final String dn, String email, String password) {
        //Toast.makeText(RegisterActivity.this,dn,Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                            String userId=currentUser.getUid();
                            mDatabase=FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                            HashMap<String,String> userMap=new HashMap<>();
                            userMap.put("name",dn);
                            userMap.put("status","Hi My name is Hafiz and I am using MyChat!");
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Intent i=new Intent(RegisterActivity.this,MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            progressDialog.hide();
                            Toast.makeText(RegisterActivity.this,"Can't sign in.Please check the form and try again.",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
