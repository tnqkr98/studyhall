package com.example.test3;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MemberActivity extends AppCompatActivity {
    ImageButton backbt;
    TextView pageName;
    private ListView listView;
    private ArrayAdapter<String> adapter, saveList;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private EditText search;

    private String text="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        listView= (ListView)  findViewById(R.id.listView);
        search = (EditText) findViewById(R.id.search);
        pageName = (TextView) findViewById(R.id.usermapname);


        backbt = (ImageButton) findViewById(R.id.backbt);
        backbt.setImageResource(R.drawable.ic_back);
        backbt.setColorFilter(Color.WHITE);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text = search.getText().toString();
                adapter.clear();
                for (int i = 0; i < saveList.getCount(); i++) {
                    if (saveList.getItem(i).toLowerCase().contains(text)) {
                        adapter.add(saveList.getItem(i));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("user");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        saveList = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(ClickListener);
        // Read from the database
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (text.length() == 0) {
                    adapter.clear();
                    // 클래스 모델이 필요?
                    for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                        String str = fileSnapshot.child("user_id").getValue().toString();
                        if (str.length() == 11) {
                            adapter.add(str);
                            saveList.add(str);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
            }
        });
    }
    private OnItemClickListener ClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            Intent intent = new Intent(MemberActivity.this, Admin_Activity_Revise.class);
            intent.putExtra("user_id",adapter.getItem(position));
            startActivity(intent);
        }
    };
    public void OnfebClicked(View view) {
        Intent intent = new Intent(MemberActivity.this, userActivity.class);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(), "버튼 클릭", Toast.LENGTH_SHORT).show();
    }
    public void backbtClick(View v){
        finish();
    }
}
