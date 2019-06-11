package com.example.test3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Member_MyCMP_adapter extends BaseAdapter {

    public ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();


    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listrowitem, parent, false);
        }
        TextView textView1 = (TextView) convertView.findViewById(R.id.textView1);
        TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);


        ListViewItem listViewItem = listViewItemList.get(position);
        textView1.setText(listViewItem.getRowtext1());
        textView2.setText(listViewItem.getRowtext2());

        return convertView;
    }

    public void addItem(String text1, String text2) {
        ListViewItem item = new ListViewItem();
        item.setRowtext1(text1);
        item.setRowtext2(text2);

        listViewItemList.add(item);
    }

    public void clearItem(){
        listViewItemList.clear();
    }
}


class  ListViewItem  {

    private String rowtext1;
    private String rowtext2;

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
}