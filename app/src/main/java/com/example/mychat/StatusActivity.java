package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText statusText;
    private Button saveStatusButton;
    private DatabaseReference statusDatabase;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        statusText=findViewById(R.id.status_edit_id);
        saveStatusButton=findViewById(R.id.save_status_id);
        toolbar=findViewById(R.id.main_activity_toolbar);
        progressBar=findViewById(R.id.progressbar_id);
        progressBar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String s=getIntent().getStringExtra("status");
        statusText.getEditableText().append(s);
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        statusDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        saveStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String status=statusText.getText().toString();
                statusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(StatusActivity.this, "Stats Update Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
