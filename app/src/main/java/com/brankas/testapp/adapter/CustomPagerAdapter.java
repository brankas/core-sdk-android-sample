package com.brankas.testapp.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.brankas.testapp.fragment.BaseFragment;
import com.brankas.testapp.fragment.ClientDetailsFragment;
import com.brankas.testapp.fragment.SourceAccountFragment;
import com.brankas.testapp.fragment.TransferDetailsFragment;
import com.brankas.testapp.listener.ScreenListener;

import java.util.HashMap;

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

public class CustomPagerAdapter extends FragmentStatePagerAdapter {

    private int numPages = 3;
    private ScreenListener screenListener;

    private HashMap<Integer, BaseFragment> fragments = new HashMap<>();

    public CustomPagerAdapter(@NonNull FragmentManager fragmentManager,
                              ScreenListener screenListener) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.screenListener = screenListener;
    }

    @Override
    public int getCount() {
        return numPages;
    }

    @NonNull
    @Override
    public BaseFragment getItem(int position) {
        if(!fragments.containsKey(position)) {
            switch (position) {
                case 0:
                    fragments.put(position, SourceAccountFragment.newInstance(screenListener));
                    break;
                case 1:
                    fragments.put(position, TransferDetailsFragment.newInstance(screenListener));
                    break;
                case 2:
                    fragments.put(position, ClientDetailsFragment.newInstance(screenListener));
                    break;
            }
        }
        return fragments.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
        if(fragments.containsKey(position))
            fragments.remove(position);
    }

    public HashMap<Integer, BaseFragment> getFragments() {
        return fragments;
    }
}