package com.example.test3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Member_Frag_support extends Fragment {
    private ImageView imageView1,imageView2,imageView3,imageView4;
    private TextView go1,go2,go3,go4;
    View view;  // 여기에 쓴 코드는 유저 학습보조기능 화면이니까 갖다 쓰도록

    Intent intent;
    Bundle parentbundle;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_memsupport,container,false);
        imageView1 = (ImageView) view.findViewById(R.id.imageView1);
        imageView2 = (ImageView) view.findViewById(R.id.imageView2);
        imageView3 = (ImageView) view.findViewById(R.id.imageView3);
        go1 = (TextView) view.findViewById(R.id.go1);
        go2 = (TextView) view.findViewById(R.id.go2);
        go3 = (TextView) view.findViewById(R.id.go3);

        intent = getActivity().getIntent();
        parentbundle = intent.getExtras();

        //Log.i("hihi",parentbundle.getString("branch")+parentbundle.getString("user_id"));

        go1.setOnClickListener(new View.OnClickListener() {  // 임시
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(view.getContext(), dday.class);
                logIntent.putExtra("branch",parentbundle.getString("branch"));
                logIntent.putExtra("user_id",parentbundle.getString("user_id"));
                startActivity(logIntent);
            }
        });
        go2.setOnClickListener(new View.OnClickListener() {  // 임시
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(view.getContext(), stopwatch.class);
                startActivity(logIntent);
            }
        });
        go3.setOnClickListener(new View.OnClickListener() {  // 임시
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(view.getContext(), Timer.class);
                startActivity(logIntent);
            }
        });

        return view;

    }
}
