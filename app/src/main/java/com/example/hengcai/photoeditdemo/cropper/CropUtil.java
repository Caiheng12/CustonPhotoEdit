package com.example.hengcai.photoeditdemo.cropper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;

/**
 * Created by shiju.wang on 2018/1/2.
 */

public class CropUtil {
    /**
     * description 固定宽度截屏
     *
     * @author caiheng
     * created at 2018/2/1 9:57
     */
    public void startCropFixWidth(Activity activity, String path, String resourseType) {
        Bundle bundle = new Bundle();
        bundle.putString(CropImage.CROP_IMAGE_EXTRA_SOURCE, path);
        Intent intent = new Intent(activity, CustomCropImageActivity.class);
        intent.putExtra("resourse_type", resourseType);
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle);
        activity.startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

}
