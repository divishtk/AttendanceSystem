package com.example.attendancesystem;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StudentVerifyTable extends AppCompatActivity {


    private static final String TAG = "StudentVerifyTable";
    /* TextView t1;
     TextView t2;
     TextView t3;
     TextView t4;
     TextView t5;
     TextView t6;*/
    /*FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mas;
    FirebaseFirestore ff;
    String name;
    String  sapid;*/
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mas;
    FirebaseFirestore ff;
    String name;
    String sapid;
    String verification;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    ArrayList<StudentClass> sal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentverify);

        firebaseAuth = FirebaseAuth.getInstance();
        String userid = firebaseAuth.getCurrentUser().getUid();
        Log.d(TAG, "User id " + userid);

        ff = FirebaseFirestore.getInstance();
        ff.collection("Users").whereEqualTo("isVerified", "NO").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                //getting data
                                name = documentSnapshot.getString("Full_name");
                                verification = documentSnapshot.getString("isVerified");
                                sapid = documentSnapshot.getString("Sap_ID");
                                String type = documentSnapshot.getString("Student");
                                String DOB = documentSnapshot.getString("DOB");
                                String gender = documentSnapshot.getString("Gender");

                                StudentClass sc = new StudentClass();
                                sc.setName(name);
                                sc.setSapid(sapid);
                                sc.setIsVerified(verification);
                                sal.add(sc);

                                recyclerView = (RecyclerView) findViewById(R.id.recycle21);
                                recyclerAdapter = new RecyclerAdapter(getApplicationContext(),sal);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                recyclerView.setAdapter(recyclerAdapter);

                                Log.i("Students", "-> "+ documentSnapshot.getData().values());
                            }
                        } else {
                            Log.d(TAG, "ERROR" + task.getException());
                        }
                    }
                });

    }
}