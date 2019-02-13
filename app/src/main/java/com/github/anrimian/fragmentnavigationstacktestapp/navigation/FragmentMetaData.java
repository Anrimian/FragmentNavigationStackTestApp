package com.github.anrimian.fragmentnavigationstacktestapp.navigation;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

class FragmentMetaData {

    private final String fragmentClassName;

    @Nullable
    private final Bundle arguments;

    static FragmentMetaData from(Fragment fragment) {
        return new FragmentMetaData(fragment.getClass().getCanonicalName(), fragment.getArguments());
    }

    private FragmentMetaData(String fragmentClassName, @Nullable Bundle arguments) {
        this.fragmentClassName = fragmentClassName;
        this.arguments = arguments;
    }

    String getFragmentClassName() {
        return fragmentClassName;
    }

    @Nullable
    Bundle getArguments() {
        return arguments;
    }
}
