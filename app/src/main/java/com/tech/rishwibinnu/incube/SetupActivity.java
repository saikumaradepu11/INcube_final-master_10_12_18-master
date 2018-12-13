package com.tech.rishwibinnu.incube;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName,setupht,setupyear,setupsection,setupbranch,setupmobile;
    private Button setupBtn;
    private ProgressBar setupProgress;

    private static final int gallery_pic=1;
    Bitmap crop_bitmap=null;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Boolean emailchecker=false;

    DatabaseReference reference;

    private Bitmap compressedImageFile=null;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("profiles");
        reference=FirebaseDatabase.getInstance().getReference().child("Users");
        reference.keepSynced(true);


        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupbranch = findViewById(R.id.setup_branch);
        setupyear = findViewById(R.id.setup_year);
        setupsection = findViewById(R.id.setup_sec);
        setupht = findViewById(R.id.setup_ht);
        setupmobile = findViewById(R.id.setup_mobile);
        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);

        pd=new ProgressDialog(this);

        reference.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("profile"))
                    {
                        isChanged=true;
                        final String setting_profile=dataSnapshot.child("profile").getValue().toString();

                        Picasso.get().load(setting_profile).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image).into(setupImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                                Picasso.get().load(setting_profile).placeholder(R.drawable.default_image).into(setupImage);
                            }
                        });

                    }
                    if(dataSnapshot.hasChild("name"))
                    {
                        String s1=dataSnapshot.child("name").getValue().toString();
                        setupName.setText(s1);
                    }
                    if(dataSnapshot.hasChild("htno"))
                    {
                        String s1=dataSnapshot.child("htno").getValue().toString();
                        setupht.setText(s1);
                    }
                    if(dataSnapshot.hasChild("branch"))
                    {
                        String s1=dataSnapshot.child("branch").getValue().toString();
                        setupbranch.setText(s1);
                    }
                    if(dataSnapshot.hasChild("section"))
                    {
                        String s1=dataSnapshot.child("section").getValue().toString();
                        setupsection.setText(s1);
                    }
                    if(dataSnapshot.hasChild("year"))
                    {
                        String s1=dataSnapshot.child("year").getValue().toString();
                        setupyear.setText(s1);
                    }
                    if(dataSnapshot.hasChild("mobile"))
                    {
                        String s1=dataSnapshot.child("mobile").getValue().toString();
                        setupmobile.setText(s1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString();
                final String user_ht=setupht.getText().toString();
                final String user_year=setupyear.getText().toString();
                final String user_sec=setupsection.getText().toString();
                final String user_branch=setupbranch.getText().toString();
                final String user_mobile=setupmobile.getText().toString();

                if(TextUtils.isEmpty(user_name))
                {
                    Toast.makeText(SetupActivity.this,"Please fill the Username",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(user_ht))
                {
                    Toast.makeText(SetupActivity.this,"Please fill the Hall tic",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(user_branch))
                {
                    Toast.makeText(SetupActivity.this,"Please fill the Branch",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(user_year))
                {
                    Toast.makeText(SetupActivity.this,"Please fill the year",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(user_sec))
                {
                    Toast.makeText(SetupActivity.this,"Please fill the Section",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(user_mobile))
                {
                    Toast.makeText(SetupActivity.this,"Please fill the Mobile Number",Toast.LENGTH_LONG).show();
                }

                else {
                    if(isChanged)
                    {
                        reference.child(user_id).child("name").setValue(user_name);
                        reference.child(user_id).child("htno").setValue(user_ht);
                        reference.child(user_id).child("branch").setValue(user_branch);
                        reference.child(user_id).child("year").setValue(user_year);
                        reference.child(user_id).child("section").setValue(user_sec);
                        reference.child(user_id).child("mobile").setValue(user_mobile);
                        Intent intent=new Intent(SetupActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
                        Toast.makeText(SetupActivity.this,"Please Select the Image",Toast.LENGTH_LONG).show();
                    }



                }


            }

        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(SetupActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    else {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(SetupActivity.this);
                    }
                }
                else {

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(SetupActivity.this);

                }

            }

        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallery_pic && resultCode==RESULT_OK && data!=null){
            Uri  imageuri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                pd.setTitle("Profile Updating");
                pd.setMessage("Please wait");
                pd.show();
                Uri resultUri = result.getUri();

                File crop_path=new File(resultUri.getPath());

                String userid=firebaseAuth.getCurrentUser().getUid();

                try {
                    compressedImageFile=new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200).setQuality(50).compressToBitmap(crop_path);
                }

                catch (IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

                final byte[] cropbyte=byteArrayOutputStream.toByteArray();


                StorageReference filepath=storageReference.child(userid+".jpg");

                final StorageReference thumb_filepath=storageReference.child(userid+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            String cropdownloaduri=task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask=thumb_filepath.putBytes(cropbyte);

                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task_thump) {

                                    String thumb_downloadurl=task_thump.getResult().getDownloadUrl().toString();

                                    if(task_thump.isSuccessful())
                                    {
                                        HashMap update=new HashMap();
                                        update.put("profile",thumb_downloadurl);
                                        String currentid=firebaseAuth.getCurrentUser().getUid();
                                        reference.child(currentid).updateChildren(update).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                Toast.makeText(SetupActivity.this,"Image saved",Toast.LENGTH_LONG).show();
                                                pd.dismiss();
                                            }
                                        });

                                    }

                                }
                            });

                        }
                        else {
                            Toast.makeText(SetupActivity.this,"error occured",Toast.LENGTH_LONG).show();
                            pd.dismiss();

                        }

                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



}

