package com.tech.rishwibinnu.incube;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class splash_activity extends AppCompatActivity {

    ImageView imageview1;
    TextView textView,textview1,imageView;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activity);

        imageView=findViewById(R.id.sl1);
        textView=findViewById(R.id.sl2);
        imageview1=findViewById(R.id.s10);
        textview1=findViewById(R.id.s15);

        animation=AnimationUtils.loadAnimation(splash_activity.this,R.anim.push_down);
        imageview1.setAnimation(animation);

        animation=AnimationUtils.loadAnimation(splash_activity.this,R.anim.push_up);
        textView.setAnimation(animation);

        animation=AnimationUtils.loadAnimation(splash_activity.this,R.anim.push_fromleft);
        textview1.setAnimation(animation);

        animation=AnimationUtils.loadAnimation(splash_activity.this,R.anim.push_fromright);
        imageView.setAnimation(animation);


        Thread thread=new Thread()
        {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Intent intent=new Intent(splash_activity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }
}
