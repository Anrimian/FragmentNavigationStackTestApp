package com.github.anrimian.fragmentnavigationstacktestapp;

import android.content.res.Resources;
import android.support.annotation.AnimRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.LinkedList;

public class FragmentNavigation {

    private final JugglerView jugglerView;
    private final FragmentManager fragmentManager;
    private final LinkedList<FragmentCreator> fragments = new LinkedList<>();

    private Fragment topFragment;
    private Fragment bottomFragment;

    private boolean isNavigationEnabled = true;

    public FragmentNavigation(JugglerView jugglerView, FragmentManager fragmentManager) {
        this.jugglerView = jugglerView;
        jugglerView.init();
        this.fragmentManager = fragmentManager;
    }

    public void addNewFragment(FragmentCreator fragmentCreator) {
        addNewFragment(fragmentCreator, 0);
    }

    public void addNewFragment(FragmentCreator fragmentCreator,
                               @AnimRes int enterAnimation) {
        if (isNavigationEnabled) {
            isNavigationEnabled = false;
            fragments.add(fragmentCreator);
            int id = jugglerView.prepareTopView();
            bottomFragment = topFragment;
            topFragment = fragments.getLast().createFragment();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(enterAnimation, 0)
                    .replace(id, topFragment)
                    .runOnCommit(() -> isNavigationEnabled = true)
                    .commit();
        }
    }

    public boolean goBack() {
        return goBack(0);
    }

    public boolean goBack(@AnimRes int exitAnimation) {
        if (fragments.size() <= 1) {
            return false;
        }
        if (isNavigationEnabled) {
            isNavigationEnabled = false;
            fragments.removeLast();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(0, exitAnimation)
                    .remove(topFragment)
                    .runOnCommit(() -> replaceBottomFragment(exitAnimation))
                    .commit();
        }
        return true;
    }

    public int getScreenCount() {
        return fragments.size();
    }

    private void replaceBottomFragment(@AnimRes int exitAnimation) {
        jugglerView.postDelayed(() -> {
            int id = jugglerView.prepareBottomView();
            topFragment = bottomFragment;
            if (fragments.size() > 1) {
                bottomFragment = fragments.get(fragments.size() - 2).createFragment();//find better solution later
                fragmentManager.beginTransaction()
                        .replace(id, bottomFragment)
                        .runOnCommit(() -> isNavigationEnabled = true)
                        .commit();
            } else {
                isNavigationEnabled = true;
            }
        }, getAnimationDuration(exitAnimation));
    }

    private long getAnimationDuration(@AnimRes int exitAnimation) {
        if (exitAnimation == 0) {
            return 0;
        }
        try {
            Animation animation = AnimationUtils.loadAnimation(jugglerView.getContext(), exitAnimation);
            return animation.getDuration();
        } catch (Resources.NotFoundException e) {
            return 0;
        }
    }
}
