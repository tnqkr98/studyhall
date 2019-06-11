package com.example.test3;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.app.Activity.RESULT_OK;
import static java.sql.DriverManager.println;

public class Member_Frag_cmp extends Fragment {

    View view;
    Button sendbt,selectbt,sendadminbt;
    TextView messagetarget;
    boolean selectTarget = false;
    String[] complains = {"마우스를 딸각거림", "키스킨 없이 키보드 사용", "다리를 심하게 떪", "중얼거리는 소리를 냄", "소음을 일으킴", "냄새가 남"};
    Spinner spinner;

    public static final int MEM_FRAG_CMP = 111;

    String selectedReciever;  // cmp target user
    RequestQueue queue;
    String regID,targetRegID,adminRegID;
    String message;

    Intent intent;
    Bundle bundle;
    private static DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_memcmp, container, false);

        intent = getActivity().getIntent();  //부모 액티비티에서 인탠드 가져오기
        bundle = intent.getExtras();
        myRef = FirebaseDatabase.getInstance().getReference(bundle.getString("branch"));

        spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), R.layout.custom_row_spinner, complains);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        messagetarget = (TextView)view.findViewById(R.id.messagetarget);

        selectbt = view.findViewById(R.id.selectreciv);
        selectbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seatIntent = new Intent(view.getContext(), SeatActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("name","  수신자 선택");
                bundle.putString("branch",getActivity().getIntent().getExtras().getString("branch"));
                bundle.putInt("mode",3); // seatActivity를 cmp 모드로 호출

                seatIntent.putExtras(bundle);
                startActivityForResult(seatIntent,MEM_FRAG_CMP);
            }
        });

        sendbt = view.findViewById(R.id.sendbt);   // 회원 대 회원 DM
        sendbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 예외처리 요망
                if(selectTarget) {
                    message = spinner.getSelectedItem().toString();
                    send(message,targetRegID,"1");
                    Toast.makeText(view.getContext(), " 경고 발송 완료 ", Toast.LENGTH_SHORT).show();
                    selectTarget = false;
                    messagetarget.setText("-- 번 좌석");

                    // blacklist DB 저장
                    Date d = new Date(System.currentTimeMillis());
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat sf2 = new SimpleDateFormat("hhmmss");
                    String today = sf.format(d);
                    int yearmonth = Integer.parseInt(today)/100;

                    myRef.child("cmp").child(yearmonth+"").child(bundle.getString("user_id")+"_to_"+selectedReciever+"_"+sf2.format(d)).child("sender").setValue(bundle.getString("user_id"));
                    myRef.child("cmp").child(yearmonth+"").child(bundle.getString("user_id")+"_to_"+selectedReciever+"_"+sf2.format(d)).child("reciver").setValue(selectedReciever);
                    myRef.child("cmp").child(yearmonth+"").child(bundle.getString("user_id")+"_to_"+selectedReciever+"_"+sf2.format(d)).child("day").setValue(today);
                    myRef.child("cmp").child(yearmonth+"").child(bundle.getString("user_id")+"_to_"+selectedReciever+"_"+sf2.format(d)).child("reason").setValue(message);
                    myRef.child("cmp").child(yearmonth+"").child(bundle.getString("user_id")+"_to_"+selectedReciever+"_"+sf2.format(d)).child("time").setValue(sf2.format(d));

                }
                else
                    Toast.makeText(view.getContext(), " 수신자를 선택해 주세요 ", Toast.LENGTH_SHORT).show();
            }
        });

        sendadminbt = view.findViewById(R.id.sendToAdminBt); //관리자에게 DM 보내기
        sendadminbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_sendAdmin();
            }
        });

        myRef.child("current_admin_regID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminRegID = dataSnapshot.getValue().toString();  //관리자 regID 받아오기
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        queue = Volley.newRequestQueue(view.getContext());
        getRegisterationID();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == MEM_FRAG_CMP ) {
            if (resultCode == RESULT_OK) {
                messagetarget.setText(data.getIntExtra("num",0)+" 번 좌석");
                selectedReciever = data.getStringExtra("reciever");
                targetRegID = data.getStringExtra("regID");
                selectTarget = true;
                Log.d("return by SeatActivity", "소음유발자 regID : "+data.getStringExtra("regID"));
            }
        }
    }

    // 관리자에게 메시지 보내기, 다이얼로그

    private void dialog_sendAdmin() {

        final View dlgView = View.inflate(view.getContext(), R.layout.dialog_sendtoadim, null);
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(dlgView);

        final EditText message = (EditText)dlgView.findViewById(R.id.message);
        message.getText().toString();

        Button sendbt = (Button)dlgView.findViewById(R.id.sendbt);
        sendbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send
                send(message.getText().toString(),adminRegID,"2");

                //DB에 log 저장
                Date d = new Date(System.currentTimeMillis());
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat sf2 = new SimpleDateFormat("hhmmss");
                TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                sf.setTimeZone(tz);
                sf2.setTimeZone(tz);

                String today = sf.format(d);
                int yearmonth = Integer.parseInt(today)/100;

                myRef.child("message").child(yearmonth+"").child(bundle.getString("user_id")+"_to_Admin"+sf2.format(d)).child("day").setValue(today);
                myRef.child("message").child(yearmonth+"").child(bundle.getString("user_id")+"_to_Admin"+sf2.format(d)).child("reason").setValue(message.getText().toString());
                myRef.child("message").child(yearmonth+"").child(bundle.getString("user_id")+"_to_Admin"+sf2.format(d)).child("time").setValue(sf2.format(d));
                myRef.child("message").child(yearmonth+"").child(bundle.getString("user_id")+"_to_Admin"+sf2.format(d)).child("sender").setValue(bundle.getString("user_id"));

                dialog.cancel();
                Toast.makeText(view.getContext(), " 메시지를 발송했습니다. ", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    //---------------------------------------------------------------------------------------------------------
    //    FCM 구현(발송)
    //---------------------------------------------------------------------------------------------------------
    public void getRegisterationID() {
        Log.i("통신","getRegisterationID() 호출됨");

        regID = FirebaseInstanceId.getInstance().getToken(); // 단말의 등록ID 확인
        Log.i("통신","regID : " + regID);
    }

    public void send(String input,String targetID,String mode) {
        JSONObject requestData = new JSONObject();

        try {
            requestData.put("priority", "high");
            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", input);
            dataObj.put("mode",mode);  //admin = 2, user = 1


            Log.i("cmp 모드 체크------------>", mode);
            requestData.put("data", dataObj);

            JSONArray idArray = new JSONArray();
            //idArray.put(0, "dyaMNSJMYWU:APA91bEOS1C481XBXFJw7GsS3wnpWFLZJgktLg46vz4jieWTdKvrt0Hz3dLl3MXmPLYzjgPu_VlDfYJGbOltgeseDjpClf1yX_KorQhBOK789Ru5yg-tsO_3i-XjHpy7j_lyy9xGCHby");
            idArray.put(0, targetID);
            requestData.put("registration_ids", idArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestStarted() {
                Log.i("통신","onRequestStarted() 호출됨");
            }

            @Override
            public void onRequestCompleted() {
                Log.i("통신","onRequestCompleted() 호출됨");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                Log.i("통신","onRequestWithError() 호출됨");
            }
        });
    }

    public interface SendResponseListener{
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }

    public void sendData(JSONObject requestData, final SendResponseListener listener){
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String,String>();
                headers.put(
                        "Authorization",
                        "key=AAAAl_kQghg:APA91bFByVoQB99_maCKJ8ZmEJe--x4RMBP6IaN-MRdTmmy09mHws6M2X3802UeSN9zZc33NpAEjKaXtzv0qw88cEPy1wd7-sf-cHVxqc_dN0g2CEeS8W9EfPOxBqjLyWyTvOHyYTROZ");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);
    }
}