package com.tech.rishwibinnu.incube;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ticket extends AppCompatActivity {

    TextView event,username,htno;
    ImageView postimage;
    String postkey;

    DatabaseReference reference;
    FirebaseAuth auth;
    String currentid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        auth=FirebaseAuth.getInstance();
        currentid=auth.getCurrentUser().getUid();

        postkey=getIntent().getExtras().get("postkey").toString();

        reference=FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey);

        event=(TextView)findViewById(R.id.e1);
        username=(TextView)findViewById(R.id.e2);
        htno=(TextView)findViewById(R.id.e3);

        postimage=(ImageView)findViewById(R.id.e4);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("event"))
                    {
                        String t1=dataSnapshot.child("event").getValue().toString();
                        event.setText(t1);
                    }
                    if(dataSnapshot.hasChild("postimage"))
                    {
                        final String t1=dataSnapshot.child("postimage").getValue().toString();
                        Picasso.get().load(t1).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.image_placeholder)
                                .into(postimage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(t1).placeholder(R.drawable.image_placeholder).into(postimage);
                                    }
                                });

                    }
                    if(dataSnapshot.hasChild("Register"))
                    {
                        if(dataSnapshot.child("Register").hasChild(currentid))
                        {
                            if(dataSnapshot.child("Register").child(currentid).hasChild("name"))
                            {
                                String t1=dataSnapshot.child("Register").child(currentid).child("name").getValue().toString();
                                username.setText(t1);

                            }
                        }

                    }
                    if(dataSnapshot.hasChild("Register"))
                    {
                        if(dataSnapshot.child("Register").hasChild(currentid))
                        {
                            if(dataSnapshot.child("Register").child(currentid).hasChild("htno"))
                            {
                                String t1=dataSnapshot.child("Register").child(currentid).child("htno").getValue().toString();
                                htno.setText(t1);

                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(ticket.this,MainActivity.class);
        startActivity(intent);
    }
}
