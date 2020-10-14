package com.example.attendancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class Splash extends AppCompatActivity
{

    Handler h;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView txt = (TextView)findViewById(R.id.textView11);
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.sample_anim);
        txt.startAnimation(animation);

        h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                // startActivity(new Intent(this,firstFragment.class));
                Intent i= new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        },3000);
    }
}