package com.example.pranav.cgpitmaterials;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashActivity extends AppCompatActivity {

    private static int TIME_OUT = 1800;
    CircleImageView splashImage;
    TextView splashText;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashImage = findViewById(R.id.splash_image);
        splashText = findViewById(R.id.splash_text);

        if(!(getWindow().getWindowManager().getDefaultDisplay().getHeight()<=2000))
            splashText.animate().translationY(500f).setDuration(1200);
        else
            splashText.animate().translationY(250f).setDuration(1200);

        splashImage.animate().rotationX(360f).translationY(-450f).setDuration(1500);
        splashImage.animate().rotationX(360f).translationY(100f).setDuration(1500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent;
                preferences = getSharedPreferences("PrefFile", 0);
                String usernamePref = preferences.getString("username",null);
                String identityPref=preferences.getString("identity",null);
                if (usernamePref == null && identityPref == null) {
                    homeIntent=new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(homeIntent);
                    finish();

                } else {
                    if(preferences.getString("identity","").equals("Student")){
                       homeIntent=new Intent(SplashActivity.this,StudentMain.class);
                        startActivity(homeIntent);
                        finish();
                    }
                    else if(preferences.getString("identity","").equals("Faculty")){
                        homeIntent=new Intent(SplashActivity.this,FacultyMain.class);
                        startActivity(homeIntent);
                        finish();
                    }
                    else{
                        Log.e("Tag","Unsuccessfull SP. Identity is:"+preferences.getString("identity",""));
                    }
                }

            }
        }, TIME_OUT);
    }
}

