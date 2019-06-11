package com.example.test3;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Member_Frag_asset extends Fragment {

    View view;
    static ListView listView;
    static ItemAdapter adapter;
    Button dateSelectBt;
    private static String globalDay;
    Intent intent;
    Bundle bundle;
    int mode = 0;

    TextView number;
    EditText editNumber;

    private static DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_memasset,container,false);

        dateSelectBt = (Button)view.findViewById(R.id.datebt);
        dateSelectBt.setText("----/--/--");
        dateSelectBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment dialogFragment = new DatePickerDialogTheme1();
                dialogFragment.show(getFragmentManager(),"Theme 1");
            }
        });

        //--------------------------- 시간표 -----------------------------------
        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new ItemAdapter();

        // DataBase 연동 파트(현재 임의값)------------------------------------------------------------------------------
        //----- 시설물 초기화

        intent = getActivity().getIntent();  //부모 액티비티에서 인탠드 가져오기

        bundle = intent.getExtras();
        mode = bundle.getInt("mode"); // 회원은 1, 비회원은 2

        myRef = FirebaseDatabase.getInstance().getReference(bundle.getString("branch")); //회원이 속한 독서실로 예약물 동기화

        myRef.child("facility").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userData : dataSnapshot.getChildren()){
                    String asset_name = userData.child("facility_name").getValue().toString();
                    adapter.addItem(new Member_Frag_asset_Item(asset_name));
                    //adapter.addItem(new Member_Frag_asset_Item("스터디룸1"));
                    Log.i("DBconnection","add :" + asset_name);
                }

                //임시시간설정
                //adapter.getItem(0).setTime(10,19);

                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //----- 예약 테이블 초기화
        myRef.child("reservation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Date d = new Date(System.currentTimeMillis());
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
                TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                sf.setTimeZone(tz);

                globalDay = sf.format(d);

                sf = new SimpleDateFormat("yyyy/MM/dd");
                sf.setTimeZone(tz);
                dateSelectBt.setText(sf.format(d));

                for(DataSnapshot userData : dataSnapshot.getChildren()){
                    int startTime = Integer.parseInt(userData.child("start_time").getValue().toString());
                    int endTime = Integer.parseInt(userData.child("end_time").getValue().toString());
                    String date = userData.child("date").getValue().toString();
                    int facID = Integer.parseInt(userData.child("facility_id").getValue().toString());

                    if(date.equals(globalDay)) {   //오늘 날짜 것만 테이블에 초기화
                        Log.i("시간테이블 초기화",""+facID+" : "+startTime+" ~ "+endTime);
                        try {
                            adapter.getItem(facID).setTime(startTime, endTime);
                        }
                        catch (Exception e)
                        {

                        }
                    }


                    //Log.i("DBconnection","add :" + asset_name);
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //Log.i("hi","adapter count :"+adapter.getCount());

        return view;
    }

    //==============================================================================================================================
    // 각개 동적할당된 예약 뷰 컨트롤
    //==============================================================================================================================
    public class ItemAdapter extends BaseAdapter {
        ArrayList<Member_Frag_asset_Item> items = new ArrayList<Member_Frag_asset_Item>();

        @Override
        public int getCount(){
            return items.size();
        }

        public void addItem(Member_Frag_asset_Item item){
            items.add(item);
        }

        public Member_Frag_asset_Item getItem(int position){
            return items.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup){   //어떤 포지션의 뷰가 자동으로 call되는가봄. (뷰가 보여질때마다 호출되는 call-back함수)
            Member_Frag_asset_ItemView view = new Member_Frag_asset_ItemView(getContext());   // 아이템 뷰와 아이템 클래스 연동. 아이템클래스의 정보를 아이템뷰에 설정.
            final Member_Frag_asset_Item item = items.get(position);

            view.setName(item.getName());  // 포지션(인덱스) 에 저장된 커스텀뷰(예약물)의 각개 내부 뷰의 조작

            for(int i=5;i<25;i++){
                if(item.getTime(i)) // 해당 타임에 예약이 존재(true)하면.
                    view.setTimeColor(i);  // 해당 타임의 색깔을 검정으로.
                //Log.i("TimeTableUpdate","pos :" + position + ", time : " +i + ", set : " + item.getTime(i));
            }


            final int gettingPos = position;

            view.getRentButton().setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){
                    Button bt = (Button)getActivity().findViewById(R.id.datebt);
                    Dialog_rent(items.get(gettingPos).getName(),""+bt.getText(),gettingPos);
                    Log.i("DialogCall","asset name : " + items.get(gettingPos).getName());
                }
            });

            return view;
        }
    }

    //==============================================================================================================================
    // 날짜선택 다이얼로그 및 예약테이블 갱신 class
    //==============================================================================================================================
    public static class DatePickerDialogTheme1 extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

            return datepickerdialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day){
            Button bt = (Button)getActivity().findViewById(R.id.datebt);
            bt.setText(year+" / "+(month+1)+" / "+day);

            if(month <10 && day<10)
                globalDay = year + "0"+(month+1)+"0"+day;
            else if(day <10)
                globalDay = year +(month+1)+"0"+day;
            else if(month <10)
                globalDay = year +"0"+(month+1)+day;
            else
                globalDay = year+(month+1)+day+"";

            //globalDay = year+"0"+(month+1)+""+day+"";

            final int inyear = year;  //inner클래스 전달용
            final int inmonth = month;
            final int inday = day;

            //--------------------------------------시설물 예약현황 갱신 -------------------------------------------
            adapter.getItem(0).setAlltimeClear(); //일단 테이블 클리어
            adapter.getItem(1).setAlltimeClear();
            adapter.getItem(2).setAlltimeClear();
            adapter.getItem(3).setAlltimeClear();
            adapter.getItem(4).setAlltimeClear();

            myRef.child("reservation").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int startTime, endTime;
                    String date,selectedDate;

                    for (DataSnapshot userData : dataSnapshot.getChildren()) {
                        startTime = Integer.parseInt(userData.child("start_time").getValue().toString());
                        endTime = Integer.parseInt(userData.child("end_time").getValue().toString());
                        date = userData.child("date").getValue().toString();
                        int facID = Integer.parseInt(userData.child("facility_id").getValue().toString());

                        if(inmonth <10 && inday<10)
                            selectedDate = inyear + "0"+(inmonth+1)+"0"+inday;
                        else if(inday <10)
                            selectedDate = inyear +(inmonth+1)+"0"+inday;
                        else if(inmonth <10)
                            selectedDate = inyear +"0"+(inmonth+1)+inday;
                        else
                            selectedDate = inyear+(inmonth+1)+inday+"";

                        //globalDay = selectedDate;
                        Log.i("DBconnection","add :" +selectedDate);

                        if (date.equals(selectedDate)) {   //선택한 날짜의 것만 테이블에 초기화
                            adapter.getItem(facID).setTime(startTime, endTime);
                        }

                    }
                    listView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            listView.setAdapter(adapter);
        }
    }

    //==============================================================================================================================
    // 예약버튼 다이얼로그 dialog
    //==============================================================================================================================
    private void Dialog_rent(String asset,String day,int pos){   //예약 팝업창 설정

        final View dlgView = View.inflate(view.getContext(),R.layout.dialog_rent,null);
        final Dialog rentDialog = new Dialog(view.getContext());
        rentDialog.setContentView(dlgView);

        TextView assetText,dayText;
        assetText = (TextView)dlgView.findViewById(R.id.rent_asset);
        dayText = (TextView)dlgView.findViewById(R.id.rent_day);
        assetText.setText(asset);
        dayText.setText(day);

        number = (TextView)dlgView.findViewById(R.id.number);
        editNumber = (EditText)dlgView.findViewById(R.id.editNumber);

        if(mode == 1){  // 현재 액티비티의 모드가 회원모드인 경우
            number.setVisibility(View.INVISIBLE);
            editNumber.setVisibility(View.INVISIBLE);
        }


        List<Integer> timeData = new ArrayList<Integer>();
        for(int i = 5;i<=24;i++) // 스피너에 넣을 시간값
            timeData.add(i);

        final Spinner spinner1 = (Spinner)dlgView.findViewById(R.id.spinner4);
        ArrayAdapter<Integer> adapter1 = new ArrayAdapter<Integer>(dlgView.getContext(),R.layout.custom_row_spinner,timeData);
        adapter1.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner1.setAdapter(adapter1);

        final Spinner spinner2 = (Spinner)dlgView.findViewById(R.id.spinner3);
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(dlgView.getContext(),R.layout.custom_row_spinner,timeData);
        adapter2.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner2.setAdapter(adapter2);
        rentDialog.show();

        Button rentBt,cancelBt;
        //---------------------------------------------------------------------------------------------------
        rentBt = dlgView.findViewById(R.id.rentbt);
        cancelBt = dlgView.findViewById(R.id.cancelbt);

        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rentDialog.cancel();
            }
        });


        final int position = pos;
        rentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //검증
                int start,end;
                start = Integer.parseInt(spinner1.getSelectedItem().toString());
                end = Integer.parseInt(spinner2.getSelectedItem().toString());

                if(end > start && adapter.getItem(position).addTimeCheck(start,end)) // 예약된시간이 없어 예약가능하면
                {
                    //서버에 db추가
                    String userid = bundle.getString("user_id");
                    String key = "";
                    if(mode == 1) //회원
                        key = globalDay+"_"+position+"_"+userid+"_"+start+"to"+end;
                    else if(mode == 2) //비회원
                        key = globalDay+"_"+position+"_nonMember_"+start+"to"+end;

                    myRef.child("reservation").child(key).child("date").setValue(globalDay);
                    myRef.child("reservation").child(key).child("end_time").setValue(end);
                    myRef.child("reservation").child(key).child("facility_id").setValue(position);
                    myRef.child("reservation").child(key).child("name").setValue(bundle.getString("name"));
                    myRef.child("reservation").child(key).child("start_time").setValue(start);
                    if(mode == 1)
                        myRef.child("reservation").child(key).child("user_id").setValue(bundle.getString("user_id"));
                    else if(mode == 2)
                        myRef.child("reservation").child(key).child("user_id").setValue(editNumber.getText().toString());

                    Toast.makeText(view.getContext(),"  예약 되었습니다.  ",Toast.LENGTH_SHORT).show();
                    listView.setAdapter(adapter); // 동적 예약객체 업데이트
                    rentDialog.cancel();
                }
                else if(start > end)
                    Toast.makeText(view.getContext(),"예약시간은 시작시간이 더 빨라야합니다. 다시 선택해주세요.",Toast.LENGTH_SHORT).show();
                else // 예약불가능하면
                    Toast.makeText(view.getContext(),"해당 시간엔 이미 다른 예약이 있습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
