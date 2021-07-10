package com.modules.selfgoal;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sagesurfer.constant.General;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 24/03/2018
 * Last Modified on
 */

class AddGoalPagerAdapter extends FragmentStatePagerAdapter {

    Fragment fragment1 = new AddGoalFragmentOne();
    Fragment fragment2 = new AddGoalFragmentTwo();
    Fragment fragment3 = new AddGoalFragmentThree();

    Fragment fragment;

    private List<Fragment> fragments;


    AddGoalPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);

        fragments=new ArrayList<>();

        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
    }


//    @Override
//    public Fragment getItem(int position) {
//        //Fragment fragment = new AddGoalFragmentOne();
//        Bundle bundle = new Bundle();
//        bundle.putString(General.TITLE, "Screen: " + position);
//        switch (position) {
//            case 0:
//                //fragment = new AddGoalFragmentOne();
//                fragment = fragment1;
//                break;
//            case 1:
////                fragment = new AddGoalFragmentTwo();
//                fragment = fragment2;
//                break;
//            case 2:
////                fragment = new AddGoalFragmentThree();
//                fragment = fragment3;
//                break;
//        }
//        fragment.setArguments(bundle);
//        return fragment;
//    }


    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getItemPosition(@NonNull @NotNull Object object) {
        //return super.getItemPosition(object);
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

//    @Override
//    public int getCount() {
//        return 3;
//    }

}
