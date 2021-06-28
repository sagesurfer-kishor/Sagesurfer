package com.sagesurfer.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.sagesurfer.directory.DirectoryList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class ImageDownloader {
    public static void getImage(String url, Context context) {
        new DownloadProfile(url, context).execute();
    }

    @SuppressLint("StaticFieldLeak")
    private static class DownloadProfile extends AsyncTask<Void, Void, Void> {
        String url = null;

        DownloadProfile(String url, Context context) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL wallpaperURL;
            File root = new File(DirectoryList.DIR_MAIN);
            try {
                if (!root.exists()) {
                    root.mkdir();
                }
                wallpaperURL = new URL(url);
                InputStream inputStream = new BufferedInputStream(wallpaperURL.openStream(), 10240);
                File cacheFile = new File(new File(DirectoryList.DIR_MAIN), "my_pic.jpg");
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                byte buffer[] = new byte[1024];
                int dataSize;
                // int loadedSize = 0;
                while ((dataSize = inputStream.read(buffer)) != -1) {
                    // loadedSize += dataSize;
                    outputStream.write(buffer, 0, dataSize);
                }
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}