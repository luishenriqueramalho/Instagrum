package com.example.instagrum.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.instagrum.R;
import com.example.instagrum.fragment.FeedFragment;
import com.example.instagrum.fragment.PostFragment;
import com.example.instagrum.fragment.ProfileFragment;
import com.example.instagrum.fragment.SearchFragment;
import com.example.instagrum.helper.ConfigFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Instagrum");
        setSupportActionBar(toolbar);

        // Configurações de obj
        auth = ConfigFirebase.getFirebaseAuth();

        // Configrações do Bottom Navigation
        configBottomNavigationView();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();

    }

    // Método responsável por criar o Bottom Navigation
    private void configBottomNavigationView() {

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

        // Config inicial
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableItemShiftingMode(false);

        // Habilitar navigation
        enableNavigation(bottomNavigationViewEx);

    }

    /*
    * Método que trata eventos de click no BottomNavigation
    * @param viewEx
    * */
    private void enableNavigation(BottomNavigationViewEx viewEx) {

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (menuItem.getItemId()) {
                    case R.id.ic_home :
                        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                        return true;
                    case R.id.ic_search :
                        fragmentTransaction.replace(R.id.viewPager, new SearchFragment()).commit();
                        return true;
                    case R.id.ic_post :
                        fragmentTransaction.replace(R.id.viewPager, new PostFragment()).commit();
                        return true;
                    case R.id.ic_profile :
                        fragmentTransaction.replace(R.id.viewPager, new ProfileFragment()).commit();
                        return true;
                }

                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_signout :
                signOutUser();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOutUser() {
        try {
            auth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
