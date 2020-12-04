package com.sagesurfer.snack;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.sagesurfer.collaborativecares.R;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * class to show snack bar for different views
 */

public class ShowSnack {

    public static void buttonWarning(Button button, String message, Context _context) {
        if (button != null) {
            Snackbar snackbar = Snackbar.make(button, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextAppearance(_context, R.style.CustomFontStylePrimary);
            textView.setTextColor(_context.getResources().getColor(R.color.warning_text_color));
            sbView.setBackgroundColor(ContextCompat.getColor(_context, R.color.snack_bar_background_color));
            snackbar.show();
        }
    }

    public static void viewWarning(View view, String message, Context _context) {
        if (view != null) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextAppearance(_context, R.style.CustomFontStylePrimary);
            textView.setTextColor(_context.getResources().getColor(R.color.warning_text_color));
            sbView.setBackgroundColor(ContextCompat.getColor(_context, R.color.snack_bar_background_color));
            snackbar.show();
        }
    }

    public static void viewWarning_One(View view, String message, Context _context) {
        if (view != null) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextAppearance(_context, R.style.CustomFontStylePrimary);
            textView.setMaxLines(3);
            textView.setTextColor(_context.getResources().getColor(R.color.warning_text_color));
            sbView.setBackgroundColor(ContextCompat.getColor(_context, R.color.snack_bar_background_color));
            snackbar.show();
        }
    }



    public static void viewSuccess(View view, String message, Context _context) {
        if (view != null) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextAppearance(_context, R.style.CustomFontStylePrimary);
            textView.setTextColor(_context.getResources().getColor(R.color.text_color_green));
            sbView.setBackgroundColor(ContextCompat.getColor(_context, R.color.list_selector));
            snackbar.show();
        }
    }

    public static void compatImageButtonSuccess(AppCompatImageButton imageButton, String message, Context _context) {
        if (imageButton != null) {
            Snackbar snackbar = Snackbar.make(imageButton, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextAppearance(_context, R.style.CustomFontStylePrimary);
            textView.setTextColor(_context.getResources().getColor(R.color.text_color_green));
            sbView.setBackgroundColor(ContextCompat.getColor(_context, R.color.snack_bar_background_color));
            snackbar.show();
        }
    }

    public static void compatImageButtonWarning(AppCompatImageButton imageButton, String message, Context _context) {
        if (imageButton != null) {
            Snackbar snackbar = Snackbar.make(imageButton, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextAppearance(_context, R.style.CustomFontStylePrimary);
            textView.setTextColor(_context.getResources().getColor(R.color.warning_text_color));
            sbView.setBackgroundColor(ContextCompat.getColor(_context, R.color.snack_bar_background_color));
            snackbar.show();
        }
    }

    public static void textViewWarning(TextView text_view, String message, Context _context) {
        if (text_view != null) {
            Snackbar snackbar = Snackbar.make(text_view, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextAppearance(_context, R.style.CustomFontStylePrimary);
            textView.setTextColor(_context.getResources().getColor(R.color.warning_text_color));
            sbView.setBackgroundColor(ContextCompat.getColor(_context, R.color.snack_bar_background_color));
            snackbar.show();
        }
    }
}
