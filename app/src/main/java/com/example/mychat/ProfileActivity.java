package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView nameText,statusText,total_friends;
    private Button sendFriendRequestButton,declineRequestButton;
    private DatabaseReference mDatabase;
    private String current_state;
    private DatabaseReference friendReqDatabase,friendDatabase;
    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profie);
        friendReqDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        friendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        current_user= FirebaseAuth.getInstance().getCurrentUser();
        final String user_id=getIntent().getStringExtra("user_id");
        declineRequestButton=findViewById(R.id.decline_button_id);
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
                final String image=dataSnapshot.child("image").getValue().toString();

                nameText.setText(name);
                statusText.setText(status);
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).into(imageView);
                    }
                });
                friendReqDatabase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String req_type;
                        if(dataSnapshot.hasChild(user_id)){
                            req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("received")){
                                current_state="req_received";
                                sendFriendRequestButton.setText("Accept Friend Request");
                            }else if(req_type.equals("sent")){
                                current_state="req_sent";
                                sendFriendRequestButton.setText("Cancel Friend Request");
                                declineRequestButton.setVisibility(View.INVISIBLE);
                                declineRequestButton.setEnabled(false);
                            }
                        }else{
                            friendDatabase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id)){
                                        current_state="friends";
                                        sendFriendRequestButton.setText("Unfriend this persion");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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
                                    declineRequestButton.setVisibility(View.INVISIBLE);
                                    declineRequestButton.setEnabled(false);

                                }
                            });
                        }
                    });
                }
                else if(current_state.equals("req_received")){
                    final String date= DateFormat.getDateInstance().format(new Date());
                    friendDatabase.child(current_user.getUid()).child(user_id).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendDatabase.child(user_id).child(current_user.getUid()).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendReqDatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            friendReqDatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sendFriendRequestButton.setEnabled(true);
                                                    current_state="friends";
                                                    sendFriendRequestButton.setText("Unfriend this persion");
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    });
                }
                else if(current_state.equals("friends")){
                    friendDatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendDatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
        declineRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("decline","Decline Button");
                friendDatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendDatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendFriendRequestButton.setEnabled(true);
                                current_state="not_friends";
                                declineRequestButton.setVisibility(View.GONE);
                                declineRequestButton.setEnabled(false);
                            }
                        });
                    }
                });
            }
        });

    }
}
