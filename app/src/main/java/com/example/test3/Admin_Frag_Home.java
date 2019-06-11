package com.example.test3;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.Date;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;

public class Admin_Frag_Home extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    View view;
    private Button gotomember, gotoutil, gotoseat, gotoexit, button5, button2, button3, button4;
    private TextView percent,user_num,textView;
    private ProgressBar progressBar;
    private List<String> adapter = new ArrayList<>();
    private List<String> total = new ArrayList<>();
    private List<String> reserve = new ArrayList<>();
    private List<String> totaluser = new ArrayList<>();
    private List<String> cmp = new ArrayList<>();
    long today = System.currentTimeMillis();
    SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat dayTime2 = new SimpleDateFormat("yyyyMM");

    Intent intent;
    Bundle parentbundle;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_admin_home,container,false);

        intent = getActivity().getIntent();
        parentbundle = intent.getExtras();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference(parentbundle.getString("branch"));

        textView = (TextView) view.findViewById(R.id.textView6);
        percent = (TextView) view.findViewById(R.id.percent);
        user_num = (TextView) view.findViewById(R.id.user_num);
        gotoexit = (Button) view.findViewById(R.id.gotoexit);
        gotomember = (Button) view.findViewById(R.id.gotomember);
        gotoutil = (Button) view.findViewById(R.id.gotoutil);
        gotoseat = (Button) view.findViewById(R.id.gotoseat);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        button5 = (Button)view.findViewById(R.id.button5);
        button2 = (Button)view.findViewById(R.id.button2);
        button3 = (Button)view.findViewById(R.id.button3);
        button4 = (Button)view.findViewById(R.id.button4);

        //AdminMain_Activity
        gotomember.setOnClickListener(new View.OnClickListener() {
            final Intent intent1 = new Intent(view.getContext(), MemberActivity.class);
            @Override
            public void onClick(View v) {
                startActivity(intent1);
            }
        });

        gotoseat.setOnClickListener(new View.OnClickListener() {  // 임시
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(view.getContext(), Admin_Activity_SeatSetting.class);
                logIntent.putExtras(parentbundle);
                startActivity(logIntent);
            }
        });

        gotoutil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent utilIntent = new Intent(view.getContext(), Admin_Activty_Asset.class);
                utilIntent.putExtra("branch",parentbundle.getString("branch"));
                startActivity(utilIntent);
            }
        });

        gotoexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seatIntent = new Intent(view.getContext(), Admin_Activity_Exit.class);
                seatIntent.putExtra("branch",parentbundle.getString("branch"));
                //String branch = spinner.getSelectedItem().toString();
                //Bundle bundle = new Bundle();
                //bundle.putString("branch",branch);
                //seatIntent.putExtras(bundle);
                startActivity(seatIntent);
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        database.getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String str = fileSnapshot.child("branch").getValue().toString();
                    if(str.equals(parentbundle.getString("branch"))) total.add(str);
                }
                button3.setText(total.size()+"\n회원 수");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        databaseRef.child("current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    adapter.add(fileSnapshot.child("user_id").getValue().toString());
                    Log.i("갯수",""+fileSnapshot.child("user_id").getValue().toString());
                }
                Log.i("갯수",total.size()+"a"+adapter.size());
                user_num.setText(adapter.size()+"명");
                progressBar.setProgress(adapter.size()*100/total.size());
                percent.setText(adapter.size()*100/total.size()+"%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        databaseRef.child("reservation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reserve.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String a = fileSnapshot.child("date").getValue().toString();
                    Log.i("dd",""+dayTime.format(new Date(today)));
                    if (a.equals(dayTime.format(new Date(today))))
                        reserve.add(a);
                }
                button5.setText(reserve.size()+"\n예약 수");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        databaseRef.child("message").child(dayTime2.format(new Date(today))).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cmp.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String s = fileSnapshot.child("day").getValue().toString();
                    if (s.equals(dayTime.format(new Date(today))))
                        cmp.add(s);
                }
                button2.setText(cmp.size()+"\nDM 수");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        databaseRef.child("cmp").child(dayTime2.format(new Date(today))).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cmp.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String s = fileSnapshot.child("day").getValue().toString();
                    if (s.equals(dayTime.format(new Date(today))))
                        cmp.add(s);
                }
                button4.setText(cmp.size()+"\nCMP");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
