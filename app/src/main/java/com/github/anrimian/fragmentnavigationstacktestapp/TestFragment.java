package com.github.anrimian.fragmentnavigationstacktestapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Random;

public class TestFragment extends Fragment {

    private static final String ID = "id";

    private Button button;
    private int backgroundColor;

    public static TestFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(ID, id);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TestFragment() {
        Random rnd = new Random();
        backgroundColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.button);
        button.setText(String.valueOf(getArguments().getInt(ID)));
        button.setOnClickListener(v -> onButtonClicked());

        View container = view.findViewById(R.id.container);
        container.setBackgroundColor(backgroundColor);
    }

    private void onButtonClicked() {
        ((MainActivity) getActivity()).addNewFragment();
    }
}
