package com.example.twinster.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference myUserDatabase;
    private FirebaseUser currentUser;
    private CircleImageView displayImage;
    private TextView tvUserName;
    private static final int gallery_pick = 1;
    private StorageReference myImageStorage;
    private ProgressDialog myProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        tvUserName = findViewById(R.id.tvUserName);
        displayImage = findViewById(R.id.profileImg);

        myImageStorage = FirebaseStorage.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String  currentUserId = currentUser.getUid();
        myUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        myUserDatabase.keepSynced(true);

        myUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb = dataSnapshot.child("thumb_image").getValue().toString();

                tvUserName.setText(name);
                if (!image.equals("default")){

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                            placeholder(R.drawable.defaultpic).into(displayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.defaultpic).into(displayImage);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void changeImage(View view) {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.select_image)), gallery_pick);
//        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(SettingsActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallery_pick && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                myProgressDialog = new ProgressDialog(SettingsActivity.this);
                myProgressDialog.setTitle(getString(R.string.uploading_image));
                myProgressDialog.setMessage(getString(R.string.wait_while_uploading_image));
                myProgressDialog.setCanceledOnTouchOutside(false);
                myProgressDialog.show();


                Uri resultUri = result.getUri();

                File thumb_file_path = new File(resultUri.getPath());

                String currentUserId = currentUser.getUid();

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_file_path);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                final byte[] byte_thumb = baos.toByteArray();


                StorageReference filePath = myImageStorage.child("profile_images").child(currentUserId + ".jpg");

                final StorageReference thumbnail_file_path = myImageStorage.child("profile_images").child("thumbs").child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            final UploadTask upTask = thumbnail_file_path.putBytes(byte_thumb);
                            upTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_download_url = thumb_task.getResult().getDownloadUrl().toString();


                                    if (thumb_task.isSuccessful()){

                                        Map updateHash = new HashMap();
                                        updateHash.put("image", downloadUrl);
                                        updateHash.put("thumb_image", thumb_download_url);


                                        myUserDatabase.updateChildren(updateHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    myProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, R.string.image_uploaded_successfully, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        myProgressDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, R.string.error_while_upploading, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            myProgressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "error while uploading", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public static String randomImageName(){
        Random generator = new Random();
        StringBuilder randomBilder = new StringBuilder();
        int randomLegth = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLegth; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomBilder.append(tempChar);
        }
        return randomBilder.toString();
    }
}
