package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class teacherProfilePage extends AppCompatActivity {

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
                            i = new Intent(getApplicationContext(), profilePage.class);
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
        setContentView(R.layout.activity_profile_page_teacher);

        final TextView tx1 = (TextView) findViewById(R.id.teach_profile_name);
        final TextView tx2 = (TextView) findViewById(R.id.teach_profile_Id);
        final TextView tx3 = (TextView) findViewById(R.id.teach_profile_title);
        final TextView tx4 = (TextView) findViewById(R.id.teach_profile_dept);

        bottomNavigation = findViewById(R.id.bottom_navigation_teacher);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ff= FirebaseFirestore.getInstance();
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
                        tx3.setText(name.split(" ")[0]);

                        ff.collection("Users").document(user.getUid()).collection("StudentDetails").document("CollegeDetails")
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot docSnap) {
                                tx4.setText(docSnap.getString("CourseName"));//change to department
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