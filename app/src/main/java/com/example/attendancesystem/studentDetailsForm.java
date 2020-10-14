package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class studentDetailsForm extends AppCompatActivity {

    FirebaseUser user;
    DocumentReference docRef = null;
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
                            Toast.makeText(getApplicationContext(), "Button is Clicked", Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), SubjectsForm.class);
                            startActivity(i);
                            break;
                        case R.id.navigation_profilePage:
                            Toast.makeText(getApplicationContext(), "My Profile Button is Clicked", Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), profilePage.class);
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
        setContentView(R.layout.activity_student_details_form);

        final TextView sapId = (TextView) findViewById(R.id.editSap);
        final TextView course = (TextView) findViewById(R.id.editCourse);
        final TextView class1 = (TextView) findViewById(R.id.editClass);
        final TextView TS = (TextView) findViewById(R.id.editTS);
        final TextView rollno = (TextView) findViewById(R.id.editRollno);
        final Button btn = (Button) findViewById(R.id.addDetails);

        bottomNavigation = findViewById(R.id.bottom_navigation_teacher);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        user = FirebaseAuth.getInstance().getCurrentUser();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Log.d("TAG", "Processing ");
                    Query query = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("Sap_ID", "70611019008").limit(1);
                    try {

                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("TAG", document.getId() + " => " + document.getData());
                                        Map<String, Object> details = new HashMap<>();
                                        details.put("SapId", sapId.getText().toString());
                                        details.put("Class", class1.getText().toString());
                                        details.put("CourseName", course.getText().toString());
                                        details.put("TotalSubjects", TS.getText().toString());
                                        details.put("RollNo", rollno.getText().toString());
//                                    Toast.makeText(getApplicationContext(), "User data !! "+ document.getData(), Toast.LENGTH_SHORT).show();
                                        FirebaseFirestore.getInstance().collection("Users").document(document.getId())
                                                .collection("StudentDetails").document("CollegeDetails").set(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isComplete()) {
                                                    Toast.makeText(getApplicationContext(), "Data Successfully Added", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    } catch (Exception ex) {
                        Log.d("TAG", "Error getting documents: " + ex.getMessage());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "User not logged in !!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}