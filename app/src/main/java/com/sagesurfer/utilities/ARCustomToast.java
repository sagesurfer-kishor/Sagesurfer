package com.sagesurfer.utilities;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sagesurfer.collaborativecares.R;

public class ARCustomToast {
    static int width, height, y_axis;

    public static void showToast(Context context, String message, int timeinterval) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        y_axis = (height / 4) + (width / 6) - 9;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_screen, null);
        TextView text = (TextView) layout.findViewById(R.id.custom_toast_message);
        text.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, y_axis);
        toast.setDuration(timeinterval);
        toast.setView(layout);
        toast.show();
    }
}  



