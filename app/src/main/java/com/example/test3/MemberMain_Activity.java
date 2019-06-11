package com.example.test3;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class MemberMain_Activity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private ViewPager viewPager;
    private ViewAdapter memAdapter;
    private MenuItem prevMenuItem;

    ImageButton menubt,alertbt;
    BitmapDrawable menuBit,alertBit;
    Bundle bundle;

    public static final int REQUEST_MEMMAIN_TO_SEAT = 101;

    public boolean exitResult = false;  // 종료 질문에 대한 답

    //left 페이지 구현
    Animation translateLeftAnim;
    Animation translateRightAnim;
    LinearLayout leftPage;
    ImageButton menubackbt;
    boolean isPageOpen = false;

    private DatabaseReference logInDBRef,myRef;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_dashboard2:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
               super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_main);

        viewPager = (ViewPager)findViewById(R.id.viewpager_id);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        memAdapter = new ViewAdapter(getSupportFragmentManager());
        memAdapter.AddFragment(new Member_Frag_Home(),"frag1");
        memAdapter.AddFragment(new Member_Frag_cmp(),"frag2");
        memAdapter.AddFragment(new Member_Frag_support(),"freg3");
        memAdapter.AddFragment(new Member_Frag_asset(),"freg4");

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

        menubt = (ImageButton)findViewById(R.id.menubt);
        alertbt = (ImageButton)findViewById(R.id.alertbt);

        Resources res = getResources();
        menuBit = (BitmapDrawable) res.getDrawable(R.drawable.menu);
        alertBit = (BitmapDrawable) res.getDrawable(R.drawable.alert);

        menubt.setImageDrawable(menuBit);
        menubt.getLayoutParams().width = menuBit.getIntrinsicWidth();
        menubt.getLayoutParams().height = menuBit.getIntrinsicHeight();

        alertbt.setImageDrawable(alertBit);
        alertbt.getLayoutParams().width = alertBit.getIntrinsicWidth();
        alertbt.getLayoutParams().height = alertBit.getIntrinsicHeight();

        // 왼쪽 메뉴화면 구현
        leftPage = (LinearLayout)findViewById(R.id.menucontainer);
        translateLeftAnim = AnimationUtils.loadAnimation(this,R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this,R.anim.translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);


        // 블루투스 비콘 연동을 위해 자동 위치권한 승인.
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        logInDBRef = FirebaseDatabase.getInstance().getReference("user");

        alertbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_notice();
            }
        });
        bundle = getIntent().getExtras();
        myRef = FirebaseDatabase.getInstance().getReference(bundle.getString("branch"));

    }

    public void menuClick(View v){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.member_menu,leftPage,true);

        menubackbt = (ImageButton)leftPage.findViewById(R.id.menubackbt);
        menubackbt.setImageResource(R.drawable.ic_back);
        menubackbt.setColorFilter(Color.WHITE);
        menubackbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftPage.startAnimation(translateLeftAnim);
            }
        });

        if(!isPageOpen){
          //  leftPage.startAnimation(translateLeftAnim);
      //  }else{
            leftPage.setVisibility(View.VISIBLE);
            leftPage.startAnimation(translateRightAnim);
        }
    }

    //슬라이딩 리스너 클래스 정의
    private class SlidingPageAnimationListener implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation a){ }

        @Override
        public void onAnimationRepeat(Animation a){ }

        @Override
        public void onAnimationEnd(Animation animation){
            if(isPageOpen){
                leftPage.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            }
            else
                isPageOpen = true;
        }
    }

    @Override
    public void onBackPressed(){ //뒤로가기 버튼 누를 시
        AlertDialog.Builder dig = new AlertDialog.Builder(this);

        dig.setMessage("Study hall 을 종료하시겠습니까?");
        dig.setIcon(R.drawable.ic_launcher_foreground).setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dig.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        dig.show();
    }

    public void logoutClick(View view){ //로그아웃 후 종료
        AlertDialog.Builder dig = new AlertDialog.Builder(this);

        dig.setMessage("로그아웃 하시겠습니까?");
        dig.setIcon(R.drawable.ic_launcher_foreground).setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dig.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = getIntent().getExtras();
                logInDBRef.child(bundle.getString("user_id")).child("reg_ID").setValue("0");
                moveTaskToBack(true);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        dig.show();
    }

    public void complainClick(View view){
        Intent intent = new Intent(getApplicationContext(), Member_MyCMP_Activity.class);
        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
    }

    public void rentCancelClick(View view){
        Intent intent = new Intent(getApplicationContext(),Member_Activity_rentcancel.class);
        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }


    // ===================== notice dialog =================================================//

    public void dialog_notice(){
        final ListView listView;
        final simpleAdapter adapter = new simpleAdapter();
        View dlgView = View.inflate(this, R.layout.dialog_notice, null);

        listView = (ListView)dlgView.findViewById(R.id.listView);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(dlgView);

        myRef.child("notice").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userData : dataSnapshot.getChildren()) {
                    String date = userData.child("date").getValue().toString();
                    String content = userData.child("content").getValue().toString();
                    String title = userData.child("title").getValue().toString();
                    adapter.addItem(title,content,date);
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        dialog.show();
    }

    // ================= 어댑터 정의 ==================================

    public class simpleAdapter extends BaseAdapter {

        public ArrayList<ListViewItem2> listViewItemList = new ArrayList<ListViewItem2>();

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
                convertView = inflater.inflate(R.layout.listrowitem3, parent, false);
            }
            TextView textView1 = (TextView) convertView.findViewById(R.id.textView1);
            TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);
            TextView textView3 = (TextView) convertView.findViewById(R.id.textView3);

            ListViewItem2 listViewItem = listViewItemList.get(position);
            textView1.setText(listViewItem.getRowtext1());
            textView2.setText(listViewItem.getRowtext2());
            textView3.setText(listViewItem.getRowtext3());

            return convertView;
        }

        public void addItem(String text1, String text2, String text3) {
            ListViewItem2 item = new ListViewItem2();
            item.setRowtext1(text1);
            item.setRowtext2(text2);
            item.setRowtext3(text3);

            listViewItemList.add(item);
        }

        public void clearItem(){
            listViewItemList.clear();
        }
    }
}

