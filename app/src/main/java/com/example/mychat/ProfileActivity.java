package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView nameText,statusText,total_friends;
    private Button sendImageRequestButton;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profie);

        String user_id=getIntent().getStringExtra("user_id");
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        imageView=findViewById(R.id.profile_activity_img_id);
        nameText=findViewById(R.id.profile_activity_name_id);
        statusText=findViewById(R.id.profile_activity_status_id);
        total_friends=findViewById(R.id.profile_activity_total_friends_id);
        sendImageRequestButton=findViewById(R.id.profile_activity_button_id);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String status =dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                nameText.setText(name);
                statusText.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
