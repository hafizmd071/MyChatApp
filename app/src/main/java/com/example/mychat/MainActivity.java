package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        tabLayout=findViewById(R.id.tablayoutid);
        viewPager=findViewById(R.id.viewpagerid);
        toolbar=findViewById(R.id.main_activity_toolbar);
        sectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("MyChat");
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        mUserRef=FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent i=new Intent(MainActivity.this,StartActivity.class);
            startActivity(i);
            finish();
        }else{
            mUserRef.child("online").setValue(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserRef.child("online").setValue(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.logoutid){
            FirebaseAuth.getInstance().signOut();
            Intent i=new Intent(MainActivity.this,StartActivity.class);
            startActivity(i);
            finish();
        }
        else if(item.getItemId()==R.id.accountsettingsid){
            Intent i=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(i);
            finish();
        }
        else if(item.getItemId()==R.id.all_users_id){
            Intent i=new Intent(MainActivity.this,UsersActivity.class);
            startActivity(i);
            finish();
        }
        return true;
    }
}
