package com.modules.wall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.modules.team.MemberStatisticsFragment;
import com.modules.team.PollListFragment;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetFragments;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.Wall_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 13-07-2017
 *         Last Modified on 15-12-2017
 */

public class CommentDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = CommentDialog.class.getSimpleName();
    private ArrayList<Comment_> commentArrayList;
    private int _id;
    FragmentActivity myContext;
    private Activity activity;
    private CommentListAdapter commentListAdapter;
    public callGetPollMethod callGetPollMethodListener;
    private AppCompatImageButton sendButton;
    private EditText commentBox;
    private TextView titleText;

    public interface callGetPollMethod{
        public void getPollFromCommentBackPressed();
    }


    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_dialog_layout, null);

        activity = getActivity();
        myContext = (FragmentActivity) activity;
        commentArrayList = new ArrayList<>();

        Preferences.initialize(activity.getApplicationContext());
        AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.comment_dialog_back);
        backButton.setOnClickListener(this);

        sendButton = (AppCompatImageButton) view.findViewById(R.id.comment_dialog_send);
        sendButton.setOnClickListener(this);

        titleText = (TextView) view.findViewById(R.id.comment_dialog_title);

        ListView listView = (ListView) view.findViewById(R.id.comment_dialog_list);
        commentListAdapter = new CommentListAdapter(activity.getApplicationContext(), commentArrayList);
        listView.setAdapter(commentListAdapter);

        commentBox = (EditText) view.findViewById(R.id.comment_dialog_box);

        Bundle data = getArguments();
        if (data != null && data.containsKey(General.ID)) {
            _id = data.getInt(General.ID);
            if (_id != 0) {
                getComments();
            } else {
                dismiss();
            }
        } else {
            dismiss();
        }

        return view;
    }

    // make network call to fetch comment list
    private void getComments() {
        commentArrayList.clear();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.LOAD_WALL_COMMENT);
        requestMap.put(General.LAST_DATE, "0");
        requestMap.put(General.FEED_ID, "" + _id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.WALL_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        try {
            String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
            if (response != null) {
                commentArrayList.addAll(Wall_.parseComment(response, activity.getApplicationContext(), TAG));
                if (commentArrayList.size() > 0) {
                    commentListAdapter.notifyDataSetChanged();
                    String commentString = activity.getApplicationContext().getResources().getString(R.string.comments) + " (" + commentArrayList.size() + ")";
                    titleText.setText(commentString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // make network call to post new comment
    private void postComment(String comment) {
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.POST_COMMENT);
        requestMap.put(General.COMMENT, comment);
        requestMap.put(General.FEED_ID, "" + _id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.WALL_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        try {
            String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
            if (response != null) {
                if (Error_.oauth(response, activity.getApplicationContext()) == 0) {
                    JsonArray jsonArray = GetJson_.getArray(response, General.COMMENT);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            status = object.get(General.STATUS).getAsInt();
                        } else {
                            status = 11;
                        }
                    }
                } else {
                    status = 13;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (status == 1) {
            commentBox.setText("");
            getComments();
        }
        sendButton.setEnabled(true);
        showResponses(status);
    }

    private void showResponses(int status) {
        String message;
        if (status == 1) {
            message = activity.getApplicationContext().getString(R.string.successful);
        } else if (status == 3) {
            status = 2;
            message = this.getResources().getString(R.string.action_failed);
        } else {
            status = 2;
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, sendButton, activity.getApplicationContext());
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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_dialog_back:
                //dismiss();
                if (getArguments().get(General.FROM).equals("Poll")){
                    Fragment fragment = new PollListFragment();
                    FragmentTransaction ft = myContext.getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                    ft.replace(R.id.app_bar_main_container, fragment, TAG);
                    ft.commit();
                    dismiss();
                }else {
                    Fragment fragment = GetFragments.get(1, null);
                    FragmentTransaction ft = myContext.getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                    ft.replace(R.id.app_bar_main_container, fragment, TAG);
                    ft.commit();
                    dismiss();
                }
                break;
            case R.id.comment_dialog_send:
                String comment = commentBox.getText().toString();
                if (comment.trim().length() < 2 || comment.trim().length() > 250) {
                    commentBox.setError(activity.getApplicationContext().getResources()
                            .getString(R.string.wall_comment_error));
                } else {
                    sendButton.setEnabled(false);
                    commentBox.setError(null);
                    postComment(comment);
                }
                break;
        }
    }
}
