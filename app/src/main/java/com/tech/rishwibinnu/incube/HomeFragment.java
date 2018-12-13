package com.tech.rishwibinnu.incube;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private BlogRecyclerAdapter blogRecyclerAdapter;

    DatabaseReference postref,likeref;
    String currentid;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    Boolean likechecker=false;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        blog_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.blog_list_view);

        firebaseAuth = FirebaseAuth.getInstance();
        currentid=firebaseAuth.getCurrentUser().getUid();
        postref = FirebaseDatabase.getInstance().getReference().child("Posts");
        postref.keepSynced(true);
        likeref=FirebaseDatabase.getInstance().getReference().child("Likes");
        likeref.keepSynced(true);


        blog_list_view = view.findViewById(R.id.blog_list_view);
        blog_list_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        blog_list_view.setLayoutManager(linearLayoutManager);

        Displayposts();

        return view;
    }

    private void Displayposts() {

        com.google.firebase.database.Query sort=postref.orderByChild("counter");

        FirebaseRecyclerAdapter<Posts, postsViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, postsViewholder>
                (
                        Posts.class,
                        R.layout.blog_list_item,
                        postsViewholder.class,
                        sort
                ) {
            @Override
            protected void populateViewHolder(postsViewholder viewHolder, Posts model, final int position) {

                final String postkey=getRef(position).getKey();

                viewHolder.setDate(model.getDate());
                viewHolder.setFullname(model.getFullname());
                viewHolder.setPostimage(model.getPostimage());
                viewHolder.setProfileimage(model.getProfileimage());
                viewHolder.setTime(model.getTime());
                viewHolder.setDiscription(model.getDiscription());
                viewHolder.setEvent(model.getEvent());
                viewHolder.setCount(model.getCount());

                viewHolder.setstatusoflikes(postkey);

                viewHolder.setregstudents(postkey);

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent postview=new Intent(getContext(),Post_view.class);
                        postview.putExtra("postkey",postkey);
                        startActivity(postview);
                    }
                });

                viewHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent coomenti1=new Intent(getContext(),CommentsActivity.class);
                        coomenti1.putExtra("postkey",postkey);
                        startActivity(coomenti1);

                    }
                });

                viewHolder.like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likechecker=true;

                        likeref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(likechecker.equals(true))
                                {
                                    if(dataSnapshot.child(postkey).hasChild(currentid))
                                    {
                                        likeref.child(postkey).child(currentid).removeValue();
                                        likechecker=false;

                                    }
                                    else
                                    {
                                        likeref.child(postkey).child(currentid).setValue(true);
                                        likechecker=false;
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };

        blog_list_view.setAdapter(firebaseRecyclerAdapter);


    }

    public static class postsViewholder extends RecyclerView.ViewHolder {

        View mview;
        ImageView comment,like_button;
        TextView comment_text,like_text,reg;
        int countlikes,regstudents;
        DatabaseReference likeref,regref;
        String currentuserid;

        public postsViewholder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;

            comment=mview.findViewById(R.id.blog_comment_icon);
            comment_text=mview.findViewById(R.id.blog_comment_count);
            like_button=mview.findViewById(R.id.blog_like_btn);
            like_text=mview.findViewById(R.id.blog_like_count);
            reg=mview.findViewById(R.id.blog_eventregister);

            likeref=FirebaseDatabase.getInstance().getReference().child("Likes");
            currentuserid=FirebaseAuth.getInstance().getCurrentUser().getUid();
            regref=FirebaseDatabase.getInstance().getReference().child("Register");

        }

        public void setFullname(String fullname) {
            TextView username = mview.findViewById(R.id.blog_user_name);
            username.setText(fullname);
        }

        public void setProfileimage(final String profileimage) {
            final CircleImageView profile = mview.findViewById(R.id.blog_user_image);
            Picasso.get().load(profileimage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.image_placeholder)
                    .into(profile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profileimage).placeholder(R.drawable.image_placeholder).into(profile);
                        }
                    });

        }

        public void setTime(String time) {
            TextView time1 = mview.findViewById(R.id.blog_time);
            time1.setText("   at " + time);
        }

        public void setDate(String date) {
            TextView date1 = mview.findViewById(R.id.blog_date);
            date1.setText("Posted on  " + date);
        }

        public void setPostimage(final String postimage) {
            final ImageView profile = mview.findViewById(R.id.blog_image);
            Picasso.get().load(postimage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.image_placeholder)
                    .into(profile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(postimage).placeholder(R.drawable.image_placeholder).into(profile);

                        }
                    });

        }

        public void setDiscription(String discription) {

            TextView desc = mview.findViewById(R.id.blog_desc);
            desc.setText(discription);

        }

        public void setEvent(String event) {
            TextView event1 = mview.findViewById(R.id.blog_eventname);
            event1.setText(event);

        }

        public void setCount(String count) {
            TextView evennt2 = mview.findViewById(R.id.blog_eventregister);
            evennt2.setText(count);
        }


        public void setstatusoflikes(final String postkey) {

            likeref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        if(dataSnapshot.child(postkey).hasChild(currentuserid))
                        {
                            countlikes=(int)dataSnapshot.child(postkey).getChildrenCount();
                            like_button.setImageResource(R.mipmap.action_like_accent);
                            like_text.setText(Integer.toString(countlikes)+"  Likes");
                        }
                        else
                        {
                            countlikes=(int)dataSnapshot.child(postkey).getChildrenCount();
                            like_button.setImageResource(R.mipmap.action_like_gray);
                            like_text.setText(Integer.toString(countlikes)+"  Likes");

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setregstudents(final String postkey)
        {
            regref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                    {
                        if(dataSnapshot.hasChild(postkey))
                        {
                            regstudents=(int)dataSnapshot.child(postkey).getChildrenCount();
                            reg.setText(Integer.toString(regstudents));
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
