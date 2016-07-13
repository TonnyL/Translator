package com.marktony.translator.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.marktony.translator.R;

/**
 * Created by lizhaotailang on 2016/7/13.
 */

public class SnackBarHelper {

    private Snackbar snackbar;
    private Context context;

    public SnackBarHelper(Context context){
        this.context = context;
    }

    public void make(View view,CharSequence content,int time){
        snackbar = Snackbar.make(view,content,time);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    public void make(View view,int resId,int time){
        snackbar = Snackbar.make(view,resId,time);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    public void setAction(CharSequence action,View.OnClickListener listener){
        snackbar.setAction(action,listener);
    }

    public void setAction(int resId,View.OnClickListener listener){
        snackbar.setAction(resId,listener);
    }

    public void show(){
        snackbar.show();
    }

}
