package com.example.test3;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Member_Frag_Home extends Fragment implements BeaconConsumer {

    View view;
    ImageView profileIcon, profileImg;
    BitmapDrawable icon,img;
    Resources res;
    TextView name, due, info;
    boolean current = false ,bluetoothon = false, bluetoothAbON = true; // 블루투스 강제 온
    public static final int REQUEST_MEMMAIN_TO_SEAT = 100;

    private static DatabaseReference myRef;
    Intent parentintent;
    Bundle parentbundle;

    //--- 버튼
    private  int seat_id;
    Button inoutbt,movebt;  //입퇴실, 자리이동

    //------------ 블루투스 비콘
    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    Handler handler;
    //------------ 블루투스 비콘

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_memhome,container,false);
        res = getResources();

        //db 참조
        parentintent = getActivity().getIntent();
        parentbundle = parentintent.getExtras();
        myRef = FirebaseDatabase.getInstance().getReference(parentbundle.getString("branch"));

        //------------------------------홈 프로필------------------------
        profileIcon = (ImageView)view.findViewById(R.id.profile);  //프로필 아이콘
        img = (BitmapDrawable) res.getDrawable(R.drawable.pro_example2);
        profileIcon.setImageDrawable(img);
        profileIcon.getLayoutParams().width = (int)(img.getIntrinsicWidth()*0.4);
        profileIcon.getLayoutParams().height = (int)(img.getIntrinsicHeight()*0.4);;

        profileImg = (ImageView)view.findViewById(R.id.homeimg);  //프로필 이미지
        img = (BitmapDrawable) res.getDrawable(R.drawable.img1);
        profileImg.setImageDrawable(img);
        profileImg.getLayoutParams().width = (int)(img.getIntrinsicWidth()*1.2);
        profileImg.getLayoutParams().height = (int)(img.getIntrinsicHeight()*1.2);;


        //사용자정보 초기화
        String start_time, end_time, branch;

        name = (TextView)view.findViewById(R.id.memberName);
        due = (TextView)view.findViewById(R.id.dueText);
        info = (TextView)view.findViewById(R.id.infoText);

        name.setText(parentbundle.getString("name"));

        start_time = parentbundle.getString("start_date");
        end_time = parentbundle.getString("end_date");
        branch = parentbundle.getString("branch");
        seat_id = parentbundle.getInt("seat_id");

        due.setText("사용기간 : " + start_time + " ~ " + end_time);

        if(seat_id == -1)
            info.setText(branch + "독서실 " + "자유석 이용자");
        else
            info.setText(branch + "독서실 " + "지정석 "+seat_id+" 이용자");


        //-------------------------------입퇴실 버튼--------------------------
        final String fbranch = branch;
        final int fseat_id = seat_id;
        inoutbt = (Button)view.findViewById(R.id.inoutbt);
        inoutbt.setText("입실하기");// 비입실 상황
        inoutbt.setBackgroundColor(Color.LTGRAY);
        inoutbt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Intent gotoseatIntent = new Intent(view.getContext(), SeatActivity.class);
                final Bundle bundle = new Bundle();
                bundle.putString("name","  입실 및 퇴실");
                bundle.putString("branch",fbranch);
                bundle.putString("user_id",parentbundle.getString("user_id"));
                bundle.putString("regID",parentbundle.getString("regID"));
                bundle.putInt("seat_id",fseat_id);
                bundle.putInt("mode",1);  // 입퇴실 모드로 seatActivity 호출

                // 현재 입실되어있는지 DB를 뒤져보자.
                myRef.child("current").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userData : dataSnapshot.getChildren()) {
                            if(parentbundle.getString("user_id").equals(userData.getKey()+"")) {
                                current = true;
                                break;
                            }
                        }

                        if(current) // 입실되어있다면
                            dilog_yesorno("퇴실하시겠습니까?");
                        else{ //` 입실되어있지않다면
                            if(fseat_id == -1 && bluetoothon){ //자유석이용자이면
                                gotoseatIntent.putExtras(bundle);
                                startActivityForResult(gotoseatIntent,REQUEST_MEMMAIN_TO_SEAT);
                            }
                            else if(fseat_id != -1 && bluetoothon)  //지정석 이용자이면
                                dialogStatic();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });

        //------------------------자리이동 버튼--------------------------

        movebt = (Button)view.findViewById(R.id.movebt);
        movebt.setBackgroundColor(Color.LTGRAY);

        if(fseat_id != -1)
            movebt.setVisibility(View.GONE);

        movebt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(current) {
                    Intent gotoseatIntent = new Intent(view.getContext(), SeatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", "  자리 이동");
                    bundle.putString("branch",fbranch);
                    bundle.putString("user_id",parentbundle.getString("user_id"));
                    bundle.putInt("seat_id",fseat_id);
                    bundle.putInt("mode",2);  // 자리이동 모드로 seatActivity 호출

                    gotoseatIntent.putExtras(bundle);
                    startActivity(gotoseatIntent);
                }
            }
        });

        if(bluetoothAbON){   // 블루투스 강제 온
            bluetoothon = true;
            if(current)
                inoutbt.setBackgroundColor(Color.parseColor("#6799FF"));
            else
                inoutbt.setBackgroundColor(Color.parseColor("#6EC4C4"));
        }

        //블루투스 비콘 연동
        handler = new Handler() {
            public void handleMessage(Message msg) {
                // 비콘의 아이디와 거리를 측정하여 textView에 넣는다.
                for(Beacon beacon : beaconList) {
                    //Log.i("블루투스2","beancon connected : " + beacon.getId1() + " 거리 : " + Double.parseDouble(String.format("%.3f", beacon.getDistance())) + "m");
                    if(beacon.getDistance()<=5) {  // 범위 내에 들어와야 입실 가능
                        bluetoothon = true;
                        if(current)
                            inoutbt.setBackgroundColor(Color.parseColor("#6799FF"));
                        else
                            inoutbt.setBackgroundColor(Color.parseColor("#6EC4C4"));
                    }
                    else{
                        bluetoothon = false;
                    }
                }
                // 자기 자신을 1초마다 호출
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        };
        handler.sendEmptyMessage(0);
        beaconManager = BeaconManager.getInstanceForApplication(view.getContext());
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"); //altbeacon 인식
        //BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
        beaconManager.bind(this);

        return view;
    }

    //====================================== 여기까지가 onCreate ====================================================================================

    @Override
    public void onResume() {
        super.onResume();
        myRef.child("current").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    if (parentbundle.getString("user_id").equals(userData.getKey() + "")) {
                        current = true;
                        saveInoutLog(1);
                        inoutbt.setText("퇴실하기");
                        inoutbt.setBackgroundColor(Color.parseColor("#6799FF"));
                        movebt.setBackgroundColor(Color.parseColor("#6EC4C4"));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    //==========================================================================================
    //  블루투스 비콘 연동
    //==========================================================================================
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
                else {
                    beaconList.clear();
                    //inoutbt.setBackgroundColor(Color.LTGRAY);
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("org.altbeacon.beaconreference",
                    Identifier.parse("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"), null, null));

        } catch (RemoteException e) {   }
    }

    @Override
    public Context getApplicationContext() { return null; }

    @Override
    public void unbindService(ServiceConnection serviceConnection) { }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) { return false; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    //==========================================================================================
    //  퇴실하기 버튼
    //==========================================================================================
    private void dilog_yesorno(String pques){
        Button yesBt,noBt;
        TextView question,title;
        View dlgView = View.inflate(view.getContext(),R.layout.diallog_yesorno,null);

        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(dlgView);

        yesBt = (Button)dlgView.findViewById(R.id.yesbt);
        noBt = (Button)dlgView.findViewById(R.id.nobt);
        question = (TextView)dlgView.findViewById(R.id.question);
        question.setText(pques);
        title = (TextView)dlgView.findViewById(R.id.dlgtitle);
        title.setText("독서실 퇴실");

        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = false; //현재 상태를 퇴실된 상태로
                myRef.child("current").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String floor = dataSnapshot.child(parentbundle.getString("user_id")).child("floor").getValue().toString();
                        String room = dataSnapshot.child(parentbundle.getString("user_id")).child("room").getValue().toString();
                        String key = dataSnapshot.child(parentbundle.getString("user_id")).child("key").getValue().toString();     // 현재 회원의 입실정보 받아오기
                        if(seat_id == -1)// 받아온 입실정보를 기반으로 배치도 state 재설정
                            myRef.child("seat").child("floor").child(floor).child(room).child(key).child("state").setValue(2);
                        else
                            myRef.child("seat").child("floor").child(floor).child(room).child(key).child("state").setValue(5);
                        myRef.child("current").child(parentbundle.getString("user_id")).removeValue(null);                 // 현재 회원의 입실정보 삭제
                        Toast.makeText(view.getContext(),"  퇴실 되었습니다.  ",Toast.LENGTH_SHORT).show();
                        saveInoutLog(2);
                        inoutbt.setText("입실하기");
                        inoutbt.setBackgroundColor(Color.parseColor("#6EC4C4"));
                        movebt.setBackgroundColor(Color.LTGRAY);
                        dialog.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
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

    //==================================================================================================================
    //                     지정석 입실 질문 다이얼로그
    //==================================================================================================================
    private void dialogStatic(){
        Button yesBt,noBt;
        TextView question,title;
        View dlgView = View.inflate(view.getContext(),R.layout.diallog_yesorno,null);
        Log.i("지정석 입실","입실입실???????????????????????????");
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(dlgView);

        yesBt = (Button)dlgView.findViewById(R.id.yesbt);
        noBt = (Button)dlgView.findViewById(R.id.nobt);
        question = (TextView)dlgView.findViewById(R.id.question);
        question.setText("입실 하시겠습니까?");
        title = (TextView)dlgView.findViewById(R.id.dlgtitle);
        title.setText("독서실 입실");

        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 먼저 current DB에 현재 입실한 회원으로 저장 및 seat DB 에 입실된 자리 state 3으로 변경
                myRef.child("static").child(""+parentbundle.getString("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String floor = dataSnapshot.child("floor").getValue().toString();
                        String room = dataSnapshot.child("room").getValue().toString();
                        String key = dataSnapshot.child("key").getValue().toString();
                        myRef.child("seat").child("floor").child(floor).child(room).child(key).child("state").setValue(6);
                        myRef.child("current").child(""+parentbundle.getString("user_id")).child("floor").setValue(floor);
                        myRef.child("current").child(""+parentbundle.getString("user_id")).child("room").setValue(room);
                        myRef.child("current").child(""+parentbundle.getString("user_id")).child("key").setValue(key);
                        myRef.child("current").child(""+parentbundle.getString("user_id")).child("reg_id").setValue(parentbundle.getString("regID"));
                        myRef.child("current").child(""+parentbundle.getString("user_id")).child("user_id").setValue(parentbundle.getString("user_id"));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
                Toast.makeText(view.getContext(), "  입실 되었습니다.  ", Toast.LENGTH_SHORT).show();
                saveInoutLog(1);

                // 버튼 이미지변경
                current = true;
                inoutbt.setText("퇴실하기");
                inoutbt.setBackgroundColor(Color.parseColor("#6799FF"));
                movebt.setBackgroundColor(Color.parseColor("#6EC4C4"));

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

    //===================================================================================================================
    // Member_Activity_InOutLog 저장함수
    //===================================================================================================================
    public void saveInoutLog(int inOrout){
        Date d = new Date(System.currentTimeMillis());
        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sf2 = new SimpleDateFormat("HH:mm:ss");
        sf.setTimeZone(tz);
        sf2.setTimeZone(tz);
        String today = sf.format(d);

        if(inOrout == 1) {
            myRef.child("log").child(today).child("" + parentbundle.getString("user_id")).child("date").setValue(today);
            myRef.child("log").child(today).child("" + parentbundle.getString("user_id")).child("start_time").setValue(sf2.format(d));
            myRef.child("log").child(today).child("" + parentbundle.getString("user_id")).child("end_time").setValue("null");
            myRef.child("log").child(today).child("" + parentbundle.getString("user_id")).child("user_id").setValue(""+parentbundle.getString("user_id"));
        }
        else if(inOrout == 2)
            myRef.child("log").child(today).child("" + parentbundle.getString("user_id")).child("end_time").setValue(sf2.format(d));
    }
}
