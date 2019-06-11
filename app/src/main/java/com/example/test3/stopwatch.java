package com.example.test3;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class stopwatch extends Activity implements SensorEventListener {
    TextView myOutput;
    TextView myRec;
    Button myBtnStart;
    Button myBtnRec;

    private SensorManager mSensorManager;
    private Sensor mProximity;

    final static int Init = 0;
    final static int Run = 1;
    final static int Pause = 2;

    int cur_Status = Init; //현재의 상태를 저장할변수를 초기화함.
    int myCount = 1;
    long myBaseTime;
    long myPauseTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        myOutput = (TextView) findViewById(R.id.time_out);
        myRec = (TextView) findViewById(R.id.record);
        myBtnStart = (Button) findViewById(R.id.btn_start);
        myBtnRec = (Button) findViewById(R.id.btn_rec);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        loadData();
    }

    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    public final void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];

        if (event.values[0] == 0) {
            switch (cur_Status) {
                case Init:
                    myBaseTime = SystemClock.elapsedRealtime();
                    System.out.println(myBaseTime);
                    myTimer.sendEmptyMessage(0);
                    myBtnStart.setText("멈춤"); //버튼의 문자"시작"을 "멈춤"으로 변경
                    myBtnRec.setEnabled(true); //기록버튼 활성
                    cur_Status = Run; //현재상태를 런상태로 변경
                    //String str = myRec.getText().toString();
                    // str +=  String.format("%d. %s\n",myCount,getTimeOut());
                    // myRec.setText(str);
                    myCount++;
                    break;
                case Run:
                    myTimer.removeMessages(0); //핸들러 메세지 제거
                    myPauseTime = SystemClock.elapsedRealtime();
                    myBtnStart.setText("시작");
                    myBtnRec.setText("리셋");
                    cur_Status = Pause;
                    break;
                case Pause:
                    long now = SystemClock.elapsedRealtime();
                    myTimer.sendEmptyMessage(0);
                    myBaseTime += (now - myPauseTime);
                    myBtnStart.setText("멈춤");
                    myBtnRec.setText("기록");
                    cur_Status = Run;
                    break;
            }

        } else {
            myTimer.removeMessages(0);
            myBtnStart.setText("시작");
            myCount = 1;
            myRec.setText("");
            myBtnRec.setEnabled(true);
            myPauseTime = SystemClock.elapsedRealtime();
            myBtnRec.setText("리셋");
            cur_Status = Pause;
        }
        saveState();
    }

    protected void saveState() {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        myBaseTime = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void myOnClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start: //시작버튼을 클릭했을때 현재 상태값에 따라 다른 동작을 할수있게끔 구현.
                switch (cur_Status) {
                    case Init:
                        myBaseTime = SystemClock.elapsedRealtime();
                        System.out.println(myBaseTime);
                        //myTimer이라는 핸들러를 빈 메세지를 보내서 호출
                        myTimer.sendEmptyMessage(0);
                        myBtnStart.setText("멈춤"); //버튼의 문자"시작"을 "멈춤"으로 변경
                        myBtnRec.setEnabled(true); //기록버튼 활성
                        cur_Status = Run; //현재상태를 런상태로 변경
                        String str = myRec.getText().toString();
                        str += String.format("%d. %s\n", myCount, getTimeOut());
                        myRec.setText(str);
                        myCount++;
                        break;
                    case Run:
                        myTimer.removeMessages(0); //핸들러 메세지 제거
                        myPauseTime = SystemClock.elapsedRealtime();
                        myBtnStart.setText("시작");
                        myBtnRec.setText("리셋");
                        cur_Status = Pause;
                        break;
                    case Pause:
                        long now = SystemClock.elapsedRealtime();
                        myTimer.sendEmptyMessage(0);
                        myBaseTime += (now - myPauseTime);
                        myBtnStart.setText("멈춤");
                        myBtnRec.setText("기록");
                        cur_Status = Run;
                        break;
                }
                break;
            case R.id.btn_rec:
                switch (cur_Status) {
                    case Run:
                        String str = myRec.getText().toString();
                        str += String.format("%d. %s\n", myCount, getTimeOut());
                        myRec.setText(str);
                        myCount++; //카운트 증가
                        break;
                    case Pause:
                        //핸들러를 멈춤
                        myTimer.removeMessages(0);

                        myBtnStart.setText("시작");
                        myBtnRec.setText("기록");
                        myOutput.setText("00:00:00");
                        cur_Status = Init;
                        myCount = 1;
                        myRec.setText("");
                        myBtnRec.setEnabled(false);
                        break;
                }
                break;

        }
    }

    Handler myTimer = new Handler() {
        public void handleMessage(Message msg) {
            myOutput.setText(getTimeOut());

            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            myTimer.sendEmptyMessage(0);
        }
    };

    //현재시간을 계속 구해서 출력하는 메소드
    String getTimeOut() {
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        long outTime = now - myBaseTime;
        String easy_outTime = String.format("%02d:%02d:%02d", outTime / 1000 / 60, (outTime / 1000) % 60, (outTime % 1000) / 10);
        return easy_outTime;
    }
}