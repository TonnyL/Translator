package com.marktony.translator.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by lizhaotailang on 2016/2/18.
 */
public class UTF8Encoder {

    public static String encode(String url){
        if (url == null){
            return null;
        }
        try {
            url = URLEncoder.encode(url,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url;
    }

}
