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

    public int addViewToTop() {
        View viewToTop = firstView;
        if (topViewId == R.id.first_view) {
            viewToTop = secondView;
        }
        removeView(viewToTop);
        addView(viewToTop);
        return viewToTop.getId();
    }

    public int addViewToBottom() {
        View viewToBottom = secondView;
        if (topViewId == R.id.second_view) {
            viewToBottom = firstView;
        }
        removeView(viewToBottom);
        addView(viewToBottom, 0);
        return viewToBottom.getId();
    }
}
