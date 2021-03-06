package com.github.anrimian.fragmentnavigationstacktestapp.navigation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NavigationFragment extends Fragment {

    private FragmentNavigation fragmentNavigation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    FragmentNavigation getFragmentNavigation() {
        if (fragmentNavigation == null) {
            fragmentNavigation = new FragmentNavigation(this::getFragmentManager);
        }
        return fragmentNavigation;
    }
}
