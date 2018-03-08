package com.example.hengcai.photoeditdemo.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.hengcai.photoeditdemo.R;
import com.example.hengcai.photoeditdemo.view.FocusSurfaceView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import static android.Manifest.permission.CAMERA;

/**
 * 自定义相机
 */
public class CustomCameraActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback ,SurfaceHolder.Callback{
    public final String filePath = Environment.getExternalStorageDirectory() + "/PictureTest/";

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private static final String FRAGMENT_DIALOG = "dialog";

    private ImageView ivCameraCancel;

    private String originPath; // 传进来的路径
    private ImageView ivCameraAlbum;
    private FocusSurfaceView previewSv;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean focus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //设置全屏
        setContentView(R.layout.activity_custom_camera);

        originPath = getIntent().getStringExtra("path");

        ivCameraCancel = (ImageView) findViewById(R.id.iv_camera_cancel);
        ivCameraAlbum = (ImageView) findViewById(R.id.iv_camera_album);


        previewSv = (FocusSurfaceView) findViewById(R.id.preview_sv);
        previewSv.setCropMode(FocusSurfaceView.CropMode.FIT_IMAGE);
        previewSv.setKeepScreenOn(false);
        mHolder = previewSv.getHolder();
        mHolder.addCallback(CustomCameraActivity.this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        initCamera();
        ivCameraCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCameraActivity.this.finish();
            }
        });
        ivCameraAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("path", "toAlbum");
                setResult(RESULT_OK, intent);
                CustomCameraActivity.this.finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_take_picture);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.btn_take_picture) {
                        if (!focus) {
                            takePicture();
                        }
                    }
                }
            });
        }
    }

    private void initCamera() {
        if (checkPermission()) {
            try {
                mCamera = Camera.open(0);//1:采集指纹的摄像头. 0:拍照的摄像头.
                mCamera.setPreviewDisplay(mHolder);
            } catch (Exception e) {
                finish();
                e.printStackTrace();
            }
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, 10000);
    }

    /**
     * 拍照
     */
    private void takePicture() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                focus = success;
                if (success) {
                    mCamera.cancelAutoFocus();
                    mCamera.takePicture(new Camera.ShutterCallback() {
                        @Override
                        public void onShutter() {
                        }
                    }, null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Bitmap cropBitmap = previewSv.getPicture(data);
                            Intent intent = new Intent();
                            intent.putExtra("path", saveBitmap(cropBitmap));
                            setResult(RESULT_OK, intent);
                            CustomCameraActivity.this.finish();
                            focus = false;
                            mCamera.startPreview();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment
                    .newInstance(R.string.camera_permission_confirmation,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION,
                            R.string.camera_permission_not_granted)
                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
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

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCamera();
        setCameraParams();
    }

    private void setCameraParams() {
        if (mCamera == null) {
            return;
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            int orientation = judgeScreenOrientation();
                mCamera.setDisplayOrientation(0);
                parameters.setRotation(0);
//            if (Surface.ROTATION_0 == orientation) {
//                System.out.println("gggggggggggggggggggggggggggggggggggggggggggggg:ROTATION_0");
//                mCamera.setDisplayOrientation(90);
//                parameters.setRotation(90);
//            } else if (Surface.ROTATION_90 == orientation) {
//                mCamera.setDisplayOrientation(0);
//                parameters.setRotation(0);
//            } else if (Surface.ROTATION_180 == orientation) {
//                mCamera.setDisplayOrientation(180);
//                parameters.setRotation(180);
//            } else if (Surface.ROTATION_270 == orientation) {
//                mCamera.setDisplayOrientation(180);
//                parameters.setRotation(180);
//            }
            parameters.setPictureSize(1024, 768);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断屏幕方向
     *
     * @return 0：竖屏 1：左横屏 2：反向竖屏 3：右横屏
     */
    private int judgeScreenOrientation() {
        return getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    public static class ConfirmationDialogFragment extends DialogFragment {

        private static final String ARG_MESSAGE = "message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

        public static ConfirmationDialogFragment newInstance(@StringRes int message,
                                                             String[] permissions, int requestCode, @StringRes int notGrantedMessage) {
            ConfirmationDialogFragment
                    fragment = new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE, message);
            args.putStringArray(ARG_PERMISSIONS, permissions);
            args.putInt(ARG_REQUEST_CODE, requestCode);
            args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(args.getInt(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] permissions = args.getStringArray(ARG_PERMISSIONS);
                                    if (permissions == null) {
                                        throw new IllegalArgumentException();
                                    }
                                    ActivityCompat.requestPermissions(getActivity(),
                                            permissions, args.getInt(ARG_REQUEST_CODE));
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(),
                                            args.getInt(ARG_NOT_GRANTED_MESSAGE),
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                    .create();
        }

    }
}
