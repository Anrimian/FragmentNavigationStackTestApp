package com.github.anrimian.fragmentnavigationstacktestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.anrimian.fragmentnavigationstacktestapp.navigation.FragmentNavigation;
import com.github.anrimian.fragmentnavigationstacktestapp.navigation.JugglerView;

public class MainActivity extends AppCompatActivity {

    private FragmentNavigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JugglerView jugglerView = findViewById(R.id.juggler_view);

        navigation = FragmentNavigation.from(getSupportFragmentManager());
        navigation.initialize(jugglerView);

        if (savedInstanceState == null) {
            navigation.addNewFragment(() -> TestFragment.newInstance(0));
        }
    }

    @Override
    public void onBackPressed() {
        if (!navigation.goBack(R.anim.anim_slide_out_right)) {
            super.onBackPressed();
        }
    }

    public void addNewFragment() {
        int fragmentsCount = navigation.getScreensCount();
        navigation.addNewFragment(() -> TestFragment.newInstance(fragmentsCount), R.anim.anim_slide_in_right);
    }
}
