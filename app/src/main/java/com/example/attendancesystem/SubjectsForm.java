package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SubjectsForm extends AppCompatActivity {

    TextView sapid, course, subject, teach;
    ListView list_view;
    BottomNavigationView bottomNavigation;
    Button btn;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();
    CustomItemAdapter customItemAdapter;
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
    int clickCounter = 0;
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
        setContentView(R.layout.activity_subjects_form);

        sapid = (TextView) findViewById(R.id.editSubSapId);
        course = (TextView) findViewById(R.id.editSubCourse);
        subject = (TextView) findViewById(R.id.editSubSubject);
        teach = (TextView) findViewById(R.id.editSubTeach);
        list_view = (ListView) findViewById(R.id.list_view);
        btn = (Button) findViewById(R.id.addSubj);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItems(view);
            }
        });


        bottomNavigation = findViewById(R.id.bottom_navigation_teacher);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        customItemAdapter = new CustomItemAdapter(getApplicationContext(), R.layout.activity_row_layout);
        list_view.setAdapter(customItemAdapter);

        List<String[]> fruitList = readData();
        for(String[] fruitData:fruitList ) {
            String cell1 = fruitData[0];
            String cell2 = fruitData[1];
            String cell3 = fruitData[2];
            String cell4 = fruitData[3];
            SubjectsClass subjects = new SubjectsClass(cell1,cell2,cell3,cell4);
            customItemAdapter.add(subjects);
        }
    }

    public List<String[]> readData(){
        List<String[]> resultList = new ArrayList<String[]>();

        String[] fruit7 = new String[4];
        fruit7[0] = "70611019008";
        fruit7[1] = "MCA";
        fruit7[2] = "IEM";
        fruit7[3] = "Radhika";
        resultList.add(fruit7);

        String[] fruit1 = new String[4];
        fruit1[0] = "70611019008";
        fruit1[1] = "MCA";
        fruit1[2] = "OOSE";
        fruit1[3] = "Swartantala";
        resultList.add(fruit1);

        return  resultList;
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItems(View v) {
//        listItems.add("Clicked : " + (clickCounter++) + " Attr: " + sapid.getText());
        String cell1 = sapid.getText().toString();
        String cell2 = course.getText().toString();
        String cell3 = subject.getText().toString();
        String cell4 = teach.getText().toString();
        SubjectsClass subjects = new SubjectsClass(cell1,cell2,cell3,cell4);
        customItemAdapter.add(subjects);
        customItemAdapter.notifyDataSetChanged();
    }
}