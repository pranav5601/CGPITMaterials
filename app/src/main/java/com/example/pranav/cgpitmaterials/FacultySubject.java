package com.example.pranav.cgpitmaterials;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FacultySubject extends AppCompatActivity {

    ListView subList;
    String branch, sem;
    FirebaseDatabase mDatabase;
    ArrayList<String> sub_list;
    DatabaseReference subjectRef;
    ArrayAdapter<String> sub_adapter;
    SubjectList subjectList;
    Toolbar barLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_subject);

        Bundle bundle=getIntent().getExtras();
        sub_list = new ArrayList<>();
        sub_adapter = new ArrayAdapter<String>(this, R.layout.row_subject_faculty,R.id.subject_name, sub_list);

        branch=bundle.getString("branch");
        sem=bundle.getString("semester");

        if(branch.equals("COMPUTER"))
            branch="CO";
        else if(branch.equals("INFORMATION TECHNOLOGY"))
            branch="IT";
        else if(branch.equals("MECHANICAL"))
            branch="MECH";
        else if(branch.equals("AUTOMOBILE"))
            branch="AUTO";
        else if(branch.equals("CIVIL"))
            branch="CIVIL";
        else if(branch.equals("CHEMICAL"))
            branch="CHEM";

        subList = findViewById(R.id.sub_list);
        barLayout = findViewById(R.id.subLayout);
        setSupportActionBar(barLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Subject List");
        subjectList = new SubjectList();

        mDatabase = FirebaseDatabase.getInstance();
        subjectRef = mDatabase.getReference().child(branch).child(sem).child("Subject");

        subjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    subjectList = ds.getValue(SubjectList.class);
                    sub_list.add(subjectList.getSub_name().toString());
                }
                subList.setAdapter(sub_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        subList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(FacultySubject.this,ChatActivity.class);
                intent.putExtra("subject_name",sub_list.get(i).toString());
                intent.putExtra("branch", branch);
                intent.putExtra("sem",sem);
                intent.putExtra("identity","Faculty");
                startActivity(intent);
            }
        });

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
                Intent loginIntent = new Intent(FacultySubject.this, MainActivity.class);
                startActivity(loginIntent);
                finish();
                Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
