package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profilePage extends AppCompatActivity {

    private static final String TAG = "profilePage";
    FirebaseFirestore ff;

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
        setContentView(R.layout.activity_profile_page_student);

        final TextView tx1 = (TextView) findViewById(R.id.student_profile_name);
        final TextView tx2 = (TextView) findViewById(R.id.student_profile_name);
        final TextView tx3 = (TextView) findViewById(R.id.student_profile_name);
        final TextView tx4 = (TextView) findViewById(R.id.student_profile_name);

        bottomNavigation = findViewById(R.id.bottom_navigation_teacher);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ff = FirebaseFirestore.getInstance();
        ff.collection("Users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = documentSnapshot.getString("Full_name");
                        String verification = documentSnapshot.getString("isVerified");
                        String sapid = documentSnapshot.getString("Sap_ID");
                        String type = documentSnapshot.getString("Student");
                        String DOB = documentSnapshot.getString("DOB");
                        String gender = documentSnapshot.getString("Gender");

                        tx1.setText(name);
                        tx2.setText(sapid);
                        tx3.setText(DOB);

                        ff.collection("Users").document(user.getUid()).collection("StudentDetails").document("CollegeDetails")
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot docSnap) {
                                tx4.setText(docSnap.getString("CourseName"));
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                tx4.setText("No course assigned");
                            }
                        });

                    }
                });
    }
}