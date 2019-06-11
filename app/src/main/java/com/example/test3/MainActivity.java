package com.example.test3;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.sql.DriverManager;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView,background;
    BitmapDrawable logo,back;
    Resources res;

    private EditText id,password;
    //boolean isMember = false;

    CheckBox autologincheckBox;

    private DatabaseReference logInDBRef;
    String regID; //단말기 고유 ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();

        imageView = (ImageView)findViewById(R.id.imageView);  // 로고 이미지
        logo = (BitmapDrawable) res.getDrawable(R.drawable.logo3);
        imageView.setImageDrawable(logo);
        imageView.getLayoutParams().width = logo.getIntrinsicWidth();
        imageView.getLayoutParams().height = logo.getIntrinsicHeight();

        background = (ImageView)findViewById(R.id.background);
        back = (BitmapDrawable) res.getDrawable(R.drawable.background);
        background.setImageDrawable(back);

        // ------------------------------------------------------
        FirebaseApp.initializeApp(getApplicationContext());
        logInDBRef = FirebaseDatabase.getInstance().getReference("user");  // 로그인을 위한 디비 가져오기

        regID = FirebaseInstanceId.getInstance().getToken(); // 단말기 등록아이디 확인
        autoLogInCheck();
    }

    public void click1(View view) {
        //Toast.makeText(getApplicationContext(),"회원",Toast.LENGTH_LONG).show();
        Dialog_Login();
    }

    public void click2(View view) {
        //Toast.makeText(getApplicationContext(),"비회원",Toast.LENGTH_LONG).show();

        Intent gotoMemberMainIntent = new Intent(getApplicationContext(), NonMember_Activity.class);
        startActivity(gotoMemberMainIntent);
    }

    private void Dialog_Login(){   //회원 로그인 팝업창 설정

        View dlgView = View.inflate(this,R.layout.dialog_login,null);

        Dialog loginDialog = new Dialog(this);
        loginDialog.setContentView(dlgView);

        id = (EditText)dlgView.findViewById(R.id.loginid);
        password = (EditText)dlgView.findViewById(R.id.loginpassword);
        id.setText("");
        password.setText("");


        /*Window dialogWindow = loginDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //lp.width = 900;
        //lp.height = 1000;
        //lp.alpha = 0.7f;
        dialogWindow.setAttributes(lp);*/


        autologincheckBox = (CheckBox)dlgView.findViewById(R.id.autologincheckBox);


        loginDialog.show();
    }

    public void loginClick(View view)
    {
        ////// 로그인 구현

        logInDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = id.getText().toString();
                String passwd = password.getText().toString();
                Log.i("하이","읽은 정보 : " + name);
                for(DataSnapshot userData : dataSnapshot.getChildren()) {

                    String nameofdb = userData.child("user_id").getValue().toString();
                    String passwdofdb =  userData.child("password").getValue().toString();
                    String num = userData.child("user_or_admin").getValue().toString();

                    if(name.equals(nameofdb)){
                        if(passwd.equals(passwdofdb)) {
                            switch (num) {   // 0이면 회원로그인, 1이면 관리자 로그인
                                case "0" :
                                    Intent gotoMemIntent = new Intent(getApplicationContext(), MemberMain_Activity.class);
                                    Log.i("회원 로그인","읽은 정보 : " + name);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name",userData.child("name").getValue().toString());
                                    bundle.putString("start_date",userData.child("start_date").getValue().toString());
                                    bundle.putString("end_date",userData.child("end_date").getValue().toString());
                                    bundle.putString("branch",userData.child("branch").getValue().toString());
                                    bundle.putString("user_id",userData.child("user_id").getValue().toString());
                                    bundle.putString("regID",regID);
                                    bundle.putInt("seat_id",Integer.parseInt(userData.child("seat_id").getValue().toString()));
                                    bundle.putInt("mode",1);  // 1은 회원의 예약 모드

                                    Intent fcmserviceIntent = new Intent(getApplicationContext(),MyFirebaseMessagingService.class);
                                    fcmserviceIntent.putExtra("user_id",name);
                                    startService(fcmserviceIntent);

                                    //자동로그인 구현
                                    if(autologincheckBox.isChecked())  //자동로그인 체크박스가 체크되어있다면
                                        logInDBRef.child(nameofdb).child("reg_ID").setValue(regID);
                                    else
                                        logInDBRef.child(nameofdb).child("reg_ID").setValue("0");


                                    gotoMemIntent.putExtras(bundle);
                                    startActivity(gotoMemIntent);
                                    break;
                                case "1" :
                                    Intent gotoAdminIntent = new Intent(getApplicationContext(), AdminMain_Activity.class);
                                    Log.i("관리자 로그인","읽은 정보 : " + name);
                                    Bundle bundle2 = new Bundle();
                                    bundle2.putString("branch",userData.child("branch").getValue().toString());
                                    bundle2.putString("regID",regID);
                                    gotoAdminIntent.putExtras(bundle2);

                                    Intent fcmserviceIntent2 = new Intent(getApplicationContext(),MyFirebaseMessagingService.class);
                                    fcmserviceIntent2.putExtra("user_id",name);
                                    startService(fcmserviceIntent2);

                                    startActivity(gotoAdminIntent);
                                    break;
                            }
                        }
                        //else
                            //Toast.makeText(getApplicationContext(),"비밀번호를 확인해 주세요",Toast.LENGTH_LONG).show();
                    }
                    //else
                        //Toast.makeText(getApplicationContext(),"아이디를 확인해 주세요",Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //------------------------------------------------------------------------------------------------------------
    public void autoLogInCheck() {
        logInDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userData : dataSnapshot.getChildren()){
                    if(userData.child("reg_ID").getValue().toString().equals(regID)){
                        Intent gotoMemIntent = new Intent(getApplicationContext(), MemberMain_Activity.class);
                        Log.i("회원 자동 로그인","읽은 정보 : " + userData.child("user_id").getValue().toString());
                        Bundle bundle = new Bundle();
                        bundle.putString("name",userData.child("name").getValue().toString());
                        bundle.putString("start_date",userData.child("start_date").getValue().toString());
                        bundle.putString("end_date",userData.child("end_date").getValue().toString());
                        bundle.putString("branch",userData.child("branch").getValue().toString());
                        bundle.putString("user_id",userData.child("user_id").getValue().toString());
                        bundle.putString("regID",regID);
                        bundle.putInt("seat_id",Integer.parseInt(userData.child("seat_id").getValue().toString()));
                        bundle.putInt("mode",1);  // 1은 회원의 예약 모드

                        Intent fcmserviceIntent = new Intent(getApplicationContext(),MyFirebaseMessagingService.class);
                        fcmserviceIntent.putExtra("user_id",userData.child("user_id").getValue().toString());
                        startService(fcmserviceIntent);

                        gotoMemIntent.putExtras(bundle);
                        startActivity(gotoMemIntent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    //----------------------------------------------------------------------------------------------------------

    @Override
    protected void onNewIntent(Intent intent){
        Log.i("통신","onNewIntent() called");

        if(intent != null)
            processIntent(intent);
        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent){
        String from = intent.getStringExtra("from");
        if(from == null){
            Log.i("통신","from is null");
            return;
        }

        String contents = intent.getStringExtra("contents");
        Log.i("통신","DATA : "+from+","+contents);

        Log.i("DATA : ","["+from+"]으로 부터 수신받은 메시지 : "+contents);

    }

    @Override
    public void onClick(View view){ }
}


