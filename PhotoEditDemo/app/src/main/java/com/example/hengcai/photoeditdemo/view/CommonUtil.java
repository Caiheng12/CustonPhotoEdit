package com.example.hengcai.photoeditdemo.util;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by kai.chen on 2017/7/4.
 */

public class CommonUtil {
    public static final String SYS_MESSAGE_URL = "system_message_url";
    //wallpaper
    public static final String USERMUMBER = "wtk_stubook_teacher_user_number";
    public static final String USERNAME = "wtk_stubook_teacher_user_name";
    public static final String SCHOOLNAME = "wtk_stubook_teacher_school_name";
    public static final String GRADESTATE = "wtk_stubook_teacher_grade_state";
    public static final String SCHOOLID = "wtk_smartbook_school_id";
    public static final String WTK_SYS_UPDATE = "wtk_sys_update";
    public static ContentResolver contentResolver = null;
    private static final String APP_CACAHE_DIRNAME = "/Apollo/webcache";
    private static long lastClickTime;
    private static final String SECRET_KEY = "wheatek@123!";

    /**
     * 重启应用
     */
    public static void restartApp() {
        System.exit(0);
    }

    /**
     * 判断是否是系统app
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isSystemApp(@NonNull Context context, @NonNull String pkgName) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (info != null
                && (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
            // app
            return false;
        } else {
            // system app
            return true;
        }
    }

    /**
     * 检查是否是debug模式
     *
     * @param context
     * @return
     */
    public static boolean isApkDebugable(@NonNull Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 保留两位小数
     *
     * @param data
     * @return
     */
    public static String trafficFormat(float data) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(data);

    }

    /**
     * 手机号验证
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        CharSequence inputStr = phoneNumber;
        //正则表达式
        String phone = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 文件大小自然显示
     *
     * @param context
     * @param size
     * @return
     */
    public static String formatFileSize(@NonNull Context context, int size) {
        return Formatter.formatFileSize(context, Long.valueOf(size));
    }

    /**
     * 日期格式转换
     *
     * @param date yyyy-MM-dd HH:mm:ss
     * @return yyyy年MM月dd日 HH:mm
     */

    /**
     * @param date yyyy-MM-dd HH:mm:ss
     * @return yyyy年MM月dd日 HH:mm
     */

    public static String formatDate(@NonNull String format, String date) {
        SimpleDateFormat strToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dataToStr = new SimpleDateFormat(format);
        String sendTime = null;
        try {
            Date mDate = strToDate.parse(date);
            sendTime = dataToStr.format(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendTime;
    }

    /**
     * 时间大小比较(前一个是否大于后一个)
     * date1 当前时间 yyyy-MM-dd HH:mm:ss
     * date2 选择的时间 yyyy-MM-dd HH:mm:ss
     */
    public static boolean datecompare(@NonNull String date1, @NonNull String date2) {
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long diff = 0;
        try {
            Date mDate1 = currentTime.parse(date1);
            Date mDate2 = currentTime.parse(date2);
            diff = mDate2.getTime() / 1000 - mDate1.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (diff > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查是否有权限
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean checkPermission(@NonNull Context context, String[] permissions) {
        ArrayList<String> needQuestPer = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (context.getPackageManager().checkPermission(permissions[i]
                    , context.getPackageName())
                    != PackageManager.PERMISSION_GRANTED) {
                needQuestPer.add(permissions[i]);
            }
        }

        if (needQuestPer.size() == 0) {
            return true;
        }

        return false;
    }

    public static final int REQUEST_PERMISSIONS = 0x001;

    public static final int CONTEXT_ACTIVITY = 1;
    public static final int CONTEXT_FRAGMENT = 2;
    public static final int CONTEXT_SUPPORT_FRAGMENT = 3;

    /**
     * 获取权限
     *
     * @param context
     * @param permissions
     * @param requestCode
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean requestPermission(@NonNull Object context, String[] permissions, int requestCode) {
        final int contextType;
        Context finalContext;

        if (context instanceof Activity) {
            contextType = CONTEXT_ACTIVITY;
            finalContext = (Activity) context;
        } else if (context instanceof Fragment) {
            contextType = CONTEXT_FRAGMENT;
            finalContext = ((Fragment) context).getActivity();
        } else if (context instanceof android.support.v4.app.Fragment) {
            contextType = CONTEXT_SUPPORT_FRAGMENT;
            finalContext = ((android.support.v4.app.Fragment) context).getActivity();
        } else {
            throw new IllegalArgumentException("context not found");
        }

        ArrayList<String> needQuestPer = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (finalContext.getPackageManager().checkPermission(permissions[i]
                    , finalContext.getPackageName())
                    != PackageManager.PERMISSION_GRANTED) {
                needQuestPer.add(permissions[i]);
            }
        }

        if (needQuestPer.size() != 0) {
            String[] Questper = new String[needQuestPer.size()];
            for (int i = 0; i < needQuestPer.size(); i++) {
                Questper[i] = needQuestPer.get(i);
            }

            if (contextType == CONTEXT_ACTIVITY) {
                ((Activity) context).requestPermissions(Questper, requestCode);
            } else if (contextType == CONTEXT_FRAGMENT) {
                ((Fragment) context).requestPermissions(Questper, requestCode);
            } else if (contextType == CONTEXT_SUPPORT_FRAGMENT) {
                ((android.support.v4.app.Fragment) context).requestPermissions(Questper, requestCode);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取 sim iccid
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static String getSimCardIccId(@NonNull Context context) {
        if (!checkPermission(context, new String[]{Manifest.permission.READ_PHONE_STATE})) {
            return null;
        }
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        List<SubscriptionInfo> subInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (subInfoList != null) {
            return subInfoList.get(0).getIccId();
        }
        return null;
    }

    /**
     * 获取 imei
     *
     * @return
     */
    public static String getIMEINum(@NonNull Context context) {
        if (!checkPermission(context, new String[]{Manifest.permission.READ_PHONE_STATE})) {
            return null;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 获取手机IMSI号
     */
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId();
    }

    /**
     * 获取版本code
     *
     * @param context
     * @return
     */
    public static int getVersionCode(@NonNull Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = -1;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本name
     *
     * @param context
     * @return
     */
    public static String getVersionName(@NonNull Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取内部版本号
     *
     * @return
     */
    public static String getInternalVersion() {
        StringBuffer m_version_buffer = new StringBuffer();
        try {
            FileInputStream mFileInputStream = new FileInputStream("/system/build.prop");
            InputStreamReader mInputStreamReader = new InputStreamReader(mFileInputStream);
            BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
            String versionString = null;
            String[] versionCh = null;
            while ((versionString = mBufferedReader.readLine()) != null) {
                versionCh = versionString.split("=");
                if (versionCh[0].equals("ro.internal.version")) {
                    m_version_buffer.append(versionCh[1]);
                }
            }
            mFileInputStream.close();
        } catch (Exception e) {
            m_version_buffer.append("");
        }
        String versionBufferString = m_version_buffer.toString();
        if (versionBufferString.isEmpty()) {
            return versionBufferString;
        }

        return versionBufferString;
    }

    /**
     * 检查sdcard是否可用
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static void initWebView(Context context, WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能
        webView.getSettings().setDatabaseEnabled(true);
        //String cacheDirPath = SmartBookApplication.getContext().getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        String cacheDirPath = context.getCacheDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        //设置数据库缓存路径
        webView.getSettings().setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        webView.getSettings().setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        webView.getSettings().setAppCacheEnabled(true);
    }

    /**
     * 检查文件是否存在
     */
    public static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    public static void restartApplication() {
        System.exit(0);
    }

    /**
     * 加密
     *
     * @param data
     * @return
     */
    public static String encrypt(String data) {
        try {
            byte[] bt = encrypt(data.getBytes(), SECRET_KEY.getBytes());
            String strs = Base64.encodeToString(bt, Base64.NO_WRAP);
            return strs;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }

    /**
     * 防止重复点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            lastClickTime = time;
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 防止重复点击
     *
     * @return
     */
    public static boolean isFastDoubleClick(int milisecond) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < milisecond) {
            lastClickTime = time;
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 禁止EditText输入空格
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText, int length) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(length)});
    }

    public static boolean checkEndsWithInStringArray(String checkItsEnd,
                                                     String[] fileEndings) {
        for (String aEnd : fileEndings) {
            if (checkItsEnd.toLowerCase().endsWith(aEnd))
                return true;
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    public static boolean isEmojiCharacter(char codePoint) {
        if (codePoint == '，' || codePoint == '？' || codePoint == '！' || codePoint == '～' || codePoint == '：' || codePoint == '；' || codePoint == '￥' || codePoint == '（' || codePoint == '）') {
            return false;
        }
        return !(codePoint == 0x0 || codePoint == 0x9 || codePoint == 0xA || codePoint == 0xD || codePoint >= 0x20 && codePoint <= 0xD7FF) || codePoint >= 0xE000 && codePoint <= 0xFFFD;
    }

    /**
     * list转换为string,用","隔开
     *
     * @param stringList
     * @return
     */
    public static String listToString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    public static void hideKeyboard(IBinder token, Context context) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 通过文件地址获取包名
     *
     * @param context
     * @param filePath
     * @return
     */
    public static String getPackageName(Context context, String filePath) {
        String packageName = null;

        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            packageName = applicationInfo.packageName;
        }
        return packageName;
    }

    /**
     * description 删除单个文件
     *
     * @author caiheng
     * created at 2017/11/9 15:41
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * description 安装apk
     *
     * @author caiheng
     * created at 2017/11/15 9:54
     */
    public static boolean installApk(Context context, String savePath) {
        try {
            // 通过Intent安装APK文件
            File file = new File(savePath);
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setData(Uri.fromFile(file));
            intents.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intents);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //把秒转换为时分秒
    public static String formatTime(int time) {

        int hour = time / 3600;
        int minute = (time % 3600) / 60;
        int second = (time % 3600) % 60;
        StringBuilder sb = new StringBuilder();

        if (hour > 0) {
            sb.append(hour).append("时");
        }
        if (minute > 0) {
            sb.append(minute).append("分");
        }
        if (second > 0) {
            sb.append(second).append("秒");
        }
        if (hour == 0 && minute == 0 && second == 0) {
            sb.append(second).append("秒");
        }
        return sb.toString();
    }

    /**
     * 获取img标签中的图片地址
     *
     * @param htmlStr html字符串
     */
    public static String getImgAddress(String htmlStr) {
        ArrayList<String> pictures = new ArrayList<>();
        String img = "";
        Pattern p_image;
        Matcher m_image;
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            // 得到<img />数据
            img = m_image.group();
            // 匹配<img>中的src数据
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                pictures.add(m.group(1));
            }
        }
        if (pictures != null && pictures.size() > 0) {
            return pictures.get(0);
        } else {
            return htmlStr;
        }
    }


    /**
     * 给图片地址添加标签
     *
     * @param url html字符串
     */
    public static String addHtmlStyle(String url) {
        if (!ValueUtil.isStrEmpty(url)) {
            if (url.contains("<img") && url.contains("/>")) {
                return url;
            }
        }
        String tempUrl = "<img style=\"max-width:100%;\" src=\"" + url + "\"/>";
        return tempUrl;
    }

    /**
     * 根据路径获取图片并且压缩，适应view
     *
     * @param filePath    图片路径
     * @param contentView 适应的view
     * @return Bitmap 压缩后的图片
     */
    public static Bitmap compressToFitView(Activity activity, String filePath, View contentView) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth = metric.widthPixels; // 屏幕宽度（像素）
        int screenHeight = metric.widthPixels; // 屏幕宽度（像素）
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, opt);
        int layoutHeight = contentView.getHeight();
        float scale = 0f;
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        scale = bitmapHeight > bitmapWidth
                ? layoutHeight / (bitmapHeight * 1f)
                : screenWidth / (bitmapWidth * 1f);
        Bitmap resizeBmp;
        if (scale != 0) {
            int bitmapheight = bitmap.getHeight();
            int bitmapwidth = bitmap.getWidth();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale); // 长和宽放大缩小的比例
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapwidth,
                    bitmapheight, matrix, true);
        } else {
            resizeBmp = bitmap;
        }
        return resizeBmp;
    }

    /**
     * description 叠加图片（拼接）
     *
     * @author caiheng
     * created at 2018/1/23 15:18
     */
    public static Bitmap mergeBitmapVerticalMax660(Bitmap firstBitmap, Bitmap secondBitmap) {
        if (firstBitmap == null || secondBitmap == null) {
            return null;
        }
        int width = firstBitmap.getWidth();
        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh:"+(firstBitmap.getHeight()+secondBitmap.getHeight()));
        if((firstBitmap.getHeight()+secondBitmap.getHeight())>660){
            int totalHeight = firstBitmap.getHeight() + secondBitmap.getHeight();
            
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh:"+firstBitmap.getHeight());

            firstBitmap = zoomBitmap(firstBitmap, 660*1.0f / totalHeight);
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh:"+firstBitmap.getHeight());
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh:"+secondBitmap.getHeight());

            secondBitmap = zoomBitmap(secondBitmap, 660*1.0f / totalHeight);
            width= (int) (660*1.0f*width / totalHeight);
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh:"+secondBitmap.getHeight());
        }

        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh:"+(firstBitmap.getHeight()+secondBitmap.getHeight()));


        // float width = (firstBitmap.getWidth() + secondBitmap.getWidth()) / 2.0f;
        //  firstBitmap = zoomBitmap(firstBitmap, width / firstBitmap.getWidth());
        // secondBitmap = zoomBitmap(secondBitmap, width / secondBitmap.getWidth());
        if (firstBitmap == null || secondBitmap == null) {
            return null;
        }
        int height = firstBitmap.getHeight() + secondBitmap.getHeight();
        Bitmap monBitmap = null;
        try {
            monBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
        }
        if (monBitmap == null) {
            return null;
        }
        Canvas canvas = new Canvas(monBitmap);
        canvas.drawBitmap(firstBitmap, 0, 0, null);
        canvas.drawBitmap(secondBitmap, 0, firstBitmap.getHeight(), null);

        if (!firstBitmap.isRecycled()) {
            firstBitmap.recycle();
        }
        if (!secondBitmap.isRecycled()) {
            secondBitmap.recycle();
        }
        SoftReference<Bitmap> commentsBitmaps = new SoftReference<>(monBitmap);
        return commentsBitmaps.get();
    }

    /**
     * description 缩放图片
     *
     * @author caiheng
     * created at 2018/1/23 15:19
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, float scale) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newbmp = null;
        try {
            newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newbmp;
    }

    /**
     * description 将生成的图片保存到SD中
     *
     * @author caiheng
     * created at 2018/1/24 10:51
     */
    public static String saveBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory() + "/Picturetemp/");
            if (!dir.exists())
                dir.mkdir();
            File file = new File(Environment.getExternalStorageDirectory() + "/Picturetemp/" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
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
    *description 判断图片地址是否有效
    *@author caiheng
    *created at 2018/2/9 15:06
    */
    public static boolean isImage(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=16;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if(bitmap!=null&&bitmap.getHeight()>0&&bitmap.getWidth()>0){
            bitmap.recycle();
            return true;
        }
        return false;
    }

    /**
    *description drawable转bitmip
    *@author caiheng
    *created at 2018/2/26 11:17
    */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
