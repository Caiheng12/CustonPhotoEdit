package com.example.hengcai.photoeditdemo.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * 静态Toast工具
 * Created by Fan on 2016/2/19.
 */
public class ToastUtils {

    public static void showToast(final Activity act, final String msg){
		//获取当前线程名字
        String nowThreadName = Thread.currentThread().getName();
		//判断是否在主线程中
        if("main".equals(nowThreadName)){
            thisToast(act,msg);

		//如果在子线程中
        }else{
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    thisToast(act, msg);
                }
            });
        }
    }

    public static void showToast(final Context context, final String msg){
        //获取当前线程名字
        String nowThreadName = Thread.currentThread().getName();
        //判断是否在主线程中
        if("main".equals(nowThreadName)){
            thisToast((Activity) context,msg);

            //如果在子线程中
        }else{
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    thisToast((Activity) context, msg);
                }
            });
        }
    }

    private static Toast toast;

    private static void thisToast(Activity act, final String msg){
        if(toast == null){
            toast = Toast.makeText(act, msg, Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }

}
