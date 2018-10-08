package com.github.anrimian.fragmentnavigationstacktestapp;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private JugglerView jugglerView;

    private LinkedList<FragmentCreator> fragments = new LinkedList<>();
    private Fragment topFragment;
    private Fragment bottomFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jugglerView = findViewById(R.id.juggler_view);
        jugglerView.init();

        if (savedInstanceState == null) {
            fragments.add(() -> TestFragment.newInstance(0));

            int id = jugglerView.prepareTopView();
            topFragment = fragments.getLast().createFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(id, topFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (fragments.size() > 1) {
            fragments.removeLast();
            Log.d("KEK", "remove fragment, current list size: " + fragments.size());
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(0, R.anim.anim_slide_out_right)
                    .remove(topFragment)
                    .runOnCommit(() -> {

                        //async issue
                        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_right);
                        jugglerView.postDelayed(() -> {
                            Log.d("KEK", "move top view to bottom");
                            int id = jugglerView.prepareBottomView();
                            topFragment = bottomFragment;
                            if (fragments.size() > 1) {
                                Log.d("KEK", "create bottom fragment at: " + (fragments.size() - 2));
                                bottomFragment = fragments.get(fragments.size() - 2).createFragment();//find better solution later
                                getSupportFragmentManager().beginTransaction()
                                        .replace(id, bottomFragment)
                                        .commit();
                            }
                        }, animation.getDuration());
                    })
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    public void addNewFragment() {
        int fragmentsCount = fragments.size();
        Log.d("KEK", "addNewFragment, number: " + fragmentsCount);
        fragments.add(() -> TestFragment.newInstance(fragmentsCount));
        int id = jugglerView.prepareTopView();
        bottomFragment = topFragment;
        topFragment = fragments.getLast().createFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.anim_slide_in_right, 0/*, 0, R.anim.anim_slide_out_right*/)
                .replace(id, topFragment)
                .commit();
    }

    private interface FragmentCreator {
        Fragment createFragment();
    }
}
