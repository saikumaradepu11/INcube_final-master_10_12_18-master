package com.tech.rishwibinnu.incube;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    FirebaseAuth auth;
    TextView t1,t2,t3,t4,t5,t6;
    CircleImageView c1;
    DatabaseReference ref;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_account, container, false);

        auth=FirebaseAuth.getInstance();
        ref=FirebaseDatabase.getInstance().getReference().child("Users");
        ref.keepSynced(true);
        String userid=auth.getCurrentUser().getUid();

        t1=(TextView)rootView.findViewById(R.id.acc1);
        t2=(TextView)rootView.findViewById(R.id.acc2);
        t3=(TextView)rootView.findViewById(R.id.acc3);
        t4=(TextView)rootView.findViewById(R.id.acc4);
        t5=(TextView)rootView.findViewById(R.id.acc5);
        t6=(TextView)rootView.findViewById(R.id.acc7);

        c1=(CircleImageView)rootView.findViewById(R.id.acc6);

        ref.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("name"))
                    {
                        String name=dataSnapshot.child("name").getValue().toString();
                        t1.setText(name);
                    }
                    if(dataSnapshot.hasChild("htno"))
                    {
                        String name=dataSnapshot.child("htno").getValue().toString();
                        t2.setText(name);
                    }
                    if(dataSnapshot.hasChild("branch"))
                    {
                        String name=dataSnapshot.child("branch").getValue().toString();
                        t3.setText(name);
                    }
                    if(dataSnapshot.hasChild("section"))
                    {
                        String name=dataSnapshot.child("section").getValue().toString();
                        t4.setText(name);
                    }
                    if(dataSnapshot.hasChild("year"))
                    {
                        String name=dataSnapshot.child("year").getValue().toString();
                        t5.setText(name);
                    }
                    if(dataSnapshot.hasChild("mobile"))
                    {
                        String name=dataSnapshot.child("mobile").getValue().toString();
                        t6.setText(name);
                    }
                    if(dataSnapshot.hasChild("profile"))
                    {
                        final String setting_profile=dataSnapshot.child("profile").getValue().toString();
                        Picasso.get().load(setting_profile).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image).into(c1, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                                Picasso.get().load(setting_profile).placeholder(R.drawable.default_image).into(c1);
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return rootView;
    }

}
