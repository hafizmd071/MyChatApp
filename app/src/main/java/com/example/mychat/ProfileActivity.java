package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView nameText,statusText,total_friends;
    private Button sendFriendRequestButton,declineRequestButton;
    private DatabaseReference mDatabase;
    private String current_state;
    private DatabaseReference friendReqDatabase;
    private FirebaseUser current_user;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profie);
        friendReqDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");

        current_user= FirebaseAuth.getInstance().getCurrentUser();
        final String user_id=getIntent().getStringExtra("user_id");
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        imageView=findViewById(R.id.profile_activity_img_id);
        nameText=findViewById(R.id.profile_activity_name_id);
        statusText=findViewById(R.id.profile_activity_status_id);
        total_friends=findViewById(R.id.profile_activity_total_friends_id);
        sendFriendRequestButton=findViewById(R.id.profile_activity_send_button_id);
        current_state="not_friends";

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String status =dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                nameText.setText(name);
                statusText.setText(status);

                friendReqDatabase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type=="received"){
                                current_state="req_received";
                                sendFriendRequestButton.setText("Accept Friend Request");
                            }else if(req_type=="sent"){
                                current_state="req_sent";
                                sendFriendRequestButton.setText("Cancel Friend Request");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFriendRequestButton.setEnabled(false);
                if(current_state.equals("not_friends")){
                    friendReqDatabase.child(current_user.getUid()).child(user_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        sendFriendRequestButton.setEnabled(true);
                                        current_state="req_sent";
                                        sendFriendRequestButton.setText("Cancel Friend Request");
                                        friendReqDatabase.child(user_id).child(current_user.getUid()).child("request_type").
                                                setValue("received")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ProfileActivity.this,"Friend Request send Successful.",Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    else{
                                        Toast.makeText(ProfileActivity.this,"Friend Request send Failed.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else if(current_state.equals("req_sent")){
                    friendReqDatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendReqDatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendFriendRequestButton.setEnabled(true);
                                    current_state="not_friends";
                                    sendFriendRequestButton.setText("Send Friend Request");
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
