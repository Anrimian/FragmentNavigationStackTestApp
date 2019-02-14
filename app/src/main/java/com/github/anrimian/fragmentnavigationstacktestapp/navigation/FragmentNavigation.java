package com.github.anrimian.fragmentnavigationstacktestapp.navigation;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentNavigation {

    private static final String NAVIGATION_FRAGMENT_TAG = "navigation_fragment_tag";
    private static final String SCREENS = "screens";

    private final FragmentManagerProvider fragmentManagerProvider;
    private final LinkedList<FragmentMetaData> fragments = new LinkedList<>();
    private final List<FragmentStackListener> stackListeners = new LinkedList<>();

    private JugglerView jugglerView;

    private boolean isNavigationEnabled = true;

    private boolean checkOnEqualityOnReplace = false;

    @AnimRes private int enterAnimation = 0;
    @AnimRes private int exitAnimation = 0;
    @AnimRes private int rootEnterAnimation = 0;
    @AnimRes private int rootExitAnimation = 0;

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

    public void initialize(@NonNull JugglerView jugglerView, @Nullable Bundle savedState) {
        this.jugglerView = jugglerView;
        jugglerView.initialize(savedState);

        if (!fragments.isEmpty()) {//just orientation change
            hideBottomFragmentMenu();
            notifyFragmentMovedToTop(getFragmentOnTop());
            return;
        }
        if (savedState != null) {
            ArrayList<Bundle> bundleFragments = new ArrayList<>();
            fragments.addAll(toMetaScreens(bundleFragments));
        }
        restoreFragmentStack();
    }

    public void onSaveInstanceState(Bundle state) {
        jugglerView.saveInstanceState(state);
        state.putParcelableArrayList(SCREENS, getBundleScreens());
    }

    public void addNewFragment(Fragment fragment) {
        addNewFragment(fragment, enterAnimation);
    }

    //TODO create with exist stack feature
    //TODO save screens stack
    //TODO fragment changes class name on update? Same with arguments
    //TODO lock screen on enter animation

    public void addNewFragment(Fragment fragment,
                               @AnimRes int enterAnimation) {
        checkForInitialization();
        if (!isNavigationEnabled) {
            return;
        }
        isNavigationEnabled = false;
        fragments.add(new FragmentMetaData(fragment));
        int id = jugglerView.prepareTopView();
        fragmentManagerProvider.getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enterAnimation, 0)
                .replace(id, fragment)
                .runOnCommit(() -> {
                    isNavigationEnabled = true;
                    hideBottomFragmentMenu();
                    notifyStackListeners();
                    notifyFragmentMovedToTop(getFragmentOnTop());
                })
                .commit();
    }

    public void newRootFragment(Fragment fragment) {
        newRootFragment(fragment, checkOnEqualityOnReplace, rootExitAnimation);
    }

    public void newRootFragment(Fragment fragment, boolean checkForEquality) {
        newRootFragment(fragment, checkForEquality, rootExitAnimation);
    }

    public void newRootFragment(Fragment fragment,
                                boolean checkForEquality,
                                @AnimRes int exitAnimation) {
        newRootFragment(fragment, checkForEquality, exitAnimation, rootEnterAnimation);
    }

    public void newRootFragment(Fragment fragment, @AnimRes int exitAnimation) {
        newRootFragment(fragment, checkOnEqualityOnReplace, exitAnimation, rootEnterAnimation);
    }

    public void newRootFragment(Fragment fragment,
                                @AnimRes int exitAnimation,
                                @AnimRes int enterAnimation) {
        newRootFragment(fragment, checkOnEqualityOnReplace, exitAnimation, enterAnimation);
    }

    public void newRootFragment(Fragment newRootFragment,
                                boolean checkForEquality,
                                @AnimRes int exitAnimation,
                                @AnimRes int enterAnimation) {
        checkForInitialization();
        if (!isNavigationEnabled) {
            return;
        }
        Fragment oldRootFragment = getFragmentOnTop();
        if (checkForEquality && equalClass(oldRootFragment, newRootFragment)) {
            return;
        }

        isNavigationEnabled = false;
        Fragment oldBottomFragment = getFragmentOnBottom();
        fragments.clear();
        fragments.add(new FragmentMetaData(newRootFragment));
        int topViewId = jugglerView.getTopViewId();
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
                .runOnCommit(() -> {
                    isNavigationEnabled = true;
                    notifyStackListeners();
                    notifyFragmentMovedToTop(getFragmentOnTop());
                })
                .commit();
    }

    public boolean goBack() {
        return goBack(exitAnimation);
    }

    /**
     *
     * @return if back accepted or not
     */
    public boolean goBack(@AnimRes int exitAnimation) {
        checkForInitialization();
        if (fragments.size() <= 1) {
            return false;
        }
        if (!isNavigationEnabled) {
            return true;
        }
        Fragment fragmentOnTop = getFragmentOnTop();
        if (fragmentOnTop == null) {
            return false;
        }
        isNavigationEnabled = false;
        fragments.removeLast();
        fragmentManagerProvider.getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(0, exitAnimation)
                .remove(fragmentOnTop)
                .runOnCommit(() -> {
                    moveBottomFragmentToTop(exitAnimation);
                    notifyStackListeners();
                })
                .commit();
        return true;
    }

    public void clearRootFragment(@AnimRes int exitAnimation) {
        checkForInitialization();
        if (fragments.size() < 1) {
            return;
        }
        if (fragments.size() > 1) {
            throw new IllegalStateException("can not clear: fragment is not root");
        }
        Fragment fragmentOnTop = getFragmentOnTop();
        if (fragmentOnTop == null) {
            return;
        }
        fragments.removeLast();
        fragmentManagerProvider.getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(0, exitAnimation)
                .remove(fragmentOnTop)
                .runOnCommit(this::notifyStackListeners)
                .commit();
    }

    /**
     *
     * Don't forget to remove listener if you don't need it more
     *
     * @param listener to notify
     */
    public void addStackChangeListener(FragmentStackListener listener) {
        stackListeners.add(listener);
    }

    public void removeStackChangeListener(FragmentStackListener listener) {
        stackListeners.remove(listener);
    }

    public void clearStackChangeListeners() {
        stackListeners.clear();
    }

    public void checkForEqualityOnReplace(boolean checkOnEqualityOnReplace) {
        this.checkOnEqualityOnReplace = checkOnEqualityOnReplace;
    }

    public void setEnterAnimation(int enterAnimation) {
        this.enterAnimation = enterAnimation;
    }

    public void setExitAnimation(int exitAnimation) {
        this.exitAnimation = exitAnimation;
    }

    public void setRootEnterAnimation(int rootEnterAnimation) {
        this.rootEnterAnimation = rootEnterAnimation;
    }

    public void setRootExitAnimation(int rootExitAnimation) {
        this.rootExitAnimation = rootExitAnimation;
    }

    public int getScreensCount() {
        return fragments.size();
    }

    public int getStackScreensCount() {
        int count = fragments.size() - 1;
        return count < 0? 0: count;
    }

    public boolean hasScreens() {
        return !fragments.isEmpty();
    }

    @Nullable
    public Fragment getFragmentOnTop() {
        return fragmentManagerProvider.getFragmentManager()
                .findFragmentById(jugglerView.getTopViewId());
    }

    @Nullable
    public Fragment getFragmentOnBottom() {
        return fragmentManagerProvider.getFragmentManager()
                .findFragmentById(jugglerView.getBottomViewId());
    }

    private void notifyFragmentMovedToTop(Fragment fragment) {
        if (fragment instanceof FragmentLayerListener) {
            ((FragmentLayerListener) fragment).onFragmentMovedOnTop();
        }
    }

    private void notifyStackListeners() {
        for (FragmentStackListener listener: stackListeners) {
            listener.onStackChanged(getScreensCount());
        }
    }

    private void restoreFragmentStack() {
        checkForInitialization();
        if (!isNavigationEnabled) {
            return;
        }
        if (fragments.isEmpty()) {
            return;
        }
        isNavigationEnabled = false;
        FragmentMetaData topFragment = fragments.getLast();

        fragmentManagerProvider.getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(rootEnterAnimation, 0)
                .replace(jugglerView.getTopViewId(), createFragment(topFragment))
                .runOnCommit(() -> {
                    isNavigationEnabled = true;
                    notifyStackListeners();
                    notifyFragmentMovedToTop(getFragmentOnTop());

                    //..
                    if (fragments.size() > 1) {
                        FragmentMetaData bottomFragment = fragments.get(fragments.size() - 2);
                        fragmentManagerProvider.getFragmentManager()
                                .beginTransaction()
                                .replace(jugglerView.getBottomViewId(), createFragment(bottomFragment))
                                .runOnCommit(this::hideBottomFragmentMenu)
                                .commit();
                    }

                    //..
                })
                .commit();
    }

    private void moveBottomFragmentToTop(@AnimRes int exitAnimation) {
        Fragment fragment = requireFragmentAtBottom();
        fragment.setMenuVisibility(true);
        notifyFragmentMovedToTop(fragment);

        jugglerView.postDelayed(() -> {
            int id = jugglerView.prepareBottomView();
            if (fragments.size() > 1) {
                FragmentMetaData metaData = fragments.get(fragments.size() - 2);
                Fragment bottomFragment = createFragment(metaData);
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

    private Fragment requireFragmentAtBottom() {
        Fragment fragment = getFragmentOnBottom();
        if (fragment == null) {
            throw new NullPointerException("required fragment from bottom is null");
        }
        return fragment;
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


    private Fragment createFragment(FragmentMetaData metaData) {
        return fragmentManagerProvider.getFragmentManager()
                .getFragmentFactory()
                .instantiate(jugglerView.getContext().getClassLoader(),
                        metaData.getFragmentClassName(),
                        metaData.getArguments()
                );
    }

    private ArrayList<Bundle> getBundleScreens() {
        ArrayList<Bundle> screens = new ArrayList<>(fragments.size());
        for (FragmentMetaData metaData: fragments) {
            screens.add(metaData.toBundle());
        }
        return screens;
    }

    private LinkedList<FragmentMetaData> toMetaScreens(List<Bundle> bundles) {
        LinkedList<FragmentMetaData> fragments = new LinkedList<>();
        for (Bundle bundle: bundles) {
            fragments.add(new FragmentMetaData(bundle));
        }
        return fragments;
    }

    private boolean equalClass(@Nullable Object first, @NonNull Object second) {
        return (first != null && first.getClass().equals(second.getClass()));
    }
}
