package com.tech.rishwibinnu.incube;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentToolbar;

    private EditText comment_field;
    private ImageView comment_post_btn;

    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String blog_post_id;
    private String current_user_id;
    String username,userprofile;

    DatabaseReference userref,postref;

    String postkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postkey=getIntent().getExtras().get("postkey").toString();

        commentToolbar = findViewById(R.id.comment_toolbar1);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comments");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userref=FirebaseDatabase.getInstance().getReference().child("Users");
        userref.keepSynced(true);
        postref=FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey).child("Comments");
        postref.keepSynced(true);

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        blog_post_id = getIntent().getStringExtra("blog_post_id");

        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);

        comment_list = findViewById(R.id.comment_list);
        comment_list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CommentsActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        comment_list.setLayoutManager(linearLayoutManager);

        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userref.child(current_user_id).addValueEventListener(new ValueEventListener() {
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
                                userprofile=dataSnapshot.child("profile").getValue().toString();
                            }
                            validatecomment(username,userprofile);

                            comment_field.setText("");

                            comment_field.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });




    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Comments,commentviewholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Comments, commentviewholder>
                (
                        Comments.class,
                        R.layout.common_list_item,
                        commentviewholder.class,
                        postref
                ) {
            @Override
            protected void populateViewHolder(commentviewholder viewHolder, Comments model, int position) {

                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setProfile(model.getProfile());
                viewHolder.setTime(model.getTime());
                viewHolder.setUsername(model.getUsername());

            }
        };

        comment_list.setAdapter(firebaseRecyclerAdapter);
    }

    public static class commentviewholder extends RecyclerView.ViewHolder {

        View mview;
        public commentviewholder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
        }

        public void setUsername(String username){
            TextView user=mview.findViewById(R.id.comment_username);
            user.setText(username);
        }
        public void setProfile(final String profile) {
            final CircleImageView image=mview.findViewById(R.id.comment_image);


            Picasso.get().load(profile).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.image_placeholder)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profile).placeholder(R.drawable.image_placeholder).into(image);

                        }
                    });
        }
        public void setComment(String comment)
        {
            TextView comment1=mview.findViewById(R.id.comment_message);
            comment1.setText(comment);

        }
        public void setDate(String date)
        {
            TextView date1=mview.findViewById(R.id.comment_date);
            date1.setText(date);
        }
        public void setTime(String time) {
            TextView time1=mview.findViewById(R.id.comment_time);
            time1.setText( " at "+time);
        }
    }

    private void validatecomment(String s, String username) {
        String commnetmsg=comment_field.getText().toString();
        if(TextUtils.isEmpty(commnetmsg))
        {
            Toast.makeText(CommentsActivity.this,"Please write the Comment",Toast.LENGTH_LONG).show();
        }
        else
        {
            Calendar calendardate=Calendar.getInstance();
            SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
            final String postdate=currentdate.format(calendardate.getTime());

            Calendar calendartime=Calendar.getInstance();
            SimpleDateFormat currenttime=new SimpleDateFormat("hh:mm a");
            final String posttime=currenttime.format(calendartime.getTime());

            final String randomkey=current_user_id+postdate+posttime;

            HashMap commentmap=new HashMap();
            commentmap.put("uid",current_user_id);
            commentmap.put("comment",commnetmsg);
            commentmap.put("date",postdate);
            commentmap.put("time",posttime);
            commentmap.put("username",s);
            commentmap.put("profile",username);

            postref.child(randomkey).updateChildren(commentmap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(CommentsActivity.this,"Commented",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String msg=task.getException().toString();
                        Toast.makeText(CommentsActivity.this,"Error Occured :"+msg ,Toast.LENGTH_LONG).show();
                    }
                }
            });



        }
    }
}
