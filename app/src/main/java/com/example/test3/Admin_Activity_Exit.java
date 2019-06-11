package com.example.test3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Admin_Activity_Exit extends AppCompatActivity {

    public static final String KEY_SEATNAME = "key_seatname";
    private static final int EXIT_MODE = 1;
    int ActivityMode = 1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        backbt = (ImageButton) findViewById(R.id.backbt);
        backbt.setImageResource(R.drawable.ic_back);
        backbt.setColorFilter(Color.WHITE);

        parentintent = getIntent();
        //parentbundle = parentintent.getExtras();
        //seat_id = parentbundle.getInt("seat_id");

        pageName = (TextView) findViewById(R.id.seatmapname);

        mainScroll = findViewById(R.id.mapverti);
        mainScroll.setHorizontalScrollBarEnabled(true);

        selectSeat = false;
        returnBundle = new Bundle();

        //-----------------------------------db분석(스피너 초기화 부분)-----------------------------------------------------------
        myRef = FirebaseDatabase.getInstance().getReference(parentintent.getStringExtra("branch"));
        floors = new ArrayList<String>();
        rooms = new ArrayList<String>();

        myRef.child("seat").child("floor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    floors.add(userData.getKey() + "");
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


        //-------------------------------------------------------------------------------------------------------------------
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
                            case EXIT_MODE :
                                AlertDialog.Builder dialog = new AlertDialog.Builder(Admin_Activity_Exit.this,R.style.AlertDialog);
                                if(mapState[ti][tj]==3) {
                                    dialog.setTitle("퇴실 처리")
                                            .setMessage("해당 자리 사용자를 퇴실 처리 하시겠습니까?")
                                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    myRef.child("current").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                                                                String s = fileSnapshot.getKey();
                                                                String str = fileSnapshot.child("key").getValue().toString();
                                                                if (str.equals(""+mapKey[ti][tj])) {
                                                                    Log.i("gk", "" + myRef.child("current").child(s).getKey());
                                                                    myRef.child("current").child(s).removeValue();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });
                                                    //myRef.child("current").child(""+parentbundle.getString("user_id")).removeValue(null);
                                                    myRef.child("seat").child("floor").child(mapFloor[ti][tj]).child(mapRoom[ti][tj]).child(mapKey[ti][tj]).child("state").setValue(2);
                                                    Admin_Activity_Exit.this.finish();
                                                    Toast.makeText(getApplicationContext(), "퇴실 완료", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    //Toast.makeText(getApplicationContext(), "퇴실 취소", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .create()
                                            .show();
                                }
                                else if(mapState[ti][tj]==6) {
                                    dialog.setTitle("퇴실 처리")
                                            .setMessage("해당 자리 사용자를 퇴실 처리 하시겠습니까?")
                                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    myRef.child("current").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                                                                String s = fileSnapshot.getKey();
                                                                String str = fileSnapshot.child("key").getValue().toString();
                                                                if (str.equals(""+mapKey[ti][tj])) {
                                                                    Log.i("gk", "" + myRef.child("current").child(s).getKey());
                                                                    myRef.child("current").child(s).removeValue();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });
                                                    //myRef.child("current").child(""+parentbundle.getString("user_id")).removeValue(null);
                                                    myRef.child("seat").child("floor").child(mapFloor[ti][tj]).child(mapRoom[ti][tj]).child(mapKey[ti][tj]).child("state").setValue(5);
                                                    Admin_Activity_Exit.this.finish();
                                                    Toast.makeText(getApplicationContext(), "퇴실 완료", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    //Toast.makeText(getApplicationContext(), "퇴실 취소", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .create()
                                            .show();
                                }
                                break;
                        }
                    }
                });
            }

    }

    //==================================================================================================
    // 조회 버튼
    //===================================================================================================
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
                                mapButton[i][j].setText("좌석"+mapSeatID[i][j]);
                                break;
                            case 3:
                                mapButton[i][j].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.inborder));
                                mapButton[i][j].setVisibility(View.VISIBLE);
                                mapButton[i][j].setText("좌석"+mapSeatID[i][j]);
                                break;
                            case 4:
                                mapButton[i][j].setBackgroundColor(Color.BLACK);
                                mapButton[i][j].setVisibility(View.VISIBLE);
                                mapButton[i][j].setText("입구");
                                mapButton[i][j].setTextColor(Color.WHITE);
                            case 5:
                                mapButton[i][j].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.outborder));
                                mapButton[i][j].setVisibility(View.VISIBLE);
                                mapButton[i][j].setText("지정석"+mapSeatID[i][j]);
                                break;
                            case 6:
                                mapButton[i][j].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.inborder));
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

    public void backbtClick(View v){
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
