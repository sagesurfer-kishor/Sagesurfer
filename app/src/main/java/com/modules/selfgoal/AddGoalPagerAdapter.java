package com.modules.selfgoal;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sagesurfer.constant.General;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 24/03/2018
 *         Last Modified on
 */

class AddGoalPagerAdapter extends FragmentStatePagerAdapter {

    AddGoalPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new AddGoalFragmentOne();
        Bundle bundle = new Bundle();
        bundle.putString(General.TITLE, "Screen: " + position);
        switch (position){
            case 0:
                fragment = new AddGoalFragmentOne();
                break;
            case 1:
                fragment = new AddGoalFragmentTwo();
                break;
            case 2:
                fragment = new AddGoalFragmentThree();
                break;
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
