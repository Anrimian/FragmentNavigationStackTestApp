package com.github.anrimian.fragmentnavigationstacktestapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.anrimian.fragmentnavigationstacktestapp.navigation.FragmentNavigation;
import com.github.anrimian.fragmentnavigationstacktestapp.navigation.FragmentVisibilityListener;

import java.util.Random;

public class TestFragment extends Fragment implements FragmentVisibilityListener {

    private static final String ID = "id";
    private static final String COLOR = "color";

    private Button button;

    public static TestFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(ID, id);
        Random rnd = new Random();
        int backgroundColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        args.putInt(COLOR, backgroundColor);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        container.setBackgroundColor(getArguments().getInt(COLOR));
    }

    @Override
    public void onFragmentVisible() {
        getActivity().setTitle("title: " + getArguments().getInt(ID));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getArguments().getInt(ID) % 2 == 0) {
            inflater.inflate(R.menu.test_fragment_menu, menu);
        } else {
            inflater.inflate(R.menu.test_fragment_menu_two, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_test: {
                Random rnd = new Random();
                int backgroundColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                button.setBackgroundColor(backgroundColor);
                return true;
            }
            case R.id.menu_test_root: {
                FragmentNavigation.from(requireFragmentManager())
                        .newRootFragment(() -> TestFragment.newInstance(0));
            }
        }
        return false;
    }

    private void onButtonClicked() {
        FragmentNavigation navigation = FragmentNavigation.from(requireFragmentManager());
        int fragmentsCount = navigation.getScreensCount();
        navigation.addNewFragment(() -> TestFragment.newInstance(fragmentsCount));
    }
}
