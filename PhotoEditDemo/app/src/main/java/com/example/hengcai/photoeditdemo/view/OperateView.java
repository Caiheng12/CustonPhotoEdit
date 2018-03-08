/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.hengcai.photoeditdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jarlen
 */
public class OperateView extends View {
	private List<ImageObject> imgLists = new ArrayList<ImageObject>();
	private Rect mCanvasLimits;
	private Bitmap bgBmp;
	private Paint paint = new Paint();
//	private Context mContext;
	private boolean isMultiAdd;// true 代表可以添加多个水印图片（或文字），false 代表只可添加单个水印图片（或文字）
	private float picScale = 0.4f;
	private ItemClickListener myListener;
	private int starX=0;
	private int starY=0;
	private int endX=0;
	private int endY=0;
	boolean isSelect=false;
	private EraserBean eraserBean;
	private boolean isEraserEnable = false;
	private EraserBean.Line cacheLine;
	private float preX;
	private float preY;
	private Path cachePath = new Path();
	private Paint cachePaint = new Paint(Paint.DITHER_FLAG);
    private EraserNumChangeListener eraserNumChangeListener;
	/**
	 * 设置水印图片初始化大小
	 * @param picScale
	 */
	public void setPicScale(float picScale)
	{
		this.picScale = picScale;
	}
	/**
	 * 设置是否可以添加多个图片或者文字对象
	 * 
	 * @param isMultiAdd
	 *            true 代表可以添加多个水印图片（或文字），false 代表只可添加单个水印图片（或文字）
	 */
	public void setMultiAdd(boolean isMultiAdd)
	{
		this.isMultiAdd = isMultiAdd;
	}
	public OperateView(Context context, Bitmap resizeBmp) {
		super(context);
		if(resizeBmp==null){
			return;
		}
		cachePaint.setAntiAlias(true);
		cachePaint.setDither(true);
		cachePaint.setColor(Color.WHITE);
		cachePaint.setStyle(Paint.Style.STROKE);
		cachePaint.setStrokeJoin(Paint.Join.ROUND);
		cachePaint.setStrokeCap(Paint.Cap.ROUND);
		cachePaint.setStrokeWidth(20);
		eraserBean=new EraserBean();
		bgBmp = resizeBmp;
		int width = bgBmp.getWidth();
		int height = bgBmp.getHeight();
		mCanvasLimits = new Rect(0, 0, width, height);
	}

	public void setBgBmp(Bitmap resizeBmp){
		bgBmp = resizeBmp;
	}

	/**
	 * 将图片对象添加到View中
	 * 
	 * @param imgObj
	 *            图片对象
	 */
	public void addItem(ImageObject imgObj)
	{
		if (imgObj == null)
		{
			return;
		}
		if (!isMultiAdd && imgLists != null){
			imgLists.clear();
		}
		imgObj.setSelected(true);
		if (!imgObj.isTextObject) {
			imgObj.setScale(picScale);
		}
		ImageObject tempImgObj = null;
		for (int i = 0; i < imgLists.size(); i++) {
			tempImgObj = imgLists.get(i);
			tempImgObj.setSelected(false);
		}
		imgLists.add(imgObj);
		selectChangeListener.isChange();
		invalidate();
	}

	/**
	*description 设置橡皮擦可用状态
	*@author caiheng
	*created at 2018/2/27 13:38
	*/
	public void setEraserEnable(){
		isEraserEnable=true;
		if(eraserNumChangeListener!=null){
			eraserNumChangeListener.eraserNumChange(eraserBean.lines.size());
		}
	}

	/**
	 *description 设置橡皮擦可用状态
	 *@author caiheng
	 *created at 2018/2/27 13:38
	 */
	public void setEraserUnenable(){
		isEraserEnable=false;
	}

	/**
	*description 获取当前选中的条目
	*@author caiheng
	*created at 2018/1/19 17:49
	*/
	public TextObject getCurrentSelectedItem(){
		if(imgLists==null||imgLists.size()==0){
			return null;
		}
		for (int i = 0; i < imgLists.size(); i++) {
			if(imgLists.get(i).isSelected()){
				if(imgLists.get(i) instanceof TextObject){
					return ((TextObject)imgLists.get(i));
				}
			}
		}
		return null;
	}
	/**
	*description 重置白块的状态
	*@author caiheng
	*created at 2018/2/27 14:17
	*/
	public void resetBlockState(){
		if(imgLists==null||imgLists.size()==0){
			return ;
		}
		for (int i = 0; i < imgLists.size(); i++) {
			if(imgLists.get(i).isSelected()){
				imgLists.get(i).setSelected(false);
			}
		}
		invalidate();
	}
	/**
	 * 画出容器内所有的图像
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int sc = canvas.save();
		canvas.clipRect(mCanvasLimits);
		canvas.drawBitmap(bgBmp, 0, 0, paint);
		drawEraser(canvas);
		drawImages(canvas);
		canvas.restoreToCount(sc);
		for (ImageObject ad : imgLists) {
			if (ad != null && ad.isSelected()) {
				ad.drawIcon(canvas);
			}
		}
	}

	/**
	*description 撤销橡皮擦
	*@author caiheng
	*created at 2018/2/27 13:29
	*/
	public void revokeEraser() {
		if(eraserBean==null||eraserBean.lines==null||eraserBean.lines.size()==0){
           return;
		}
		eraserBean.lines.removeLast();
		if(eraserNumChangeListener!=null){
			eraserNumChangeListener.eraserNumChange(eraserBean.lines.size());
		}
		invalidate();
	}

	/**
	*description 绘制橡皮擦
	*@author caiheng
	*created at 2018/2/27 11:44
	*/
	private void drawEraser(Canvas canvas) {
		 float preX = 0;
		 float preY =0;
		if(eraserBean==null){
			return;
		}
		if(eraserBean.lines!=null&&eraserBean.lines.size()>0){
			for(int i=0;i<eraserBean.lines.size();i++){
				if(eraserBean.lines.get(i).points!=null&&eraserBean.lines.get(i).points.size()>0){
					cachePath.reset();
					for(int j=0;j<eraserBean.lines.get(i).points.size();j++){
						if(j==0){
							preX=eraserBean.lines.get(i).points.get(j).x;
							preY=eraserBean.lines.get(i).points.get(j).y;
							cachePath.moveTo(eraserBean.lines.get(i).points.get(j).x, eraserBean.lines.get(i).points.get(j).y);
						}
						cachePath.quadTo(preX, preY, (eraserBean.lines.get(i).points.get(j).x + preX) / 2, (eraserBean.lines.get(i).points.get(j).y + preY) / 2);
						preX=eraserBean.lines.get(i).points.get(j).x;
						preY=eraserBean.lines.get(i).points.get(j).y;
					}
					canvas.drawPath(cachePath, cachePaint);
				}
			}
		}
	}

	public void save()
	{
		ImageObject io = getSelected();
		if (io != null)
		{
			io.setSelected(false);
		}
		selectChangeListener.isChange();
		invalidate();
	}

	/**
	 * 根据触控点重绘View
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isEraserEnable){
			if (event.getPointerCount() == 1) {
				float x = ((int) (event.getX() * 10)) * 0.1f;
				float y = ((int) (event.getY() * 10)) * 0.1f;
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						touchDown(x, y);
						break;
					case MotionEvent.ACTION_MOVE:
						touchMove(x, y);
						break;
					case MotionEvent.ACTION_UP:
						touchUp(x, y);
						break;
				}
				invalidate();
			}
		}else {
			if (event.getPointerCount() == 1) {
				handleSingleTouchManipulateEvent(event);
				invalidate();
			} else {
				//handleMultiTouchManipulateEvent(event);
			}
		}
		super.onTouchEvent(event);
		return true;
	}

	private void touchDown(float x, float y) {
		preX = x;
		preY = y;
		cacheLine = new EraserBean.Line();
		if (eraserBean != null) {
			if (eraserBean.lines != null) {
				eraserBean.lines.add(cacheLine);
			}
		}
		EraserBean.Point point = new EraserBean.Point();
		point.x = x;
		point.y = y;
		cacheLine.points.add(point);
	}

	private void touchMove(float x, float y) {
		if (Math.abs(x - preX) > 0 || Math.abs(y - preY) > 0) {
			preX = x;
			preY = y;
			EraserBean.Point point = new EraserBean.Point();
			point.x = x;
			point.y = y;
			cacheLine.points.add(point);
		}
	}

	private void touchUp(float x, float y) {
		EraserBean.Point point = new EraserBean.Point();
		point.x = x;
		point.y = y;
		cacheLine.points.add(point);
		if(cacheLine.points.size()<3){
			//三个点才能画出一条线
			eraserBean.lines.removeLast();
		}
		if(eraserNumChangeListener!=null){
			eraserNumChangeListener.eraserNumChange(eraserBean.lines.size());
		}
	}

	private boolean mMovedSinceDown = false;
	private boolean mResizeAndRotateSinceDown = false;
	private float mStartDistance = 0.0f;
	private float mStartScale = 0.0f;
	private float mStartRot = 0.0f;
	private float mPrevRot = 0.0f;
	static public final double ROTATION_STEP = 2.0;
	static public final double ZOOM_STEP = 0.01;
	static public final float CANVAS_SCALE_MIN = 0.25f;
	static public final float CANVAS_SCALE_MAX = 3.0f;
	private Point mPreviousPos = new Point(0, 0); // single touch events
	float diff;
	float rot;

	/**
	 * 多点触控操作
	 * 
	 * @param event
	 */
	private void handleMultiTouchManipulateEvent(MotionEvent event)
	{
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_POINTER_UP :
				break;
			case MotionEvent.ACTION_POINTER_DOWN :
				float x1 = event.getX(0);
				float x2 = event.getX(1);
				float y1 = event.getY(0);
				float y2 = event.getY(1);
				float delX = (x2 - x1);
				float delY = (y2 - y1);
				diff = (float) Math.sqrt((delX * delX + delY * delY));
				mStartDistance = diff;
				// float q = (delX / delY);
				mPrevRot = (float) Math.toDegrees(Math.atan2(delX, delY));
				for (ImageObject io : imgLists) {
					if (io.isSelected()) {
						mStartScale = io.getScale();
						mStartRot = io.getRotation();
						break;
					}
				}
				break;

			case MotionEvent.ACTION_MOVE :
				x1 = event.getX(0);
				x2 = event.getX(1);
				y1 = event.getY(0);
				y2 = event.getY(1);
				delX = (x2 - x1);
				delY = (y2 - y1);
				diff = (float) Math.sqrt((delX * delX + delY * delY));
				float scale = diff / mStartDistance;
				float newscale = mStartScale * scale;
				rot = (float) Math.toDegrees(Math.atan2(delX, delY));
				float rotdiff = mPrevRot - rot;
				for (ImageObject io : imgLists) {
					if (io.isSelected() && newscale < 10.0f && newscale > 0.1f) {
						float newrot = Math.round((mStartRot + rotdiff) / 1.0f);
						if (Math.abs((newscale - io.getScale()) * ROTATION_STEP) > Math
								.abs(newrot - io.getRotation())) {
							io.setScale(newscale);
						} else {
							io.setRotation(newrot % 360);
						}
						break;
					}
				}
				break;
		}
	}
	/**
	 * 获取选中的对象ImageObject
	 * 
	 * @return
	 */
	private ImageObject getSelected()
	{
		for (ImageObject ibj : imgLists)
		{
			if (ibj.isSelected())
			{
				return ibj;
			}
		}
		return null;
	}

	/**
	 * 单点触控操作
	 * 
	 * @param event
	 */
	private void handleSingleTouchManipulateEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				isSelect=false;
				starX=0;
				starY=0;
				starX= (int) event.getX();
				starY= (int) event.getY();
				mMovedSinceDown = false;
				mResizeAndRotateSinceDown = false;
				int selectedId = -1;
				for (int i = imgLists.size() - 1; i >= 0; --i) {
					ImageObject io = imgLists.get(i);
					if (io.contains(event.getX(), event.getY()) || io.pointOnCorner(event.getX(), event.getY(),
							OperateConstants.RIGHTBOTTOM) || io.pointOnCorner(event.getX(), event.getY(), OperateConstants.LEFTTOP)) {
						isSelect=io.isSelected();
						if(io.isSelected()){
						//	myListener.onClick();
						}
						io.setSelected(true);
						selectChangeListener.isChange();
						imgLists.remove(i);
						imgLists.add(io);
						selectedId = imgLists.size() - 1;
						break;
					}
				}
				if (selectedId < 0) {
					for (int i = imgLists.size() - 1; i >= 0; --i) {
						ImageObject io = imgLists.get(i);
						if (io.contains(event.getX(), event.getY())
								|| io.pointOnCorner(event.getX(), event.getY(), OperateConstants.RIGHTBOTTOM)
								|| io.pointOnCorner(event.getX(), event.getY(), OperateConstants.LEFTTOP)) {
							io.setSelected(true);
							selectChangeListener.isChange();
							imgLists.remove(i);
							imgLists.add(io);
							selectedId = imgLists.size() - 1;
							break;
						}
					}
				}
				for (int i = 0; i < imgLists.size(); ++i) {
					ImageObject io = imgLists.get(i);
					if (i != selectedId) {
						io.setSelected(false);
						selectChangeListener.isChange();
					}
				}
				ImageObject io = getSelected();
				if (io != null) {
					if (io.pointOnCorner(event.getX(), event.getY(),
							OperateConstants.LEFTTOP)) {
						//imgLists.remove(io);
					} else if (io.pointOnCorner(event.getX(), event.getY(),
							OperateConstants.RIGHTBOTTOM)) {
						mResizeAndRotateSinceDown = true;
						float x = event.getX();
						float y = event.getY();
						float delX = x - io.getPoint().x;
						float delY = y - io.getPoint().y;
						diff = (float) Math.sqrt((delX * delX + delY * delY));
						mStartDistance = diff;
						mPrevRot = (float) Math.toDegrees(Math
								.atan2(delX, delY));
						mStartScale = io.getScale();
						mStartRot = io.getRotation();
					} else if (io.contains(event.getX(), event.getY())) {
						mMovedSinceDown = true;
						mPreviousPos.x = (int) event.getX();
						mPreviousPos.y = (int) event.getY();
					}
				}
				break;

			case MotionEvent.ACTION_UP :
				endX=0;
				endY=0;
				endX= (int) event.getX();
				endY= (int) event.getY();
				mMovedSinceDown = false;
				mResizeAndRotateSinceDown = false;
				if(isSelect){
					if(Math.abs(endX-starX)<3&&Math.abs(endY-starY)<3){
						myListener.onClick();
					}
				}
				break;
			case MotionEvent.ACTION_MOVE :
				// Log.i("jarlen"," 移动了");
				// 移动
				int curX = (int) event.getX();
				int curY = (int) event.getY();
				int diffX = curX - mPreviousPos.x;
				int diffY = curY - mPreviousPos.y;
				mPreviousPos.x = curX;
				mPreviousPos.y = curY;
				if (mMovedSinceDown) {
					io = getSelected();
					Point p = io.getPosition();
					int x = p.x + diffX;
					int y = p.y + diffY;
					if (p.x + diffX >= mCanvasLimits.left
							&& p.x + diffX <= mCanvasLimits.right
							&& p.y + diffY >= mCanvasLimits.top
							&& p.y + diffY <= mCanvasLimits.bottom)
						io.moveBy((int) (diffX), (int) (diffY));
				}
				// 旋转和缩放
				if (mResizeAndRotateSinceDown) {
					io = getSelected();
					float x = event.getX();
					float y = event.getY();
					float delX = x - io.getPoint().x;
					float delY = y - io.getPoint().y;
					diff = (float) Math.sqrt((delX * delX + delY * delY));
					float scale = diff / mStartDistance;
					float newscale = mStartScale * scale;
					rot = (float) Math.toDegrees(Math.atan2(delX, delY));
					float rotdiff = mPrevRot - rot;
					if (newscale < 10.0f && newscale > 0.1f)
					{
						float newrot = Math.round((mStartRot) / 1.0f);
						if (Math.abs((newscale - io.getScale()) * ROTATION_STEP) > Math
								.abs(newrot - io.getRotation()))
						{
						   io.setScale(newscale);
						} else
						{
							io.setRotation(newrot % 360);
						}
					}
				}
				break;
		}
		cancelLongPress();
	}

	/**
	*description 删除当前选中的文字
	*@author caiheng
	*created at 2018/1/21 10:45
	*/
	public void removeCurrentText() {
       if(imgLists==null&&imgLists.size()==0){
		   return;
	   }
	   for(int i=0;i<imgLists.size();i++){
		   if(imgLists.get(i).isSelected()){
			   imgLists.remove(i);
		   }
	   }
	   invalidate();
	}

	/**
	 * 循环画图像
	 * 
	 * @param canvas
	 */
	private void drawImages(Canvas canvas)
	{
		for (ImageObject ad : imgLists)
		{
			if (ad != null) {
				ad.draw(canvas,ad.isSelected());
			}
		}
	}

	/**
	 * 向外部提供双击监听事件（双击弹出自定义对话框编辑文字）
	 */
	SelectChangeListener selectChangeListener;

	public void setSelectChangeListener(SelectChangeListener myListener) {
		this.selectChangeListener = myListener;
	}

	public interface SelectChangeListener {
		 void isChange();
	}

	/**
	*description 点击已选中的白块
	*@author caiheng
	*created at 2018/2/7 18:52
	*/
	public void setItemClickListener(ItemClickListener myListener)
	{
		this.myListener = myListener;
	}

	public interface ItemClickListener {
		void onClick();
	}

	/**
	*description 橡皮擦数量监听
	*@author caiheng
	*created at 2018/2/27 14:07
	*/
	public interface EraserNumChangeListener {
		void eraserNumChange(int num);
	}

	public void setEraserNumChangeListener(EraserNumChangeListener listener) {
		eraserNumChangeListener = listener;
	}

}
