package com.example.pranav.cgpitmaterials;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableContainer;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.pranav.cgpitmaterials.Api.APIResponse;
import com.example.pranav.cgpitmaterials.Api.MyApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    FirebaseDatabase mDatabase;
    DatabaseReference messageRef, nameRef;
    DatabaseReference retriveRef;
    FirebaseAuth mAuth;
    TextView percentUpload;
    String branch;
    String sem, facName;
    String push_id;
    String identity, subject_name;
    String message, uid;
    EditText chatSend;
    ImageView chatSendBtn, attach_image, attach_document,backButton;
    LinearLayout sendLayout;
    Messages messages;
    PopupWindow popupWindow;
    ListView mMessageList;
    CustomAdapter customAdapter;
    TextView no_data_found, subject_title, subject_code;
    RelativeLayout main_chat_layout;
    StorageReference mImageStorage;
    DatabaseReference codeRef;
    Uri pdfUri;
    final static int PICK_PDF_CODE = 2342;
    String pdfName;
    AlertDialog alertDialog;
    ProgressBar uploadProgressBar;
    String filename;
    DatabaseReference uploadPdfRef;
    Button ok,cancel;
    MyApi myApi;


    //the firebase objects for storage and database
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;


    private final List<Messages> messagesList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        identity = getIntent().getStringExtra("identity");
        subject_name = getIntent().getStringExtra("subject_name");
        branch = getIntent().getStringExtra("branch");
        sem = getIntent().getStringExtra("sem");
        mDatabase = FirebaseDatabase.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        myApi=new MyApi(getApplicationContext());

        main_chat_layout = (RelativeLayout) findViewById(R.id.main_chat_layout);

        sendLayout = (LinearLayout) findViewById(R.id.send_message_layout);
        chatSend = findViewById(R.id.chat_send);
        chatSendBtn = findViewById(R.id.chat_send_btn);
        no_data_found = (TextView) findViewById(R.id.no_data_found);

        subject_title = (TextView) findViewById(R.id.subject_name_toolbar);
        subject_code = (TextView) findViewById(R.id.subject_code_toolbar);



        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        backButton=findViewById(R.id.back_btn_chat);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mMessageList = findViewById(R.id.chat_list);
        customAdapter = new CustomAdapter(this,ChatActivity.this, messagesList, subject_name);
        mMessageList.setAdapter(customAdapter);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        codeRef = mDatabase.getReference().child(branch).child(sem).child("Subject").child(subject_name);
        codeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String code = dataSnapshot.child("code").getValue().toString();
                subject_code.setText("(" + code + ")");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        subject_title.setText(subject_name);


        messages = new Messages();
        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid().toString();
        }


        if (identity.equals("Faculty")) {
            sendLayout.setVisibility(View.VISIBLE);
            nameRef = mDatabase.getReference().child("Faculty").child(uid);
            nameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    facName = dataSnapshot.child("name").getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (identity.equals("Student")) {
            sendLayout.setVisibility(View.GONE);
        }
        loadMessage();

        chatSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (motionEvent.getAction() == motionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() + 75 >= (chatSend.getRight() - chatSend.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        showAttachPopup();

                        return true;
                    }
                }

                return false;


            }
        });


        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = chatSend.getText().toString();
                messageRef = mDatabase.getReference().child(branch).child(sem).child("ChatRoom").child(subject_name).push();

                Map messageMap = new HashMap();
                messageMap.put("message", message);
                messageMap.put("from", facName);
                messageMap.put("seen", "false");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("type", "text");

                if (!message.isEmpty()) {
                    messageRef.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                chatSend.setText("");
                                String m=facName+ ": "+message;
                                firenotification(m);
                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });

    }

    private void showAttachPopup() {

        LayoutInflater layoutInflater = (LayoutInflater) ChatActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup = layoutInflater.inflate(R.layout.attachment_popup, null);
        popupWindow = new PopupWindow(popup, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.popup_anim);

        popupWindow.showAtLocation(sendLayout, Gravity.CENTER, 0, 0);

        attach_image = (ImageView) popup.findViewById(R.id.attach_imae);
        attach_document = (ImageView) popup.findViewById(R.id.attach_document);


        //FOR UPLOAD IMAGE
        attach_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(ChatActivity.this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        //Toast.makeText(ChatActivity.this, "Alredy have a permission", Toast.LENGTH_SHORT).show();
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(ChatActivity.this);

                    }
                } else {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(ChatActivity.this);

                }

                popupWindow.dismiss();

            }
        });
        //FOR UPLOAD DOCS
        attach_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getPDF();
                Toast.makeText(ChatActivity.this, "Attach document clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessage() {
        retriveRef = mDatabase.getReference().child(branch).child(sem).child("ChatRoom").child(subject_name);

        retriveRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                customAdapter.notifyDataSetChanged();
                mMessageList.setVisibility(View.VISIBLE);
                no_data_found.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (messagesList.isEmpty()) {
            no_data_found.setVisibility(View.VISIBLE);
            mMessageList.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final DatabaseReference imageMsgRef = mDatabase.getReference().child(branch).child(sem).child("ChatRoom").child(subject_name).push();
                String pushId = imageMsgRef.getKey();
                final StorageReference filepath = mImageStorage.child(branch).child(sem).child(subject_name).child(pushId + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String download_url = task.getResult().getDownloadUrl().toString();
                            Map messageMap = new HashMap();
                            messageMap.put("message", download_url);
                            messageMap.put("from", facName);
                            messageMap.put("seen", "false");
                            messageMap.put("time", ServerValue.TIMESTAMP);
                            messageMap.put("type", "image");

                            imageMsgRef.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.e("CHAT_LOG", databaseError.getMessage().toString());
                                    } else {
                                        Log.e("CHAT_LOG", "Success in uploading image");
                                    }
                                }
                            });

                            String imagenoti=facName+" uploaded an image.";
                            firenotification(imagenoti);

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                pdfUri = data.getData();
                pdfName = data.getData().getLastPathSegment();
                //TODO Show Box
                showAttachFileDialog(pdfUri, pdfName);
            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void showAttachFileDialog(final Uri uri, final String name) {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ChatActivity.this);
        View dialog = getLayoutInflater().inflate(R.layout.upload_file_dialog, null);

        uploadProgressBar = (ProgressBar) dialog.findViewById(R.id.file_upload_progress_bar);
        percentUpload = dialog.findViewById(R.id.file_upload_text_view);
        final EditText file_name = dialog.findViewById(R.id.file_upload_name_edit_text);


        ok = (Button) dialog.findViewById(R.id.file_upload_ok_btn);
        cancel = (Button) dialog.findViewById(R.id.file_upload_cancel_btn);

        mBuilder.setView(dialog);
        alertDialog = mBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        file_name.setText(getFileName(uri));
        try {
            file_name.setSelection(0, file_name.getText().toString().lastIndexOf('.'));
        }
        catch (Exception e){
            Log.e("exception",e.getMessage());
        }
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                filename = file_name.getText().toString();
                ok.setClickable(false);

                if(filename.lastIndexOf('.') == -1){
                    Toast.makeText(ChatActivity.this, "Please Provide file extension", Toast.LENGTH_SHORT).show();
                }
                else if (!(TextUtils.isEmpty(file_name.getText()))) {
                    uploadProgressBar.setVisibility(View.VISIBLE);
                    percentUpload.setVisibility(View.VISIBLE);
                    uploadFile(uri, filename);
                }

                else{
                    Toast.makeText(ChatActivity.this, "Please provide file name", Toast.LENGTH_SHORT).show();
                }

                ok.setClickable(true);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        alertDialog.show();
        popupWindow.dismiss();
    }

    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_PDF_CODE);

    }

    private void uploadFile(final Uri data, final String fName) {

        StorageReference sRef = mStorageReference.child(branch).child(sem).child(subject_name).child(fName);
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Map messageMap = new HashMap();
                        messageMap.put("message", taskSnapshot.getDownloadUrl().toString());
                        messageMap.put("from", facName);
                        messageMap.put("seen", fName);
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("type", "doc");

                        //Upload upload = new Upload(fName, taskSnapshot.getDownloadUrl().toString());
                        uploadPdfRef = mDatabaseReference.child(branch).child(sem).child("ChatRoom").child(subject_name).push();
                        uploadPdfRef.setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    uploadProgressBar.setVisibility(View.GONE);
                                    percentUpload.setVisibility(View.GONE);
                                    Toast.makeText(ChatActivity.this, "Upload successful.", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                    String filenoti=facName+" uploaded a file.";
                                    firenotification(filenoti);


                                }else {
                                    Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        percentUpload.setText((int) progress + "% Uploading...");
                        //textViewStatus.setText((int) progress + "% Uploading...");
                    }
                });

    }

    private void firenotification(String message) {
        //message=facName+" "+message;
        Log.e("14nov",sem+" "+branch+" "+subject_name+" "+message);

        myApi.sendNotification(new APIResponse() {
            @Override
            public void onSuccess(JSONObject object) {

            }

            @Override
            public void onFailure(String error) {

            }
        },sem,branch,subject_name,message);
    }
}
