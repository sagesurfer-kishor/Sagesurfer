package com.sagesurfer.captcha;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.sagesurfer.collaborativecares.R;

import java.util.List;
import java.util.Random;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * This class generates and match captcha code
 */

abstract class Captcha {

    Bitmap image;
    String answer = "";
    private int width;
    private int height;
    int x = 0;
    int y = 0;
    static List usedColors;

    protected abstract Bitmap image();

    @SuppressWarnings("unchecked")
    static int color() {
        Random r = new Random();
        int number;
        do {
            number = r.nextInt(9);
        } while (usedColors.contains(number));
        usedColors.add(number);
        switch (number) {
            case 0:
                return R.color.colorPrimary;
            case 1:
                return R.color.colorPrimaryDark;
            case 2:
                return R.color.sos_chat;
            case 3:
                return R.color.sos_other;
            case 4:
                return R.color.sos_attending;
            case 5:
                return R.color.sos_not_attending;
            case 6:
                return R.color.color_tif;
            case 7:
                return R.color.task_assigned;
            case 8:
                return R.color.task_in_progress;
            case 9:
                return Color.WHITE;
            default:
                return Color.WHITE;
        }
    }

    int getWidth() {
        return this.width;
    }

    void setWidth(int width) {
        if (width > 0 && width < 10000) {
            this.width = width;
        } else {
            this.width = 300;
        }
    }

    int getHeight() {
        return this.height;
    }

    void setHeight(int height) {
        if (height > 0 && height < 10000) {
            this.height = height;
        } else {
            this.height = 100;
        }
    }

    public Bitmap getImage() {
        return this.image;
    }

    public boolean checkAnswer(String ans) {
        return ans.equals(this.answer);
    }
}