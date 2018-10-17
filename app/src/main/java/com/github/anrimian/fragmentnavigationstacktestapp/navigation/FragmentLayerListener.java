package com.github.anrimian.fragmentnavigationstacktestapp.navigation;

public interface FragmentLayerListener {

    /**
     * Be careful, it can call before onCreateView().
     * Can be useful for update come common ui, like title in toolbar
     */
    void onFragmentMovedOnTop();
}
