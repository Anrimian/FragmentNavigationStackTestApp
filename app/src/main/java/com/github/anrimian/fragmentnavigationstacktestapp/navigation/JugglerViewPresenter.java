package com.github.anrimian.fragmentnavigationstacktestapp.navigation;

import android.support.v4.view.ViewCompat;

class JugglerViewPresenter {

    private int firstViewId = ViewCompat.generateViewId();
    private int secondViewId = ViewCompat.generateViewId();

    private int topViewId = secondViewId;

    void initializeView(JugglerView view) {
        view.init(firstViewId, secondViewId, topViewId);
    }

    int getFirstViewId() {
        return firstViewId;
    }

    int getSecondViewId() {
        return secondViewId;
    }

    int getTopViewId() {
        return topViewId;
    }

    void onTopViewIdSelected(int topViewId) {
        this.topViewId = topViewId;
    }
}
