package com.yvision.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.faceplusplus.api.FaceDetecter.Face;
/**
 * face++ 外部包：
 * （1）armeabi文件夹下
 * （2）armeabi-v7a文件夹下
 * （3）faceppofflinesdl.jar
 * （4）faceppsdk.jar
 * 
 * @author JackSong
 *
 */
public class FaceMask extends View {
	Paint localPaint = null;
	// 外部工具包的数组类
	Face[] faceinfos = null;
	// 矩形类
	RectF rect = null;

	public FaceMask(Context context, AttributeSet atti) {
		super(context, atti);
		// 矩形类
		rect = new RectF();
		// 实例化绘图类
		localPaint = new Paint();
		// 设置画笔颜色 青色
		localPaint.setColor(0xff00b4ff);
		// 设置空心线宽
		localPaint.setStrokeWidth(5);
		// 设置样式-空心矩形心
		localPaint.setStyle(Paint.Style.STROKE);
	}

	// CameraPreview类中调用该方法，
	public void setFaceInfo(Face[] faceinfos) {
		this.faceinfos = faceinfos;
		// 请求View树进行重绘,调用该方法后会自动调用OnDraw方法
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// faceinfos的数据来自CameraPreview,判断faceifos
		if (faceinfos == null)
			return;
		for (Face localFaceInfo : faceinfos) {
			// 设置一个矩形的边界
			rect.set(getWidth() * localFaceInfo.left, // 矩形左上角X坐标值
					getHeight() * localFaceInfo.top, // 矩形左上角Y坐标值
					getWidth() * localFaceInfo.right, // 矩形右下角X坐标值
					getHeight() * localFaceInfo.bottom);// 矩形右下角Y坐标值
			// 绘制一个指定形状的图形，本例rect是个矩形
			canvas.drawRect(rect, localPaint);
		}
	}
}