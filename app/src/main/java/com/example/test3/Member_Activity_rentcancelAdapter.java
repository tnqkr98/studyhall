package com.example.test3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


class  ListViewItem2  {

    private String rowtext1;
    private String rowtext2;
    private String rowtext3;
    public String day,st,et,fn;


    public String getRowtext1() {
        return rowtext1;
    }

    public void setRowtext1(String rowtext1) {
        this.rowtext1 = rowtext1;
    }

    public String getRowtext2() {
        return rowtext2;
    }

    public void setRowtext2(String rowtext2) {
        this.rowtext2 = rowtext2;
    }

    public String getRowtext3() {
        return rowtext3;
    }

    public void setRowtext3(String rowtext2) {
        this.rowtext3 = rowtext2;
    }
}
