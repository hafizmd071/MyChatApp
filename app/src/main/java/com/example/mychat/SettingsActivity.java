package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private static final int MAX_LENGTH =10;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private ImageView profileImage;
    private TextView displaName,statusText;
    private Button changeImageButton,changeStatusButton;
    private StorageReference mStorageRef;
    private String userId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressBar=findViewById(R.id.imageloader_progressbar_id);
        progressBar.setVisibility(View.GONE);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        profileImage=findViewById(R.id.profile_image_id);
        displaName=findViewById(R.id.display_name_id);
        statusText=findViewById(R.id.user_status_id);
        changeImageButton=findViewById(R.id.change_image_button_id);
        changeStatusButton=findViewById(R.id.change_status_button_id);
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        userId=currentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String status =dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                String thumb_image =dataSnapshot.child("thumb_image").getValue().toString();
                StorageReference storageReference=mStorageRef.child("images").child("thumbs").child(thumb_image);

                displaName.setText(name);
                statusText.setText(status);
                if(!thumb_image.equals("default")){
                   // Picasso.get().load(image).into(profileImage);
                    // Picasso.get().load(thumb_image).into(profileImage);
                    Glide.with(SettingsActivity.this)
                            .load(storageReference)
                            .into(profileImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status=statusText.getText().toString();
                Intent i=new Intent(SettingsActivity.this, StatusActivity.class);
                i.putExtra("status",status);
                startActivity(i);
            }
        });
        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File thumb_filepath = new File(resultUri.getPath());
                Bitmap compressedImage = null;
                try {
                    compressedImage = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] thumb_byte = baos.toByteArray();
            progressBar.setVisibility(View.VISIBLE);
            StorageReference imageStorageRef = mStorageRef.child("images").child(userId + ".jpg");
            final StorageReference thumb_path = mStorageRef.child("images").child("thumbs").child(userId + ".jpg");

            imageStorageRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        final String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                        UploadTask uploadTask = thumb_path.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                        final String thumb_downloadUrl=thumb_task.getResult().getStorage().getDownloadUrl().toString();
                                        if(thumb_task.isSuccessful()){
                                            Map<String,Object> hashMap= new HashMap<>();
                                            hashMap.put("image",downloadUrl);
                                            hashMap.put("thumb_image",thumb_downloadUrl);
                                            mDatabase.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(SettingsActivity.this, "Added to Storage.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{
                                            Toast.makeText(SettingsActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            });
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SettingsActivity.this,"Error",Toast.LENGTH_SHORT).show();

            }
        }
    }
}

