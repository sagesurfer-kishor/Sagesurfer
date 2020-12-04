package com.sagesurfer.download;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Broadcast;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.ConvertImage;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.snack.ShowToast;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;

/**
 * @author Kailash Karankal
 * Created on 4/12/2018
 * Last Modified on 4/12/2018
 */


public class DownloadFile {
    private Context context;
    private static NotificationManager mNotifyManager;
    private static NotificationCompat.Builder build;

    private static final int DOWNLOAD_THREAD_POOL_SIZE = 10;
    private static final String TAG = DownloadFile.class.getSimpleName();

    public void download(long file_id, String url, String file_name, String directory, Activity activity) {
        this.context = activity.getApplicationContext();

        String channelId = "global";
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        build = new NotificationCompat.Builder(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            build.setContentTitle(file_name)
                    .setChannelId(channelId)
                    .setOnlyAlertOnce(true)
                    .setContentText("Downloading....")
                    .setSmallIcon(R.drawable.vi_download_white)
                    .setLargeIcon(ConvertImage.getBitmapFromVectorDrawable(
                            activity.getApplicationContext(), R.drawable.ic_download_notify_primary)
                    );
        } else {
            build.setContentTitle(file_name)
                    .setContentText("Downloading....")
                    .setSmallIcon(R.drawable.vi_download_white)
                    .setLargeIcon(ConvertImage.getBitmapFromVectorDrawable(
                            activity.getApplicationContext(), R.drawable.ic_download_notify_primary)
                    );
        }

        FileOperations.checkFolderPresence(directory);
        Download download = new Download(file_id, url, file_name, directory);
        downloadRequest(download);
    }


    private void downloadRequest(Download download) {
        RetryPolicy retryPolicy = new DefaultRetryPolicy();
        ThinDownloadManager downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
        DownloadRequest downloadRequest = new DownloadRequest(Uri.parse(download.getUrl()))
                .setRetryPolicy(retryPolicy)
                .setDestinationURI(Uri.parse(download.getDirectory() + download.getName()))
                .setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadContext(download)
                .setDeleteDestinationFileOnFailure(true)
                .setStatusListener(downloadStatusListenerV1);
        downloadManager.add(downloadRequest);
    }

    private DownloadStatusListenerV1 downloadStatusListenerV1 = new DownloadStatusListenerV1() {
        @Override
        public void onDownloadComplete(DownloadRequest downloadRequest) {
            ShowToast.toast(context.getResources().getString(R.string.download_completed), context);
            sendBroadcast(downloadRequest);
        }

        @Override
        public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
            ShowToast.toast(context.getResources().getString(R.string.download_failed), context);
        }

        @Override
        public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
            Download download = (Download) downloadRequest.getDownloadContext();
            progress(download, progress);
        }
    };

    private void progress(Download download, int progress) {
        if (progress != 100) {
            build.setProgress(100, progress, false);
            mNotifyManager.notify((int) download.getId(), build.build());
        } else {
            build.setContentText("Download completed");
            build.setProgress(100, progress, false);
            build.setContentIntent(getOpenFileIntent(download.getDirectory(), download.getName()));
            build.setDefaults(Notification.DEFAULT_ALL);
            build.setAutoCancel(true);
            mNotifyManager.notify((int) download.getId(), build.build());
        }
    }

    private PendingIntent getOpenFileIntent(String path, String file_name) {
        File file = new File(path + file_name);
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, CheckFileType.getMimeType(file_name));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private void sendBroadcast(DownloadRequest downloadRequest) {
        Download download = (Download) downloadRequest.getDownloadContext();
        Intent intent = new Intent();
        intent.setAction(Broadcast.DOWNLOAD_BROADCAST);
        intent.putExtra(General.URL, download.getUrl());
        context.sendBroadcast(intent);
    }

    class Download {
        private long id;
        private String name;
        private String url;
        private String directory;

        Download(long id, String url, String name, String directory) {
            this.id = id;
            this.url = url;
            this.name = name;
            this.directory = directory;
        }

        long getId() {
            return id;
        }

        String getName() {
            return name;
        }

        String getUrl() {
            return url;
        }

        String getDirectory() {
            return directory;
        }
    }
}
