package com.example.hengcai.photoeditdemo.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.hengcai.photoeditdemo.R;

/**
 * Created by heng.cai on 2018/1/21.
 */

public class DeleteWordDialog extends Dialog {

    private Context context;
    private TextView ivDeleteWordCancel;
    private TextView tvDeleteWordComfirm;
    private CancelConfirmCallBack cancelConfirmCallBack;

    public DeleteWordDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commonbus_delete_word_layout);
        initDialog();
        initView();
        initListener();
    }

    private void initListener() {
        ivDeleteWordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cancelConfirmCallBack!=null){
                    cancelConfirmCallBack.cancel();
                }
                dismiss();
            }
        });
        tvDeleteWordComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cancelConfirmCallBack!=null){
                    cancelConfirmCallBack.confirm();
                }
                dismiss();
            }
        });
    }

    public void show(CancelConfirmCallBack cancelConfirmCallBack){
        this.cancelConfirmCallBack=cancelConfirmCallBack;
        show();
    }

    private void initView() {
        ivDeleteWordCancel = (TextView)findViewById(R.id.iv_delete_word_cancel);
        tvDeleteWordComfirm = (TextView)findViewById(R.id.tv_delete_word_comfirm);
    }

    private void initDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        params.width = (int) (display.getWidth() * 0.5); //设置dialog宽度
        params.height = (int) (display.getHeight() * 0.36); //设置dialog高度
        getWindow().setAttributes(params);
        window.setAttributes(params);
    }
}
