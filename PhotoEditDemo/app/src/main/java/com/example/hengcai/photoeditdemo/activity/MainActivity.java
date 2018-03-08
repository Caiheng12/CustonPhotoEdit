package com.example.hengcai.photoeditdemo.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.hengcai.photoeditdemo.R;
import com.example.hengcai.photoeditdemo.cropper.CropImage;
import com.example.hengcai.photoeditdemo.dialog.AddEditWordDialog;
import com.example.hengcai.photoeditdemo.dialog.AddEditWordDialogCallBack;
import com.example.hengcai.photoeditdemo.dialog.CancelConfirmCallBack;
import com.example.hengcai.photoeditdemo.dialog.DeleteWordDialog;
import com.example.hengcai.photoeditdemo.util.CommonUtil;
import com.example.hengcai.photoeditdemo.util.Constants;
import com.example.hengcai.photoeditdemo.util.CropUtil;
import com.example.hengcai.photoeditdemo.util.OperateUtils;
import com.example.hengcai.photoeditdemo.util.ToastUtils;
import com.example.hengcai.photoeditdemo.util.ValueUtil;
import com.example.hengcai.photoeditdemo.view.OperateView;
import com.example.hengcai.photoeditdemo.view.TextObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OperateView.SelectChangeListener
        ,OperateView.ItemClickListener,OperateView.EraserNumChangeListener{

    private LinearLayout contentLayout;
    private OperateView operateView;
    private String cameraPath;
    private OperateUtils operateUtils;
    private String typeface;
    private TextView title;
    private TextView tvAddText;
    private SeekBar sbFontSize;
    private Handler handler;
    private TextView tvDeleteWord;
    private ImageView ivAddWord;
    private ImageView ivAddWordFinish;
    private ImageView ivAddWordScreen;
    private final int CAMERA_CODE=10;//相机返回
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    //private String originPath;
   // private String editType;
    private TextView tvLeaveForJigsaw;
    private TextView tvRetakePhoto;
    private LinearLayout bottomLinear;
    private Bitmap viewBitmap = null;
  //  private String resourceType;
    //private String cameraType;
    private ImageView ivAddWordEraser;
    private ImageView ivEraserRevoke;
    private LinearLayout llBottomRevoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commonbus_addtext);
        Intent intent = getIntent();
//        resourceType = intent.getStringExtra("resource_type");//资源类型 默认图片  doc
//        cameraPath = intent.getStringExtra("camera_path");//相机图片路径
//        originPath = intent.getStringExtra("camera_path");//保存原始图片 用于编辑
//        cameraType = intent.getStringExtra("camera_type");//是相机拍照还是相册 album相册
//        editType = intent.getStringExtra("editType");//拼图编辑 editMore  reEdit重新编辑
//        String origin_path = intent.getStringExtra("origin_path");//重新编辑传过来原始图片origin_path
//        if(!ValueUtil.isStrEmpty(origin_path)){
//            originPath=origin_path;
//        }
        operateUtils = new OperateUtils(this);
        handler=new Handler();
        initView();
        initData();
        initListener();
    }


    private void initListener() {
        tvAddText.setOnClickListener(this);
        tvDeleteWord.setOnClickListener(this);
        ivAddWord.setOnClickListener(this);
        ivAddWordFinish.setOnClickListener(this);
        ivAddWordScreen.setOnClickListener(this);
        tvLeaveForJigsaw.setOnClickListener(this);
        tvRetakePhoto.setOnClickListener(this);
        ivAddWordEraser.setOnClickListener(this);
        ivEraserRevoke.setOnClickListener(this);
        sbFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //动态设置大小
                operateView.getCurrentSelectedItem().setTextSize(22+(seekBar.getProgress()-30)*40/100);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(operateView.getCurrentSelectedItem()!=null){
                            operateView.getCurrentSelectedItem().commit();
                            //重新绘制
                            operateView.invalidate();
                        }
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initData(){
        title.setText(getString(R.string.commonbus_edit_picture));
        tvRetakePhoto.setText(getString(R.string.commonbus_back_to_album));
       // initImage();
        tvLeaveForJigsaw.setVisibility(View.GONE);
        tvRetakePhoto.setVisibility(View.VISIBLE);
    }

    private void initView() {
        contentLayout = (LinearLayout) findViewById(R.id.mainLayout);
        tvAddText = (TextView) findViewById(R.id.tv_add_text);
        title = (TextView) findViewById(R.id.title);
        sbFontSize = (SeekBar) findViewById(R.id.sb_font_size);
        tvDeleteWord = (TextView) findViewById(R.id.tv_delete_word);
        tvLeaveForJigsaw = (TextView) findViewById(R.id.tv_leave_for_jigsaw);
        tvRetakePhoto = (TextView) findViewById(R.id.tv_retake_photo);
        ivAddWord = (ImageView) findViewById(R.id.iv_add_word);
        ivAddWordFinish = (ImageView) findViewById(R.id.iv_add_word_finish);
        ivAddWordScreen = (ImageView) findViewById(R.id.iv_add_word_screen);
        bottomLinear = (LinearLayout) findViewById(R.id.bottom_linear);
        llBottomRevoke = (LinearLayout) findViewById(R.id.ll_bottom_revoke);
        ivAddWordEraser = (ImageView) findViewById(R.id.iv_add_word_eraser);
        ivEraserRevoke = (ImageView) findViewById(R.id.iv_eraser_revoke);
        setSeekBarColor(sbFontSize, Color.parseColor("#ffffff"));
    }

    /**
     *description 设置字体大小控制器的进度颜色
     *@author caiheng
     *created at 2018/1/21 15:20
     */
    public void setSeekBarColor(SeekBar seekBar, int color){
        LayerDrawable layerDrawable = (LayerDrawable)seekBar.getProgressDrawable();
        Drawable dra=layerDrawable.getDrawable(2);
        dra.setColorFilter(color, PorterDuff.Mode.SRC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        seekBar.invalidate();
    }

    /**
     *description 初始化原始图片
     *@author caiheng
     *created at 2018/1/21 15:19
     */
    private void initImage() {
        for(int i=0;i<contentLayout.getChildCount();i++){
            if(contentLayout.getChildAt(i) instanceof OperateView){
                contentLayout.removeViewAt(i);
            }
        }
        if(viewBitmap!=null&&!viewBitmap.isRecycled()){
            viewBitmap.recycle();
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try {
            viewBitmap = BitmapFactory.decodeStream(new FileInputStream(new File(cameraPath)) , null, opts);
            operateView = new OperateView(MainActivity.this, viewBitmap);
            operateView.setSelectChangeListener(this);
            operateView.setItemClickListener(this);
            operateView.setEraserNumChangeListener(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(viewBitmap.getWidth(), viewBitmap.getHeight());
            operateView.setLayoutParams(layoutParams);
            contentLayout.addView(operateView);
            operateView.setMultiAdd(true); //设置此参数，可以添加多个文字
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *description 添加文字完成
     *@author caiheng
     *created at 2018/1/21 15:18
     */
    private void addWordFinish() {
//        operateView.save();
//        Bitmap bmp = getBitmapByView(operateView);
//        if (bmp != null){
//            cameraPath = saveBitmap(bmp);
//            Intent okData = new Intent();
//            okData.putExtra("camera_path", cameraPath);
//            okData.putExtra("origin_path", originPath);
//            if(!ValueUtil.isStrEmpty(cameraType)&&cameraType.equals("album")){
//                okData.putExtra("camera_type","album");
//            }
//            setResult(RESULT_OK, okData);
//            this.finish();
//        }
    }

    /**
     *description 将模板View的图片转化为Bitmap
     *@author caiheng
     *created at 2018/1/21 15:26
     */
    public Bitmap getBitmapByView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    /**
     *description 将生成的图片保存到内存中
     *@author caiheng
     *created at 2018/1/21 15:26
     */
    public String saveBitmap(Bitmap bitmap){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File dir = new File(Constants.filePath);
            if (!dir.exists())
                dir.mkdir();
            File file = new File(Constants.filePath + System.currentTimeMillis() + ".jpg");
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

    /**
     *description 输入文字弹框
     *@author caiheng
     *created at 2018/1/21 15:25
     */
    private void addWordItem(){
        AddEditWordDialog addEditWordDialog=new AddEditWordDialog(MainActivity.this);
        final TextObject currentSelectedItem = operateView.getCurrentSelectedItem();
        addEditWordDialog.show(new AddEditWordDialogCallBack() {
            @Override
            public void cancel() {
            }

            @Override
            public void confirm(String content,int color) {
                currentSelectedItem.setText(content);
                currentSelectedItem.setColor(color);
                currentSelectedItem.commit();
                operateView.invalidate();
            }
        });
        addEditWordDialog.setText(currentSelectedItem.getText());
        addEditWordDialog.setTextColor(currentSelectedItem.getColor());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_add_text) {
            if(ValueUtil.isStrEmpty(cameraPath)){
                return;
            }
            //添加文字
            if(operateView.getCurrentSelectedItem()==null){
                ToastUtils.showToast(MainActivity.this,getString(R.string.commonbus_no_select_word));
                return;
            }
            addWordItem();
        } else if (i == R.id.iv_add_word_finish) {
            if(ValueUtil.isStrEmpty(cameraPath)){
                return;
            }
            //完成
          //  addWordFinish();
        } else if(i == R.id.tv_delete_word){
            if(ValueUtil.isStrEmpty(cameraPath)){
                return;
            }
            //删除
            if(operateView.getCurrentSelectedItem()==null){
                ToastUtils.showToast(MainActivity.this,getString(R.string.commonbus_no_select_word));
                return;
            }
            DeleteWordDialog deleteWordDialog=new DeleteWordDialog(MainActivity.this);
            deleteWordDialog.show(new CancelConfirmCallBack() {
                @Override
                public void cancel() {
                }

                @Override
                public void confirm() {
                    operateView.removeCurrentText();
                    bottomLinear.setVisibility(View.INVISIBLE);
                }
            });
        }else if(i == R.id.iv_add_word){
            if(ValueUtil.isStrEmpty(cameraPath)){
                return;
            }
            //添加白块
            operateView.setEraserUnenable();
            ivAddWordEraser.setImageResource(R.mipmap.commonbus_eraser);
            TextObject textObj = operateUtils.getTextObject();
            if(textObj != null){
                textObj.setTypeface(typeface);
                textObj.commit();
                operateView.addItem(textObj);
            }
        }else if(i == R.id.iv_add_word_screen){
            if(ValueUtil.isStrEmpty(cameraPath)){
                return;
            }
            //截屏
            operateView.setEraserUnenable();
            ivAddWordEraser.setImageResource(R.mipmap.commonbus_eraser);
            if(ValueUtil.isStrNotEmpty(cameraPath)){
                operateView.save();
                Bitmap bmp = getBitmapByView(operateView);
                if (bmp != null) {
                    cameraPath = saveBitmap(bmp);
                }

                new CropUtil().startCropFixWidth(this,cameraPath,"");
            }
        }else if(i == R.id.tv_leave_for_jigsaw){
            if(ValueUtil.isStrEmpty(cameraPath)){
                return;
            }
            finish();
        }else if(i == R.id.tv_retake_photo){
            if(operateView!=null){
                operateView.setEraserUnenable();
            }
            ivAddWordEraser.setImageResource(R.mipmap.commonbus_eraser);
            Intent intent = new Intent(this, CustomCameraActivity.class);
            intent.putExtra("path", getTempFile().getAbsolutePath());
            startActivityForResult(intent, CAMERA_CODE);
        }else if(i == R.id.iv_add_word_eraser){
            operateView.setEraserEnable();
            operateView.resetBlockState();
            ivAddWordEraser.setImageResource(R.mipmap.commonbus_eraser_gray);
        }else if(i == R.id.iv_eraser_revoke){
            operateView.revokeEraser();
        }
    }

    public File getTempFile(){
        File tempFile = new File(CommonUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/Apollo/Crop/"),
                System.currentTimeMillis() + ".jpg");
        return tempFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            // 自定义裁剪
            if(resultCode == RESULT_OK) {
                if(data!=null){
                    // 获取到裁剪后的图像
                    String path = data.getStringExtra("camera_path");
                    String resourseType = data.getStringExtra("resourse_type");
                    if(!ValueUtil.isStrEmpty(resourseType)&&resourseType.equals("album")){
                        cameraPath=path;
                        initImage();
                    }else {
//                        if(!cameraPath.equals(originPath)){
//                            deleteOldPhoto(cameraPath);
//                        }
                        cameraPath = path;
                        initImage();
                    }
                }
            }
        }else if(requestCode==CAMERA_CODE){
            if (resultCode == RESULT_OK) {
                if(data!=null){
                    if(data.getStringExtra("path").equals("toAlbum")){//相机点击相册
                        Intent openphotoIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(openphotoIntent, PHOTO_PICKED_WITH_DATA);
                    }else {
                        //获得拍的照片
                        // deleteOldPhoto(originPath);
                        cameraPath=data.getStringExtra("path");
                        initImage();
                    }
                }
            }
        }else if(requestCode==PHOTO_PICKED_WITH_DATA){
            if(data!=null){
                Uri selectedImage = data.getData();
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                String photoPath = c.getString(columnIndex);
                c.close();
                if(CommonUtil.isImage(photoPath)){
                    new CropUtil().startCropFixWidth(this,photoPath,"album");
                }else {
                    ToastUtils.showToast(MainActivity.this,getString(R.string.commonbus_invaild_photo));
                }
            }
        }
    }

    /**
     *description 删除图片文件
     *@author caiheng
     *created at 2018/1/31 17:53
     */
    private void deleteOldPhoto(String cameraPath) {
        File file=new File(cameraPath);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     *description 添加的文字状态有改变
     *@author caiheng
     *created at 2018/1/31 18:24
     */
    @Override
    public void isChange() {
        TextObject currentSelectedItem = operateView.getCurrentSelectedItem();
        if(currentSelectedItem==null){
            bottomLinear.setVisibility(View.INVISIBLE);
        }else {
            operateView.setEraserUnenable();
            bottomLinear.setVisibility(View.VISIBLE);
            sbFontSize.setProgress((int) ((currentSelectedItem.getTextSize()-22)*2.5f+30));
            llBottomRevoke.setVisibility(View.GONE);
        }
    }

    /**
     *description 第二次点击条目
     *@author caiheng
     *created at 2018/2/7 18:55
     */
    @Override
    public void onClick() {
        //添加文字
        if(operateView.getCurrentSelectedItem()==null){
            ToastUtils.showToast(MainActivity.this,getString(R.string.commonbus_no_select_word));
            return;
        }
        addWordItem();
    }

    /**
     *description 橡皮擦笔画数量监听
     *@author caiheng
     *created at 2018/2/27 17:20
     */
    @Override
    public void eraserNumChange(int num) {
        if(num>0){
            llBottomRevoke.setVisibility(View.VISIBLE);
            bottomLinear.setVisibility(View.GONE);
        }else {
            llBottomRevoke.setVisibility(View.GONE);
            bottomLinear.setVisibility(View.INVISIBLE);
        }
        operateView.resetBlockState();
    }
}
