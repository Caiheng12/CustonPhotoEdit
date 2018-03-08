package com.example.hengcai.photoeditdemo.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hengcai.photoeditdemo.R;
import com.example.hengcai.photoeditdemo.util.CommonUtil;
import com.example.hengcai.photoeditdemo.util.ToastUtils;
import com.example.hengcai.photoeditdemo.util.ValueUtil;

/**
 * Created by heng.cai on 2018/1/19.
 */

public class AddEditWordDialog extends Dialog {

    private Context context;
    private TextView ivEditWorkCancel;
    private TextView tvEditWorkComfirm;
    private TextView tvEditWordNum;
    private EditText etEditWord;
    private RadioButton rbColorBlack;
    private RadioButton rbCloloRed;
    private RadioGroup rgColorSelector;
    private AddEditWordDialogCallBack addEditWordDialogCallBack;
    private String inputContent;
    private int textColor=Color.parseColor("#333333");

    public AddEditWordDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commonbus_edit_text_layout);
        initDialog();
        initView();
        initListener();
    }

    private void initDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        params.width = (int) (display.getWidth() * 0.5); //设置dialog宽度
        params.height = (int) (display.getHeight() * 0.56); //设置dialog高度
        getWindow().setAttributes(params);
        window.setAttributes(params);
    }

    public void show(AddEditWordDialogCallBack addEditWordDialogCallBack){
        this.addEditWordDialogCallBack=addEditWordDialogCallBack;
        show();
    }

    private void initListener() {
        etEditWord.addTextChangedListener(new TextWatcher() {
            public String beforeText;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeText= charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int index = etEditWord.getSelectionStart() - 1;
                if (index > 0) {
                    if (CommonUtil.isEmojiCharacter(editable.charAt(index))) {//是否为表情
                        etEditWord.setText(beforeText);
                        etEditWord.setSelection(etEditWord.length());
                        ToastUtils.showToast(context,context.getString(R.string.commonbus_invaild_char));
                    }
                }
                int len = etEditWord.getText().toString().length();
                if (len > 10) {//限制了最多输入长度15
                    etEditWord.setText(etEditWord.getText().subSequence(0,10));
                    etEditWord.setSelection(10);
                    ToastUtils.showToast(context,context.getString(R.string.commonbus_input_max_num));
                    tvEditWordNum.setText(context.getString(R.string.commonbus_input_num,etEditWord.getText().length()));
                } else {
                    tvEditWordNum.setText(context.getString(R.string.commonbus_input_num,len));
                }
                inputContent=etEditWord.getText().toString();
            }
        });
        rgColorSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                if(id==R.id.rb_color_black){
                    etEditWord.setTextColor(Color.parseColor("#333333"));
                    textColor=Color.parseColor("#333333");
                } else {
                    etEditWord.setTextColor(Color.parseColor("#F32D2D"));
                    textColor=Color.parseColor("#F32D2D");
                }
            }
        });

        ivEditWorkCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addEditWordDialogCallBack!=null){
                    addEditWordDialogCallBack.cancel();
                }
                dismiss();
            }
        });
        tvEditWorkComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(ValueUtil.isStrEmpty(inputContent)){
//                    ToastUtils.showToast(context,context.getString(R.string.commonbus_no_content));
//                     return;
//                }
                if(addEditWordDialogCallBack!=null){
                    addEditWordDialogCallBack.confirm(inputContent,textColor);
                }
                dismiss();
            }
        });
    }

    private void initView() {
        ivEditWorkCancel = (TextView) findViewById(R.id.iv_edit_work_cancel);
        tvEditWorkComfirm = (TextView) findViewById(R.id.tv_edit_work_comfirm);
        tvEditWordNum = (TextView) findViewById(R.id.tv_edit_word_num);
        etEditWord = (EditText) findViewById(R.id.et_edit_word);
        rbColorBlack = (RadioButton) findViewById(R.id.rb_color_black);
        rbCloloRed = (RadioButton) findViewById(R.id.rb_clolo_red);
        rgColorSelector = (RadioGroup) findViewById(R.id.rg_color_selector);
    }

    public void setText(String text) {
        if(!ValueUtil.isStrEmpty(text)){
            etEditWord.setText(text);
        }
    }

    public void setTextColor(int color) {
        if(etEditWord!=null){
            etEditWord.setTextColor(color);
        }
        if(rbColorBlack!=null&&rbCloloRed!=null){
            if(color==Color.parseColor("#333333")){
                rbColorBlack.setChecked(true);
                rbCloloRed.setChecked(false);
            }else {
                rbColorBlack.setChecked(false);
                rbCloloRed.setChecked(true);
            }
        }
    }
}
