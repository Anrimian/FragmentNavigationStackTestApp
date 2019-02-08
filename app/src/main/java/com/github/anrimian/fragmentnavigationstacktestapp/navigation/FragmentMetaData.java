package com.github.anrimian.fragmentnavigationstacktestapp.navigation;

import android.os.Bundle;
import androidx.annotation.Nullable;

public class FragmentMetaData {

    private final String fragmentClassName;

    @Nullable
    private final Bundle arguments;

    public FragmentMetaData(String fragmentClassName, @Nullable Bundle arguments) {
        this.fragmentClassName = fragmentClassName;
        this.arguments = arguments;
    }

    public String getFragmentClassName() {
        return fragmentClassName;
    }

    @Nullable
    public Bundle getArguments() {
        return arguments;
    }
}
