package com.example.test3;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import java.util.TimeZone;

public class Member_Activity_rentcancel extends AppCompatActivity {

    ImageButton backbt;
    Bundle bundle;
    ListView listView;
    private Member_Activity_rentcancelAdapter adapter;
    ArrayList<String> facilitylist;

    private static DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member__rentcancel);

        backbt = (ImageButton)findViewById(R.id.rentback);
        backbt.setImageResource(R.drawable.ic_back);
        backbt.setColorFilter(Color.WHITE);

        bundle = getIntent().getExtras();
        myRef = FirebaseDatabase.getInstance().getReference(bundle.getString("branch"));

        listView = (ListView)findViewById(R.id.rentlist);

        adapter = new Member_Activity_rentcancelAdapter(bundle.getString("branch"),bundle.getString("user_id"));

        facilitylist = new ArrayList<String>();

        //시설물 번호 배열에 초기화
        myRef.child("facility").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userData : dataSnapshot.getChildren())
                    facilitylist.add(userData.child("facility_name").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        myRef.child("reservation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userData : dataSnapshot.getChildren()) {
                    if (userData.child("user_id").getValue().toString().equals(bundle.getString("user_id"))) {
                        String day = userData.child("date").getValue().toString();
                        String startTime = userData.child("start_time").getValue().toString();
                        String endTime = userData.child("end_time").getValue().toString();
                        int assetID = Integer.parseInt(userData.child("facility_id").getValue().toString());

                        adapter.addItem(day,startTime+" ~ "+endTime,""+facilitylist.get(assetID),startTime,endTime,assetID+"");

                        listView.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    //================================어댑터 정의-========================================//
    public class Member_Activity_rentcancelAdapter extends BaseAdapter {

        public ArrayList<ListViewItem2> listViewItemList = new ArrayList<ListViewItem2>();
        Button cancelbt;
        String userid;

        public Member_Activity_rentcancelAdapter(String branch,String id){
            myRef = FirebaseDatabase.getInstance().getReference(branch);
            userid = id;
        }

        @Override
        public int getCount() {
            return listViewItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return position ;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listrowitem2, parent, false);
            }
            TextView textView1 = (TextView) convertView.findViewById(R.id.textView1);
            TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);
            TextView textView3 = (TextView) convertView.findViewById(R.id.textView3);
            cancelbt = (Button) convertView.findViewById(R.id.rentcancelbt);

            ListViewItem2 listViewItem = listViewItemList.get(position);
            textView1.setText(listViewItem.getRowtext1());
            textView2.setText(listViewItem.getRowtext2());
            textView3.setText(listViewItem.getRowtext3());

            Date d = new Date(System.currentTimeMillis());
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
            sf.setTimeZone(tz);
            final int today = Integer.parseInt(sf.format(d));

            if(today > Integer.parseInt(textView1.getText().toString())) {
                cancelbt.setVisibility(View.INVISIBLE);
                cancelbt.setEnabled(false);
            }
            else {
                cancelbt.setVisibility(View.VISIBLE);
                cancelbt.setEnabled(true);
            }

            final String sday = listViewItem.getRowtext1();
            final String sst = listViewItem.st;
            final String set = listViewItem.et;
            final String sfn = listViewItem.fn;

            cancelbt.setOnClickListener(new View.OnClickListener() {   // 예약취소 클릭 리스너 구현
                @Override
                public void onClick(View v) {
                    dilog_yesorno(sday,sfn,userid,sst,set);
                }
            });

            return convertView;
        }

        public void addItem(String text1, String text2, String text3, String pst,String pet, String pfn) {
            ListViewItem2 item = new ListViewItem2();
            item.setRowtext1(text1);
            item.setRowtext2(text2);
            item.setRowtext3(text3);
            item.st = pst;
            item.et = pet;
            item.fn = pfn;

            listViewItemList.add(item);
        }

        public Button getBt(){
            return cancelbt;
        }

        public void clearItem(){
            listViewItemList.clear();
        }
    }

//------------------------------다이얼로그----------------------------------------------//
    public void dilog_yesorno(String day,String fn,String userid,String st,String et) {
        Button yesBt, noBt;
        TextView question, title;
        View dlgView = View.inflate(this, R.layout.diallog_yesorno, null);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(dlgView);
        yesBt = (Button) dlgView.findViewById(R.id.yesbt);
        noBt = (Button) dlgView.findViewById(R.id.nobt);
        question = (TextView) dlgView.findViewById(R.id.question);
        title = (TextView) dlgView.findViewById(R.id.dlgtitle);

        question.setText("예약을 취소하시겠습니까??");
        title.setText("시설물 예약취소");

        final String sday = day, sfn = fn, suserid = userid, sst = st, set = et;
        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("reservation").child(sday + "_" + sfn + "_" + suserid + "_" + sst + "to" + set).removeValue();

                myRef.child("reservation").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        adapter.clearItem();
                        for(DataSnapshot userData : dataSnapshot.getChildren()) {
                            if (userData.child("user_id").getValue().toString().equals(bundle.getString("user_id"))) {
                                String day = userData.child("date").getValue().toString();
                                String startTime = userData.child("start_time").getValue().toString();
                                String endTime = userData.child("end_time").getValue().toString();
                                int assetID = Integer.parseInt(userData.child("facility_id").getValue().toString());

                                adapter.addItem(day,startTime+" ~ "+endTime,""+facilitylist.get(assetID),startTime,endTime,assetID+"");

                                listView.setAdapter(adapter);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
                dialog.cancel();
            }
        });

        noBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }



    public void backClick(View view){
        finish();
    }
}
