package com.example.test3;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.app.DatePickerDialog.OnDateSetListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class userActivity extends AppCompatActivity {

    // DB에 저장시킬 데이터를 입력받는 EditText
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private Spinner spinner;
    private Switch sw;

    public String str5="";

    // 입력받은 데이터를 저장시킬 버튼
    private Button inputBtn;
    private Button startbtn;
    private Button endbtn;

    //달력
    private final int free_user=-1;
    final int DIALOG_SDATE=1,DIALOG_EDATE=2;
    private String start;
    private String end;
    private DatabaseReference myRef;

    private String[] list = {"상도","서초","흑석"};
    private ArrayList<String> nameofdb = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // 변수 초기화
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        startbtn = (Button) findViewById(R.id.startbtn);
        endbtn = (Button) findViewById(R.id.endbtn);
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

        spinner = (Spinner) findViewById(R.id.spinner);
        inputBtn = (Button) findViewById(R.id.inputBtn);

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

        ArrayAdapter spinnerAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,list);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str5 = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // DB 관련 변수 초기화
        // user Reference가 없어도 상관 x
        myRef = FirebaseDatabase.getInstance().getReference("user");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    nameofdb.add(userData.child("user_id").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        // 버튼 리스너 정의
        // 클릭시 EditText의 내용이 DB에 저장
        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str1 = editText1.getText().toString();
                String str2 = editText2.getText().toString();
                String str3 = editText3.getText().toString();
                String str4 = editText4.getText().toString();

                if (nameofdb.contains(editText1.getText().toString())) {
                    Toast.makeText(userActivity.this, "이미 등록된 회원번호입니다.", Toast.LENGTH_SHORT).show();   //메세지 출력
                    return;
                }
                else if (editText1.getText().toString().equals("") ||        //입력을 하지 않았으면
                        editText2.getText().toString().equals("") ||
                        editText3.getText().toString().equals("") ||
                        startbtn.getText().equals("시작날짜") || endbtn.getText().equals("종료날짜") ||
                        str5.equals("")) {
                    Toast.makeText(userActivity.this, "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show();   //메세지 출력
                    return;
                }
                else if (editText1.getText().length() < 11) {
                    Toast.makeText(userActivity.this, "user_id(핸드폰번호)를 11자리\n 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (editText2.getText().length() < 4) {
                    Toast.makeText(userActivity.this, "비밀번호를 4자리\n 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (Integer.parseInt(start)>Integer.parseInt(end)) {
                    startbtn.setText("시작날짜");
                    endbtn.setText("종료날짜");
                    Toast.makeText(userActivity.this, "날짜가 잘못되었습니다.", Toast.LENGTH_SHORT).show();   //메세지 출력
                    return;
                }

                // push는 firebase가 임의로 중복되지 않은 키를 생성해서 저장
                // push로 하지 않을 경우 덮어 씌움
                myRef.child(editText1.getText().toString()).child("user_id").setValue(str1);
                myRef.child(editText1.getText().toString()).child("password").setValue(str2);
                myRef.child(editText1.getText().toString()).child("name").setValue(str3);
                myRef.child(editText1.getText().toString()).child("branch").setValue(str5);
                myRef.child(editText1.getText().toString()).child("user_or_admin").setValue(0);
                myRef.child(editText1.getText().toString()).child("start_date").setValue(start);
                myRef.child(editText1.getText().toString()).child("end_date").setValue(end);
                myRef.child(editText1.getText().toString()).child("reg_ID").setValue("0");
                if (editText4.getText().toString().equals(""))
                    myRef.child(editText1.getText().toString()).child("seat_id").setValue(free_user);
                else
                    myRef.child(editText1.getText().toString()).child("seat_id").setValue(Integer.parseInt(editText4.getText().toString()));
                //if(sw.isChecked()) {
                //    myRef.child("static").child(str1).child("floor").setValue();
                //    myRef.child("static").child(str1).child("key").setValue();
                //    myRef.child("static").child(str1).child("room").setValue();
                //}

                // EditText 초기화
                editText1.setText("");
                editText2.setText("");
                editText3.setText("");
                editText4.setText("");
                startbtn.setText("시작날짜");
                endbtn.setText("종료날짜");

                Toast.makeText(getApplicationContext(), "등록 완료", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SDATE:
                DatePickerDialog sdpd = new DatePickerDialog(userActivity.this, R.style.DialogTheme, new OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                        start=String.format("%02d%02d%02d", year,monthOfYear+1, dayOfMonth);
                        startbtn.setText(start);
                    }
                }
                        ,2019, 5, 1); // 기본값 연월일
                return sdpd;
            case DIALOG_EDATE:
                DatePickerDialog edpd = new DatePickerDialog(userActivity.this, R.style.DialogTheme,new OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                        end=String.format("%02d%02d%02d", year,monthOfYear+1, dayOfMonth);
                        endbtn.setText(end);
                    }
                }
                        ,2019, 5, 1); // 기본값 연월일
                return edpd;
        }
        return super.onCreateDialog(id);
    }
}