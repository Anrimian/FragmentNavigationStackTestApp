package com.github.anrimian.fragmentnavigationstacktestapp.navigation;

import android.content.res.Resources;
import android.support.annotation.AnimRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.LinkedList;

public class FragmentNavigation {

    private static final String NAVIGATION_FRAGMENT_TAG = "navigation_fragment_tag";

    private final FragmentManagerProvider fragmentManagerProvider;
    private final LinkedList<FragmentCreator> fragments = new LinkedList<>();

    private final JugglerViewPresenter jugglerViewPresenter = new JugglerViewPresenter();
    private JugglerView jugglerView;

    private boolean isNavigationEnabled = true;

    public static FragmentNavigation from(FragmentManager fm) {
        NavigationFragment container = (NavigationFragment) fm.findFragmentByTag(NAVIGATION_FRAGMENT_TAG);
        if (container == null) {
            container = new NavigationFragment();
            fm.beginTransaction()
                    .add(container, NAVIGATION_FRAGMENT_TAG)
                    .commit();
        }
        return container.getFragmentNavigation();
    }

    FragmentNavigation(FragmentManagerProvider fragmentManagerProvider) {
        this.fragmentManagerProvider = fragmentManagerProvider;
    }

    public void initialize(JugglerView jugglerView) {
        this.jugglerView = jugglerView;
        jugglerView.setPresenter(jugglerViewPresenter);
        jugglerViewPresenter.initializeView(jugglerView);

        hideBottomFragmentMenu();
    }

    public void addNewFragment(FragmentCreator fragmentCreator) {
        addNewFragment(fragmentCreator, 0);
    }

    //TODO create with exist stack feature

    public void addNewFragment(FragmentCreator fragmentCreator,
                               @AnimRes int enterAnimation) {
        checkForInitialization();
        if (!isNavigationEnabled) {
            return;
        }
        isNavigationEnabled = false;
        fragments.add(fragmentCreator);
        int id = jugglerView.prepareTopView();
        Fragment topFragment = fragmentCreator.createFragment();
        fragmentManagerProvider.getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enterAnimation, 0)
                .replace(id, topFragment)
                .runOnCommit(() -> {
                    isNavigationEnabled = true;
                    hideBottomFragmentMenu();
                })
                .commit();
    }

    public void newRootFragment(FragmentCreator fragmentCreator) {
        newRootFragment(fragmentCreator, 0);
    }

    public void newRootFragment(FragmentCreator fragmentCreator, @AnimRes int exitAnimation) {
        newRootFragment(fragmentCreator, exitAnimation, 0);
    }

    public void newRootFragment(FragmentCreator fragmentCreator,
                                @AnimRes int exitAnimation,
                                @AnimRes int enterAnimation) {
        checkForInitialization();
        if (!isNavigationEnabled) {
            return;
        }
        isNavigationEnabled = false;
        Fragment oldBottomFragment = getFragmentOnBottom();
        Fragment newRootFragment = fragmentCreator.createFragment();
        fragments.clear();
        fragments.add(fragmentCreator);
        int topViewId = jugglerViewPresenter.getTopViewId();
        FragmentTransaction transaction = fragmentManagerProvider.getFragmentManager()
                .beginTransaction();
        if (oldBottomFragment != null) {
            // I don't see it, but guess:
            // while oldTopFragment disappears, bottom fragment can be little visible.
            // How to check it?
            transaction.remove(oldBottomFragment);
        }
        transaction.setCustomAnimations(enterAnimation, exitAnimation)
                .replace(topViewId, newRootFragment)
                .runOnCommit(() -> isNavigationEnabled = true)
                .commit();
    }

    public boolean goBack() {
        return goBack(0);
    }

    public boolean goBack(@AnimRes int exitAnimation) {
        checkForInitialization();
        if (fragments.size() <= 1) {
            return false;
        }
        if (!isNavigationEnabled) {
            return true;
        }
        isNavigationEnabled = false;
        fragments.removeLast();
        fragmentManagerProvider.getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(0, exitAnimation)
                .remove(getFragmentOnTop())
                .runOnCommit(() -> replaceBottomFragment(exitAnimation))
                .commit();
        return true;
    }

    public int getScreensCount() {
        return fragments.size();
    }

    private Fragment getFragmentOnTop() {
        return fragmentManagerProvider.getFragmentManager()
                .findFragmentById(jugglerViewPresenter.getTopViewId());
    }

    private Fragment getFragmentOnBottom() {
        return fragmentManagerProvider.getFragmentManager()
                .findFragmentById(jugglerViewPresenter.getBottomViewId());
    }

    private void replaceBottomFragment(@AnimRes int exitAnimation) {
        getFragmentOnBottom().setMenuVisibility(true);
        jugglerView.postDelayed(() -> {
            int id = jugglerView.prepareBottomView();
            if (fragments.size() > 1) {
                Fragment bottomFragment = fragments.get(fragments.size() - 2).createFragment();//find better solution later
                bottomFragment.setMenuVisibility(false);
                fragmentManagerProvider.getFragmentManager()
                        .beginTransaction()
                        .replace(id, bottomFragment)
                        .runOnCommit(() -> isNavigationEnabled = true)
                        .commit();
            } else {
                isNavigationEnabled = true;
            }
        }, getAnimationDuration(exitAnimation));
    }

    private void hideBottomFragmentMenu() {
        Fragment fragment = getFragmentOnBottom();
        if (fragment != null) {
            fragment.setMenuVisibility(false);
        }
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

    private void checkForInitialization() {
        if (jugglerView == null) {
            throw new IllegalStateException("FragmentNavigator must be initialized first");
        }
    }
}
