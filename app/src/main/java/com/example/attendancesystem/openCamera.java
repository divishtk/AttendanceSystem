package com.example.attendancesystem;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class openCamera extends AppCompatActivity {

    Uri imguri;
    ImageView img;

    TextView name, sapId, add, course;
    FirebaseAuth fAuth;
    FirebaseUser user;
    DocumentReference docRef = null;
    SharedPreferences prefs;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_camera);

        name = (TextView) findViewById(R.id.studName);
        add = (TextView) findViewById(R.id.studAdd);
        sapId = (TextView) findViewById(R.id.studSap);
        course = (TextView) findViewById(R.id.course);
        Button btn = (Button) findViewById(R.id.openCamera);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toast.makeText(getApplicationContext(), prefs.getString("apiAddress", ""),Toast.LENGTH_SHORT).show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/jpeg");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, 1777);
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            mStorageRef = FirebaseStorage.getInstance().getReference("Temp-Images");

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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1777) {
                Uri filePath = data.getData();
                try {
                    imguri = data.getData();
                    sendToCompare();


//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                    Bitmap lastBitmap = null;
//                    lastBitmap = bitmap;
//                    //encoding image to string
//                    String image = getStringImage(lastBitmap);
//                    Log.d("image", image);
                    //passing the image to volley
//                    SimpleReq();
//                    SendImage2(lastBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeMap = MimeTypeMap.getSingleton();
        return mimeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void sendToCompare() {
        final String imageName1 = sapId.getText().toString().trim() + "." + getExtension(imguri);
        final String imageName2 = "Temp-" + sapId.getText().toString().trim() + "." + getExtension(imguri);
        final StorageReference sr = mStorageRef.child(imageName2);
        sr.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                        sr.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String profileImageUrl = task.getResult().toString();
                                Map<String, Object> nestedData = new HashMap<>();
                                nestedData.put("temp-urlName", profileImageUrl);
                                nestedData.put("temp-imageName", imageName2);
                                Task<Void> ds = docRef.collection("StudentDetails").document("CollegeDetails")
                                        .update(nestedData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                docRef.collection("StudentDetails").document("CollegeDetails")
                                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            String img1 = documentSnapshot.get("urlName").toString();
                                                            String img2 = documentSnapshot.get("temp-urlName").toString();
                                                            Log.i("Image 1 Url", img1);
                                                            Log.i("Image 2 Url", img2);
                                                        }
                                                    });

//                                              String[] resp = SendImage2(img1,img2);
//                                              Code for add attendance for a schedule
                                                Toast.makeText(getApplicationContext(), "Student Verified ", Toast.LENGTH_SHORT).show();

                                            }
                                        });

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

    private void SimpleReq() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.43.48:5000/name/Steven";

        // Request a string response from the provided URL.
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Simple1", "Response is: " + response);
                        Toast.makeText(getApplicationContext(), "Success" + response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET Request", "Error is: " + error);
            }
        });
        queue.add(stringRequest1);
    }

    private String[] SendImage2(final String downloadImageUrl1, final String downloadImageUrl2) {

        final String[] resp = new String[3];
//        String url = "http://192.168.43.48:5000/api/acceptImage";
        String url = "";
        url = prefs.getString("apiAddress", "");
        try {
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,url , new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    // parse success output
                    resp[0] = resultResponse;
                    resp[1] = resultResponse;
                    resp[2] = resultResponse;
                    Log.d("Volley", resultResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d("Volley", error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("image1", downloadImageUrl1);
                    params.put("image1", downloadImageUrl2);
                    return params;
                }

//                @Override
//                protected Map<String, DataPart> getByteData() {
//                    Map<String, DataPart> params = new HashMap<>();
//                    // file name could found file base or direct access from real path
//                    // for now just get bitmap data from ImageView
//                    params.put("image1", new DataPart("file.jpg", getFileDataFromDrawable(bitmap1)));
//                    params.put("image2", new DataPart("file.jpg", getFileDataFromDrawable(bitmap1)));
//                    return params;
//                }
            };

            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}
