package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class calendarView extends AppCompatActivity implements SlyCalendarDialog.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);
        findViewById(R.id.btnShowCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlyCalendarDialog()
                        .setSingle(false)
                        .setFirstMonday(false)
                        .setCallback(calendarView.this)
                        .show(getSupportFragmentManager(), "TAG_SLYCALENDAR");
            }
        });



    }

    @Override
    public void onCancelled() {
        //Nothing
    }

    @Override
    public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {

        if (firstDate != null) {
            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours);
                firstDate.set(Calendar.MINUTE, minutes);

                String getDateBySystem = new SimpleDateFormat(
                        "dd-MM-yyyy").format(firstDate.getTime());

                //to fetch the schedules
                fetchCalendarData(getDateBySystem);
                String timeFormat2 = new SimpleDateFormat(
                        "hh:mm").format(firstDate.getTime());
                Toast.makeText(
                        this,
                        getDateBySystem + " / " + timeFormat2,
                        Toast.LENGTH_LONG
                ).show();
                TextView txt = (TextView) findViewById(R.id.calendarDate);
                txt.setText(getDateBySystem + " / " + timeFormat2);
            } else {
                firstDate.set(Calendar.HOUR_OF_DAY, hours);
                firstDate.set(Calendar.MINUTE, minutes);
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd-MM-yyyy");
                SimpleDateFormat timeFormat2 = new SimpleDateFormat(
                        "hh:mm");

                Log.i("Doc", dateFormat.format(firstDate.getTime()));
                Log.i("Doc Time", timeFormat2.format(firstDate.getTime()));
                Toast.makeText(
                        this,
                        getString(
                                R.string.period,
                                new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(firstDate.getTime()),
                                new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault()).format(secondDate.getTime())
                        ),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    private void filterLectureList(LectureClass[] myListData) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LectureItemAdapter adapter = new LectureItemAdapter(myListData, getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void fetchCalendarData(String getDate) {
        Log.i("Date",getDate);
//        getDate = "21-10-2020";
        FirebaseFirestore.getInstance().collection("Schedules").whereEqualTo("Date", getDate)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<LectureClass> al = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.i("Doc", document.get("Course").toString() + " " + document.get("Subject").toString() + " course with " +
                                "teacher assigned is " + document.get("Teacher_Assigned").toString());

//                        LectureClass[] myListData = new LectureClass[]{
//                                new LectureClass("MCA", "12:00", "14:00"),
//                                new LectureClass("MCA", "14:00", "15:00"),
//                                new LectureClass("MTech", "12:00", "14:00"),
//                                new LectureClass("MBA", "12:00", "16:00")
//                        };

                        al.add(new LectureClass(document.get("Subject").toString()+" ("+document.get("Course").toString()+")", document.get("Start_Time").toString(), document.get("End_Time").toString(), document.getId()));
                    }

                    LectureClass[] myListData = new LectureClass[al.size()];
                    myListData =  al.toArray(myListData);
                    filterLectureList(myListData);
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Doc", "Failed to retrieve any");
            }
        });
    }
}