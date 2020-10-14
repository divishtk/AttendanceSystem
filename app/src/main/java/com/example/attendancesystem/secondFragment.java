package com.example.attendancesystem;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.HashMap;
import java.util.Map;


public class secondFragment extends Fragment {

    public static final String TAG = "TAG";
    EditText mfullname, msap, memail, mpass, mconpass, mdob;
    RadioGroup rg, rtype;
    RadioButton male, female;
    Button msub, mres;
    FirebaseAuth fAuth;
    private static View signupview;
    private static FragmentManager signupManager;
    FirebaseFirestore firebaseFirestore;
    String userId;
    RadioButton rb, rb2;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public secondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment secondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static secondFragment newInstance(String param1, String param2) {
        secondFragment fragment = new secondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        signupview = inflater.inflate(R.layout.fragment_second, null);

        signupManager = getActivity().getSupportFragmentManager();

        mfullname = (EditText) signupview.findViewById(R.id.editName);
        memail = (EditText) signupview.findViewById(R.id.editEA);
        msap = (EditText) signupview.findViewById(R.id.editSID);
        mpass = (EditText) signupview.findViewById(R.id.editCF);
        mconpass = (EditText) signupview.findViewById(R.id.editCF2);
        mdob = (EditText) signupview.findViewById(R.id.editDOB);
        male = (RadioButton) signupview.findViewById(R.id.radioButton3);
        female = (RadioButton) signupview.findViewById(R.id.radioButton2);
        rg = (RadioGroup) signupview.findViewById(R.id.gender);
        rtype = (RadioGroup) signupview.findViewById(R.id.type);
        msub = (Button) signupview.findViewById(R.id.button1);
        mres = (Button) signupview.findViewById(R.id.button2);
        fAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        PushDownAnim.setPushDownAnimTo(mres).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfullname.setText("");
                memail.setText("");
                msap.setText("");
                mpass.setText("");
                mconpass.setText("");
                mdob.setText("");
                male.setChecked(false);
                female.setChecked(false);
                mfullname.setText("");
                mfullname.setText("");
            }
        });


        PushDownAnim.setPushDownAnimTo(msub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = mfullname.getText().toString().trim();
                final String email = memail.getText().toString().trim();
                String password = mpass.getText().toString().trim();
                String confpassword = mconpass.getText().toString().trim();
                final String sap = msap.getText().toString().trim();
                final String dob = mdob.getText().toString().trim();

                final int checkid = rg.getCheckedRadioButtonId();
                rb = (RadioButton) signupview.findViewById(checkid);

                final int checkid2 = rtype.getCheckedRadioButtonId();
                rb2 = (RadioButton) signupview.findViewById(checkid2);

                if (TextUtils.isEmpty(name)) {
                    mfullname.setError("Name is Required.");
                    return;
                }
                if (TextUtils.isEmpty(sap)) {
                    msap.setError("Sap Id is Required.");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    memail.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mpass.setError("Password is Required.");
                    return;
                }
                if (TextUtils.isEmpty(confpassword)) {
                    mconpass.setError("Please Enter The Confirm Password!!");
                    return;
                }
                if (!password.equals(confpassword)) {
                    mconpass.setError("Password not matched");
                    return;
                }
                if (TextUtils.isEmpty(dob)) {
                    mdob.setError("Please Enter The Date");
                }
                if (password.length() < 6) {
                    mpass.setError("Password Should be 6 Character and more");
                    return;
                }
                if (TextUtils.isEmpty(rb2.getText().toString())) {
                    rb2.setError("Select Account Type");
                    return;
                }
                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(sap) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(confpassword) && TextUtils.isEmpty(dob)) {
                    mfullname.setError("Name is Required.");
                    msap.setError("Sap Id is Required.");
                    memail.setError("Email is Required.");
                    mpass.setError("Password is Required.");
                    mconpass.setError("Please Enter The Confirm Password!!");
                    mdob.setError("Please Enter The Date");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Register Successfully !!", Toast.LENGTH_SHORT).show();
                            userId = fAuth.getCurrentUser().getUid();
                            //allow read, write: if request.auth != null;
                                DocumentReference documentReference = firebaseFirestore.collection("Users").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Full_name", name);
                            user.put("Sap_ID", sap);
                            user.put("Email", email);
                            user.put("DOB", dob);
                            //  user.put("Gender",gen);
                            if (checkid == rb.getId()) {
                                user.put("Gender", rb.getText());
                            }
                            if (checkid2 == rb2.getId()) {
                                user.put("Type", rb2.getText());
                            }
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Log.d(TAG, "onSuccess: User Profile is Created for "+userId);
                                    Log.d(TAG, "onSuccess: ");
                                }
                            });
                        } else {
                            memail.setError("Email Already Exist!!");
                            //Toast.makeText(getActivity(), "Register Unsuccessful !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                validateEmail();
            }
        });


        return signupview;
    }

    public void validateEmail() {


    }
}