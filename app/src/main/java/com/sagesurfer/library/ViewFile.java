package com.sagesurfer.library;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.sagesurfer.snack.ShowToast;

import java.io.File;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 19/02/2018
 */

/*
* Open file with respective file viewer from given desired location
*/
public class ViewFile {

	public static void viewFile(Activity activity, String file_path) {
		if (isFileExist(file_path)) {

			Uri uri_img = Uri.fromFile(new File(file_path));
			String extension = android.webkit.MimeTypeMap
					.getFileExtensionFromUrl(uri_img.toString());
			String mime_type = android.webkit.MimeTypeMap.getSingleton()
					.getMimeTypeFromExtension(extension);

			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(uri_img, mime_type);
            try {
			activity.startActivity(i);
            } catch (ActivityNotFoundException e) {
                ShowToast.toast("File viewer not present", activity.getApplicationContext());
                Toast.makeText(activity.getApplicationContext(), "No handler for this type of file.",
                        Toast.LENGTH_LONG).show();
            }
		} else {
			ShowToast.fileNotFound(activity.getApplicationContext());
		}
	}

	private static boolean isFileExist(String file_path) {
        return new File(file_path).exists();
    }
}
