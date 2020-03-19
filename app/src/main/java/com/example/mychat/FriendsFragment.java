package com.example.mychat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mFriendsDatabase, mUsersDatabase;
    private String current_user_id;
    private RecyclerView mFriendList;
    private View mMainView;
    private FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder> adapter;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendList = mMainView.findViewById(R.id.friend_list_recyclerview_id);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_id);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(mFriendsDatabase, Friends.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder>(options) {
            @Override
            public FriendsFragment.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user_layout, parent, false);

                return new FriendsFragment.FriendsViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final FriendsFragment.FriendsViewHolder holder, final int position, final Friends friends) {
                final String list_user_id = getRef(position).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
//                        if(dataSnapshot.hasChild("online")){
//                            Boolean online= (Boolean) dataSnapshot.child("online").getValue();
//                            holder.setOnline(online);
//                        }
                        holder.setTextUserName(username);
                        holder.setTextStatus(status);

                        if (!thumb_image.equals("default")) {
                            Picasso.get().load(thumb_image).placeholder(R.mipmap.ic_launcher_round).into(holder.imageView);
                        }
                        holder.root.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Toast.makeText(Friends Activity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                                Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", list_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        };
        mFriendList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout root;
        public ImageView imageView,onlineImageView;
        public TextView textUserName;
        public TextView textStatus;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root_id);
            imageView = itemView.findViewById(R.id.circleImageView_id);
            textUserName = itemView.findViewById(R.id.user_name_id);
            textStatus = itemView.findViewById(R.id.status_id);
            onlineImageView=itemView.findViewById(R.id.online_imageView_id);
        }

        public void setTextUserName(String string) {
            textUserName.setText(string);
        }

        public void setTextStatus(String string) {
            textStatus.setText(string);
        }


        public void setOnline(Boolean online) {
            if(online==true){
                onlineImageView.setVisibility(View.VISIBLE);
            }else{
                onlineImageView.setVisibility(View.INVISIBLE);
            }
        }
    }
}

