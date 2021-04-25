package com.modules.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane <girish@sagesurfer.com>
 *         Created on 04-03-2016
 *         Last Modified on 13-12-2017
 */

class NoteListAdapter extends ArrayAdapter<Note_> {

    private final ArrayList<Note_> noteList;
    private final static String TAG = NoteListAdapter.class.getSimpleName();

    private final Delete delete;
    private final Activity activity;

    NoteListAdapter(Activity activity, ArrayList<Note_> noteList, Delete delete) {
        super(activity, 0, noteList);
        this.noteList = noteList;
        this.activity = activity;
        this.delete = delete;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        Note_ note_;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.note_list_item, parent, false);
            holder = new ViewHolder();

            holder.messageText = (TextView) view.findViewById(R.id.note_list_item_message);
            holder.dateText = (TextView) view.findViewById(R.id.note_list_item_date);
            holder.warningText = (TextView) view.findViewById(R.id.note_list_item_warning);
            holder.cancelButton = (ImageView) view.findViewById(R.id.note_list_item_cancel);

            holder.mainLayout = (LinearLayout) view.findViewById(R.id.note_list_item_main);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.cancelButton.setTag(position);

        note_ = noteList.get(position);
        if (note_.getStatus() != 1) {
            holder.mainLayout.setVisibility(View.GONE);
            holder.warningText.setVisibility(View.VISIBLE);
            holder.warningText.setText(activity.getApplicationContext().getResources().getString(R.string.no_record_found));
            return view;
        }
        holder.mainLayout.setVisibility(View.VISIBLE);
        holder.warningText.setVisibility(View.GONE);

        holder.messageText.setText(note_.getMessage());
        holder.dateText.setText(GetTime.month_DdYyyy(note_.getCreated_date()));

        holder.cancelButton.setOnClickListener(onClick);
        return view;
    }

    // handle click for note list view item
    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Note_ note_ = noteList.get(position);
            if (note_.getStatus() == 1) {
                confirmationDialog(position);
            }
        }
    };

    //make network call to delete note from list
    private void deleteNote(int position) {
        int result = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_STICKY);
        requestMap.put(General.ID, "" + noteList.get(position).getId());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CARE_GIVER_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    if (Error_.oauth(response, activity.getApplicationContext()) == 13) {
                        result = 13;
                    } else {
                        JsonArray jsonArray = GetJson_.getArray(response, Actions_.DELETE_STICKY);
                        assert jsonArray != null;
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            result = object.get(General.STATUS).getAsInt();
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (result == 1) {
            delete.delete(result);
            noteList.remove(position);
            if (noteList.size() <= 0) {
                Note_ note_ = new Note_();
                note_.setStatus(2);
                noteList.add(note_);
            }
            notifyDataSetChanged();
        }
    }

    // confirmation dialog to ask delete confirmation to user
    private void confirmationDialog(final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        title.setText(activity.getApplicationContext().getResources().getString(R.string.action_confirmation));
        subTitle.setText(activity.getApplicationContext().getResources().getString(R.string.note_delete_confirmation));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(position);
                dialog.dismiss();
            }
        });

        AppCompatImageButton cancelButton = (AppCompatImageButton) dialog.findViewById(R.id.delete_confirmation_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    interface Delete {
        void delete(int status);
    }

    static class ViewHolder {
        TextView messageText, dateText, warningText;
        ImageView cancelButton;
        LinearLayout mainLayout;
    }
}