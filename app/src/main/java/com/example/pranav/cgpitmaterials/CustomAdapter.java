package com.example.pranav.cgpitmaterials;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomAdapter extends BaseAdapter {

    Context context;
    List<Messages> list;
    Holder holder;
    DatabaseReference facDatabase;
    String facName;
    DownloadManager.Request request;
    Context c;
    View mView;
    Activity mActivity;
    String uri, mSub;
    int pos;
    File file;



    public CustomAdapter(Context context,Activity activity, List<Messages> arrayList, String subName) {
        this.context = context;
        this.list = arrayList;
        mActivity = activity;
        mSub = subName;
        Holder holder;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_single_layout, null);
            file = new File("/CGPIT/"+mSub+"/", list.get(pos).getSeen());
            holder = new Holder();
            setView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
            setView(convertView);
        }

        holder.faculty_name.setText(list.get(position).getFrom());
        Date date = new Date(list.get(position).getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM/hh:mm a");
        String time = sdf.format(date);
        holder.time_stamp.setText(time);


        if (list.get(position).getType().equals("text")) {
            holder.t1.setText(list.get(position).getMessage());
            holder.messageImage.setVisibility(View.GONE);
            holder.t1.setVisibility(View.VISIBLE);
            holder.document_layout.setVisibility(View.GONE);
            holder.download_file_button.setVisibility(View.GONE);

        } else if (list.get(position).getType().equals("image")) {
            holder.t1.setVisibility(View.GONE);
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.document_layout.setVisibility(View.GONE);
            holder.download_file_button.setVisibility(View.GONE);

            Picasso.get().load(list.get(position).getMessage()).placeholder(R.drawable.default_iamge).into(holder.messageImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                }
            });
        } else if (list.get(position).getType().equals("doc")) {

            holder.t1.setVisibility(View.GONE);
            holder.messageImage.setVisibility(View.GONE);
            holder.document_layout.setVisibility(View.VISIBLE);
            holder.file_text.setText(list.get(position).getSeen());

            /*if(file.exists()){
                holder.download_file_button.setVisibility(View.GONE);
            }else{
                holder.download_file_button.setVisibility(View.VISIBLE);
            }*/


            pos = position;
            holder.download_file_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uri = list.get(pos).getMessage();
                    request = new DownloadManager.Request(Uri.parse(uri));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(ContextCompat.checkSelfPermission(c, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            //Toast.makeText(c, "Permission Denied.", Toast.LENGTH_SHORT).show();
                            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }else {

                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                            request.setDestinationInExternalPublicDir("/CGPIT/"+mSub+"/", list.get(pos).getSeen());
                            request.setTitle(list.get(pos).getSeen());

                            DownloadManager manager = (DownloadManager)mView.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);
                            BroadcastReceiver onComplete=new BroadcastReceiver() {
                                public void onReceive(Context ctxt, Intent intent) {
                                    // your code
                                    Toast.makeText(ctxt, "Download complete.", Toast.LENGTH_SHORT).show();
                                }
                            };
                            mView.getContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                        }
                    }

                }
            });


            if(file.exists()){
                Toast.makeText(context, "exists", Toast.LENGTH_SHORT).show();
                holder.download_file_button.setVisibility(View.GONE);
            }else{

                holder.download_file_button.setVisibility(View.VISIBLE);
            }
        }

        holder.messageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,Image_Viewer.class);
                intent.putExtra("name",list.get(position).getFrom());
                Date d = new Date(list.get(position).getTime());
                SimpleDateFormat sdatef = new SimpleDateFormat("dd-MMM/hh:mm a");
                String time = sdatef.format(d);
                intent.putExtra("time",time);
                intent.putExtra("image",list.get(position).getMessage());
                context.startActivity(intent);
            }
        });

        return convertView;

    }


    public void setView(View view) {
        mView = view;
        holder.t1 = (TextView) view.findViewById(R.id.message_text);
        holder.faculty_name = (TextView) view.findViewById(R.id.faculty_name);
        holder.messageImage = view.findViewById(R.id.message_image);

        holder.document_layout = (LinearLayout) view.findViewById(R.id.message_document_layout);
        holder.file_text = (TextView) view.findViewById(R.id.file_text);
        holder.download_file_button = view.findViewById(R.id.download_file_button);

        holder.time_stamp = (TextView) view.findViewById(R.id.time_stamp);
        c = view.getContext();


    }

    static class Holder {
        TextView t1, faculty_name;
        ImageView messageImage;

        LinearLayout document_layout;
        TextView message_text, file_text;
        ImageView download_file_button;

        TextView time_stamp;
    }
}
