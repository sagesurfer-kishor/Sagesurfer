package com.sagesurfer.library;

import com.sagesurfer.collaborativecares.R;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 12-07-2017
 *         Last Modified on 15-12-2017
 */

public class GetThumbnails {

    // get colourful background thumbnails related to different file types/extensions
    public static int attachmentList(String fileName) {
        if (CheckFileType.imageFile(fileName)) {
            return R.drawable.vi_image_thumbnail;
        }
        if (CheckFileType.pdfFile(fileName)) {
            return R.drawable.vi_pdf_thumbnail;
        }
        if (CheckFileType.xlsFile(fileName)) {
            return R.drawable.vi_excel_thumbnail;
        }
        if (CheckFileType.docFile(fileName)) {
            return R.drawable.vi_document_thumbnail;
        }
        if (CheckFileType.csvFile(fileName)) {
            return R.drawable.vi_csv_thumbnail;
        }
        if (CheckFileType.pptFile(fileName)) {
            return R.drawable.vi_ppt_thumbnail;
        }
        if (CheckFileType.textFile(fileName)) {
            return R.drawable.vi_text_thumbnail;
        }
        if (CheckFileType.videoFile(fileName)) {
            return R.drawable.vi_video_thumbnail;
        }
        return R.drawable.vi_pdf_thumbnail;
    }


    // get transparent background thumbnails related to different file types/extensions
    public static int fileSharing(String fileName) {
        if (CheckFileType.imageFile(fileName)) {
            return R.drawable.vi_image_file;
        }
        if (CheckFileType.pdfFile(fileName)) {
            return R.drawable.vi_pdf_file;
        }
        if (CheckFileType.xlsFile(fileName)) {
            return R.drawable.vi_xls_file;
        }
        if (CheckFileType.docFile(fileName)) {
            return R.drawable.vi_doc_file;
        }
        if (CheckFileType.csvFile(fileName)) {
            return R.drawable.vi_file_csv;
        }
        if (CheckFileType.pptFile(fileName)) {
            return R.drawable.vi_ppt_file;
        }
        if (CheckFileType.textFile(fileName)) {
            return R.drawable.vi_text_file;
        }
        return R.drawable.vi_other_file;
    }

    // get male/female user icon based on url if necessary
    public static int userIcon(String url) {
        if(url != null) {
            if (url.contains("static/avatar/women.jpg") ||
                    url.contains("static/avatar/user_native_female.png")) {
                return R.drawable.ic_user_female;
            }
        }
        return R.drawable.ic_user_male;
    }

    //get background thumbnails related mood
    public static int moodIcons(int moodValue) {
        /*if (moodValue == 0) {
            return R.drawable.vi_mood_happy;
        } else if (moodValue == 1) {
            return R.drawable.vi_mood_laugh;
        } else if (moodValue == 2) {
            return R.drawable.vi_mood_cry;
        } else if (moodValue == 3) {
            return R.drawable.vi_mood_sad;
        } else if (moodValue == 4) {
            return R.drawable.vi_mood_worried;
        } else if (moodValue == 5) {
            return R.drawable.vi_mood_angry;
        } else {
            return R.drawable.vi_mood_happy;
        }*/

        return R.drawable.vi_mood_cry;
    }
}
