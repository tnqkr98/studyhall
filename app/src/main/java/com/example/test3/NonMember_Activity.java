package com.example.test3;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class NonMember_Activity extends AppCompatActivity {

    BitmapDrawable bit1;
    ImageButton eb1,eb2,backbt;
    List<String> listData = new ArrayList<String>();
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonmember);

        eb1 = (ImageButton)findViewById(R.id.im1);
        eb2 = (ImageButton)findViewById(R.id.im2);

        bit1 = (BitmapDrawable) getResources().getDrawable(R.drawable.nonmem_seat);
        eb1.setImageDrawable(bit1);
        eb1.getLayoutParams().width = bit1.getIntrinsicWidth();
        eb1.getLayoutParams().height = bit1.getIntrinsicHeight();

        bit1 = (BitmapDrawable) getResources().getDrawable(R.drawable.nonmem_ren);
        eb2.setImageDrawable(bit1);
        eb2.getLayoutParams().width = bit1.getIntrinsicWidth();
        eb2.getLayoutParams().height = bit1.getIntrinsicHeight();

        listData.add("상도");
        listData.add("흑석");
        listData.add("서초");

        spinner = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_row_spinner,listData);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);

        backbt = (ImageButton)findViewById(R.id.nonback);
        backbt.setImageResource(R.drawable.ic_back);
        backbt.setColorFilter(Color.WHITE);

    }

    public void seatClick(View view) {
        Intent seatIntent = new Intent(getApplicationContext(), SeatActivity.class);

        String branch = spinner.getSelectedItem().toString();

        Bundle bundle = new Bundle();
        bundle.putString("name","  실시간 좌석표");
        bundle.putString("branch",branch);

        seatIntent.putExtras(bundle);
        startActivity(seatIntent);
    }

    public void rentClick(View view) {
        Intent rentIntent = new Intent(getApplicationContext(), NonMember_asset_Activity.class);
        Bundle bundle = new Bundle();
        // 예약 프레그먼트 동기화를 위한 필요정보.   독서실지점, 어플리케이션 실행모드.

        String branch = spinner.getSelectedItem().toString();
        bundle.putString("branch",branch);
        bundle.putString("user_id","00011112222");
        bundle.putString("name","비회원");
        bundle.putInt("mode",2); // 2은 비회원의 예약 모드

        rentIntent.putExtras(bundle);
        startActivity(rentIntent);
    }

    public void backClick(View view) {
        finish();
    }
}
