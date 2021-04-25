package com.modules.sos;

import com.sagesurfer.collaborativecares.R;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 17-07-2017
 *         Last Modified on 14-12-2017
 */

class GetDrawable {

    static int circle(int status) {
        if (status == 0) {
            return R.drawable.ternary_gray_circle;
        } else if (status == 2) {
            return R.drawable.sos_attending_circle;
        } else if (status == 3) {
            return R.drawable.sos_attended_circle;
        } else {
            return R.drawable.sos_delivered_circle;
        }
    }

    static int line(int status) {
        if (status == 0) {
            return R.drawable.gray_dotted_line;
        } else {
            return R.drawable.red_dotted_line;
        }
    }

}
