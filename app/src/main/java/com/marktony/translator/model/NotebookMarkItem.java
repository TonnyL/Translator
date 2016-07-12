package com.marktony.translator.model;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class NotebookMarkItem {

    // 原文
    private String input = null;
    // 译文
    private String output = null;

    public NotebookMarkItem(String input,String output){
        this.input = input;
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }
}
