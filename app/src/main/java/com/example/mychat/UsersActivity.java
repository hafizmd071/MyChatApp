package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UsersActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Users, ViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        recyclerView=findViewById(R.id.users_recyclerview_id);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar=findViewById(R.id.users_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All Users");
        fetch();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("users");

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, new SnapshotParser<Users>() {
                            @NonNull
                            @Override
                            public Users parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Users(snapshot.child("name").getValue().toString(),
                                        snapshot.child("status").getValue().toString(),
                                        snapshot.child("image").getValue().toString());
                            }
                        })
                        .build();

         adapter = new FirebaseRecyclerAdapter<Users, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user_layout, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, Users users) {
                holder.setImageView(users.getImage());
                holder.setTextUserName(users.getUserName());
                holder.setTextStatus(users.getStatus());

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(UsersActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public ImageView imageView;
        public TextView textUserName;
        public TextView textStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root_id);
            imageView=itemView.findViewById(R.id.circleImageView_id);
            textUserName = itemView.findViewById(R.id.user_name_id);
            textStatus = itemView.findViewById(R.id.status_id);
        }

        public void setImageView(String string){
            //imageView.setImage
        }
        public void setTextUserName(String string) {
            textUserName.setText(string);
        }


        public void setTextStatus(String string) {
            textStatus.setText(string);
        }
    }

}
