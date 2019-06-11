package com.example.test3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class AdminMain_Activity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private ViewPager viewPager;
    private ViewAdapter memAdapter;
    private MenuItem prevMenuItem;

    ImageButton alertbt;
    BitmapDrawable alertBit;

    boolean exitResult = false;

    private static DatabaseReference myRef;
    Intent parentintent;
    Bundle parentbundle;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_1:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_2:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_3:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_4:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main_);

        viewPager = (ViewPager)findViewById(R.id.viewpager_id2);

        navigation = (BottomNavigationView) findViewById(R.id.navigation2);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        memAdapter = new ViewAdapter(getSupportFragmentManager());
        memAdapter.AddFragment(new Admin_Frag_Home(),"frag5");
        memAdapter.AddFragment(new Admin_Frag_Complain(),"frag6");
        memAdapter.AddFragment(new Admin_Frag_Message(),"freg7");
        memAdapter.AddFragment(new Admin_Frag_Blacklist(),"freg8");

        viewPager.setAdapter(memAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    navigation.getMenu().getItem(0).setChecked(false);

                navigation.getMenu().getItem(i).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //-------------------------------------------------------------------------------------------------------------
        parentintent = getIntent();
        parentbundle = parentintent.getExtras();
        myRef = FirebaseDatabase.getInstance().getReference(parentbundle.getString("branch"));

        Resources res = getResources();
        alertbt = (ImageButton)findViewById(R.id.onbt);
        alertBit = (BitmapDrawable) res.getDrawable(R.drawable.alert);
        alertbt.setImageDrawable(alertBit);
        alertbt.getLayoutParams().width = alertBit.getIntrinsicWidth();
        alertbt.getLayoutParams().height = alertBit.getIntrinsicHeight();

        alertbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("current_admin_regID").setValue(parentbundle.getString("regID"));
                Toast.makeText(getApplicationContext(), " 근무자 등록 완료 ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed(){ //뒤로가기 버튼 누를 시
        exitDialog();
        if(this.exitResult){
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        else{

        }
    }

    public void exitDialog(){  // 종료 질문 다이얼로그
        AlertDialog.Builder dig = new AlertDialog.Builder(this);

        dig.setMessage("Study hall 을 종료하시겠습니까?");
        dig.setIcon(R.drawable.ic_launcher_foreground).setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AdminMain_Activity.this.exitResult = false;
            }
        });
        dig.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AdminMain_Activity.this.exitResult = true;
            }
        });
        dig.show();
    }

}
