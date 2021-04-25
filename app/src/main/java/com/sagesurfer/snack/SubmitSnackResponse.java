package com.sagesurfer.snack;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.View;
import android.widget.Button;

import com.sagesurfer.constant.Warnings;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * It is used to show respective responses based on status code
 */

public class SubmitSnackResponse {
    //1: Success
    //2: Failed
    //11:Internal Error
    //12:Network Error
    //13:Authentication Error
    public static void showSnack(int status, String message, Button button, Context _context) {
        switch (status) {
            case 1:
                ShowToast.successful(message, _context);
                break;
            case 2:
                ShowSnack.buttonWarning(button, message, _context);
                break;
            case 11:
                ShowSnack.buttonWarning(button, Warnings.INTERNAL_ERROR_OCCURRED, _context);
                break;
            case 12:
                ShowSnack.buttonWarning(button, Warnings.NETWORK_ERROR_OCCURRED, _context);
                break;
            case 13:
                ShowSnack.buttonWarning(button, Warnings.AUTHENTICATION_FAILED, _context);
                break;
            case 20:
                ShowSnack.buttonWarning(button, message, _context);
                break;
            case 21:
                ShowSnack.buttonWarning(button, message, _context);
                break;
        }
    }

    public static void showSnack(int status, String message, AppCompatImageButton imageButton, Context _context) {
        switch (status) {
            case 1:
                ShowToast.successful(message, _context);
                break;
            case 2:
                ShowSnack.compatImageButtonWarning(imageButton, message, _context);
                break;
            case 11:
                ShowSnack.compatImageButtonWarning(imageButton, Warnings.INTERNAL_ERROR_OCCURRED, _context);
                break;
            case 12:
                ShowSnack.compatImageButtonWarning(imageButton, Warnings.NETWORK_ERROR_OCCURRED, _context);
                break;
            case 13:
                ShowSnack.compatImageButtonWarning(imageButton, Warnings.AUTHENTICATION_FAILED, _context);
                break;
        }
    }

    public static void showSnack(int status, String message, View view, Context _context) {
        switch (status) {
            case 1:
                ShowToast.successful(message, _context);
                break;
            case 2:
                ShowSnack.viewWarning(view, message, _context);
                break;
            case 11:
                ShowSnack.viewWarning(view, Warnings.INTERNAL_ERROR_OCCURRED, _context);
                break;
            case 12:
                ShowSnack.viewWarning(view, Warnings.NETWORK_ERROR_OCCURRED, _context);
                break;
            case 13:
                ShowSnack.viewWarning(view, Warnings.AUTHENTICATION_FAILED, _context);
                break;
        }
    }

    public static void showSnack(int status, String message, Context _context) {
        switch (status) {
            case 1:
                ShowToast.successful(message, _context);
                break;
            case 2:
                ShowToast.failed(message, _context);
                break;
            case 11:
                ShowToast.internalErrorOccurred(_context);
                break;
            case 12:
                ShowToast.networkError(_context);
                break;
            case 13:
                ShowToast.authenticationFailed(_context);
                break;
        }
    }
}
