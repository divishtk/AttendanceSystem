package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    ViewPager pager;
    TabLayout mTabLayout;
    TabItem first, second;
    PagerAdapter adapter;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);   //androidx toolbar
        setSupportActionBar(toolbar);

        pager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tablayout);

        first = findViewById(R.id.firstitem);
        second = findViewById(R.id.seconditem);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        adapter = new PagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mTabLayout.getTabCount());
        pager.setAdapter(adapter);

        //test

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case R.id.mhome:
                Toast.makeText(this,"Home Button is Clicked",Toast.LENGTH_SHORT).show();
                i = new Intent(getApplicationContext(), HomePage.class);
                break;
            case R.id.mAttd:
                Toast.makeText(this, "Attandance Button is Clicked", Toast.LENGTH_SHORT).show();
                i = new Intent(getApplicationContext(), AttendanceTable.class);
                break;
            case R.id.mTable:
                Toast.makeText(this, "Time Table Button is Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mProf:
                Toast.makeText(this, "My Profile Button is Clicked", Toast.LENGTH_SHORT).show();
                i = new Intent(getApplicationContext(), profilePage.class);
                break;
            case R.id.mSupp:
                Toast.makeText(this, "Support Button is Clicked", Toast.LENGTH_SHORT).show();
                i = new Intent(getApplicationContext(), calendarView.class);
                break;
            case R.id.mScan:
                Toast.makeText(this, "Logout Button is Clicked", Toast.LENGTH_SHORT).show();
                i = new Intent(getApplicationContext(), scannerCam.class);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        startActivity(i);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

}