package com.example.readcontactsexample.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Created by TIGER on 14.06.2020.
 */

public class MyAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<MainActivity.Sms> objects;

    MyAdapter(Context context, List<MainActivity.Sms> smsinfo) {
        ctx = context;
        objects = smsinfo;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        MainActivity.Sms p = getSmsPosition(position);

        // заполняем View в пункте списка данными о смс
        ((TextView) view.findViewById(R.id.sms_id)).setText("pos:/id: "+position+" "+p.getId());

        ((TextView) view.findViewById(R.id.address)).setText("address: "+p.getAddress());

        ((TextView) view.findViewById(R.id.body)).setText("body: "+p.getMsg());

        ((TextView) view.findViewById(R.id.read)).setText("readstate: "+p.getReadState());

        ((TextView) view.findViewById(R.id.date)).setText("time: "+p.getTime());

        ((TextView) view.findViewById(R.id.type)).setText("folder name: "+p.getFolderName());


        return view;
    }

    // Инфо об СМС
    MainActivity.Sms getSmsPosition(int position) {
        return ((MainActivity.Sms) getItem(position));
    }



}
