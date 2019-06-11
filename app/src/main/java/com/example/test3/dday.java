package com.example.test3;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class dday extends Activity {
    /** Called when the activity is first created. */


    public static final String Dday = "dday1";
    public static final String Result= "result1";
    public static final String Name = "name1";

    private TextView ddayText;
    private TextView todayText;
    private TextView resultText;
    private Button dateButton;
    private TextView nameText;

    private String result1;
    private String dday1;
    private String name1;


    private int tYear;           //오늘 연월일 변수
    private int tMonth;
    private int tDay;

    private int dYear=1;        //디데이 연월일 변수
    private int dMonth=1;
    private int dDay=1;


    private long d;
    private long t;
    private long r;

    private int resultNumber=0;

    static final int DATE_DIALOG_ID=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dday);

        final EditText edittext=(EditText)findViewById(R.id.edittext);
        Button button=(Button)findViewById(R.id.입력);
        nameText=(TextView)findViewById(R.id.name);

        ddayText=(TextView)findViewById(R.id.dday);
        todayText=(TextView)findViewById(R.id.today);
        resultText=(TextView)findViewById(R.id.result);
        dateButton=(Button)findViewById(R.id.datebutton);

        dateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(0);//----------------
            }
        });

        Calendar calendar =Calendar.getInstance();              //현재 날짜 불러옴
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar dCalendar =Calendar.getInstance();
        dCalendar.set(dYear,dMonth, dDay);

        t=calendar.getTimeInMillis();                 //오늘 날짜를 밀리타임으로 바꿈
        d=dCalendar.getTimeInMillis();              //디데이날짜를 밀리타임으로 바꿈
        r=(d-t)/(24*60*60*1000);                 // '일'단위로

        resultNumber=(int)r+1;

        updateDisplay();

        resultText.setText("D-");
        ddayText.setText(String.format("0년 0월 0일"));

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText.setText(edittext.getText());
                saveState();
            }
        });
        loadData();
        updateViews();
    }//OnCreate end

    private void updateDisplay(){

        Button button=(Button)findViewById(R.id.datebutton);
        final TextView textView=(TextView)findViewById(R.id.result);

        todayText.setText(String.format("%d년 %d월 %d일",tYear, tMonth+1,tDay));
        ddayText.setText(String.format("%d년 %d월 %d일",dYear, dMonth+1,dDay));

        if(resultNumber>=0){
            resultText.setText(String.format("D-%d", resultNumber));
        }
        else{
            int absR=Math.abs(resultNumber);
            resultText.setText(String.format("D+%d", absR));

        }

    }

    protected void saveState(){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor. putString(Result, resultText.getText().toString());
        editor.putString(Dday, ddayText.getText().toString());
        editor.putString(Name, nameText.getText().toString());
        editor.apply();
        Toast.makeText(this,"DataSaved",Toast.LENGTH_LONG).show();
    }
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        result1 = sharedPreferences.getString(Result, "");
        dday1 = sharedPreferences.getString(Dday,"");
        name1 = sharedPreferences.getString(Name,"");

    }

    public void updateViews() {
        resultText.setText(result1);
        ddayText.setText(dday1);
        nameText.setText(name1);
    }



    private DatePickerDialog.OnDateSetListener dDateSetListener=new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            dYear=year;
            dMonth=monthOfYear;
            dDay=dayOfMonth;
            final Calendar dCalendar =Calendar.getInstance();
            dCalendar.set(dYear,dMonth, dDay);

            d=dCalendar.getTimeInMillis();
            r=(d-t)/(24*60*60*1000);

            resultNumber=(int)r;
            updateDisplay();
            saveState();
        }
    };


    @Override
    protected Dialog onCreateDialog(int id){
        if(id==DATE_DIALOG_ID){
            return new DatePickerDialog(this,dDateSetListener,tYear,tMonth,tDay);

        }
        return null;

    }
}
