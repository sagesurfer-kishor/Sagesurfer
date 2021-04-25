package com.modules.selfcare;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/12/2018
 *         Last Modified on 4/12/2018
 */

/*
    Convert content type from string to respective integer
    1=image
    2=video
    3=Text Articles
    4=Blogs
    5=Podcasts
    6=Courses
    7=Intervention Tools
    8=Webinar
*/

class SelfCareContentType_ {
    static int nameToType(String type) {
        if (type.equalsIgnoreCase("Images")) {
            return 1;
        }
        if (type.equalsIgnoreCase("Videos")) {
            return 2;
        }
        if (type.equalsIgnoreCase("Text Articles")) {
            return 3;
        }
        if (type.equalsIgnoreCase("Blogs") || type.equalsIgnoreCase("Blog")) {
            return 4;
        }
        if (type.equalsIgnoreCase("Podcasts")) {
            return 5;
        }
        if (type.equalsIgnoreCase("Courses")) {
            return 6;
        }
        if (type.equalsIgnoreCase("Intervention Tools")) {
            return 7;
        }
        if (type.equalsIgnoreCase("Webinar")) {
            return 8;
        }

        return 0;
    }
}
