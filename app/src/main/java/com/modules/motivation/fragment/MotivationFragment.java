package com.modules.motivation.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.modules.calendar.CustomCalendar;
import com.modules.mood.BottomNavigationViewHelper;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;

/**
 * Created by kailash on 2/12/2019.
 */

public class MotivationFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private ColorStateList colorStateListText, colorStateListIcon;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.hidePlusIcon(true);
        }
    }

    @SuppressLint({"InflateParams"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_motivation, null);
        activity = getActivity();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.mood));
        CustomCalendar.getInitialCalendarInfo();

        setBottomNavigationTextColor();
        setBottomNavigationIconColor();

        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemTextColor(colorStateListText);
        bottomNavigationView.setItemIconTintList(colorStateListIcon);

        fragment = new MotivationListFragment();
        ((MotivationListFragment) fragment).motivationFragment=this;
        ((MotivationListFragment) fragment).mView=bottomNavigationView;
        replaceFragment(fragment);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.motivation));
        mainActivityInterface.setToolbarBackgroundColor();

        mainActivityInterface.setMoodToolbar(52);
    }

    public void setBottomNavigationTextColor() {
        int[][] state = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };
        int[] color = new int[]{
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.sos_grey)
        };
        colorStateListText = new ColorStateList(state, color);
    }

    public void setBottomNavigationIconColor() {
        int[][] state2 = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };
        int[] color2 = new int[]{
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.sos_grey)
        };
        colorStateListIcon = new ColorStateList(state2, color2);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mainActivityInterface.setMoodToolbar(52);
        switch (item.getItemId()) {
            case R.id.menu_motivation:
                fragment = new MotivationListFragment();
                ((MotivationListFragment) fragment).motivationFragment=this;
                ((MotivationListFragment) fragment).mView=bottomNavigationView;
                break;
            case R.id.menu_motivation_library:
                fragment = new MotivationLibraryFragment();
                break;
            case R.id.menu_wellness_toolkit:
                fragment = new WellnessToolkitFragment();
                break;
            default:
                return false;
        }

        updateNavigationBarState(item.getItemId());
        replaceFragment(fragment);
        return true;
    }

    public void updateNavigationBarState(int actionId) {
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
            boolean isChecked = menuItem.getItemId() == actionId;
            menuItem.setChecked(isChecked);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment oldFragment = fragmentManager.findFragmentByTag(Actions_.SOS);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        oldFragment = fragmentManager.findFragmentByTag(Actions_.SOS);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.replace(R.id.mood_container, fragment, Actions_.SOS);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.commit();
    }
}
