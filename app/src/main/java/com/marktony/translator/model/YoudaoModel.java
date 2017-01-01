package com.marktony.translator.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by lizhaotailang on 2017/1/1.
 */

public class YoudaoModel {

    private int errorCode;
    private String query;
    private ArrayList<String> translation;
    private Basic basic; // 基本词典
    private ArrayList<WebResult> web; // 网络释义

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<String> getTranslation() {
        return translation;
    }

    public void setTranslation(ArrayList<String> translation) {
        this.translation = translation;
    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public ArrayList<WebResult> getWeb() {
        return web;
    }

    public void setWeb(ArrayList<WebResult> web) {
        this.web = web;
    }

    public class Basic {

        private String phonetic;
        @SerializedName("uk-phonetic")
        private String uk_pron; // 英式发音
        @SerializedName("us-phonetic")
        private String us_pron; // 美式发音
        private ArrayList<String> explains;

        public String getPhonetic() {
            return phonetic;
        }

        public void setPhonetic(String phonetic) {
            this.phonetic = phonetic;
        }

        public String getUk_pron() {
            return uk_pron;
        }

        public void setUk_pron(String uk_pron) {
            this.uk_pron = uk_pron;
        }

        public String getUs_pron() {
            return us_pron;
        }

        public void setUs_pron(String us_pron) {
            this.us_pron = us_pron;
        }

        public ArrayList<String> getExplains() {
            return explains;
        }

        public void setExplains(ArrayList<String> explains) {
            this.explains = explains;
        }
    }

    public class WebResult {

        private String key;
        private ArrayList<String> value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public ArrayList<String> getValue() {
            return value;
        }

        public void setValue(ArrayList<String> value) {
            this.value = value;
        }
    }

}
