package com.example.test3;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Member_Activity_InOutLog extends AppCompatActivity {
    ImageButton backbt;
    TextView pageName;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    Intent intent;
    Bundle parentbundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        listView= (ListView)  findViewById(R.id.listView);

        backbt = (ImageButton) findViewById(R.id.backbt);
        backbt.setImageResource(R.drawable.ic_back);
        backbt.setColorFilter(Color.WHITE);
        pageName = (TextView) findViewById(R.id.seatmapname);

        long mm = System.currentTimeMillis();
        SimpleDateFormat m = new SimpleDateFormat("yyyyMM");

        intent = getIntent();
        parentbundle = intent.getExtras();

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference(parentbundle.getString("branch"));

        Log.i("ggg",parentbundle.getString("branch")+parentbundle.getString("user_id"));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        listView.setAdapter(adapter);
        databaseRef.child("log").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                // 클래스 모델이 필요?
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String str = fileSnapshot.child(parentbundle.getString("user_id")).child("date").getValue().toString();
                    String start = fileSnapshot.child(parentbundle.getString("user_id")).child("start_time").getValue().toString();
                    String end = fileSnapshot.child(parentbundle.getString("user_id")).child("end_time").getValue().toString();
                    int result = (Integer.parseInt(end.substring(0, 2))-Integer.parseInt(start.substring(0, 2)))*60+(Integer.parseInt(end.substring(3, 5))-Integer.parseInt(start.substring(3, 5)));
                    int result2 = Integer.parseInt(end.substring(6, 8))-Integer.parseInt(start.substring(6, 8));
                    if (result2<0) result2*=-1;
                    if(!end.equals("null"))
                        adapter.add(str+"　　　"+ start +"부터 "+end+"까지 총 "+result+"분 "+result2+"초 이용");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


    }
    public void backbtClick(View v){
        finish();
    }
}
