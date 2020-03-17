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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mFriendDatabase;
    private String current_user_id;
    private RecyclerView mFriendList;

    private View mMainView;
    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView= inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendList=mMainView.findViewById(R.id.friend_list_recyclerview_id);
        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        mFriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_id);

        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends").child(current_user_id);

        FirebaseRecyclerOptions<Friends > options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, new SnapshotParser<Friends >() {
                            @NonNull
                            @Override
                            public Friends  parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Friends (snapshot.child("name").getValue().toString(),
                                        snapshot.child("status").getValue().toString(),
                                        snapshot.child("image").getValue().toString(),
                                        snapshot.child("thumb_image").getValue().toString());
                            }
                        })
                        .build();

        FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder>(options) {
            @Override
            public FriendsFragment.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user_layout, parent, false);

                return new FriendsFragment.FriendsViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(FriendsFragment.FriendsViewHolder holder, final int position, Friends friends) {
                holder.setImageView(friends.getThumb_image(),getContext());
                holder.setTextUserName(friends.getUserName());
                holder.setTextStatus(friends.getStatus());
                final String user_id=getRef(position).getKey();
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(Friends Activity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                        Intent profileIntent=new Intent(getActivity(), ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);
                    }
                });
            }

        };
        mFriendList.setAdapter(adapter);

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout root;
        public ImageView imageView;
        public TextView textUserName;
        public TextView textStatus;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root_id);
            imageView=itemView.findViewById(R.id.circleImageView_id);
            textUserName = itemView.findViewById(R.id.user_name_id);
            textStatus = itemView.findViewById(R.id.status_id);
        }

        public void setImageView(String string, Context context){
            Glide.with(context).load(string).into(imageView);
        }
        public void setTextUserName(String string) {
            textUserName.setText(string);
        }


        public void setTextStatus(String string) {
            textStatus.setText(string);
        }
    }
}

