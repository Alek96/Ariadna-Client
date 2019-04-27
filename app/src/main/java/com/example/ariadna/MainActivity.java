package com.example.ariadna;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private DrawerLayout drawer;
    private BluetoothConnectionService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new BTDeviceListFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Started");
        super.onDestroy();
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: Started");
        super.onResume();
        if (bluetoothService != null) {
            if (bluetoothService.getState() == BluetoothConnectionService.STATE_NONE) {
                bluetoothService.start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: Started");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.d(TAG, "onBackPressed: close drawer");
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void active() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: Started");
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        } else if (id == R.id.nav_tests) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new TestsFragment()).commit();
        } else if (id == R.id.nav_tools) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ConfigurationsFragment()).commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void createBluetoothService(@NonNull String address) {
        Log.d(TAG, "createBluetoothService: Started with: " + address);
        bluetoothService = new BluetoothConnectionService(this, address);
        bluetoothService.start();
        Toast.makeText(this, R.string.connecting, Toast.LENGTH_SHORT).show();
    }

    public BluetoothConnectionService getBluetoothService() {
        Log.d(TAG, "getBluetoothService: Started");
        return bluetoothService;
    }
}
