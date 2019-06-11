package com.example.test3;

//회원정보 수정 액티비티

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog.OnDateSetListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Admin_Activity_Revise extends AppCompatActivity {
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private Button setbtn;
    private Button delete;
    private Button button1;

    final int DIALOG_SDATE=1,DIALOG_EDATE=2;
    private String start;
    private String end;

    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    private String name, seat_id, password;
    private String newString;
    private Switch sw;
    private Button startbtn;
    private Button endbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                newString= extras.getString("user_id");
            }
        }

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("user");

        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);

        sw = (Switch) findViewById(R.id.switch1);
        sw.setChecked(false);
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Button seat_name = (Button)findViewById(R.id.seat_name);
                if(sw.isChecked()) {
                    sw.setText("지정석");
                    editText4.setVisibility(View.VISIBLE);
                }
                else{
                    sw.setText("자유석");
                    editText4.setVisibility(View.INVISIBLE);
                }
            }
        });

        startbtn = (Button) findViewById(R.id.startbtn);
        endbtn = (Button) findViewById(R.id.endbtn);
        button1 = (Button) findViewById(R.id.button1);
        startbtn.setBackgroundColor(Color.parseColor("#6EC4C4"));
        endbtn.setBackgroundColor(Color.parseColor("#6EC4C4"));
        button1.setBackgroundColor(Color.parseColor("#FFFFFF"));

        startbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SDATE);
            }
        });
        endbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_EDATE); // 날짜 설정 다이얼로그 띄우기
            }
        });

        setbtn = (Button) findViewById(R.id.setbtn);
        delete = (Button) findViewById(R.id.delete);
        setbtn.setBackgroundColor(Color.parseColor("#6EC4C4"));
        delete.setBackgroundColor(Color.parseColor("#6EC4C4"));

        databaseRef.child(newString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                password = dataSnapshot.child("password").getValue().toString();
                seat_id = dataSnapshot.child("seat_id").getValue().toString();
                start = dataSnapshot.child("start_date").getValue().toString();
                end = dataSnapshot.child("end_date").getValue().toString();

                editText2.setText(password);
                editText3.setText(name);
                if (seat_id.equals("-1")) {
                    sw.setText("자유석");
                    editText4.setVisibility(View.INVISIBLE);
                    editText4.setText("");
                }
                else {
                    sw.setText("지정석");
                    editText4.setVisibility(View.VISIBLE);
                    editText4.setText(seat_id);
                    sw.toggle();
                }

                startbtn.setText(start);
                endbtn.setText(end);
                //Toast.makeText(Admin_Activity_Revise.this, name, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText2.getText().toString().equals("") || editText3.getText().toString().equals("")) {
                    Toast.makeText(Admin_Activity_Revise.this, "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show();   //메세지 출력
                    return;
                }
                else if (editText2.getText().length() < 4) {
                    Toast.makeText(Admin_Activity_Revise.this, "비밀번호를 4자리\n 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (Integer.parseInt(startbtn.getText().toString())>Integer.parseInt(endbtn.getText().toString())) {
                    startbtn.setText(start);
                    endbtn.setText(end);
                    Toast.makeText(Admin_Activity_Revise.this, "날짜 수정 오류", Toast.LENGTH_SHORT).show();   //메세지 출력
                    return;
                }

                databaseRef.child(newString).child("password").setValue(editText2.getText().toString());
                databaseRef.child(newString).child("name").setValue(editText3.getText().toString());

                if(sw.isChecked())
                    databaseRef.child(newString).child("seat_id").setValue(Integer.parseInt(editText4.getText().toString()));
                else databaseRef.child(newString).child("seat_id").setValue(-1);

                databaseRef.child(newString).child("start_date").setValue(Integer.parseInt(startbtn.getText().toString()));
                databaseRef.child(newString).child("end_date").setValue(endbtn.getText().toString());

                Toast.makeText(Admin_Activity_Revise.this, "수정 완료", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Admin_Activity_Revise.this,R.style.AlertDialog);
                dialog.setTitle("데이터 삭제")
                        .setMessage("해당 데이터를 삭제하시겠습니까?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseRef.child(newString).removeValue(null);
                                Admin_Activity_Revise.this.finish();
                                Toast.makeText(getApplicationContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(), "삭제 취소", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
            }
        });
        button1.append(newString);
    }
    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SDATE:
                DatePickerDialog sdpd = new DatePickerDialog(Admin_Activity_Revise.this, R.style.DialogTheme, new OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startbtn.setText(String.format("%02d%02d%02d", year,monthOfYear+1, dayOfMonth));
                    }
                }
                        ,2019, 4, 1); // 기본값 연월일
                return sdpd;
            case DIALOG_EDATE:
                DatePickerDialog edpd = new DatePickerDialog(Admin_Activity_Revise.this, R.style.DialogTheme,new OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                        endbtn.setText(String.format("%02d%02d%02d", year,monthOfYear+1, dayOfMonth));
                    }
                }
                        ,2019, 4, 1); // 기본값 연월일
                return edpd;
        }
        return super.onCreateDialog(id);
    }
}

