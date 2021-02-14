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

import com.android.volley.DefaultRetryPolicy;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class openCamera extends AppCompatActivity {

    Uri imguri;
    ImageView img;

    TextView name, sapId, add, course, lectId, subj;
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
        lectId = (TextView) findViewById(R.id.lectId);
        subj = (TextView) findViewById(R.id.subject);
        Button btn = (Button) findViewById(R.id.openCamera);

        String lect_Id = getIntent().getStringExtra("lectId");
        String subject = getIntent().getStringExtra("subject");
        Toast.makeText(getApplicationContext(), "Lecture Id is "+lect_Id ,Toast.LENGTH_SHORT).show();
        lectId.setText(lect_Id);
        subj.setText(subject);



        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        Toast.makeText(getApplicationContext(), prefs.getString("apiAddress", ""),Toast.LENGTH_SHORT).show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/jpeg");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, 1777);
                // TODO: Convert to camera images ;)
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
        if(!"".equalsIgnoreCase(lect_Id)){
            //code for add attendance
            FirebaseFirestore.getInstance().collection("Schedules").document(lect_Id)
                    .update("sap_attend", FieldValue.arrayUnion(sapId.getText()));
            Log.i("FireStore","Added in doc "+ lect_Id);
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
                    Toast.makeText(getApplicationContext(),"uploading...",Toast.LENGTH_LONG).show();
                    sendToCompare();

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
                                                            String[] resp = SimpleReq(img1,img2);
                                                            Log.d("Resp",resp.toString());
                                                        }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.i("Response","Failed"+e);
                                                    }
                                                });
//                                              String[] resp = SendImage2(img1,img2);
//                                              Code for add attendance for a schedule
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


    private String[] SimpleReq(final String img1, final String img2) {
        // Instantiate the RequestQueue.
        final String[] resp = new String[3];
        RequestQueue queue = Volley.newRequestQueue(this);
//        String REGISTER_URL = "http://192.168.29.48:5000/api/testParams";
        String REGISTER_URL = "http://192.168.29.48:5000/api/faceRecog2";
        if(!prefs.getString("apiAddress", "").equals("")){
            REGISTER_URL = prefs.getString("apiAddress", "") + "/api/faceRecog2";
            Log.i("REGISTER_URL","url set "+ REGISTER_URL);
        }
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response ",response);
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            Log.i("resp 1",jsonObject.getString("Matched"));
                            Log.i("resp 2",jsonObject.getString("percentage"));
                            resp[0] = response;
                            resp[1] = jsonObject.getString("percentage");
//
                            if(jsonObject.getString("Matched").equals("True")){

                                String lect_Id = getIntent().getStringExtra("lectId");
                                if(!"".equalsIgnoreCase(lect_Id)){
                                    //code for add attendance
                                    FirebaseFirestore.getInstance().collection("Schedules").document(lect_Id)
                                            .update("sap_attend", FieldValue.arrayUnion(sapId.getText()));
                                    Log.i("FireStore","Added in doc "+ lect_Id);
                                }
                                Toast.makeText(getApplicationContext(), "Attendance Given!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Face ID didn't Match. Failed to give Attendance!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("img_url1",img1);
                params.put("img_url2",img2);
//                params.put("img_url1","https://firebasestorage.googleapis.com/v0/b/virtualattendance-c2d55.appspot.com/o/Images%2Fselena.PNG?alt=media&token=c7ec705c-16d4-4bfa-a0a0-966e0341e5b5");
//                params.put("img_url1","https://firebasestorage.googleapis.com/v0/b/virtualattendance-c2d55.appspot.com/o/Images%2Fselena_test.png?alt=media&token=78445ea3-51a3-4127-ae22-6f082b4a763e");
                return params;
            }

        };
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                1,
                1f));
        queue.add(stringRequest);
        return resp;
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
