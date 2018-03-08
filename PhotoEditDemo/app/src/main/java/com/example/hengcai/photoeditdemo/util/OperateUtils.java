
package com.example.hengcai.photoeditdemo.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.View;
import com.example.hengcai.photoeditdemo.R;
import com.example.hengcai.photoeditdemo.view.TextObject;

/**
 * 添加文字图片工具类
 */
public class OperateUtils
{
	private Activity activity;
	private int screenWidth;// 手机屏幕的宽（像素）
	private int screenHeight;// 手机屏幕的高（像素）

	public static final int LEFTTOP = 1;
	public static final int RIGHTTOP = 2;
	public static final int LEFTBOTTOM = 3;
	public static final int RIGHTBOTTOM = 4;
	public static final int CENTER = 5;

	public OperateUtils(Activity activity)
	{
		this.activity = activity;
		if (screenWidth == 0)
		{
			DisplayMetrics metric = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
			screenWidth = metric.widthPixels; // 屏幕宽度（像素）
			screenHeight = metric.widthPixels; // 屏幕宽度（像素）
		}
	}

	/**
	 * 根据路径获取图片并且压缩，适应view
	 *
	 * @param filePath
	 *            图片路径
	 * @param contentView
	 *            适应的view
	 * @return Bitmap 压缩后的图片
	 */
	public Bitmap compressionFiller(String filePath, View contentView)
	{
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
		if (scale != 0)
		{
			int bitmapheight = bitmap.getHeight();
			int bitmapwidth = bitmap.getWidth();
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale); // 长和宽放大缩小的比例
			resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapwidth,
					bitmapheight, matrix, true);
		} else
		{
			resizeBmp = bitmap;
		}
		return resizeBmp;
	}

	/**
	 * 根据压缩图片并且适应view
	 *
	 * @param bitmap
	 *            压缩图片
	 * @param contentView
	 *            适应的view
	 * @return 压缩后的图片
	 */
	public Bitmap compressionFiller(Bitmap bitmap, View contentView)
	{
		int layoutHeight = contentView.getHeight();
		float scale = 0f;
		int bitmapHeight = bitmap.getHeight();
		int bitmapWidth = bitmap.getWidth();
		scale = bitmapHeight > bitmapWidth
				? layoutHeight / (bitmapHeight * 1f)
				: screenWidth / (bitmapWidth * 1f);
		Bitmap resizeBmp;
		if (scale != 0)
		{
			int bitmapheight = bitmap.getHeight();
			int bitmapwidth = bitmap.getWidth();
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale); // 长和宽放大缩小的比例
			resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapwidth,
					bitmapheight, matrix, true);
		} else
		{
			resizeBmp = bitmap;
		}
		return resizeBmp;
	}

	public TextObject getTextObject(){
		TextObject textObj = null;
		Bitmap rotateBm = BitmapFactory.decodeResource(activity.getResources(),
				R.mipmap.commonbus_bule_point);
		Bitmap deleteBm = BitmapFactory.decodeResource(activity.getResources(),
				R.mipmap.commonbus_del);
		textObj = new TextObject(activity,"", 67, 16, rotateBm, deleteBm);
		textObj.setTextObject(true);
		return textObj;
	}

}
