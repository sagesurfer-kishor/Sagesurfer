package com.modules.selfgoal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 24/03/2018
 *         Last Modified on
 */

public class AddDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = AddDialogFragment.class.getSimpleName();

    @BindView(R.id.imageview_fragmentaddgoal_cancel)
    ImageView imageViewFragmentAddGoalCancel;
    @BindView(R.id.recycleview_fragmentaddgoal)
    RecyclerView recycleViewFragmentAddGoal;

    private ArrayList<HashMap<String, String>> imageList;

    private Activity activity;
    private Unbinder unbinder;

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_goal_dialog, null);
        unbinder = ButterKnife.bind(this, view);

        activity = getActivity();

        Bundle b = getArguments();
        if (b != null && b.containsKey(General.IMAGES)) {
            imageList = (ArrayList<HashMap<String, String>>) b.getSerializable(General.IMAGES);
        } else {
            dismiss();
        }

        recycleViewFragmentAddGoal.callOnClick();
        imageViewFragmentAddGoalCancel.setOnClickListener(this);

        initViews();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initViews() {
        recycleViewFragmentAddGoal.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity.getApplicationContext(), 2);
        recycleViewFragmentAddGoal.setLayoutManager(layoutManager);

        AddGoalImageGalleryAdapter adapter = new AddGoalImageGalleryAdapter(activity, imageList, this);
        recycleViewFragmentAddGoal.setAdapter(adapter);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        assert dialog.getWindow() != null;
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d.getWindow() != null) {
            int width = ViewGroup.LayoutParams.FILL_PARENT;
            int height = ViewGroup.LayoutParams.FILL_PARENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setStyle(Window.FEATURE_NO_TITLE, R.style.MY_DIALOG);
        d.setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        setRetainInstance(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_fragmentaddgoal_cancel:
                dismiss();
                break;
        }
    }
}
