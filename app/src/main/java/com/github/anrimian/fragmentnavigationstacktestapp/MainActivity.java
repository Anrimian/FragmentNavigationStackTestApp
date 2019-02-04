package com.github.anrimian.fragmentnavigationstacktestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.anrimian.fragmentnavigationstacktestapp.navigation.FragmentNavigation;
import com.github.anrimian.fragmentnavigationstacktestapp.navigation.FragmentStackListener;
import com.github.anrimian.fragmentnavigationstacktestapp.navigation.JugglerView;

public class MainActivity extends AppCompatActivity {

    private FragmentNavigation navigation;
    private final FragmentStackListener listener = new StackChangeListenerImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JugglerView jugglerView = findViewById(R.id.juggler_view);

        navigation = FragmentNavigation.from(getSupportFragmentManager());
        navigation.initialize(jugglerView, savedInstanceState);
        navigation.addStackChangeListener(listener);
        navigation.setExitAnimation(R.anim.anim_slide_out_right);
        navigation.setEnterAnimation(R.anim.anim_slide_in_right);
        navigation.setRootExitAnimation(R.anim.anim_alpha_disappear);

        if (savedInstanceState == null) {
            navigation.newRootFragment(() -> TestFragment.newInstance(0));
        }
    }

    @Override
    public void onBackPressed() {
        if (!navigation.goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        navigation.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigation.removeStackChangeListener(listener);
    }

    private class StackChangeListenerImpl implements FragmentStackListener {
        @Override
        public void onStackChanged(int stackSize) {
            Toast.makeText(MainActivity.this, "stack size: " + stackSize, Toast.LENGTH_SHORT).show();

        }
    }
}
