package com.lexaloris.yotabrowser.utils;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.lexaloris.yotabrowser.MainActivity;
import com.lexaloris.yotabrowser.R;
import com.lexaloris.yotabrowser.fragments.RequestFragment;
import com.lexaloris.yotabrowser.fragments.ResponseFragment;

public class MyFragmentManager {

    public final int noFragment = 0;
    public final int requestFragmentPosition = 1;
    public final int responseFragmentPosition = 2;

    Toolbar toolbar;
    private MainActivity activity;
    int lastFragment = noFragment;

    public MyFragmentManager(MainActivity activity) {
        this.activity = activity;
        setToolBar();
    }

    public void setToolBar() {
        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
    }

    public void addRequestFragment() {
        toolbar.setTitle(R.string.request_page_title);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        FragmentTransaction fTran = activity.getSupportFragmentManager().beginTransaction();
        RequestFragment requestFragment = RequestFragment.newInstance();
        fTran.replace(R.id.mainLayout, requestFragment);
        fTran.commit();
        lastFragment = requestFragmentPosition;
    }

    public void addResponseFragment() {
        toolbar.setTitle(R.string.response_page_title);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        FragmentTransaction fTran = activity.getSupportFragmentManager().beginTransaction();
        ResponseFragment responseFragment = ResponseFragment.newInstance();
        fTran.replace(R.id.mainLayout, responseFragment);
        fTran.commit();
        lastFragment = responseFragmentPosition;
    }

    public void addFragment() {
        switch (lastFragment) {
            case 0:
                addRequestFragment();
                break;
            case 1:
                addRequestFragment();
                break;
            case 2:
                addResponseFragment();
                break;
            default:
                addRequestFragment();
        }
    }

    public int getLastFragment() {
        return lastFragment;
    }

    public void addFragment(int lastFragment) {
        this.lastFragment = lastFragment;
        addFragment();
    }

    public void addFragmentReceived(int isReceived) {
        if (isReceived == 1) {
            addResponseFragment();
        } else {
            addRequestFragment();
        }
    }
}
