package com.example.attendancesystem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;


public class HomePage extends AppCompatActivity {
    Button log;
    TextView Name;
    TextView Sapid;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mas;
    FirebaseFirestore ff;
    private Fragment frag;
    ConstraintLayout a, b;
    FrameLayout f;
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
                            Toast.makeText(getApplicationContext(), "Logged In UID"+fAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                            if(!"".equals(fAuth.getCurrentUser().getUid())){
                                fAuth.signOut();
                                Toast.makeText(getApplicationContext(), "Logout Button is Clicked", Toast.LENGTH_SHORT).show();
                                i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                            }else {
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

        log = (Button) findViewById(R.id.logout);
        Name = findViewById(R.id.nans);
        //Sapid=findViewById(R.id.sans);
        firebaseAuth = FirebaseAuth.getInstance();
        ff = FirebaseFirestore.getInstance();

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();

        float randomMultiplier = 30f;
//
//        for (int i = 1; i < 6; i++) {
//            values1.add(new BarEntry(i, (int) (Math.random() * randomMultiplier)%15));
//            values2.add(new BarEntry(i, (int) (Math.random() * randomMultiplier)%15));
//        }
//        BarDataSet dataSet = new BarDataSet(values1, "Present"); // add entries to dataset
//        BarDataSet dataSet2 = new BarDataSet(values2, "Absent"); // add entries to dataset
//        dataSet.setColor(Color.rgb(1, 231, 0));
//        dataSet2.setColor(Color.rgb(255, 0, 0));
//        dataSet.setValueTextColor(Color.rgb(0, 0, 0));
//        dataSet2.setValueTextColor(Color.rgb(0, 0, 0));
        float groupSpace = 0.04f;
        float barSpace = 0.02f;
        float barWidth = 0.46f;
        BarData bar = new BarData(getDataSet().get(0),getDataSet().get(1));
        BarChart chart = (BarChart) findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);


        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisValues()));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        chart.fitScreen();
        chart.setDrawBorders(true);
        chart.setPadding(20,0,20,0);
        chart.setData(bar);
        chart.getBarData().setBarWidth(barWidth);
        chart.groupBars(0f,groupSpace, barSpace);
        chart.invalidate();
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
                    Name.setText(documentSnapshot != null ? "Welcome, "+documentSnapshot.getString("Full_name")+"." : null);

                    log.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseAuth.getInstance().signOut();
                            //  ((ConstraintLayout) findViewById(R.id.cc)).removeAllViews();
                            // FragmentManager f=getSupportFragmentManager();
                            //FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                            //frag=new firstFragment();
                            //ft.replace(R.id.cc,frag).addToBackStack(null);
                            //  ft.addToBackStack(null);

                            //ft.commit();
                            Intent i = new Intent(HomePage.this, MainActivity.class);
//                            startActivity(i);
                            Toast.makeText(getApplicationContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                            // a.setVisibility(View.GONE);
                            // Intent(getApplicationContext(),)
                        }
                    });
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(1, 3); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(2, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(3, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(4, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(5, 4); // May
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(6, 5); // Jun
        valueSet1.add(v1e6);
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(1, 4); // Jan
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(2, 1); // Feb
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(3, 2); // Mar
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(4, 3); // Apr
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(5, 4); // May
        valueSet2.add(v2e5);
        BarEntry v2e6 = new BarEntry(6, 5); // Jun
        valueSet2.add(v2e6);
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Present");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Absent");
        barDataSet2.setColors(Color.rgb(251, 2, 0));
        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        return xAxis;
    }
}
