package com.example.test3;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Member_MyCMP_Activity extends AppCompatActivity {

    private ListView listView;
    private Member_MyCMP_adapter adapter;

    Bundle bundle;
    ImageButton backbt;

    private static DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member__my_cmp_);

        backbt = (ImageButton)findViewById(R.id.rentback);
        backbt.setImageResource(R.drawable.ic_back);
        backbt.setColorFilter(Color.WHITE);


        bundle = getIntent().getExtras();
        myRef = FirebaseDatabase.getInstance().getReference(bundle.getString("branch"));

        listView = (ListView)findViewById(R.id.complainlist);
        adapter = new Member_MyCMP_adapter();

        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        sf.setTimeZone(tz);
        String today = sf.format(d);

        int yearmonth = Integer.parseInt(today)/100;
        myRef.child("cmp").child(yearmonth+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userData : dataSnapshot.getChildren()) {
                    if (userData.child("reciver").getValue().toString().equals(bundle.getString("user_id"))) {
                        String day = userData.child("day").getValue().toString();
                        String time = userData.child("time").getValue().toString();
                        String reason = userData.child("reason").getValue().toString();

                        adapter.addItem(day+" "+time, reason);

                        listView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void backClick(View view){
        finish();
    }
}
