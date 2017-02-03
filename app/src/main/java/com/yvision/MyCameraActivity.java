package com.yvision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.faceplusplus.api.FaceDetecter;
import com.faceplusplus.api.FaceDetecter.Face;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yvision.common.MyApplication;
import com.yvision.inject.ViewInject;
import com.yvision.common.FaceMask;
import com.yvision.common.ImageLoadingConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * face++人脸框外部包：armeabi文件夹/armabi-v7a文件夹/faceppofflinesdk.jar/faceppsdk.jar
 * 图片异步加载外部包：universal-image-loader-1.9.2.jar
 * 
 * @author JackSong
 *
 */

public class MyCameraActivity extends BaseActivity implements Callback, PreviewCallback {
	// 拍照按钮
	@ViewInject(id = R.id.btn_takepic, click = "btnTakePicture")
	ImageView btnTakePicture;

	// back键
	@ViewInject(id = R.id.img_back, click = "imgBack")
	ImageView imgBack;

	// change键
	@ViewInject(id = R.id.img_change, click = "toChange")
	ImageView toChange;

	@ViewInject(id = R.id.surfaceView01)
	SurfaceView surfaceView;

	// 步骤（1）继承SurfaceView并实现SurfaceHolder.Callback接口，SurfaceHolder.Callback是用来预览摄像头视频
	private SurfaceHolder holder = null;
	private Camera myCamera = null;
	Display display;
	WindowManager windowManager;
	private int faceWidth;
	private int faceHeight;

	// 外部包：universal-image-loader-1.9.2.jar
	private ImageLoader imgLoader;
	@SuppressWarnings("unused")
	private DisplayImageOptions imgOption;
	// 人脸识别
	private Thread faceThread;
	FaceDetecter faceDetecter = null;
	FaceMask faceMask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题
		setContentView(R.layout.act_mycamera);
		faceMask = (FaceMask) findViewById(R.id.mask);
		// 外部包处理图片
		imgLoader = ImageLoader.getInstance();
		imgLoader.init(ImageLoaderConfiguration.createDefault(this));
		imgOption = ImageLoadingConfig.generateDisplayImageOptions(R.mipmap.ic_launcher);

		// DisplayMetrics metrics = getResources().getDisplayMetrics();// 初始化
		// faceWidth = metrics.widthPixels;// 图片的宽
		// faceHeight = metrics.heightPixels;// 图片的高
		// 人脸框宽高
		faceWidth = 640;
		faceHeight = 480;

		// 人脸识别 surfaceView/faceMask
		holder = surfaceView.getHolder();// 步骤（2）SurfaceView.getHolder()获得SurfaceHolder对象
		holder.addCallback(this);// 步骤（3）SurfaceHolder.addCallback(callback)添加回调函数
		// 设置屏幕常亮
		surfaceView.setKeepScreenOn(true);
		// 实例化外部jar包类，
		faceDetecter = new FaceDetecter();
		// 第二个参数为申请的API_KEY
		if (!faceDetecter.init(this, "a3a64d11b408e839a7c00b24d3bda705")) {
			sendToastMessage("调用外部包有错误 ");
			return;
		}
		faceDetecter.setTrackingMode(true);
	}

	/**
	 * 取消按钮
	 */
	public void imgBack(View view) {
		this.finish();
		releaseCamera();// 释放资源
	}

	/**
	 * 切换摄像头
	 *
	 */
	public void toChange(View view){

	}
	// 释放资源
	public void releaseCamera() {
		if (myCamera != null) {
			// 关闭线程
			myCamera.setPreviewCallback(null);
			// 停止预览
			myCamera.stopPreview();
			// 释放资源
			myCamera.release();
			myCamera = null;

		}
	}

	/**
	 * 拍照按钮
	 */
	public void btnTakePicture(View view) {
		// 设置拍照分辨率（重要）
		setPicturePix();
		//
		myCamera.takePicture(null, null, pictureCallback);
	}

	/**
	 * 拍照调用
	 */
	private PictureCallback pictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// 图片路径:YUEVISION(三种情况)/tempPics/uploadTemp/unhandled.jpg
			String path02 = MyApplication.getUnhandledUserPhotoPath(MyApplication.getInstance());// 注意context
			File newfile02 = new File(path02);// 未处理图片：unhandled.jpg
			try {
				newfile02.createNewFile();// 创建
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 文件流
			FileOutputStream out = null;
			byte[] newData = null;
			try {
				// 将图片保存到指定位置
				out = new FileOutputStream(newfile02);
				Bitmap bitmap01 = BitmapFactory.decodeByteArray(data, 0, data.length);
				// 竖屏，设置旋转
				Matrix matrix = new Matrix();
				matrix.setRotate(270);
				Bitmap newBitmap = Bitmap.createBitmap(bitmap01, 0, 0, bitmap01.getWidth(), bitmap01.getHeight(),
						matrix, true);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 图片压缩操作
				newData = bos.toByteArray();
				out.write(newData);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			/**
			 * 拍照结束后，跳转显示图片,需要图片路径
			 */
			Intent intent = new Intent();
			MyCameraActivity.this.setResult(RESULT_OK, intent);
			MyCameraActivity.this.finish();
		}
	};

	/**
	 * 人脸识别视频流（PreviewCallback实现方法）
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		camera.setPreviewCallback(null);
		faceThread = new MyThread(data);
		faceThread.start();
	}

	/**
	 * 人脸识别视频处理线程
	 */
	class MyThread extends Thread {
		byte[] data = null;

		public MyThread(byte[] data) {
			this.data = data;
		}

		@Override
		public void run() {
			super.run();
			// 480*640用途：在预览中可以看到，显示的人脸框是高大于宽的效果
			byte[] ori = new byte[faceWidth * faceHeight];// 共四处之三
			int is = 0;
			for (int x = faceWidth - 1; x >= 0; x--) {
				for (int y = faceHeight - 1; y >= 0; y--) {
					ori[is] = data[y * faceWidth + x];
					is++;
				}
			}
			// 调用人脸检测外部工具包
			final Face[] faceinfo = faceDetecter.findFaces(ori, faceHeight, faceWidth);// 共四处之四//faceHeight
			// faceWidth
			// 更新ui的两种方法之一（Activity.runOnUiThread(Runnable)和handler）
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// 调用java类中的方法，实现功能:人脸信息
					faceMask.setFaceInfo(faceinfo);
				}
			});
			// 通过该方法不断调用onPreviewFrame方法
			MyCameraActivity.this.myCamera.setPreviewCallback(MyCameraActivity.this);
		}
	}

	/**
	 * Callback 实现的三个方法
	 */
	//01
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}
	//02
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// 设置显示
		try {
			myCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 根据获得的degree设置旋转角度
		myCamera.setDisplayOrientation(90);
		// 画面预览
		myCamera.startPreview();
		// 通过该方法不断调用onPreviewFrame方法
		myCamera.setPreviewCallback(this);
	}
	//03
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	// 周期函数调用01
	@Override
	protected void onResume() {
		super.onResume();
		if (myCamera == null) {
			myCamera = Camera.open(1);// 打开前置相机 参数为1/后置默认为0
		} else {
			myCamera.release();
			myCamera = Camera.open(1);// 打开前置相机 参数为1/后置默认为0
		}

	}
	// 周期函数调用02
	// 退出界面后释放摄像头
	@Override
	protected void onPause() {
		super.onPause();
		if (faceThread != null) {
			faceThread.interrupt();
			faceThread = null;
		}
		if (myCamera != null) {
			// 释放资源
			faceDetecter.release(this);
			// 关闭线程
			myCamera.setPreviewCallback(null);
			// 停止预览
			myCamera.stopPreview();
			// 释放资源
			myCamera.release();
		}
	}
	// 周期函数调用03
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 释放资源
		faceDetecter.release(this);
		// 关闭线程
	}

	/**
	 * 设置拍照分辨率
	 * btnTakePicture中调用
	 */
	private void setPicturePix() {
		Parameters parameters = myCamera.getParameters();// 获取相机参数集
		// 设置预览照片的大小
		List<Size> SupportedPreviewSizes = parameters.getSupportedPreviewSizes();// 获取支持预览照片的尺寸
		int index = getPictureSize(SupportedPreviewSizes);
		int ww, hh;
		ww = SupportedPreviewSizes.get(index).width;
		hh = SupportedPreviewSizes.get(index).height;
		parameters.setPreviewSize(ww, hh);//

		// 图片设置
		// 获取照相机支持的分辨率列表
		List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();// 获取支持保存图片的尺寸
		int index02 = getPictureSize(supportedPictureSizes);
		int w, h;
		w = supportedPictureSizes.get(index02).width;
		h = supportedPictureSizes.get(index02).height;
		// 设置参数
		parameters.setPictureSize(w, h);
		myCamera.setParameters(parameters);
	}

	/**
	 * 获取拍照之后的尺寸
	 * setPicturePix中调用
	 *
	 * @param sizes
	 * @return
	 */
	private int getPictureSize(List<Size> sizes) {

		// 屏幕的宽度
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int index = -1;
		for (int i = 0; i < sizes.size(); i++) {
			if (Math.abs(screenWidth - sizes.get(i).width) == 0) {
				index = i;
				break;
			}
		}
		// 当未找到与手机分辨率相等的数值,取列表中间的分辨率
		if (index == -1) {
			index = sizes.size() / 2;
		}

		return index;
	}
}
