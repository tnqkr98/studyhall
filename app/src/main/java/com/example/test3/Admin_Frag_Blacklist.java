package com.example.test3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Admin_Frag_Blacklist extends Fragment {
    private TextView textView1, textView2, textView3;
    private ListView listView;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private ArrayList<String> n, b;
    Admin_adapter_blalist mMyAdapter;
    long mm = System.currentTimeMillis();
    SimpleDateFormat m = new SimpleDateFormat("yyyyMM");
    View view;  // 여기에 쓴 코드는 유저 학습보조기능 화면이니까 갖다 쓰도록

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_admin_blacklist,container,false);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        textView3 = (TextView) view.findViewById(R.id.textView3);
        listView = (ListView) view.findViewById(R.id.listView);
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("상도"); // 그 회원의 branch 받아와야 함

        mMyAdapter = new Admin_adapter_blalist();
        listView.setAdapter(mMyAdapter);
        n = new ArrayList<String>();
        databaseRef.child("cmp").child(m.format(new Date(mm))).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String to = fileSnapshot.child("reciver").getValue().toString();
                    Log.i("branch",to);
                    n.add(to);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        b = new ArrayList<String>();
        database.getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String id = fileSnapshot.child("user_id").getValue().toString();

                    if(fileSnapshot.child("branch").getValue().toString().equals("상도")&&fileSnapshot.child("user_or_admin").getValue().toString().equals("0")) {
                        b.add(id);
                        //Log.i("branch",id);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        database.getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    for (int i=0;i<b.size();i++) {
                        if(fileSnapshot.child("user_id").getValue().toString().equals(b.get(i))) {
                            String id = fileSnapshot.child("user_id").getValue().toString();
                            String name = fileSnapshot.child("name").getValue().toString();
                            Log.i("test",""+b+n);
                            int minus=0;
                            for (int j=0;j<n.size();j++) {
                                if (id.equals(n.get(j)))
                                    minus += 1;
                                Log.i("num",""+minus);
                            }
                            mMyAdapter.addItem(id, name, "" + minus);
                        }
                    }
                }
                mMyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
