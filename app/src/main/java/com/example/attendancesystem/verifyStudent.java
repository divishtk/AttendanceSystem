package com.example.attendancesystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class verifyStudent extends AppCompatActivity {

    TextView t1;
    TextView t2;
    TextView t3;
    ImageView iv;
    StorageReference storageRef;
    FirebaseUser user;

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
        setContentView(R.layout.activity_verify_student);

        t1 = (TextView) findViewById(R.id.namee);
        t2 = (TextView) findViewById(R.id.sapid);
        t3 = (TextView) findViewById(R.id.course);
        iv = (ImageView) findViewById(R.id.imageView4);

        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String sapid = bundle.getString("sapid");
        String course = bundle.getString("course");

        t1.setText(name);
        t2.setText(sapid);
        t3.setText(course);

        bottomNavigation = findViewById(R.id.bottom_navigation_teacher);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            storageRef = FirebaseStorage.getInstance().getReference("Images");

            storageRef.child(sapid + ".jpg").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    iv.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.e("Bitmap", exception.getMessage());
                }
            });
        }

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> details = new HashMap<>();
                details.put("isVerified", "YES");

                DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
                docRef.update(details).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Student Face Id has been accepted", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> details = new HashMap<>();
                details.put("isVerified", "NO");

                DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
                docRef.update(details).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Student Face Id has been rejected", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}