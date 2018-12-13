package com.tech.rishwibinnu.incube;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.cardemulation.OffHostApduService;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;

    private ImageView newPostImage;
    private EditText newPostDesc,neweventname;
    private Button newPostBtn;

    private Uri postImageUri = null;

    String postdate,posttime,postrandomname,downloadurl;
    DatabaseReference useref,postref;
    String profileimage,username;
    String event;

    private ProgressBar newPostProgress;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;
    String userid;
    String thumb_downloadurl;

    private Bitmap compressedImageFile=null;

    long countpost=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        useref=FirebaseDatabase.getInstance().getReference().child("Users");
        useref.keepSynced(true);
        postref=FirebaseDatabase.getInstance().getReference().child("Posts");
        postref.keepSynced(true);

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        neweventname = findViewById(R.id.new_post_eventname);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress = findViewById(R.id.new_post_progress);

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);

            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String disc=newPostDesc.getText().toString();
                event=neweventname.getText().toString();
                if(TextUtils.isEmpty(event))
                {
                    Toast.makeText(NewPostActivity.this,"Plaese enter the Event Name",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(disc))
                {
                    Toast.makeText(NewPostActivity.this,"Plaese fill the Discription of the Post",Toast.LENGTH_LONG).show();
                }
                else if(postImageUri==null)
                {
                    Toast.makeText(NewPostActivity.this,"Plaese Select the Image to Post",Toast.LENGTH_LONG).show();
                }
                else {

                    newPostProgress.setVisibility(View.VISIBLE);

                    Calendar calendardate=Calendar.getInstance();
                    SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
                    postdate=currentdate.format(calendardate.getTime());

                    Calendar calendartime=Calendar.getInstance();
                    SimpleDateFormat currenttime=new SimpleDateFormat("hh:mm a");
                    posttime=currenttime.format(calendartime.getTime());

                    postrandomname=postdate+posttime;


                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

                    final byte[] cropbyte=byteArrayOutputStream.toByteArray();

                    StorageReference filepath=storageReference.child("postimages").child(userid+".jpg");

                    final StorageReference thumb_filepath=storageReference.child("postimages").child(userid+".jpg");

                    filepath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                UploadTask uploadTask=thumb_filepath.putBytes(cropbyte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task_thump) {

                                        thumb_downloadurl=task_thump.getResult().getDownloadUrl().toString();

                                        if(task_thump.isSuccessful())
                                        {
                                            savingpostinfo();

                                        }

                                    }
                                });


                            }

                        }
                    });


                }
            }
        });


    }

    private void savingpostinfo() {

        postref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    countpost=dataSnapshot.getChildrenCount();
                }
                else
                {
                    countpost=0;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        useref.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("name"))
                    {
                        username=dataSnapshot.child("name").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("profile"))
                    {
                        profileimage=dataSnapshot.child("profile").getValue().toString();
                    }

                    String description=newPostDesc.getText().toString();

                    String counterpost=Long.toString(countpost);

                    HashMap postmap=new HashMap();
                    postmap.put("uid",current_user_id);
                    postmap.put("date",postdate);
                    postmap.put("time",posttime);
                    postmap.put("event",event);
                    postmap.put("discription",description);
                    postmap.put("postimage",thumb_downloadurl);
                    postmap.put("profileimage",profileimage);
                    postmap.put("fullname",username);
                    postmap.put("counter",counterpost);
                    postmap.put("count","0");


                    postref.child(current_user_id+postrandomname).updateChildren(postmap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                newPostProgress.setVisibility(View.INVISIBLE);
                                sendtomainactivity();
                            }
                            else
                            {
                                newPostProgress.setVisibility(View.INVISIBLE);
                                String msg=task.getException().toString();
                                Toast.makeText(NewPostActivity.this,"Error Occured :"+msg,Toast.LENGTH_LONG).show();
                            }

                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void sendtomainactivity() {

        Intent intent=new Intent(NewPostActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                Picasso.get().load(postImageUri).networkPolicy(NetworkPolicy.OFFLINE).into(newPostImage);

                File crop_path=new File(postImageUri.getPath());

                userid=firebaseAuth.getCurrentUser().getUid();

                try {
                    compressedImageFile=new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200).setQuality(50).compressToBitmap(crop_path);
                }

                catch (IOException e)
                {
                    e.printStackTrace();
                }






            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();


            }
        }

    }

}