package com.example.pranav.cgpitmaterials;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pranav.cgpitmaterials.Api.APIResponse;
import com.example.pranav.cgpitmaterials.Api.MyApi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.ArrayList;

public class StudentMain extends AppCompatActivity {

    String enr_number;
    String name;
    private FirebaseDatabase mDatabase;
    private DatabaseReference studentRef;
    private DatabaseReference subRef;
    private String sem;
    private String branch;
    private ListView subList;
    private Toolbar barLayout;
    ArrayList<String> sub_list;
    ArrayAdapter<String> sub_adapter;
    SubjectList subjectList;
    SharedPreferences preferences;
    MyApi myApi;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        myApi=new MyApi(getApplicationContext());
        preferences = getSharedPreferences("PrefFile", 0);
        enr_number = preferences.getString("username",null);
        mDatabase = FirebaseDatabase.getInstance();
        barLayout = findViewById(R.id.studBar);
        studentRef = mDatabase.getReference().child("Student").child(enr_number);
        subList = findViewById(R.id.studSubList);
        subjectList = new SubjectList();
        setSupportActionBar(barLayout);
        getSupportActionBar().setTitle("Subject List");
        sub_list = new ArrayList<>();
        sub_adapter = new ArrayAdapter<String>(StudentMain.this, R.layout.row_subject_faculty,R.id.subject_name, sub_list);



        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                sem = dataSnapshot.child("sem").getValue().toString();
                branch = dataSnapshot.child("branch").getValue().toString();
                subRef = mDatabase.getReference().child(branch).child(sem).child("Subject");
                subRef.addValueEventListener(new ValueEventListener() {
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

                token=FirebaseInstanceId.getInstance().getToken();

                String tempSem=sem;
                String tempBr=branch;

                Log.e("16octSagar",""+enr_number+" "+sem+" "+branch+" "+token);

                myApi.sendToken(new APIResponse() {
                    @Override
                    public void onSuccess(JSONObject object) {
                    }

                    @Override
                    public void onFailure(String error) {
                    }
                },enr_number,tempSem,tempBr,token);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        subList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(StudentMain.this,ChatActivity.class);
                String subject=sub_list.get(i).toString();
                intent.putExtra("subject_name",subject);
                intent.putExtra("identity","Student");
                intent.putExtra("branch", branch);
                intent.putExtra("sem", sem);
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
                Intent loginIntent = new Intent(StudentMain.this, MainActivity.class);
                startActivity(loginIntent);
                finish();
                Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                myApi.deleteToken(new APIResponse() {
                    @Override
                    public void onSuccess(JSONObject object) {
                        Toast.makeText(StudentMain.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(StudentMain.this, "fail", Toast.LENGTH_SHORT).show();
                        Log.e("16oct","delete api fail");
                    }
                },enr_number,token);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}