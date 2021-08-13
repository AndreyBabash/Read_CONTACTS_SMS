package com.example.readcontactsexample.app;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
        public ArrayList<Contact> contactlist = new ArrayList<Contact>();
        public List<Sms> mysmslist = new ArrayList<Sms>();
        MyAdapter adapter;
        ContactsAdapter cadapter;
        Boolean flag=true;
        private final int PERMISSIONS_REQUEST_READ_CONTACTS = 10;
        private final int PERMISSIONS_REQUEST_READ_SMS = 11;
        private final int PERMISSIONS_REQUEST_СALL_PHONE = 12;
        Button mybtn,mybtnsms;
        ListView lv;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(contactlist!=null) {
            outState.putParcelableArrayList("MyList", contactlist);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Toast.makeText(getApplicationContext(),"Restore state!)))", Toast.LENGTH_LONG).show();
        ArrayList<Contact> myarraylist;
        myarraylist=savedInstanceState.getParcelableArrayList("MyList");
        contactlist=myarraylist;
        cadapter=new ContactsAdapter(getApplicationContext(),myarraylist);
        lv.setAdapter(cadapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mybtn=(Button)findViewById(R.id.btn);
        mybtnsms=(Button)findViewById(R.id.btnsms);
        lv=(ListView)findViewById(R.id.mylistview);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!flag)
                {
                    Toast.makeText(getApplicationContext(), "id: " + adapter.getSmsPosition(i).getId() + "\n" +
                            "address: " + adapter.getSmsPosition(i).getAddress() +
                            "\n" + "body: " + adapter.getSmsPosition(i).getMsg() + "\n" + "folder name: " +
                            adapter.getSmsPosition(i).getFolderName(), Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "id: " + cadapter.getContactPosition(i).getName() + "\n" +
                            "phone: " + cadapter.getContactPosition(i).getPhone(), Toast.LENGTH_LONG).show();

                    // Проверка разрешения
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                    {
                        // Звоним по телефону
                        startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + cadapter.getContactPosition(i).getPhone()))); // запускаем намерение
                    }
                    else
                    {
                        // Разрешений нет
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_СALL_PHONE);
                    }

                }
            }
        });

        mybtnsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Проверка разрешения
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)
                {
                    // Разрешения чтения контактов имеются
                    getAllSms();
                }
                else
                {
                    // Разрешений нет
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
                }
            }
        });

        mybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Проверка разрешения
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                {
                    // Разрешения чтения контактов имеются
                    readContacts(getApplicationContext());
                }
                else
                {
                    // Разрешений нет
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        });
    }

    // Читаем контакты
    private void readContacts(Context context)
    {
        flag=true;
        Contact contact;
        Cursor cursor=context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext())
            {
                contact = new Contact();
                // Считываем id и имя
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                contact.setId(id);
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contact.setName(name);

                // Есть ли номер телефона
                String has_phone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (Integer.parseInt(has_phone) > 0)
                {
                    // Извлекаем номер телефона
                    Cursor pCur;
                    pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, ContactsContract.CommonDataKinds.Phone.NUMBER);

                    // Считываем все контакты
                    while(pCur.moveToNext())
                    {
                        String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contact.setPhone(phone);
                    }
                    pCur.close();
                }
                // Добавляем контакты в список
                contactlist.add(contact);
            }
        }
            // Добавляем список в адаптер
            cadapter = new ContactsAdapter(getApplicationContext(),contactlist);
            // Присваиваем адаптер ListView
            lv.setAdapter(cadapter);
    }


    public void getAllSms()
    {
            flag=false;
            Sms objSms;
            Uri message = Uri.parse("content://sms/");
            ContentResolver cr = this.getContentResolver();

            Cursor c = cr.query(message, null, null, null, null);
            this.startManagingCursor(c);
            int totalSMS = c.getCount();

        if (c.moveToFirst())
        {
            for (int i = 0; i < totalSMS; i++)
            {
                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));

                // Входящие или отправленные сообщения
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1"))
                {
                    objSms.setFolderName("inbox");
                }
                else
                {
                    objSms.setFolderName("sent");
                }

                // Добавляем инфо об СМС в список
                mysmslist.add(objSms);

                c.moveToNext();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Нет ни одного СМС сообщения",Toast.LENGTH_LONG).show();
        }
            c.close();
            // Добавляем список СМС в адаптер и присваиваем его ListView
            adapter = new MyAdapter(getApplicationContext(),mysmslist);
            lv.setAdapter(adapter);
    }

    // Класс информации об СМС
    public class Sms{
        private String _id;
        private String _address;
        private String _msg;
        private String _readState; //"0" for have not read sms and "1" for have read sms
        private String _time;
        private String _folderName;

        public String getId(){
            return _id;
        }
        public String getAddress(){
            return _address;
        }
        public String getMsg(){
            return _msg;
        }
        public String getReadState(){
            return _readState;
        }
        public String getTime(){
            return _time;
        }
        public String getFolderName(){
            return _folderName;
        }


        public void setId(String id){
            _id = id;
        }
        public void setAddress(String address){
            _address = address;
        }
        public void setMsg(String msg){
            _msg = msg;
        }
        public void setReadState(String readState){
            _readState = readState;
        }
        public void setTime(String time){
            _time = time;
        }
        public void setFolderName(String folderName){
            _folderName = folderName;
        }

    }

    // Класс информации о контакте
    public class Contact implements Parcelable {
        private String id;
        private String name  = "";
        private String phone = "";

        public Contact() {

        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }
        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }

        public Contact(Parcel parcel)
        {
            id=parcel.readString();
            name=parcel.readString();
            phone=parcel.readString();
        }

        // Методы Parcelable
        @Override
        public int describeContents() {
            return 0;
        }
        // Методы Parcelable
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeString(phone);

        }
        // Константа CREATOR
        public final Parcelable.Creator<Contact> CREATOR = new Creator<Contact>() {
            @Override
            public Contact createFromParcel(Parcel source) {
                return new Contact(source);
            }

            @Override
            public Contact[] newArray(int size) {
                return new Contact[size];
            }
        };
    }

    // Обработка результата запроса разрешения у Андроид
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_READ_CONTACTS:
            {
                // При отмене запроса массив результатов пустой
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    // Разрешения получены
                    Toast.makeText(getApplicationContext(),"Permission granted!", Toast.LENGTH_LONG).show();
                    // Чтение контактов
                    readContacts(getApplicationContext());
                }
                else
                {
                    // Разрешения НЕ получены.
                    Toast.makeText(getApplicationContext(),"Permission denied!", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case PERMISSIONS_REQUEST_READ_SMS:
            {
                // другой 'case' получения permissions
                // При отмене запроса массив результатов пустой
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Разрешения получены
                    Toast.makeText(getApplicationContext(), "Permission granted!", Toast.LENGTH_LONG).show();
                    // Чтение смс
                    getAllSms();
                } else {
                    // Разрешения НЕ получены.
                    Toast.makeText(getApplicationContext(), "Permission denied!", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case PERMISSIONS_REQUEST_СALL_PHONE:
            {
                // другой 'case' получения permissions
                // При отмене запроса массив результатов пустой
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Разрешения получены
                    Toast.makeText(getApplicationContext(), "Permission granted!", Toast.LENGTH_LONG).show();
                } else {
                    // Разрешения НЕ получены.
                    Toast.makeText(getApplicationContext(), "Permission denied!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
