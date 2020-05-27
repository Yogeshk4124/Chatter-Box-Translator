package com.example.translator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.Toast;

import com.example.translator.Chat.fragmentChat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity {
    static int selector=R.id.Voice;
    int[][] states = new int[][] {
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {android.R.attr.state_checked}, // enabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] { android.R.attr.state_pressed},// pressed
    };

    int[] colors1 = new int[] {
            Color.WHITE,
            Color.parseColor("#BB86FC"),
            Color.WHITE,
            Color.WHITE
    };

    BottomNavigationView bottomNav;
    ColorStateList ColorStateList1 = new ColorStateList(states, colors1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomBar);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setItemTextColor(ColorStateList1);
    bottomNav.setItemIconTintList(ColorStateList1);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new fragmentVoice()).commit();
//            bottomNav.setItemIconTintList(ColorStateList1);
//            bottomNav.setItemTextColor(ColorStateList1);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.Voice:
                            if(selector==R.id.Voice)
                                return true;
                            Toast.makeText(getApplicationContext(),"reset",Toast.LENGTH_LONG).show();
//                          mNavView.setItemTextColor(navMenuTextList);
                            //                           mNavView.setItemIconTintList(navMenuIconList);
                            selector=R.id.Voice;
                            selectedFragment = new fragmentVoice();
//                            bottomNav.setItemIconTintList(ColorStateList1);
//                            bottomNav.setItemTextColor(ColorStateList1);


                            break;
                        case R.id.Text:
                            if(selector==R.id.Text)
                                return true;
//                            bottomNav.setItemIconTintList(ColorStateList1);
//                            bottomNav.setItemTextColor(ColorStateList1);
                            selector=R.id.Text;
                            selectedFragment = new fragmentText();
                            break;
                        case R.id.Image:
                            if(selector==R.id.Image)
                                return true;
//                            bottomNav.setItemIconTintList(ColorStateList1);
//                            bottomNav.setItemTextColor(ColorStateList1);
                            selector=R.id.Image;
                            selectedFragment = new fragmentImage();
                            break;
                        case R.id.Chat:
                            if(selector==R.id.Chat)
                                return true;
//                            bottomNav.setItemIconTintList(ColorStateList1);
//                            bottomNav.setItemTextColor(ColorStateList1);
                            selector=R.id.Chat;
                            selectedFragment=new fragmentChat();
                            break;
                        case R.id.Profile:
                            if(selector==R.id.Profile)
                                return true;
//                            bottomNav.setItemIconTintList(ColorStateList1);
//                            bottomNav.setItemTextColor(ColorStateList1);
                            selector=R.id.Profile;
                            selectedFragment=new Setting();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}