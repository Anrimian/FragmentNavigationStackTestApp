package com.github.anrimian.fragmentnavigationstacktestapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class JugglerView extends FrameLayout {

    private FrameLayout firstView;
    private FrameLayout secondView;

    private int topViewId;

    public JugglerView(@NonNull Context context) {
        super(context);
    }

    public JugglerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        firstView = findViewById(R.id.first_view);
        secondView = findViewById(R.id.second_view);
        topViewId = R.id.second_view;
    }

    public int getTopViewId() {
        return topViewId;
    }

    public int prepareTopView() {
        View viewToTop = topViewId == R.id.first_view? secondView: firstView;

        ((FrameLayout) viewToTop).removeAllViews();
        removeView(viewToTop);
        addView(viewToTop);
        topViewId = viewToTop.getId();
        return topViewId;
    }

    public int prepareBottomView() {
        View viewToBottom = topViewId == R.id.first_view? firstView: secondView;

        ((FrameLayout) viewToBottom).removeAllViews();
        removeView(viewToBottom);
        addView(viewToBottom, 0);
        topViewId = topViewId == R.id.first_view? secondView.getId(): firstView.getId();
        return viewToBottom.getId();
    }
}