package com.example.askdoctors.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.askdoctors.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        bottomNavigation = findViewById(R.id.Admin_nav);

        getSupportFragmentManager().beginTransaction().replace(R.id.admins_container, new Admin_DoctorsFragment()).commit();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.doctors_nav:
                        fragment = new Admin_DoctorsFragment();
                        break;
                    case R.id.admins_nav:
                        fragment = new Admin_AdminsFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.admins_container, fragment).commit();
                return true;
            }
        });
    }
}
