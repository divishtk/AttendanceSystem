package com.example.attendancesystem;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.HashMap;
import java.util.Map;

public class scannerCam extends AppCompatActivity {

    Uri imguri;
    Button btn, sendVerify;
    ImageView img;
    TextView name, sapId, add, course;
    FirebaseAuth fAuth;
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
    private StorageReference mStorageRef;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_scanner_cam);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        name = (TextView) findViewById(R.id.studName);
        add = (TextView) findViewById(R.id.studAdd);
        sapId = (TextView) findViewById(R.id.studSap);
        course = (TextView) findViewById(R.id.course);

        bottomNavigation = findViewById(R.id.bottom_navigation_student);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        final String email = prefs.getString("email", "");
        final String pwd = prefs.getString("password", "");
        Log.d("Auth", "Email" + email + "Password" + pwd);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User is signed in
            mStorageRef = FirebaseStorage.getInstance().getReference("Images");

            docRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot docSnap) {
                    name.setText(docSnap.get("Full_name").toString());
                    add.setText(docSnap.get("Email").toString());
                    sapId.setText(docSnap.get("Sap_ID").toString());
                    if (docRef.collection("StudentDetails").document("CollegeDetails").get().isComplete()) {
                        docRef.collection("StudentDetails").document("CollegeDetails")
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot docSnap) {
                                course.setText(docSnap.get("CourseName").toString());
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                course.setText("No course assigned");
                            }
                        });
                    }
                }
            });
        } else {
            // No user is signed in
            Toast.makeText(getApplicationContext(), "User not logged in !!", Toast.LENGTH_SHORT).show();
        }
        img = (ImageView) findViewById(R.id.imageView);
        btn = (Button) findViewById(R.id.camId);
        Button sendVerify = (Button) findViewById(R.id.scanForVerify);

        PushDownAnim.setPushDownAnimTo(btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    FileChooser();
                } else {
                    Toast.makeText(getApplicationContext(), "User not logged in !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        PushDownAnim.setPushDownAnimTo(sendVerify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                v.startAnimation(buttonClick);
                sendToVerification();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1777) {
                imguri = data.getData();
                img.setImageURI(imguri);
            }
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeMap = MimeTypeMap.getSingleton();
        return mimeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public void FileChooser() {
        Log.d("Image", "FileChooser...");
        Intent i = new Intent();
        i.setType("image/jpeg");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 1777);
    }

    public void sendToVerification() {
        Log.d("Image", "sendToVerification.....");
        final String imageName = sapId.getText().toString().trim() + "." + getExtension(imguri);
        final StorageReference sr = mStorageRef.child(imageName);
        sr.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();

//                        nestedData.put("urlName", downloadUrl.getEncodedPath().toString());
                        sr.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String profileImageUrl = task.getResult().toString();
                                Map<String, Object> nestedData = new HashMap<>();
                                nestedData.put("urlName", profileImageUrl);
                                nestedData.put("imageName", imageName);
                                Task<Void> ds = docRef.collection("StudentDetails").document("CollegeDetails")
                                        .update(nestedData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("File Update", "DocumentSnapshot successfully written!");
                                                Toast.makeText(getApplicationContext(), "Image URL Added", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                Toast.makeText(getApplicationContext(), "Successfully Uploaded as FaceID", Toast.LENGTH_SHORT).show();
                                Log.i("Downloaded URL", profileImageUrl);
                            }
                        });
                        Log.d("Image", "Image Successfully uploaded" + downloadUrl.getEncodedPath().toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        int errorCode = ((StorageException) exception).getErrorCode();
                        String errorMessage = exception.getMessage();
                        Log.d("Error " + errorCode, "Okay something looks fishy" + errorMessage);
                        Toast.makeText(getApplicationContext(), "Okay something looks fishy", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}