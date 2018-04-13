package com.example.hengcai.photoeditdemo.cropper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hengcai.photoeditdemo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
*description 图片编辑
*@author caiheng
*created at 2018/2/1 10:50
*/
public class CustomCropImageActivity extends AppCompatActivity {
    public  final String filePath = Environment.getExternalStorageDirectory() + "/PictureTest/";
    private TextView tvScreenTitle;
    private ImageView ivScreenBack;
    private CropView cropView;
    private FrameOverlayView overlayView;
    private String photoPath;
    private ImageView ivScreenCancel;
    private ImageView ivScreenConfirm;
    private String resourseType;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_crop_image_activity);
        Bundle bundle = getIntent().getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE);
        photoPath = bundle.getString(CropImage.CROP_IMAGE_EXTRA_SOURCE);
        resourseType = getIntent().getStringExtra("resourse_type");
        initView();
        initData();
        initListener();
    }

    private void initData(){
        cropView.setFilePath(photoPath);
    }

    private void initView() {
        tvScreenTitle = (TextView) findViewById(R.id.tv_screen_title);
        ivScreenBack = (ImageView) findViewById(R.id.iv_screen_back);
        ivScreenCancel = (ImageView) findViewById(R.id.iv_screen_cancel);
        ivScreenConfirm = (ImageView) findViewById(R.id.iv_screen_confirm);
        cropView = (CropView) findViewById(R.id.crop_view);
        overlayView = (FrameOverlayView) findViewById(R.id.overlay_view);
    }

    private void initListener() {
        ivScreenBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivScreenCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivScreenConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rect frameRect = overlayView.getFrameRect();
                Bitmap bitmap = cropView.crop(frameRect);
                photoPath = saveBitmap(bitmap);
                if(bitmap!=null&&!bitmap.isRecycled()){
                    bitmap.recycle();
                }
                Intent intent = new Intent();
                intent.putExtra("camera_path", photoPath);
                intent.putExtra("resourse_type", resourseType);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     *description 将生成的图片保存到内存中
     *@author caiheng
     *created at 2018/1/21 15:26
     */
    public String saveBitmap(Bitmap bitmap){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File dir = new File(filePath);
            if (!dir.exists())
                dir.mkdir();
            File file = new File(filePath + System.currentTimeMillis() + ".jpg");
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    out.flush();
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
