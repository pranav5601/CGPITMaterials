package com.example.pranav.cgpitmaterials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Display display;
    int height, width, h70, h30, w20, h20, w5, h5, h50;
    LinearLayout whiteLinearLayout;
    FrameLayout frameLayout;
    EditText edt_username, edt_password;
    TextView login_text;
    ImageView logo;
    Button loginButton;
    RadioGroup radioGroup;
    FirebaseAuth mAuth;
    User user;
    FirebaseDatabase mDatabase;
    DatabaseReference studentRef;
    ProgressDialog progressDialog;
    SharedPreferences pref,preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculate();
        getView();
        setPadding();
        setListener();
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    public void calculate() {

        display = getWindow().getWindowManager().getDefaultDisplay();
        height = display.getHeight();
        width = display.getWidth();

        //320x480
        h70 = height * 70 / 480;
        h30 = height * 30 / 480;
        h20 = height * 20 / 480;
        h5 = height * 5 / 480;
        h50 = height * 50 / 480;

        w20 = width * 20 / 320;
        w5 = width * 5 / 320;

    }

    public void getView() {
        whiteLinearLayout = (LinearLayout) findViewById(R.id.white_linear_layout);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        edt_username = (EditText) findViewById(R.id.editText);
        edt_password = (EditText) findViewById(R.id.editText2);
        login_text = (TextView) findViewById(R.id.login_text);
        logo = (ImageView) findViewById(R.id.logo);
        loginButton = (Button) findViewById(R.id.button);
        radioGroup = (RadioGroup) findViewById(R.id.rbg_login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        user = new User();
        progressDialog = new ProgressDialog(MainActivity.this);


    }

    public void setPadding() {
        frameLayout.setPadding(w20, 0, w20, h70 / 2);
        whiteLinearLayout.setPadding(w20, h20, w20, h20);
        edt_username.setPadding(w5, h5, w5, h5);
        edt_password.setPadding(w5, h5, w5, h5);
        login_text.setPadding(0, h20, 0, h20);
        logo.setPadding(0, h5, 0, h5);

    }

    public void setListener() {
        loginButton.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton rb = (RadioButton) findViewById(checkedId);
                if (rb.getText().toString().equals("Faculty")) {
                    edt_username.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                    edt_username.setText("");
                    edt_password.setText("");
                    edt_username.requestFocus();
                } else if (rb.getText().toString().equals("Student")) {
                    edt_username.setInputType(InputType.TYPE_CLASS_NUMBER);
                    edt_username.setText("");
                    edt_password.setText("");
                    edt_username.requestFocus();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button:
                if (TextUtils.isEmpty(edt_username.getText()))
                    Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(edt_password.getText()))
                    Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
                else
                    login();
                break;
        }
    }

    public void login() {

        String username = edt_username.getText().toString();
        String password = edt_password.getText().toString();

        int id = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(id);
        String identity = radioButton.getText().toString();

        if (identity.equals("Faculty"))
            loginfaculty(username, password);
        else if (identity.equals("Student"))
            loginstudent(username, password);

    }

    private void loginstudent(final String username, final String password) {

        studentRef = mDatabase.getReference().child("Student");

        progressDialog.show();
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(username).exists()) {
                    if (!username.isEmpty()) {
                        User login = dataSnapshot.child(username).getValue(User.class);
                        if (login.password.equals(password)) {
                            Intent i=new Intent(MainActivity.this,StudentMain.class);
                            i.putExtra("enr_number", username);
                            startActivity(i);
                            finish();
                            progressDialog.dismiss();
                            pref = getApplicationContext().getSharedPreferences("PrefFile", 0);
                            editor = pref.edit();
                            editor.putString("username",username);
                            editor.putString("identity","Student");
                            editor.commit();

                        } else {
                            Toast.makeText(MainActivity.this, "Invalid Credentials.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Credentials.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Invalid Credentials.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Log.e("Error of database",error);
                Toast.makeText(MainActivity.this, "Invalid Credentials.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    public void loginfaculty(final String username,final String password) {

        //progressBar.setVisibility(View.VISIBLE);
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent i=new Intent(MainActivity.this,FacultyMain.class);
                            startActivity(i);
                            finish();
                            progressDialog.dismiss();
                            pref = getApplicationContext().getSharedPreferences("PrefFile", 0);
                            editor = pref.edit();
                            editor.putString("username",username);
                            editor.putString("identity","Faculty");
                            editor.commit();

                        } else {
                            // If sign in fails, display a message to the user.
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, "Invalid Credentials.", Toast.LENGTH_SHORT).show();
                            Log.e("Error of database",error);
                            progressDialog.dismiss();
                        }
                    }
                });

    }
}