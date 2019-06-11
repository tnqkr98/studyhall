package com.example.test3;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import java.util.ArrayList;
import android.widget.ImageView;


public class Timer extends Activity implements OnItemSelectedListener {

    SharedPreferences prefs;
    Editor ePref;
    Button start,go,stop,allstop;
    Boolean bool = true;
    EditText in;
    TextView tv;
    String text,Str;
    int t,hour,minute,second;
    int current_img;


    int img[] = {
            0,
            R.drawable.toeic2,
            R.drawable.toeic2,
            R.drawable.toefl,
            R.drawable.toeicspeaking,
            R.drawable.toeicspeaking,
            R.drawable.jjj,
            R.drawable.com,
            R.drawable.s,
            R.drawable.s,
            R.drawable.s,
            R.drawable.s,
            R.drawable.s,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        start = (Button) findViewById(R.id.start);
        go = (Button) findViewById(R.id.go);
        stop = (Button) findViewById(R.id.stop);
        allstop = (Button) findViewById(R.id.allstop);
        in = (EditText) findViewById(R.id.in);
        tv = (TextView) findViewById(R.id.tv);


        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.test, R.layout.custom_row_spinner);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        ImageView imgView_photo = (ImageView) findViewById(R.id.imgView_photo);

        imgView_photo.setImageResource(current_img);

        prefs = getSharedPreferences("Save", Activity.MODE_PRIVATE);
        t = prefs.getInt("t", t);

        sum();
        Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
        tv.setText(Str);

        in.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                in.setText("");
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    text = in.getText().toString();
                    t = Integer.parseInt(text);
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                } catch (Exception e) {
                }
            }
        });
        go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bool = true;
                thread threadTest = new thread();
                threadTest.setDaemon(true);
                threadTest.start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bool = false;
            }
        });

        allstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bool = false;
                t = 0;
                sum();
                Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                tv.setText(Str);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {

            ImageView imgView_photo = (ImageView) findViewById(R.id.imgView_photo);
            imgView_photo.setImageResource(current_img);
            switch (position) {
                case 1:
                    imgView_photo.setImageResource(R.drawable.toeic2);
                    t = 60*45;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 2:
                    imgView_photo.setImageResource(R.drawable.toeic2);
                    t = 60*75;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 3:
                    imgView_photo.setImageResource(R.drawable.toefl);
                    t = 60*270;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 4:
                    imgView_photo.setImageResource(R.drawable.toeicspeaking);
                    t = 45;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 5:
                    imgView_photo.setImageResource(R.drawable.toeicspeaking);
                    t = 60;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 6:
                    imgView_photo.setImageResource(R.drawable.jjj);
                    t = 60*30;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 7:
                    imgView_photo.setImageResource(R.drawable.com);
                    t = 60*60;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 8:
                    imgView_photo.setImageResource(R.drawable.s);
                    t = 60*80;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 9:
                    imgView_photo.setImageResource(R.drawable.s);
                    t = 60*100;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 10:
                    imgView_photo.setImageResource(R.drawable.s);
                    t = 60*70;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 11:
                    imgView_photo.setImageResource(R.drawable.s);
                    t = 60*30;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
                case 12:
                    imgView_photo.setImageResource(R.drawable.s);
                    t = 60*40;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초", hour, minute, second);
                    tv.setText(Str);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class thread extends Thread{
        public void run(){
            while(bool){
                handler.sendEmptyMessage(0);
                try{
                    Thread.sleep(1000);
                }catch (Exception e){}
            }
        }
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what ==0){
                if(t>0){
                    Log.d("fureun","XD");
                    t--;
                    sum();
                    Str = String.format("%02d시간 %02d분 %02d초",hour,minute,second);
                    tv.setText(Str);
                }
            }
        }
    };

    public void sum(){
        hour =  t/3600;
        minute = (t%3600)/60;
        second = (t%3600)%60;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        bool = false;
        ePref = prefs.edit();
        ePref.putInt("t",t);
        ePref.commit();
    }
}
