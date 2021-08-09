package com.example.readcontactsexample.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by TIGER on 09.08.2021.
 */
public class ContactsAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<MainActivity.Contact> objects;

    ContactsAdapter(Context context, List<MainActivity.Contact> contactinfo) {
        ctx = context;
        objects = contactinfo;
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
            view = lInflater.inflate(R.layout.item_contact, parent, false);
        }

        MainActivity.Contact p = getContactPosition(position);

        // заполняем View в пункте списка данными о контакте
        ((TextView) view.findViewById(R.id.contact_name)).setText("Contact name: "+p.getName());

        ((TextView) view.findViewById(R.id.contact_number)).setText("Phone number: "+p.getPhone());

        return view;
    }

    // Инфо о контакте
    MainActivity.Contact getContactPosition(int position) {
        return ((MainActivity.Contact) getItem(position));
    }



}
