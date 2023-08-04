package com.pemesananlapanganfutsal;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.pemesananlapanganfutsal.fragment.AboutFragment;
import com.pemesananlapanganfutsal.fragment.HomeFragment;
import com.pemesananlapanganfutsal.fragment.HowtoFragment;
import com.pemesananlapanganfutsal.fragment.LoginFragment;
import com.pemesananlapanganfutsal.fragment.ProfilFragment;
import com.pemesananlapanganfutsal.fragment.RegisterFragment;
import com.pemesananlapanganfutsal.fragment.SearchFragment;
import com.pemesananlapanganfutsal.model.User;
import com.pemesananlapanganfutsal.utils.SessionHandler;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private User user;
    private NavigationView navigationView;
    private SessionHandler sessionHandler;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(drawerListener);
        sessionHandler = new SessionHandler(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerSlide(View view, float arg1) {
                super.onDrawerSlide(view, arg1);
                Menu nav_Menu = navigationView.getMenu();
                user = sessionHandler.getUserDetails();
                if (isEmpty(user.getIdPemesan())) {
                    nav_Menu.findItem(R.id.nav_login).setVisible(true);
                    nav_Menu.findItem(R.id.nav_register).setVisible(true);
                    nav_Menu.findItem(R.id.nav_logout).setVisible(false);
                } else {
                    nav_Menu.findItem(R.id.nav_login).setVisible(false);
                    nav_Menu.findItem(R.id.nav_register).setVisible(false);
                    nav_Menu.findItem(R.id.nav_logout).setVisible(true);
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState(); // hamburger menu state

        if (savedInstanceState == null) {
            // default load home fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private NavigationView.OnNavigationItemSelectedListener drawerListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_howto:
                            selectedFragment = new HowtoFragment();
                            break;
                        case R.id.nav_about:
                            selectedFragment = new AboutFragment();
                            break;
                        case R.id.nav_login:
                            selectedFragment = new LoginFragment();
                            break;
                        case R.id.nav_register:
                            selectedFragment = new RegisterFragment();
                            break;
                        case R.id.nav_logout:
                            sessionHandler.logoutUser();
                            selectedFragment = new HomeFragment();
                            break;
                    }

                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    drawer.closeDrawer(GravityCompat.START);

                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_user) {
            user = sessionHandler.getUserDetails();
            if (isEmpty(user.getIdPemesan())) {
                Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent2);
            } else {
                Fragment selectedFragment = new ProfilFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tekan 2x untuk keluar", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }
}