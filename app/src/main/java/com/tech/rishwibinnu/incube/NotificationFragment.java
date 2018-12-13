package com.tech.rishwibinnu.incube;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.tech.rishwibinnu.incube.R.layout.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    RecyclerView eventlist;
    DatabaseReference eventref;
    FirebaseAuth auth;
    String userid;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(fragment_notification, container, false);
        auth=FirebaseAuth.getInstance();
        userid=auth.getCurrentUser().getUid();
        eventref=FirebaseDatabase.getInstance().getReference().child("Events").child(userid);

        eventlist=view.findViewById(R.id.event_list1);
        eventlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        eventlist.setLayoutManager(linearLayoutManager);


        validateeventlist();



        return view;
    }

    private void validateeventlist() {

        FirebaseRecyclerAdapter<Events,eventviewholder> firebaseRecyclerAdapterne=new FirebaseRecyclerAdapter<Events, eventviewholder>
                (
                        Events.class,R.layout.registered_events,eventviewholder.class,eventref
                ) {
            @Override
            protected void populateViewHolder(eventviewholder viewHolder, Events model, int position) {

                final String eventkey=getRef(position).getKey();

                viewHolder.setEvent(model.getEvent());

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(getContext(),ticket.class);
                        intent.putExtra("postkey",eventkey);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

            }
        };
        eventlist.setAdapter(firebaseRecyclerAdapterne);




    }




    public static  class eventviewholder extends RecyclerView.ViewHolder {
        View mview;
        public eventviewholder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
        }

        public void setEvent(String event){
            TextView ev=mview.findViewById(R.id.e6);
            ev.setText(event);
        }
    }


}
