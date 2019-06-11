package com.example.test3;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Member_Frag_asset_ItemView extends LinearLayout {    // 예약물 뷰를 각개 관리하는 클래스

    private TextView[] time = new TextView[25];
    private TextView name;
    private Button rentbutton;

    public Member_Frag_asset_ItemView(Context context) {
        super(context);
        init(context);
    }

    public Member_Frag_asset_ItemView(Context context, AttributeSet attr){
        super(context);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memcmp_singleitem,this,true);

        name = (TextView)findViewById(R.id.asset_name);
        rentbutton = (Button)findViewById(R.id.rentbutton);

        time[5] = (TextView)findViewById(R.id.time5);
        time[6] = (TextView)findViewById(R.id.time6);
        time[7] = (TextView)findViewById(R.id.time7);
        time[8] = (TextView)findViewById(R.id.time8);
        time[9] = (TextView)findViewById(R.id.time9);
        time[10] = (TextView)findViewById(R.id.time10);
        time[11] = (TextView)findViewById(R.id.time11);
        time[12] = (TextView)findViewById(R.id.time12);
        time[13] = (TextView)findViewById(R.id.time13);
        time[14] = (TextView)findViewById(R.id.time14);
        time[15] = (TextView)findViewById(R.id.time15);
        time[16] = (TextView)findViewById(R.id.time16);
        time[17] = (TextView)findViewById(R.id.time17);
        time[18] = (TextView)findViewById(R.id.time18);
        time[19] = (TextView)findViewById(R.id.time19);
        time[20] = (TextView)findViewById(R.id.time20);
        time[21] = (TextView)findViewById(R.id.time21);
        time[22] = (TextView)findViewById(R.id.time22);
        time[23] = (TextView)findViewById(R.id.time23);
    }

    public void setTimeColor(int t){
        time[t].setBackgroundColor(Color.BLACK);
    }

    public void setName(String pname){ name.setText(pname); }

    public Button getRentButton(){
        return rentbutton;
    }

}
