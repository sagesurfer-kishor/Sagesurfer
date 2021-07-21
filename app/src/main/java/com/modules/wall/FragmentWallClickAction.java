package com.modules.wall;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.utils.AppLog;

/*
@author rahul maske created on 19-07-2021
*/
public class FragmentWallClickAction extends BottomSheetDialogFragment implements View.OnClickListener{
    TextView tv_update_post,tv_delete_post,tv_cancel;
    IOptionClickedListner iOptionClickedListner;
    Feed_ feed_;
    String position;
    public static Fragment fragment;


    private static final String TAG = "FragmentWallClickAction";
    public static FragmentWallClickAction newInstance(androidx.fragment.app.Fragment fm) {
        fragment=fm;
        return new FragmentWallClickAction();
    }
    Activity activity;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_wall_click_action, null);
        tv_update_post=view.findViewById(R.id.tv_update_post);
        tv_delete_post=view.findViewById(R.id.tv_delete_post);
        tv_cancel=view.findViewById(R.id.tv_cancel);
        tv_delete_post.setOnClickListener(this);
        tv_update_post.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        Bundle bundle=getArguments();
        feed_=bundle.getParcelable("Feed");
        position=bundle.getString("Position");
        activity=getActivity();
        iOptionClickedListner=new FeedListFragment();
        return view;
    }

    @Override
    public void onClick(View view) {
        iOptionClickedListner=(IOptionClickedListner) fragment;
        switch (view.getId()){
            case R.id.tv_update_post:

                AppLog.i(TAG,"tv_update_post");
                iOptionClickedListner.onEditClicked(feed_,activity,position);
                this.dismiss();
                break;

            case R.id.tv_delete_post:
                iOptionClickedListner.onDeleteClicked(feed_,getActivity(),position);
                this.dismiss();
                AppLog.i(TAG,"tv_delete_post");
                break;

            case R.id.tv_cancel:
                iOptionClickedListner.onCancelClicked();
                this.dismiss();
                AppLog.i(TAG,"cancel");

                break;
        }
    }
}
