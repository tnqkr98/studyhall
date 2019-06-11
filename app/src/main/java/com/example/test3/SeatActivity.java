package com.example.test3;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SeatActivity extends AppCompatActivity {

    public static final String KEY_SEATNAME = "key_seatname";
    private static final int IN_OUT_MODE = 1, MOVE_MODE = 2, CMP_MODE = 3;
    int ActivityMode = 0;

    ImageButton backbt;
    TextView pageName;
    ArrayList<Button> mapElement = new ArrayList<Button>();
    ScrollView mainScroll;
    Button mapButton[][] = new Button[10][10];
    int mapState[][] = new int[10][10];
    Integer[][] mapID = {{R.id.map0000,R.id.map0001,R.id.map0002, R.id.map0003,R.id.map0004,R.id.map0005,R.id.map0006,R.id.map0007,R.id.map0008,R.id.map0009},
            {R.id.map0100,R.id.map0101,R.id.map0102, R.id.map0103,R.id.map0104,R.id.map0105,R.id.map0106,R.id.map0107,R.id.map0108,R.id.map0109},
            {R.id.map0200,R.id.map0201,R.id.map0202, R.id.map0203,R.id.map0204,R.id.map0205,R.id.map0206,R.id.map0207,R.id.map0208,R.id.map0209},
            {R.id.map0300,R.id.map0301,R.id.map0302, R.id.map0303,R.id.map0304,R.id.map0305,R.id.map0306,R.id.map0307,R.id.map0308,R.id.map0309},
            {R.id.map0400,R.id.map0401,R.id.map0402, R.id.map0403,R.id.map0404,R.id.map0405,R.id.map0406,R.id.map0407,R.id.map0408,R.id.map0409},
            {R.id.map0500,R.id.map0501,R.id.map0502, R.id.map0503,R.id.map0504,R.id.map0505,R.id.map0506,R.id.map0507,R.id.map0508,R.id.map0509},
            {R.id.map0600,R.id.map0601,R.id.map0602, R.id.map0603,R.id.map0604,R.id.map0605,R.id.map0606,R.id.map0607,R.id.map0608,R.id.map0609},
            {R.id.map0700,R.id.map0701,R.id.map0702, R.id.map0703,R.id.map0704,R.id.map0705,R.id.map0706,R.id.map0707,R.id.map0708,R.id.map0709},
            {R.id.map0800,R.id.map0801,R.id.map0802, R.id.map0803,R.id.map0804,R.id.map0805,R.id.map0806,R.id.map0807,R.id.map0808,R.id.map0809},
            {R.id.map0900,R.id.map0901,R.id.map0902, R.id.map0903,R.id.map0904,R.id.map0905,R.id.map0906,R.id.map0907,R.id.map0908,R.id.map0909}};
    int mapSeatID[][] = new int[10][10];  // 좌석의 실제번호
    String mapFloor[][] = new String[10][10]; //좌석의 소속층
    String mapRoom[][] = new String[10][10];  //좌석의 소속방
    String mapKey[][] = new String[10][10];   //좌석의 DB상의 키값

    //TableRow tr = new TableRow(getApplicationContext());
    private static DatabaseReference myRef;
    ArrayList<String> rooms,floors;
    Spinner floorSpinner,roomSpinner;
    String floor;

    int seat_id, selectedRow=0,selectedCol=0;
    //Button SearchBt;
    Intent parentintent;
    Bundle parentbundle;

    boolean selectSeat; //좌석 선택
    Bundle returnBundle;
    int pastrow,pastcol; //자리이동시 과거좌표 저장용.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member__free_seat);

        backbt = (ImageButton) findViewById(R.id.backbt);
        backbt.setImageResource(R.drawable.ic_back);
        backbt.setColorFilter(Color.WHITE);

        parentintent = getIntent();
        parentbundle = parentintent.getExtras();
        seat_id = parentbundle.getInt("seat_id");
        ActivityMode = parentbundle.getInt("mode");

        pageName = (TextView) findViewById(R.id.seatmapname);
        pageName.setText(parentbundle.getString("name"));    // Activty Action Bar name

        mainScroll = findViewById(R.id.mapverti);
        mainScroll.setHorizontalScrollBarEnabled(true);

        selectSeat = false;
        returnBundle = new Bundle();

        //--------------------------------------------------------------------------------------------------------------------
        // DB분석 시작 (스피너 초기화 부분)
        //--------------------------------------------------------------------------------------------------------------------
        myRef = FirebaseDatabase.getInstance().getReference(parentbundle.getString("branch"));
        floors = new ArrayList<String>();
        rooms = new ArrayList<String>();

        myRef.child("seat").child("floor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    floors.add(userData.getKey() + "");
                    Log.i("층정보", userData.getKey());
                }
                floorSpinner = (Spinner)findViewById(R.id.spinner5);
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_row_spinner,floors);
                adapter1.setDropDownViewResource(R.layout.custom_spinner_dropdown);
                floorSpinner.setAdapter(adapter1);

                floor = floorSpinner.getSelectedItem().toString();
                myRef.child("seat").child("floor").child(floor).addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userData : dataSnapshot.getChildren())
                            rooms.add(userData.getKey()+"");
                        roomSpinner = (Spinner)findViewById(R.id.spinner6);
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_row_spinner,rooms);
                        adapter2.setDropDownViewResource(R.layout.custom_spinner_dropdown);
                        roomSpinner.setAdapter(adapter2);
                    }
                    @Override
                    public void onCancelled (@NonNull DatabaseError databaseError){ }
                });

                floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        myRef.child("seat").child("floor").child(floorSpinner.getSelectedItem().toString()).addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                rooms.clear();
                                for (DataSnapshot userData : dataSnapshot.getChildren())
                                    rooms.add(userData.getKey()+"");
                                roomSpinner = (Spinner)findViewById(R.id.spinner6);
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_row_spinner,rooms);
                                adapter2.setDropDownViewResource(R.layout.custom_spinner_dropdown);
                                roomSpinner.setAdapter(adapter2);
                                serchbt(getCurrentFocus());
                            }
                            @Override
                            public void onCancelled (@NonNull DatabaseError databaseError){ }
                        });
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) { }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //================================================================================================================================
        //  ▲ DB 분석 끝  ,  ▼ 배치도 각개 좌석 버튼 컨트롤
        //================================================================================================================================

        for (int i = 0; i < 10; i++) {  // 배치도 리프레시 초기화
            for (int j = 0; j < 10; j++) {
                mapButton[i][j] = (Button) findViewById(mapID[i][j]);
                mapButton[i][j].setVisibility(View.GONE);
                mapState[i][j] = 0;
            }
        }

        // 각개 맵버튼 조작
        for(int i = 0; i<10; i++)
            for (int j = 0; j < 10; j++) {
                final int ti = i;
                final int tj = j;
                mapButton[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch(ActivityMode){
                            case IN_OUT_MODE :
                                if(seat_id == -1 && mapState[ti][tj] == 2 && !selectSeat)   // 자유석이용자가 좌석을 선택할 시
                                    dilog_yesorno(mapFloor[ti][tj]+"층 "+mapRoom[ti][tj]+"의 "+mapSeatID[ti][tj]+"번 자리를 사용하시겠습니까?",ti,tj);
                                else if(seat_id == -1 && mapState[ti][tj] == 3)
                                    Toast.makeText(getApplicationContext(),"해당 좌석은 다른사람이 이용하고 있습니다",Toast.LENGTH_SHORT).show();
                                else if(mapState[ti][tj] == 5 || mapState[ti][tj] == 6)
                                    Toast.makeText(getApplicationContext(),"해당 좌석은 지정좌석이므로 선택하실 수 없습니다,",Toast.LENGTH_SHORT).show();
                                else if(selectSeat)
                                    Toast.makeText(getApplicationContext(),"이미 점유하신 좌석이 있습니다. 퇴실 후 다시선택해 주세요.",Toast.LENGTH_SHORT).show();
                                break;
                            case MOVE_MODE :
                                if(seat_id == -1 && mapState[ti][tj] == 2)   // 자유석이용자가 좌석을 선택할 시
                                    dilog_yesorno(mapFloor[ti][tj]+"층 "+mapRoom[ti][tj]+"의 "+mapSeatID[ti][tj]+"번 자리로 이동하시겠습니까?",ti,tj);
                                else if(seat_id == -1 && mapState[ti][tj] == 3)
                                    Toast.makeText(getApplicationContext(),"해당 좌석은 다른사람이 이용하고 있습니다",Toast.LENGTH_SHORT).show();
                                else if(mapState[ti][tj] == 5 || mapState[ti][tj] == 6)
                                    Toast.makeText(getApplicationContext(),"해당 좌석은 지정좌석이므로 선택하실 수 없습니다,",Toast.LENGTH_SHORT).show();
                                break;
                            case CMP_MODE :
                                if(mapState[ti][tj] == 3 || mapState[ti][tj] == 6)  // 입실 된 좌석을 눌러야 CMP 발송가능.
                                    dilog_yesorno(mapSeatID[ti][tj]+"번 이용자에게 경고를 보내겠습니까?",ti,tj);
                                break;
                        }
                    }
                });
            }
    }
    //====================================================================================================================
    // 조회 버튼
    //====================================================================================================================
    public void serchbt(View v){
        final String fl = floorSpinner.getSelectedItem().toString();
        final String ro = roomSpinner.getSelectedItem().toString();
        for (int i = 0; i < 10; i++) {  // 배치도 리프레시 초기화
            for (int j = 0; j < 10; j++) {
                mapButton[i][j] = (Button) findViewById(mapID[i][j]);
                mapButton[i][j].setVisibility(View.GONE);
                mapButton[i][j].setGravity(Gravity.CENTER);
                mapState[i][j] = 0;
            }
        }

        myRef.child("seat").child("floor").child(fl).child(ro).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int MAX_ROW = 0, MAX_COL = 0;
                int seat_id = -2;
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    Log.i("좌표정보", "col: " + userData.child("col").getValue().toString() + ", row: " + userData.child("row").getValue().toString());
                    int row = Integer.parseInt(userData.child("row").getValue().toString());
                    int col = Integer.parseInt(userData.child("col").getValue().toString());
                    int state = Integer.parseInt(userData.child("state").getValue().toString());

                    if(userData.child("seat_id").getValue() != null) // 좌석이면
                        seat_id = Integer.parseInt(userData.child("seat_id").getValue().toString());
                    else
                        seat_id = -2; // 좌석이 아니라 복도인경우

                    mapSeatID[row][col] = seat_id;
                    mapState[row][col] = state;
                    mapFloor[row][col] = fl; mapRoom[row][col] = ro; mapKey[row][col] = userData.getKey()+"";

                    if (MAX_COL < col) MAX_COL = col;
                    if (MAX_ROW < row) MAX_ROW = row;
                }
                for (int i = 0; i <= MAX_ROW; i++) {
                    for (int j = 0; j <= MAX_COL; j++) {
                        if (mapState[i][j] == 0)
                            mapState[i][j] = 1;
                    }
                }
                int num=1;
                for (int i = 0; i <= MAX_ROW; i++) {
                    for (int j = 0; j <=MAX_COL; j++) {
                        switch (mapState[i][j]){
                            case 0:
                                mapButton[i][j].setVisibility(View.GONE);
                                break;
                            case 1:
                                mapButton[i][j].setBackgroundColor(Color.BLACK);
                                mapButton[i][j].setVisibility(View.VISIBLE);
                                mapButton[i][j].setText("");
                                break;
                            case 2:
                                mapButton[i][j].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.outborder));
                                mapButton[i][j].setVisibility(View.VISIBLE);
                                mapButton[i][j].setText("자유석"+mapSeatID[i][j]);
                                break;
                            case 3:
                                mapButton[i][j].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.inborder));
                                mapButton[i][j].setVisibility(View.VISIBLE);
                                mapButton[i][j].setText("자유석"+mapSeatID[i][j]);
                                break;
                            case 4:
                                mapButton[i][j].setBackgroundColor(Color.BLACK);
                                mapButton[i][j].setVisibility(View.VISIBLE);
                                mapButton[i][j].setText("입구");
                                mapButton[i][j].setTextColor(Color.WHITE);
                                break;
                            case 5:
                                mapButton[i][j].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.outborder2));
                                mapButton[i][j].setVisibility(View.VISIBLE);
                                mapButton[i][j].setText("지정석"+mapSeatID[i][j]);
                                break;
                            case 6:
                                mapButton[i][j].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.inborder2));
                                mapButton[i][j].setVisibility(View.VISIBLE);
                                mapButton[i][j].setText("지정석"+mapSeatID[i][j]);
                                break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){ }
        });
    }

    //=============================================================================================================
    // 다이얼로그 설정
    //=============================================================================================================

    private void dilog_yesorno(String pques,int pi,int pj){
        Button yesBt,noBt;
        TextView question,title;
        View dlgView = View.inflate(this,R.layout.diallog_yesorno,null);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(dlgView);

        yesBt = (Button)dlgView.findViewById(R.id.yesbt);
        noBt = (Button)dlgView.findViewById(R.id.nobt);
        question = (TextView)dlgView.findViewById(R.id.question);
        question.setText(pques);
        title = (TextView)dlgView.findViewById(R.id.dlgtitle);
        if(ActivityMode == IN_OUT_MODE)
            title.setText("자유석 입실");
        else if(ActivityMode == MOVE_MODE)
            title.setText("자리 이동");
        else if(ActivityMode == CMP_MODE)
            title.setText("수신자 선택");

        final int ti =pi,tj=pj;
        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(ActivityMode){
                    case IN_OUT_MODE :
                        selectSeat = true;
                        myRef.child("seat").child("floor").child(mapFloor[ti][tj]).child(mapRoom[ti][tj]).child(mapKey[ti][tj]).child("state").setValue(3);
                        myRef.child("current").child(""+parentbundle.getString("user_id")).child("floor").setValue(Integer.parseInt(mapFloor[ti][tj]));
                        myRef.child("current").child(""+parentbundle.getString("user_id")).child("room").setValue(mapRoom[ti][tj]);
                        myRef.child("current").child(""+parentbundle.getString("user_id")).child("key").setValue(mapKey[ti][tj]);
                        myRef.child("current").child(""+parentbundle.getString("user_id")).child("user_id").setValue(parentbundle.getString("user_id"));
                        myRef.child("current").child(""+parentbundle.getString("user_id")).child("reg_id").setValue(parentbundle.getString("regID"));
                        mapButton[ti][tj].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.inborder));
                        Toast.makeText(getApplicationContext(),"  입실 되었습니다.  ",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        break;

                    case MOVE_MODE :
                        myRef.child("current").child(""+parentbundle.getString("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String pastfloor,pastroom,pastkey;
                                pastfloor = dataSnapshot.child("floor").getValue().toString();
                                pastroom = dataSnapshot.child("room").getValue().toString();
                                pastkey = dataSnapshot.child("key").getValue().toString();
                                myRef.child("seat").child("floor").child(pastfloor).child(pastroom).child(pastkey).child("state").setValue(2);

                                // 이전 좌표 퇴실 색상으로 바꾸기
                                myRef.child("seat").child("floor").child(pastfloor).child(pastroom).child(pastkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        pastcol = Integer.parseInt(dataSnapshot.child("col").getValue().toString());
                                        pastrow = Integer.parseInt(dataSnapshot.child("row").getValue().toString());
                                        mapButton[pastrow][pastcol].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outborder));
                                        mapState[pastrow][pastcol] = 2;
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });

                                myRef.child("seat").child("floor").child(mapFloor[ti][tj]).child(mapRoom[ti][tj]).child(mapKey[ti][tj]).child("state").setValue(3);
                                myRef.child("current").child(""+parentbundle.getString("user_id")).child("floor").setValue(Integer.parseInt(mapFloor[ti][tj]));
                                myRef.child("current").child(""+parentbundle.getString("user_id")).child("room").setValue(mapRoom[ti][tj]);
                                myRef.child("current").child(""+parentbundle.getString("user_id")).child("key").setValue(mapKey[ti][tj]);
                                myRef.child("current").child(""+parentbundle.getString("user_id")).child("user_id").setValue(parentbundle.getString("user_id"));
                                myRef.child("current").child(""+parentbundle.getString("user_id")).child("reg_id").setValue(parentbundle.getString("regID"));
                                mapButton[ti][tj].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.inborder));
                                Toast.makeText(getApplicationContext(),"  자리이동  ",Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        break;

                    case CMP_MODE:
                        myRef.child("current").addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {    // 선택한 좌석에 앉아있는 사람의 전화번호를 알아내자!!!
                                for(DataSnapshot userData : dataSnapshot.getChildren()){

                                    if(userData.child("key").getValue().toString().equals(mapKey[ti][tj])) {
                                        Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
                                        result.putExtra("reciever",userData.child("user_id").getValue().toString()); // 호출한 cmp 프레그먼트로 값을 가지고 복귀
                                        result.putExtra("num",mapSeatID[ti][tj]);
                                        result.putExtra("regID",userData.child("reg_id").getValue().toString());

                                        setResult(Activity.RESULT_OK, result);
                                        dialog.cancel();
                                        finish();        // 이전액티비티 복귀
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                }
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(selectSeat) {
            Intent intent = new Intent();
            intent.putExtra("selectSeat",1);
            Log.i("나간다","ㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂㅂ");
           setResult(RESULT_OK, intent);
        }
    }

    public void backbtClick(View v){
        finish();
    }
}
