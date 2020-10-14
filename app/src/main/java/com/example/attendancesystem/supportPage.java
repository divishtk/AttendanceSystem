package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class supportPage extends AppCompatActivity {

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
                            Toast.makeText(getApplicationContext(), "My Logout Button is Clicked", Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), profilePage.class);
                            startActivity(i);
                            break;
                    }
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_support_page);


        bottomNavigation = findViewById(R.id.bottom_navigation_student);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }
}