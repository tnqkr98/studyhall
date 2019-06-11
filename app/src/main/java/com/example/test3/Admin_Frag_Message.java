package com.example.test3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Admin_Frag_Message extends Fragment {
    private ListView listView;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    View view;
    Admin_adapter_cmp_msg mMyAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("상도"); // 그 회원의 branch 받아와야 함
        view = inflater.inflate(R.layout.fragment_admin_message,container,false);
        listView = (ListView)view.findViewById(R.id.listView);
        mMyAdapter = new Admin_adapter_cmp_msg();
        listView.setAdapter(mMyAdapter);
        long mm = System.currentTimeMillis();
        SimpleDateFormat m = new SimpleDateFormat("yyyyMM");

        databaseRef.child("message").child(m.format(new Date(mm))).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //mMyAdapter.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String to = fileSnapshot.child("sender").getValue().toString();
                    String reason = fileSnapshot.child("reason").getValue().toString();

                    mMyAdapter.addItem(to , reason );
                }
                mMyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }
}
