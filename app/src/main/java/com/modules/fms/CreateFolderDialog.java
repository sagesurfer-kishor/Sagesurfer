package com.modules.fms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.validator.FileSharing;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author girish M (girish@sagesurfer.com)
 *         Created on 13/09/2017.
 *         Last Modified on 13/09/2017.
 **/

public class CreateFolderDialog extends DialogFragment implements View.OnClickListener,
        FolderListAdapter.OnItemClick {

    private static final String TAG = CreateFolderDialog.class.getSimpleName();
    private static final String ROOT = "root/";
    private long parent_folder = -1;
    private int group_id;
    private ArrayList<AllFolder_> allFolderArrayList;

    private EditText inputBox;
    private TextView directoryText;

    private FolderListAdapter folderListAdapter;
    private FinalFolderList finalFolderList;
    private RelativeLayout relativeLayoutToolbar;

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_folder_dialog_layout, null);

        allFolderArrayList = new ArrayList<>();

        Bundle b = getArguments();
        if (b != null) {
            allFolderArrayList = (ArrayList<AllFolder_>) b.getSerializable(Actions_.FOLDER_LIST);
            group_id = b.getInt(General.GROUP_ID);
        } else {
            dismiss();
        }

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        AppCompatImageView backButton = (AppCompatImageView) view.findViewById(R.id.create_folder_back);
        backButton.setOnClickListener(this);

        ListView listView = (ListView) view.findViewById(R.id.create_folder_list_view);
        folderListAdapter = new FolderListAdapter(getActivity(), allFolderArrayList, this);
        listView.setAdapter(folderListAdapter);

        directoryText = (TextView) view.findViewById(R.id.create_folder_directory_name);
        directoryText.setText(ROOT);

        inputBox = (EditText) view.findViewById(R.id.create_folder_name_box);

        TextView doneText = (TextView) view.findViewById(R.id.create_folder_done);
        doneText.setOnClickListener(this);

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
            case R.id.create_folder_back:
                dismiss();
                break;
            case R.id.create_folder_done:
                String name = inputBox.getText().toString();
                if (validator(name)) {
                    create(name);
                }
                break;
        }
    }

    @Override
    public void onItemClick(long folder_id, String folder_name, long main_folder_id, String main_folder_name) {
        if (folder_id != 0) {
            ShowToast.toast("Action Not Allowed", getActivity().getApplicationContext());
        } else {
            if (main_folder_id == -1) {
                directoryText.setText(ROOT);
                parent_folder = -1;
            } else {
                String dir = ROOT + main_folder_name + "/";
                directoryText.setText(dir);
                parent_folder = main_folder_id;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        finalFolderList = (FinalFolderList) activity;
    }

    interface FinalFolderList {
        void FinalFolder(ArrayList<AllFolder_> folderArrayList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalFolderList.FinalFolder(allFolderArrayList);
    }

    // validator method to check folder name
    private boolean validator(String folder_name) {
        if (folder_name == null || folder_name.length() <= 0) {
            inputBox.setError("Invalid Folder Name");
            return false;
        }
        if (folder_name.length() > 30) {
            inputBox.setError("Max 30 char name allowed");
            return false;
        }

        if (!FileSharing.isValidFolderName(folder_name)) {
            inputBox.setError("Folder name should be alpha numeric\nOnly \"_\" allowed");
            return false;
        }
        return true;
    }

    //update folder list if new folder get added
    private void updateFolderList(long id, String name) {
        inputBox.setText("");
        ShowToast.successful(getActivity().getApplicationContext().getResources()
                .getString(R.string.successful), getActivity().getApplicationContext());
        if (parent_folder == -1) {
            AllFolder_ allFolder_ = new AllFolder_();
            allFolder_.setId(id);
            allFolder_.setStatus(1);
            allFolder_.setName(name);
            allFolder_.setSub_folder(new ArrayList<Folder_>());
            allFolderArrayList.add(allFolder_);
        } else {
            for (int i = 0; i < allFolderArrayList.size(); i++) {
                if (parent_folder == allFolderArrayList.get(i).getId()) {
                    Folder_ folder_ = new Folder_();
                    folder_.setId((int) id);
                    folder_.setName(name);
                    folder_.setStatus(1);
                    ArrayList<Folder_> subList = allFolderArrayList.get(i).getSub_folder();
                    subList.add(folder_);
                    allFolderArrayList.get(i).setSub_folder(subList);
                }
            }
        }
        folderListAdapter.notifyDataSetChanged();
    }

    // make call to create new folder
    private void create(String _name) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.CREATE_FOLDER);
        requestMap.put(General.GROUP_ID, "" + group_id);
        requestMap.put(General.NAME, _name);
        requestMap.put(General.ID, "" + parent_folder);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity().getApplicationContext(), getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity().getApplicationContext(), getActivity());
                if (response != null) {
                    if (Error_.oauth(response, getActivity().getApplicationContext()) == 13) {
                        ShowToast.toast(getActivity().getApplicationContext().getResources()
                                .getString(R.string.authentication_failed), getActivity().getApplicationContext());
                        return;
                    }
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject != null) {
                        if (jsonObject.has(General.STATUS)) {
                            int status = jsonObject.get(General.STATUS).getAsInt();
                            if (status == 1) {
                                long id = jsonObject.get(General.ID).getAsLong();
                                String name = jsonObject.get(General.NAME).getAsString();
                                updateFolderList(id, name);
                            } else {
                                ShowToast.toast(jsonObject.get("error").getAsString(),
                                        getActivity().getApplicationContext());
                            }
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ShowToast.toast(getActivity().getApplicationContext().getResources().getString(R.string.internal_error_occurred), getActivity().getApplicationContext());
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        relativeLayoutToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
