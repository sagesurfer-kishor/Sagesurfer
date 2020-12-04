package com.modules.support;

import java.util.ArrayList;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/3/2018
 *         Last Modified on 4/3/2018
 */

class Question {

    private String question;
    private ArrayList<Answer> answers;

    public String getName() {
        return question;
    }

    public void setName(String question) {
        this.question = question;
    }

    public ArrayList<Answer> getItems() {
        return answers;
    }

    public void setItems(ArrayList<Answer> answers) {
        this.answers = answers;
    }

}
