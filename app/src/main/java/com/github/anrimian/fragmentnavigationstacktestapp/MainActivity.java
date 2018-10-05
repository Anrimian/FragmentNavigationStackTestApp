package com.github.anrimian.fragmentnavigationstacktestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private JugglerView jugglerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jugglerView = findViewById(R.id.juggler_view);
        jugglerView.init();

        if (savedInstanceState == null) {
            int id = jugglerView.addViewToTop();
            getSupportFragmentManager().beginTransaction()
                    .replace(id, new TestFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void addNewFragment() {
        int id = jugglerView.addViewToTop();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.anim_slide_in_right, 0, 0, R.anim.anim_slide_out_right)
                .replace(id, new TestFragment())
                .commit();
    }
}
