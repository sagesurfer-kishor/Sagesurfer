package com.modules.fms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.download.DownloadFile;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.tasks.PerformReadTask;
import com.sagesurfer.validator.FileSharing;
import com.storage.database.constants.TableList_;
import com.storage.database.operations.DatabaseDeleteRecord_;
import com.storage.database.operations.DatabaseUpdate_;
import com.storage.preferences.Preferences;

import java.util.List;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 02-08-2017
 * Last Modified on 13-12-2017
 */

class FileSharingListAdapter extends ArrayAdapter<FileSharing_> {
    private static final String TAG = FileSharingListAdapter.class.getSimpleName();

    private PopupWindow popupWindow;

    private final List<FileSharing_> fileList;
    private final int group_id;
    private final long folder_id;
    private final String directory;

    private final Activity activity;

    FileSharingListAdapter(Activity activity, List<FileSharing_> fileList, int group_id, String directory, long folder_id) {
        super(activity, 0, fileList);
        this.fileList = fileList;
        this.group_id = group_id;
        this.folder_id = folder_id;
        this.directory = directory;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public FileSharing_ getItem(int position) {
        if (fileList != null && fileList.size() > 0) {
            return fileList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return fileList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.file_sharing_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.file_list_item_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.file_list_item_file_time);
            viewHolder.fileText = (TextView) view.findViewById(R.id.file_list_item_file_name);
            viewHolder.sizeText = (TextView) view.findViewById(R.id.file_list_item_file_size);
            viewHolder.sectionText = (TextView) view.findViewById(R.id.file_list_item_section_name);
            viewHolder.iconImage = (ImageView) view.findViewById(R.id.file_list_item_image);
            viewHolder.fileIcon = (AppCompatImageView) view.findViewById(R.id.file_list_item_image_icon);

            viewHolder.menuButton = (AppCompatImageButton) view.findViewById(R.id.file_list_item_menu);

            viewHolder.fileLayout = (RelativeLayout) view.findViewById(R.id.file_list_item_file_layout);
            viewHolder.sectionLayout = (LinearLayout) view.findViewById(R.id.file_list_item_section_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (fileList.get(position).getStatus() == 1) {
            viewHolder.menuButton.setTag(position);
            viewHolder.fileLayout.setTag(position);
            if (fileList.get(position).getIsSection()) {
                viewHolder.sectionLayout.setVisibility(View.VISIBLE);
                viewHolder.fileLayout.setVisibility(View.GONE);
                viewHolder.sectionText.setText(fileList.get(position).getName());
            } else {
                viewHolder.sectionLayout.setVisibility(View.GONE);
                viewHolder.fileLayout.setVisibility(View.VISIBLE);
                if (fileList.get(position).isFile()) {
                    viewHolder.menuButton.setVisibility(View.VISIBLE);
                    viewHolder.fileText.setText(ChangeCase.toTitleCase(fileList.get(position).getFullName()));
                    viewHolder.nameText.setText(fileList.get(position).getRealName());
                    viewHolder.sizeText.setText(FileOperations.bytes2String(fileList.get(position).getSize()));
                    viewHolder.dateText.setText(GetTime.wallTime(fileList.get(position).getDate()));
                    viewHolder.iconImage.setImageResource(R.drawable.primary_circle);
                    viewHolder.iconImage.setColorFilter(activity.getApplicationContext().getResources().getColor(GetColor.getFileIconBackgroundColor(fileList.get(position).getRealName())));
                    viewHolder.fileIcon.setImageResource(GetThumbnails.fileSharing(fileList.get(position).getRealName()));
                    viewHolder.menuButton.setOnClickListener(onClick);

                    applyReadStatus(viewHolder, fileList.get(position));
                } else {
                    viewHolder.nameText.setText(fileList.get(position).getName());
                    viewHolder.fileText.setText(getDisplayCount(fileList.get(position).getTotalFiles(), true));
                    viewHolder.dateText.setText(getDisplayCount(fileList.get(position).getTotalFolders(), false));
                    viewHolder.fileIcon.setImageResource(R.drawable.vi_folder_white);
                    viewHolder.iconImage.setImageResource(R.drawable.primary_circle);
                    viewHolder.sizeText.setVisibility(View.GONE);
                    if (Preferences.get(General.USER_ID).equalsIgnoreCase(Preferences.get(General.OWNER_ID))) {
                        viewHolder.menuButton.setVisibility(View.VISIBLE);
                        viewHolder.menuButton.setOnClickListener(onClick);
                    } else {
                        viewHolder.menuButton.setVisibility(View.GONE);
                    }
                }
            }
        }
        viewHolder.fileLayout.setOnClickListener(onClick);
        return view;
    }

    // Apply read/unread status to UI items
    private void applyReadStatus(ViewHolder holder, FileSharing_ event_) {
        if (event_.getIsRead() == 1) {
            holder.nameText.setTypeface(null, Typeface.NORMAL);
            holder.nameText.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.text_color_read));

        } else {
            holder.nameText.setTypeface(null, Typeface.BOLD);
            holder.nameText.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.text_color_primary));
        }
    }

    // get counter for file/folder with respective post-fix
    private String getDisplayCount(int count, boolean isFile) {
        String display;
        if (count <= 1) {
            if (isFile) {
                display = count + " File";
            } else {
                display = count + " Folder";
            }
        } else {
            if (isFile) {
                display = GetCounters.convertCounter(count) + " Files";
            } else {
                display = GetCounters.convertCounter(count) + " Folders";
            }
        }
        return display;
    }

    // handle on click
    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.file_list_item_menu:
                    showMenuPopup(position, v);
                    break;
                case R.id.file_list_item_file_layout:
                    if (fileList.get(position).getStatus() == 1 && !fileList.get(position).isFile()) {
                        Intent listIntent = new Intent(activity.getApplicationContext(), FileFolderListActivity.class);
                        listIntent.putExtra(General.ID, fileList.get(position).getId());
                        listIntent.putExtra(General.NAME, fileList.get(position).getName());
                        activity.startActivity(listIntent);
                        activity.overridePendingTransition(0, 0);
                    }

                    if (fileList.get(position).getStatus() == 1 && fileList.get(position).isFile()) {
                        updateFMSReadRecord(fileList.get(position).getId());
                        if (fileList.get(position).getPermission() != 1) {
                            initiatePopupWindow(v, position);
                        }
                    }
                    break;
            }
        }
    };

    //open file/folder menu popup window
    private void showMenuPopup(final int position, final View view) {
        final FileSharing_ fileSharing_ = fileList.get(position);
        final PopupMenu popup = new PopupMenu(activity, view);
        if (fileSharing_.isFile()) {
            popup.getMenuInflater().inflate(getMenu(fileSharing_.getPermission(),
                    "" + fileSharing_.getUserId(), fileSharing_.getCheckIn()), popup.getMenu());
        } else if (!fileSharing_.isFile()) {
            popup.getMenuInflater().inflate(R.menu.folder_menu, popup.getMenu());
        }

        MenuItem spam = popup.getMenu().findItem(R.id.file_menu_spam);

        if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            if(spam!=null) {
                spam.setVisible(false);
            }

        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.file_menu_open:
                        updateFMSReadRecord(fileSharing_.getId());

                        openView(fileSharing_.getId());
                        break;
                    case R.id.file_menu_details:
                        updateFMSReadRecord(fileSharing_.getId());

                        initiatePopupWindow(view, position);
                        break;
                    case R.id.file_menu_download:
                        popup.dismiss();
                        updateFMSReadRecord(fileSharing_.getId());

                        getDownload(fileSharing_.getId(), fileSharing_.getRealName());
                        break;
                    case R.id.file_menu_edit:
                        updateFMSReadRecord(fileSharing_.getId());

                        Intent editIntent = new Intent(activity.getApplicationContext(), UploadFileActivity.class);
                        editIntent.putExtra(General.ID, fileList.get(position));
                        editIntent.putExtra(General.GROUP_ID, group_id);
                        editIntent.putExtra(General.DIRECTORY, directory);
                        activity.startActivity(editIntent);
                        activity.overridePendingTransition(0, 0);
                        break;
                    case R.id.file_menu_delete:
                        confirmationDialog(position, 1, view);
                        break;
                    case R.id.file_menu_spam:
                        updateFMSReadRecord(fileSharing_.getId());

                        confirmationDialog(position, 2, view);
                        break;
                    case R.id.folder_edit:
                        editFolderDialog(fileSharing_, position);
                        break;
                    case R.id.folder_delete:
                        confirmationDialog(position, 3, view);
                        break;
                }
                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }

    // validator method for folder name
    private boolean validator(String folder_name, EditText inputBox) {
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

    //open rename folder dialog
    private void editFolderDialog(final FileSharing_ fileSharing_, final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        subTitle.setVisibility(View.GONE);
        final EditText inputBox = (EditText) dialog.findViewById(R.id.delete_confirmation_input_box);
        inputBox.setVisibility(View.VISIBLE);
        inputBox.setText(fileSharing_.getName());
        title.setText(activity.getApplicationContext().getResources().getString(R.string.rename_folder));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validator(inputBox.getText().toString().trim(), inputBox)) {
                    int result = FileSharingOperations.editFolder(inputBox.getText().toString().trim(),
                            "" + fileSharing_.getId(), "" + folder_id, "" + group_id, activity);
                    if (result == 1) {
                        ShowToast.successful(activity.getApplicationContext().getResources()
                                .getString(R.string.successful), activity.getApplicationContext());
                        fileSharing_.setName(inputBox.getText().toString().trim());
                        fileList.remove(position);
                        fileList.add(position, fileSharing_);
                        notifyDataSetChanged();
                    } else if (result == 3) {
                        ShowToast.successful("Folder name already present in same directory", activity.getApplicationContext());
                    } else {
                        ShowToast.internalErrorOccurred(activity.getApplicationContext());
                    }
                    dialog.dismiss();
                }
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

    // get file menu based on file access permissions
    private int getMenu(int permission, String owner_id, int check_in) {
        if (Preferences.get(General.USER_ID).equalsIgnoreCase(owner_id)) {
            if (check_in == 1) {
                return R.menu.file_menu_owner_check_out;
            }
            return R.menu.file_menu_owner;
        }
        if (permission == 1) {
            return R.menu.file_menu_view;
        }
        if (permission == 2) {
            return R.menu.file_menu_read;
        }
        if (permission == 3) {
            if (check_in == 1) {
                return R.menu.file_menu_admin_check_out;
            }
            return R.menu.file_menu_modify;
        }
        if (permission == 4) {
            if (check_in == 1) {
                return R.menu.file_menu_admin_check_out;
            }
            return R.menu.file_menu_admin;
        }
        return 0;
    }

    //open file in file viewer
    @SuppressLint("CommitTransaction")
    private void openView(int _id) {
        DialogFragment dialogFrag = new FileViewDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(General.ID, _id);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(activity.getFragmentManager().beginTransaction(), Actions_.VIEW);
    }

    //open file details pop up with file details
    @SuppressLint("InflateParams")
    private void initiatePopupWindow(View v, final int position) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        try {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.file_details_pop_up_dialog, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            AppCompatImageView icon = (AppCompatImageView) customView.findViewById(R.id.file_details_pop_up_icon);
            RelativeLayout background = (RelativeLayout) customView.findViewById(R.id.file_details_pop_up_icon_background);
            AppCompatImageButton closeButton = (AppCompatImageButton) customView.findViewById(R.id.file_details_pop_up_cancel);

            TextView fileName = (TextView) customView.findViewById(R.id.file_details_pop_up_file_name);
            TextView nameText = (TextView) customView.findViewById(R.id.file_details_pop_up_name);
            TextView dateText = (TextView) customView.findViewById(R.id.file_details_pop_up_time);
            TextView sizeText = (TextView) customView.findViewById(R.id.file_details_pop_up_file_size);
            TextView commentText = (TextView) customView.findViewById(R.id.file_details_pop_up_comment);
            TextView statusText = (TextView) customView.findViewById(R.id.file_details_pop_up_file_status);
            TextView downloadButton = (TextView) customView.findViewById(R.id.file_details_pop_up_download);

            fileName.setText(fileList.get(position).getRealName());
            nameText.setText(fileList.get(position).getFullName());
            dateText.setText(GetTime.wallTime(fileList.get(position).getDate()));
            sizeText.setText(FileOperations.bytes2String(fileList.get(position).getSize()));
            commentText.setText(fileList.get(position).getComment());

            if (fileList.get(position).getCheckIn() == 1) {
                statusText.setText(activity.getApplicationContext().getResources().getString(R.string.closed));
            } else {
                statusText.setText(activity.getApplicationContext().getResources().getString(R.string.open));
            }
            if (fileList.get(position).getPermission() == 1 || fileList.get(position).getPermission() == 2) {
                downloadButton.setVisibility(View.GONE);
            } else {
                downloadButton.setVisibility(View.VISIBLE);
            }

            background.setBackgroundColor(activity.getApplicationContext().getResources().getColor(GetColor.getFileIconBackgroundColor(fileList.get(position).getRealName())));
            icon.setImageResource(GetThumbnails.fileSharing(fileList.get(position).getRealName()));

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDownload(fileList.get(position).getId(), fileList.get(position).getRealName());
                    popupWindow.dismiss();
                }
            });
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            customView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFMSReadRecord(int file_id) {
        // Make network call to read entry
        int status = PerformReadTask.readAlert("" + file_id, General.FMS_GRP, TAG, activity.getApplicationContext(), activity);
        if (status == 1) {
            // Update record for read/unread
            DatabaseUpdate_ databaseUpdate_ = new DatabaseUpdate_(activity.getApplicationContext());
            databaseUpdate_.updateRead(General.IS_READ, TableList_.TABLE_FMS, "1", "" + file_id);

            notifyDataSetChanged();
        }
    }

    // Get file download url and send it to global downloader for downloading
    private void getDownload(int id, String real_name) {
        String url = FileSharingOperations.getDownload("" + id, activity.getApplicationContext(), activity);
        if (url.trim().length() > 0) {
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.download(id, url, real_name, DirectoryList.DIR_SHARED_FILES, activity);
        }
    }

    //confirmation dialog for multiple file operations
    private void confirmationDialog(final int position, final int type, final View view) {
        //type => 1: delete file; 2: spam file; 3: delete folder
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);

        if (type == 1) {
            title.setText(activity.getApplicationContext().getResources().getString(R.string.delete_confirmation));
            subTitle.setText(activity.getApplicationContext().getResources().getString(R.string.do_wan_to_delete_file));
        }
        if (type == 2) {
            title.setText(activity.getApplicationContext().getResources().getString(R.string.spam_confirmation));
            subTitle.setText(activity.getApplicationContext().getResources().getString(R.string.do_wan_to_spam_file));
        }
        if (type == 3) {
            title.setText(activity.getApplicationContext().getResources().getString(R.string.delete_confirmation));
            subTitle.setText(activity.getApplicationContext().getResources().getString(R.string.do_wan_to_delete_folder));
        }

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = 12;
                if (type == 1) {
                    DatabaseDeleteRecord_ databaseDeleteRecord_ = new DatabaseDeleteRecord_(activity.getApplicationContext());
                    status = FileSharingOperations.delete(fileList.get(position).getId(), activity.getApplicationContext(), view, activity);
                    if (status == 1) {
                        databaseDeleteRecord_.deleteRecord(TableList_.TABLE_FMS, "" + fileList.get(position).getId(), General.ID);
                    }
                }

                if (type == 2) {
                    status = FileSharingOperations.markSpam(fileList.get(position).getId(), activity.getApplicationContext(), view, activity);
                }

                if (type == 3) {
                    status = FileSharingOperations.deleteFolder(fileList.get(position).getId(), activity.getApplicationContext(), view, activity);
                }
                if (status == 1) {
                    ShowSnack.viewSuccess(view, activity.getApplicationContext().getResources()
                            .getString(R.string.successful), activity.getApplicationContext());
                    fileList.remove(position);
                    notifyDataSetChanged();
                } else if (status == 2) {
                    ShowSnack.viewWarning(view, activity.getApplicationContext().getResources()
                            .getString(R.string.action_failed), activity.getApplicationContext());
                } else {
                    ShowSnack.viewWarning(view, activity.getApplicationContext().getResources()
                            .getString(R.string.network_error_occurred), activity.getApplicationContext());
                }
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

    private class ViewHolder {
        TextView nameText, dateText, fileText, sizeText, sectionText;
        ImageView iconImage;
        AppCompatImageView fileIcon;
        AppCompatImageButton menuButton;
        RelativeLayout fileLayout;
        LinearLayout sectionLayout;
    }
}
