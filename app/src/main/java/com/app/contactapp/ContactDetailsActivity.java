package com.app.contactapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ContactDetailsActivity extends AppCompatActivity implements AddEventDialog.AddEventDialogListener, RemarkAdapter.OnItemClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    String TAG = "ContactDetailsActivity";
    static MyContact user = new MyContact();
    RecyclerView remarkRV;
    MyDatabaseHelper myDatabaseHelper;
    List<MyContactDetails> userList = new ArrayList<>();
    MyContactDetails[] array = new MyContactDetails[20];
    ImageView contactImageView, notifyCB;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;
    int pos = 0;
    TextView event1, event2, event3;
    int event=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        remarkRV = findViewById(R.id.remarkRV);
        contactImageView = findViewById(R.id.contactImageView);
        event1 = findViewById(R.id.event1Tv);
        event2 = findViewById(R.id.event2Tv);
        event3 = findViewById(R.id.event3Tv);

        Intent intent = getIntent();
        user = (MyContact) intent.getSerializableExtra("list");
        Fragment particularFragment = new ParticularFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.pFrame, particularFragment, "pframe")
                .commit();

        myDatabaseHelper = new MyDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();

        if (user.getContactImage() != null) {
            contactImageView.setImageURI(Uri.parse(user.getContactImage()));
        }
        event1.setTextSize(20);
        load(1);
        event1.setOnClickListener(this);
        event2.setOnClickListener(this);
        event3.setOnClickListener(this);

    }

    private void load(int i) {
        userList.clear();
        array = new MyContactDetails[20];
        Cursor cursor = myDatabaseHelper.read_event_2(user.getContactId());
        if (i==1) {
            cursor= myDatabaseHelper.read_event_1(user.getContactId());
        }
        if (i==2) {
            cursor= myDatabaseHelper.read_event_2(user.getContactId());
        }
        if (i==3) {
            cursor= myDatabaseHelper.read_event_3(user.getContactId());
        }
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                MyContactDetails contactDetails = new MyContactDetails();
                contactDetails.setId(cursor.getString(0));
                contactDetails.setEvent(cursor.getString(2));
                contactDetails.setRow(cursor.getString(3));
                contactDetails.setDescription(cursor.getString(4));
                contactDetails.setRemark1(cursor.getString(5));
                contactDetails.setRemark2(cursor.getString(6));
                contactDetails.setNotify(cursor.getString(7));
                contactDetails.setDate(cursor.getString(8));
                contactDetails.setStatus(cursor.getString(9));
                array[Integer.parseInt(cursor.getString(3))] = contactDetails;
//                userList.add(Integer.parseInt(cursor.getString(3)),contactDetails);
            }
//            userList= Arrays.asList(array);
        }
        userList.addAll(Arrays.asList(array).subList(0, 20));
        cursor.close();
        RemarkAdapter contactAdapter = new RemarkAdapter(userList, getApplicationContext());
        remarkRV.setLayoutManager(new LinearLayoutManager(this));
        remarkRV.setAdapter(contactAdapter);
        contactAdapter.setOnRemarkClickListener(this);
    }


    @Override
    public void applyTexts(String row_index, String description, String date, String status, String remark1, String remark2) {
        myDatabaseHelper.insertDetailsData(String.valueOf(event), row_index, user.getContactId(), description, remark1, remark2, String.valueOf(0), date, status);
        load(event);
    }

    @Override
    public void updateTexts(String id, String description, String date, String status, String remark1, String remark2) {
        boolean i = myDatabaseHelper.updateData(id, description, remark1, remark2, date, status);
        Log.d(TAG, "updateTexts: updated "+i+"id = "+id+"userid: "+user.getContactId()+" "+userList.get(pos).getId());
        load(event);

    }

    @Override
    public void setOnRemarkClickListener(int position, View v) {
        pos = position;
        if (v.getId() == R.id.notifyCB) {
            this.notifyCB = (ImageView) v;
            boolean found=false;
            Log.d(TAG, "setOnRemarkClickListener: "+userList.size());
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i)!=null && Integer.parseInt(userList.get(i).getRow()) == position) {
                    found = true;
                }
            }
            if(!found) {
                Toast.makeText(this, "Add the event first", Toast.LENGTH_SHORT).show();
            }else {
                if (notifyCB.isSelected()) {
                    notifyCB.setSelected(false);
                    myDatabaseHelper.updateNotification(userList.get(position).getId(), "0");
                    String req_code = "" + userList.get(pos).getEvent() + userList.get(pos).getRow();

                    Intent intent = new Intent(this, NotificationReceiver.class);
                    boolean isWorking = (PendingIntent.getBroadcast(this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_NO_CREATE) != null);
                    if (isWorking) {
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                        alarmMgr.cancel(pendingIntent);
                        Log.d(TAG, "setOnRemarkClickListener: cancel alarm");
                    }
                }
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ContactDetailsActivity.this, ContactDetailsActivity.this, year, month, day);
                datePickerDialog.show();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(ContactDetailsActivity.this, ContactDetailsActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        Log.d(TAG, "onTimeSet: " + "Year: " + myYear + "\n" +
                "Month: " + myMonth + "\n" +
                "Day: " + myday + "\n" +
                "Hour: " + myHour + "\n" +
                "Minute: " + myMinute);
        setAlarm(myYear, myMonth, myday, myHour, myMinute, "");
        notifyCB.setSelected(true);

    }

    private void setAlarm(int year, int monthAlarm, int dateAlarm, int hourAlarm, int minuteAlarm, String timeAlarm) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DATE, dateAlarm);
        calendar.set(Calendar.MONTH, monthAlarm);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hourAlarm);
        calendar.set(Calendar.MINUTE, minuteAlarm);
        calendar.set(Calendar.SECOND, 1);

        String req_code = "" + userList.get(pos).getEvent() + userList.get(pos).getRow();

        Log.d(TAG, "setAlarm: " + pos);

        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            boolean isWorking = (PendingIntent.getBroadcast(this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
            if (!isWorking) {
                Log.d(TAG, "setAlarm: " + req_code);
                intent.putExtra("timeAlarm", timeAlarm);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                myDatabaseHelper.updateNotification(userList.get(pos).getId(), "1");
                Log.d(TAG, "setAlarm: " + calendar.getTimeInMillis());
            } else {
                Log.d(TAG, "setAlarm: alam already setted" + req_code);
            }
        }
    }

    @Override
    public void setOnRemarkLongClickListener(int position) {
//        if (position < userList.size()) {
        pos = position;
        if (userList.size() != 0) {
            if (userList.get(position) != null) {
                Log.d("TAG", "setOnRemarkLongClickListener: 1");
                AddEventDialog exampleDialog = new AddEventDialog(String.valueOf(position), userList.get(position));
                exampleDialog.show(getSupportFragmentManager(), "remark add dialog");
            } else {
                Log.d("TAG", "setOnRemarkLongClickListener: 2");
                AddEventDialog exampleDialog = new AddEventDialog(String.valueOf(position), "null");
                exampleDialog.show(getSupportFragmentManager(), "remark add dialog");
            }
        } else {
            Log.d("TAG", "setOnRemarkLongClickListener: 2");
            AddEventDialog exampleDialog = new AddEventDialog(String.valueOf(position), "null");
            exampleDialog.show(getSupportFragmentManager(), "remark add dialog");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.event1Tv:
                event=1;
                event1.setTextSize(20);
                event2.setTextSize(17);
                event3.setTextSize(17);
                load(1);
                break;
            case R.id.event2Tv:
                event=2;
                event2.setTextSize(20);
                event1.setTextSize(17);
                event3.setTextSize(17);
                load(2);
                break;
            case R.id.event3Tv:
                event = 3;
                event3.setTextSize(20);
                event1.setTextSize(17);
                event2.setTextSize(17);
                load(3);
                break;
        }
    }
}