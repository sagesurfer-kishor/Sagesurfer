package com.modules.fms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.library.GetColor;

import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 13-09-2017
 *         Last Modified on 13-12-2017
 */

public class FolderListDialog extends DialogFragment implements View.OnClickListener,
        FolderListAdapter.OnItemClick {

    private static final String TAG = FolderListDialog.class.getSimpleName();

    private SelectedFolder selectedFolder;
    private RelativeLayout relativeLayoutToolbar;

    @Override
    public void onItemClick(long folder_id, String folder_name, long main_folder_id, String main_folder_name) {
        if (main_folder_id == -1) {
            selectedFolder.selectedFolder(main_folder_id, "root/", true);
        } else {
            if (folder_id != 0) {
                selectedFolder.selectedFolder(folder_id, "root/"
                        + main_folder_name + "/" + folder_name, true);
            } else {
                selectedFolder.selectedFolder(main_folder_id, "root/"
                        + main_folder_name + "/", true);
            }
        }
        dismiss();
    }

    interface SelectedFolder {
        void selectedFolder(long id, String directory, boolean isSelected);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        selectedFolder = (SelectedFolder) activity;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        ArrayList<AllFolder_> allFolderArrayList = new ArrayList<>();

        String title = getActivity().getApplicationContext().getResources().getString(R.string.folder);
        Bundle b = getArguments();
        if (b != null) {
            allFolderArrayList = (ArrayList<AllFolder_>) b.getSerializable(Actions_.FOLDER_LIST);
        } else {
            dismiss();
        }

        final AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.single_choice_team_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView titleText = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);
        titleText.setText(title);

        LinearLayout searchLayout = (LinearLayout) view.findViewById(R.id.team_list_search_layout);
        searchLayout.setVisibility(View.GONE);

        ListView listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);
        FolderListAdapter folderListAdapter = new FolderListAdapter(getActivity(), allFolderArrayList, this);
        listView.setAdapter(folderListAdapter);
        ///listView.setAdapter(participantsListAdapter);

        return view;
    }

    @NonNull
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
            case R.id.single_choice_team_dialog_back:
                selectedFolder.selectedFolder(0, "", false);
                dismiss();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        relativeLayoutToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
