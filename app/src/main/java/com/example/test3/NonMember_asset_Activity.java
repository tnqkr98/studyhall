package com.example.test3;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class NonMember_asset_Activity extends AppCompatActivity {

    ImageButton backbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_member_asset_);

        backbt = (ImageButton)findViewById(R.id.rentback);
        backbt.setImageResource(R.drawable.ic_back);
        backbt.setColorFilter(Color.WHITE);

        Member_Frag_asset assetFragment = new Member_Frag_asset();
        getSupportFragmentManager().beginTransaction().add(R.id.assetframe, assetFragment).commit();
    }

    public void backClick(View view){
        finish();
    }
}
