package com.github.anrimian.fragmentnavigationstacktestapp.navigation;

import android.content.res.Resources;
import android.support.annotation.AnimRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    }

    public void addNewFragment(FragmentCreator fragmentCreator) {
        addNewFragment(fragmentCreator, 0);
    }

    //TODO replace current fragment feature
    //TODO create with exist stack feature
    //TODO hide fragment menu feature

    public void addNewFragment(FragmentCreator fragmentCreator,
                               @AnimRes int enterAnimation) {
        checkForInitialization();
        if (!isNavigationEnabled) {
            return;
        }
        isNavigationEnabled = false;
        fragments.add(fragmentCreator);
        int id = jugglerView.prepareTopView();
        Fragment topFragment = fragments.getLast().createFragment();
        fragmentManagerProvider.getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enterAnimation, 0)
                .replace(id, topFragment)
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

    private void replaceBottomFragment(@AnimRes int exitAnimation) {
        jugglerView.postDelayed(() -> {
            int id = jugglerView.prepareBottomView();
            if (fragments.size() > 1) {
                Fragment bottomFragment = fragments.get(fragments.size() - 2).createFragment();//find better solution later
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
