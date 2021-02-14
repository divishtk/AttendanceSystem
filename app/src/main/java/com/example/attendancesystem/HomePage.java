package com.example.attendancesystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class HomePage extends AppCompatActivity {

    TextView Name;
    TextView Sapid;
    ImageButton btnProfile;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mas;
    FirebaseFirestore ff;
    private Fragment frag;
    ConstraintLayout a, b;
    FrameLayout f;
    SharedPreferences prefs;
    BottomNavigationView bottomNavigation;
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent i = null;
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            Toast.makeText(getApplicationContext(), "Home Button is Clicked", Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), HomePage.class);
                            startActivity(i);
                            break;
                        case R.id.navigation_scan:
                            Toast.makeText(getApplicationContext(), "Attandance Button is Clicked", Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), scannerCam.class);
                            startActivity(i);
                            break;
                        case R.id.navigation_profilePage:
                            Toast.makeText(getApplicationContext(), "My Profile Button is Clicked", Toast.LENGTH_SHORT).show();
//                            i = new Intent(getApplicationContext(), StudentVerifyTable.class);
                            i = new Intent(getApplicationContext(), profilePage.class);
                            startActivity(i);
                            break;
                        case R.id.navigation_attendance:
                            Toast.makeText(getApplicationContext(), "My Attendance Button is Clicked", Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), calendarView.class);
                            startActivity(i);
                            break;
                        case R.id.navigation_logout:
                            FirebaseAuth fAuth = FirebaseAuth.getInstance();
                            Toast.makeText(getApplicationContext(), "Logged In UID" + fAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                            if (!"".equals(fAuth.getCurrentUser().getUid())) {
                                fAuth.signOut();
                                Toast.makeText(getApplicationContext(), "Logout Button is Clicked", Toast.LENGTH_SHORT).show();
                                i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(), "Logout Button is Clicked", Toast.LENGTH_SHORT).show();
                                i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                            }
                            break;
                    }
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_home_page);

        Name = findViewById(R.id.nans);
        btnProfile = findViewById(R.id.button);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profilePage.class);
                startActivity(i);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        ff = FirebaseFirestore.getInstance();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Pie Chart
        final int[] attend = new int[3];

        addPieChart(attend);
        addSchedule(ff);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        try {
            String userId;
            userId = firebaseAuth.getCurrentUser().getUid();
            DocumentReference dd;
            dd = ff.collection("Users").document(userId);
            dd.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    //if document not empty that display data to home page
                    Name.setText(documentSnapshot != null ? "Welcome to MAT, " + documentSnapshot.getString("Full_name") + "." : null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addPieChart(final int[] attend) {
        ff.collection("Schedules")
                .whereEqualTo("Course", "MCA").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final int attend_total = queryDocumentSnapshots.size();
                        Log.i("Attd_Total", String.valueOf(attend_total));
                        attend[1] = attend_total;

                        ff.collection("Schedules")
                                .whereArrayContains("sap_attend", "70611019008").get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        int attend_present = queryDocumentSnapshots.size();
                                        Log.i("Attd_Present", String.valueOf(attend_present));
                                        attend[0] = attend_present;

                                        PieChart mPieChart = (PieChart) findViewById(R.id.piechart);

                                        mPieChart.addPieSlice(new PieModel("Present", attend_present, Color.parseColor("#31ed75")));
                                        mPieChart.addPieSlice(new PieModel("Absent", (attend_total - attend_present), Color.parseColor("#ff4d4d")));
//                                        mPieChart.addPieSlice(new PieModel("Total", attend_total, Color.parseColor("#20a1fc")));
//                                        mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));

                                        mPieChart.startAnimation();
                                    }
                                });
                    }
                });
    }

    private void addSchedule(FirebaseFirestore ff) {
        String sapid = "";//prefs.getString("sapid", "");
        String course = "";//prefs.getString("course", "");
        String date = "";//prefs.getString("date", "");
        sapid = "70611019008";
        course = "MCA";
        date = "17-10-2020";

        // get "today"
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String todayDate = new SimpleDateFormat(
                "dd-MM-yyyy").format(today);
        Log.i("today's date",todayDate);


        //get todays schedules
        ff.collection("Schedules").whereEqualTo("Course", course).whereEqualTo("Date", todayDate).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot docs:queryDocumentSnapshots) {
                            Log.i("Schedule","->"+docs.getData().values());
                        }
                        if (queryDocumentSnapshots.getDocuments().size()>=1) {
                            String sTime = queryDocumentSnapshots.getDocuments().get(0).get("Start_Time").toString();
                            String subject = queryDocumentSnapshots.getDocuments().get(0).get("Subject").toString();
                            TextView t1 = (TextView) findViewById(R.id.txtVw1);
                            t1.setText(subject);
                            TextView m1 = (TextView) findViewById(R.id.time1);
                            m1.setText(sTime);
                        }
                        if (queryDocumentSnapshots.getDocuments().size()>=2) {
                            String sTime2 = queryDocumentSnapshots.getDocuments().get(1).get("Start_Time").toString();
                            String subject2 = queryDocumentSnapshots.getDocuments().get(1).get("Subject").toString();
                            TextView t2 = (TextView) findViewById(R.id.txtVw2);
                            t2.setText(subject2);
                            TextView m2 = (TextView) findViewById(R.id.time2);
                            m2.setText(sTime2);
                        }
                        if (queryDocumentSnapshots.getDocuments().size()>=3) {
                            String sTime3 = queryDocumentSnapshots.getDocuments().get(2).get("Start_Time").toString();
                            String subject3 = queryDocumentSnapshots.getDocuments().get(2).get("Subject").toString();
                            TextView t3 = (TextView) findViewById(R.id.txtVw3);
                            t3.setText(subject3);
                            TextView m3 = (TextView) findViewById(R.id.time3);
                            m3.setText(sTime3);
                        }
                        if (queryDocumentSnapshots.getDocuments().size()>=4) {
                            String sTime4 = queryDocumentSnapshots.getDocuments().get(3).get("Start_Time").toString();
                            String subject4 = queryDocumentSnapshots.getDocuments().get(3).get("Subject").toString();
                            TextView t4 = (TextView) findViewById(R.id.txtVw4);
                            t4.setText(subject4);
                            TextView m4 = (TextView) findViewById(R.id.time4);
                            m4.setText(sTime4);
                        }
                    }
                });

        // get "tomorrow"
        Date tomorrow = calendar.getTime();
        System.out.println("tomorrow: " + tomorrow);
        String tmrwDate = new SimpleDateFormat(
                "dd-MM-yyyy").format(tomorrow);
        Log.i("tommorrow's date",tmrwDate);

        //get todays schedules
        ff.collection("Schedules").whereEqualTo("Course", course).whereEqualTo("Date", tmrwDate).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot docs:queryDocumentSnapshots) {
                            Log.i("Schedule"," -> "+docs.getData().values());
                        }
                        TextView t1 = (TextView) findViewById(R.id.tsc1);
                        TextView m1 = (TextView) findViewById(R.id.tst1);
                        if (queryDocumentSnapshots.getDocuments().size()>=1) {
                            String sTime = queryDocumentSnapshots.getDocuments().get(0).get("Start_Time").toString();
                            String subject = queryDocumentSnapshots.getDocuments().get(0).get("Subject").toString();
                            t1.setText(subject);
                            m1.setText(sTime);
                        }else{
                            t1.setText("-");
                            m1.setText("-");
                        }
                        if (queryDocumentSnapshots.getDocuments().size()>=2) {
                            String sTime2 = queryDocumentSnapshots.getDocuments().get(1).get("Start_Time").toString();
                            String subject2 = queryDocumentSnapshots.getDocuments().get(1).get("Subject").toString();
                            TextView t2 = (TextView) findViewById(R.id.tsc2);
                            t2.setText(subject2);
                            TextView m2 = (TextView) findViewById(R.id.tst2);
                            m2.setText(sTime2);
                        }
                        if (queryDocumentSnapshots.getDocuments().size()>=3) {
                            String sTime3 = queryDocumentSnapshots.getDocuments().get(2).get("Start_Time").toString();
                            String subject3 = queryDocumentSnapshots.getDocuments().get(2).get("Subject").toString();
                            TextView t3 = (TextView) findViewById(R.id.tsc3);
                            t3.setText(subject3);
                            TextView m3 = (TextView) findViewById(R.id.tst3);
                            m3.setText(sTime3);
                        }
                    }
                });
    }
}
