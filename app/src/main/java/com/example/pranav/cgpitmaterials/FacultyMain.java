package com.example.pranav.cgpitmaterials;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FacultyMain extends AppCompatActivity {

    RadioGroup branchrbg, semesterrbg;
    Button send;
    Toolbar facultyBar;

    String facName;
    FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference facultyRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_main);


        getView();
        setListener();


        mAuth = FirebaseAuth.getInstance();

         String uid = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance();
        facultyRef = mDatabase.getReference().child("Faculty").child(uid);
        facultyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               facName = dataSnapshot.child("name").getValue().toString();
                setSupportActionBar(facultyBar);
                getSupportActionBar().setTitle(facName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setListener() {

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id1 = semesterrbg.getCheckedRadioButtonId();
                RadioButton rb1 = (RadioButton) findViewById(id1);
                String sem = rb1.getText().toString();

                int id2 = branchrbg.getCheckedRadioButtonId();
                RadioButton rb2 = (RadioButton) findViewById(id2);
                String branch = rb2.getText().toString();

                if (sem.equals("2") || sem.equals("4") || sem.equals("6") || sem.equals("8")) {
                    showSemErrorDialog(sem);
                } else {
                    showConfirmationDialog(sem,branch);
                }
            }
        });
    }

    private void showConfirmationDialog(final String sem, final String branch) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(FacultyMain.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(FacultyMain.this);
        }
        builder.setTitle("Confirm")
                .setMessage("Do you want to send content to "+ branch+" ENGINEERING Semester "+sem+"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(FacultyMain.this,FacultySubject.class);
                        i.putExtra("branch",branch);
                        i.putExtra("semester",sem);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void showSemErrorDialog(String sem){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(FacultyMain.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(FacultyMain.this);
        }
        builder.setTitle("Error")
                .setMessage("You can only send content to Semester "+sem+" between December to May")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void getView() {

        branchrbg = (RadioGroup) findViewById(R.id.branch_rbg);
        semesterrbg = (RadioGroup) findViewById(R.id.semester_rbg);
        send = (Button) findViewById(R.id.send_button);
        facultyBar = findViewById(R.id.faculty_menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu:
            {
                SharedPreferences sharedPreferences = getSharedPreferences("PrefFile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent loginIntent = new Intent(FacultyMain.this, MainActivity.class);
                startActivity(loginIntent);
                mAuth.signOut();
                finish();
                Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
