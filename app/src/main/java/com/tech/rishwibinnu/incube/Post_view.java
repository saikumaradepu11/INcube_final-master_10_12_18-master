package com.tech.rishwibinnu.incube;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Post_view extends AppCompatActivity {

    ImageView postimage;
    TextView title,desc;
    Button edit,regster,delete,list;

    DatabaseReference postref,regref,regsturef,userref,registerrref,reference;

    FirebaseAuth mauth;

    String postkey;

    String eventname,discription,postimage1,databaseuseid,username,htno,mobile,email;
    String currentuserid;

    boolean checker=true;
    boolean regchecker=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        mauth=FirebaseAuth.getInstance();
        currentuserid=mauth.getCurrentUser().getUid();

        postkey=getIntent().getExtras().get("postkey").toString();

        FirebaseUser user=mauth.getCurrentUser();
        email=user.getEmail();

        postref=FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey);
        postref.keepSynced(true);
        regref=FirebaseDatabase.getInstance().getReference().child("Register").child(postkey);
        regref.keepSynced(true);
        regsturef=FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey).child("Register");
        regsturef.keepSynced(true);
        userref=FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserid);
        userref.keepSynced(true);
        registerrref=FirebaseDatabase.getInstance().getReference().child("Events").child(currentuserid);
        registerrref.keepSynced(true);
        reference=FirebaseDatabase.getInstance().getReference().child("Events");
        reference.keepSynced(true);

        title=findViewById(R.id.post_eventtitle);
        desc=findViewById(R.id.post_description);

        postimage=findViewById(R.id.post_img);

        edit=findViewById(R.id.post_edit);
        regster=findViewById(R.id.post_register);
        delete=findViewById(R.id.post_delete);
        list=findViewById(R.id.list_of_students);

        final String mail=mauth.getCurrentUser().getEmail();
        final String email="incubeagi@gmail.com";
        if(mail.equals(email))
        {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            list.setVisibility(View.VISIBLE);
        }



        postref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("event"))
                    {
                        eventname=dataSnapshot.child("event").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("discription"))
                    {
                        discription=dataSnapshot.child("discription").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("postimage"))
                    {
                       postimage1=dataSnapshot.child("postimage").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("uid"))
                    {
                        databaseuseid=dataSnapshot.child("uid").getValue().toString();
                    }

                    title.setText(eventname);
                    desc.setText(discription);
                    Picasso.get().load(postimage1).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.image_placeholder)
                            .into(postimage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(postimage1).placeholder(R.drawable.image_placeholder).into(postimage);
                                }
                            });

                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editposts(eventname,discription);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postref.removeValue();
                Intent intent=new Intent(Post_view.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                Toast.makeText(Post_view.this,"Post Deleted",Toast.LENGTH_LONG).show();
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(Post_view.this,student_list.class);
                intent1.putExtra("postkey",postkey);
                startActivity(intent1);
            }
        });

        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("name"))
                    {
                        username=dataSnapshot.child("name").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("htno"))
                    {
                        htno=dataSnapshot.child("htno").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("mobile"))
                    {
                        mobile=dataSnapshot.child("mobile").getValue().toString();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        regster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(currentuserid))
                        {
                            Intent intent=new Intent(Post_view.this,ticket.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("postkey",postkey);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            AlertDialog.Builder builder=new AlertDialog.Builder(Post_view.this);
                            builder.setTitle("Event Registration");
                            builder.setMessage("Do you Want to Register in this Event");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    regref.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            regref.child(currentuserid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        HashMap commentmap=new HashMap();
                                                        commentmap.put("name",username);
                                                        commentmap.put("htno",htno);


                                                        regsturef.child(currentuserid).updateChildren(commentmap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {

                                                                if(task.isSuccessful())
                                                                {
                                                                    Intent intent=new Intent(Post_view.this,ticket.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    intent.putExtra("postkey",postkey);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else
                                                                {
                                                                    String msg=task.getException().toString();
                                                                    Toast.makeText(Post_view.this,"Error Occured :"+msg,Toast.LENGTH_LONG).show();
                                                                }

                                                            }
                                                        });


                                                        HashMap hashMap=new HashMap();
                                                        hashMap.put("event",eventname);
                                                        hashMap.put("postimage",postimage1);
                                                        registerrref.child(postkey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {

                                                            }
                                                        });

                                                        HashMap map=new HashMap();
                                                        map.put("name",username);
                                                        map.put("htno",htno);
                                                        map.put("mobile",mobile);
                                                        map.put("email",email);
                                                        reference.child(postkey).child(currentuserid).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {

                                                            }
                                                        });

                                                    }

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            Dialog dialog=builder.create();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


    }

    private void editposts(String eventname, String discription) {

        AlertDialog.Builder builder=new AlertDialog.Builder(Post_view.this);
        builder.setTitle("Edit Post");

        final EditText input2=new EditText(Post_view.this);
        input2.setText(discription);
        builder.setView(input2);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                postref.child("discription").setValue(input2.getText().toString());

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        Dialog dialog=builder.create();
        dialog.show();
    }
}
