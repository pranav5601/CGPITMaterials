package com.example.pranav.cgpitmaterials;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Image_Viewer extends AppCompatActivity {

    RelativeLayout mainLayout;
    ImageView image1,back_button;
    PhotoView image;
    LinearLayout topLayout;
    TextView name,time;
    Boolean topVisible=true;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image__viewer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        Bundle bundle=getIntent().getExtras();
        String name_st=bundle.getString("name");
        String time_st=bundle.getString("time");
        String image_st=bundle.getString("image");

        topLayout=(LinearLayout)findViewById(R.id.top_layout_image_viewer);
        mainLayout=(RelativeLayout)findViewById(R.id.main_layout);
        image=(PhotoView)findViewById(R.id.main_image);
        back_button=(ImageView)findViewById(R.id.back_btn_image_viewer);
        name=(TextView)findViewById(R.id.faculty_name_image_viewer);
        time=(TextView)findViewById(R.id.time_stamp_image_viewer);

        name.setText(name_st);
        time.setText(time_st);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Picasso.get().setIndicatorsEnabled(false);
        Picasso.get().load(image_st).placeholder(R.drawable.default_iamge).into(image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(topVisible){
                    //topLayout.setVisibility(View.GONE);
                    topLayout.animate().alpha(0.0f);
                    topVisible=false;
                }
                else {
                    //topLayout.setVisibility(View.VISIBLE);
                    topLayout.animate().alpha(1.0f);
                    topVisible=true;
                }
            }
        });
    }
}
