package com.sagesurfer.library;

import androidx.appcompat.widget.AppCompatImageView;
import android.widget.TextView;

import com.sagesurfer.icons.GetHomeMenuIcon;
import com.sagesurfer.models.HomeMenu_;

import java.util.List;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 08-07-2017
 *         Last Modified on 08-07-2017
 */

public class SetHomeMenuPanel {

    // set home menu panel icon and text as per list provided
    public static void setHomeMenu(AppCompatImageView[] imageButtonArray,
                                   TextView[] textViewArray, List<HomeMenu_> homeMenuList) {
        for (int i = 0; i < homeMenuList.size(); i++) {
            if (homeMenuList.get(i).getId() != 0) {
                imageButtonArray[i].setImageResource(GetHomeMenuIcon.get(homeMenuList.get(i).getId()));
                textViewArray[i].setText(homeMenuList.get(i).getMenu());
            }
        }
    }
}
