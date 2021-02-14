package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherHome extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent i = null;
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            Toast.makeText(getApplicationContext(), "Home Button is Clicked", Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), TeacherHome.class);
                            startActivity(i);
                            break;
                        case R.id.navigation_student_details:
                            Toast.makeText(getApplicationContext(), "Attandance Button is Clicked", Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), studentDetailsForm.class);
                            startActivity(i);
                            break;
                        case R.id.navigation_subject_details:
                            Toast.makeText(getApplicationContext(), "My Attendance Button is Clicked", Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), SubjectsForm.class);
                            startActivity(i);
                            break;
                        case R.id.navigation_profilePage:
                            Toast.makeText(getApplicationContext(), "My Profile Button is Clicked", Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), teacherProfilePage.class);
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
        setContentView(R.layout.activity_teacher_home);

        bottomNavigation = findViewById(R.id.bottom_navigation_teacher);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            TextView teach_sapid = (TextView) findViewById(R.id.teach_sapid);
                            teach_sapid.setText(documentSnapshot.getString("Sap_ID"));
                            TextView teach_name = (TextView) findViewById(R.id.teach_name);
                            teach_name.setText(documentSnapshot.getString("Full_name"));
                            TextView teach_dept = (TextView) findViewById(R.id.teach_dept);
                            teach_dept.setText("Technical");
                            //documentSnapshot.getString("Technical");
                        }
                    });
        }

        findViewById(R.id.cardView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHome.this, StudentList.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cardView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHome.this, teacherSchedule.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cardView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHome.this, StudentVerifyTable.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cardView4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHome.this, studentDetailsForm.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cardView5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHome.this, supportPage.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cardView6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                Intent i;
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
            }
        });
    }
}