package com.example.test3;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Admin_Activty_Asset extends AppCompatActivity {
    ImageButton backbt;
    TextView pageName;
    private Button add;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseRef;
    private Button fadd,cancelBt;
    private EditText editText;
    private TextView fno;
    private int tlfgdjd;
    int str1;
    private String str2;

    Intent parentintent;
    Bundle parentbundle;
    //Intent parentintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util);

        listView= (ListView)  findViewById(R.id.listView);
        backbt = (ImageButton) findViewById(R.id.backbt);
        backbt.setImageResource(R.drawable.ic_back);
        backbt.setColorFilter(Color.WHITE);
        pageName = (TextView) findViewById(R.id.seatmapname);
        add = (Button) findViewById(R.id.add);
        //Log.i("",""+parentintent.getStringExtra("branch"));
        parentintent = getIntent();
        parentbundle = parentintent.getExtras();
        databaseRef = FirebaseDatabase.getInstance().getReference(parentbundle.getString("branch"));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_util();
            }
        });

        adapter = new ArrayAdapter<String>(this, R.layout.simple_list, new ArrayList<String>());
        listView.setAdapter(adapter);
        databaseRef.child("facility").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                str1 = 1 ;
                adapter.add("　　시설물 번호　　　　　　시설물 이름　　");
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    //str1 = 1 + Integer.parseInt(aa);
                    str2 = fileSnapshot.child("facility_name").getValue().toString();
                    adapter.add("　　　 "+str1+"　　　　　　　　"+str2);
                    str1+=1;
                }
                tlfgdjd = adapter.getCount();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
            }
        });
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(listView, new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(final ListView listView, int[] reverseSortedPositions) {
                for (final int position : reverseSortedPositions) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Admin_Activty_Asset.this,R.style.AlertDialog);
                    dialog.setTitle("데이터 삭제")
                            .setMessage("해당 데이터를 삭제하시겠습니까?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.remove(adapter.getItem(position));
                                    databaseRef.child("facility").child("facility0"+position).removeValue(null);
                                    databaseRef.child("reservation").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                                                String str = fileSnapshot.getKey();
                                                int pos = position;
                                                pos-=1;
                                                if(str.substring(9, 10).equals(""+pos)) {
                                                    Log.i("gg", "" + str);
                                                    databaseRef.child("reservation").child(str).removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //
                                        }
                                    });
                                    Toast.makeText(getApplicationContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    //Toast.makeText(getApplicationContext(), "삭제 취소", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();
                    //adapter.remove(adapter.getItem(position));
                }
                adapter.notifyDataSetChanged();
            }
        });
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());

    }
    public void backbtClick(View v){
        finish();
    }

    private void Dialog_util(){

        final View dlgView = View.inflate(Admin_Activty_Asset.this,R.layout.dialog_util,null);
        final Dialog utilDialog = new Dialog(Admin_Activty_Asset.this);
        utilDialog.setContentView(dlgView);
        utilDialog.show();

        fadd = dlgView.findViewById(R.id.fadd);
        cancelBt = dlgView.findViewById(R.id.cancelbt);
        editText = dlgView.findViewById(R.id.editText);
        fno = dlgView.findViewById(R.id.fno);
        fno.setText(tlfgdjd+"");

        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilDialog.cancel();
            }
        });

        fadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(Admin_Activty_Asset.this,"시설물 이름을 입력해주세요. ",Toast.LENGTH_SHORT).show();
                } else {
                    databaseRef.child("facility").child("facility0"+tlfgdjd).child("facility_name").setValue(editText.getText().toString());
                    databaseRef.child("facility").child("facility0"+tlfgdjd).child("facility_id").setValue(tlfgdjd-1);
                    Toast.makeText(Admin_Activty_Asset.this, "추가 완료", Toast.LENGTH_SHORT).show();
                    utilDialog.cancel();
                }
            }
        });
    }
}