package com.yvision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yvision.inject.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class LoginByFaceActivity extends BaseActivity implements Callback, PreviewCallback {
	// 返回
	@ViewInject(id = R.id.imgBack, click = "forBack")
	RelativeLayout imgBack;

	// 公司编号
	@ViewInject(id = R.id.et_face_companyNum)
	EditText et_face_companyNum;

	// 工号
	@ViewInject(id = R.id.et_face_employeeNum)
	EditText et_face_employeeNum;

	// 变量
	// 视图类，需要实现Callback接口。
	// 步骤（1）继承SurfaceView并实现SurfaceHolder.Callback接口，SurfaceHolder.Callback是用来预览摄像头视频
	private SurfaceView surfaceView = null;
	private SurfaceHolder holder = null;
	private Camera camera = null;
	int screenWidth, screenHeight;
	public FaceTask mFaceTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_login_face);
		// 获取传值
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		et_face_companyNum.setText(bundle.getString("storeId"));
		et_face_employeeNum.setText(bundle.getString("workId"));

		findById();
		holder = surfaceView.getHolder();// 步骤（2）SurfaceView.getHolder()获得SurfaceHolder对象
		holder.addCallback(this);// 步骤（3）SurfaceHolder.addCallback(callback)添加回调函数
		// 设置屏幕常亮
		surfaceView.setKeepScreenOn(true);
	}

	private void findById() {
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
	}

	public void forBack(View view) {
		// 传递数据

		startActivity(LoginActivity.class);
		releaseCamera();// 释放资源
		this.finish();
	}

	public void releaseCamera() {
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	/**
	 * PreviewCallback接口方法 视频流的处理：实时获取图片，并将图片上传
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		camera.setPreviewCallback(null);

		if (null != mFaceTask) {
			switch (mFaceTask.getStatus()) {
			case RUNNING:
				return;
			case PENDING:
				mFaceTask.cancel(false);
				break;
			default:
				break;
			}
		}
		// 是否会一直创建对象？？
		// mFaceTask = new FaceTask(data);
		// mFaceTask.execute();
		// 做延迟操作(重要)
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 通过该方法不断调用onPreviewFrame方法
		LoginByFaceActivity.this.camera.setPreviewCallback(LoginByFaceActivity.this);
	}

	/**
	 * 自定义的FaceTask类，开启一个线程 上传图片 人脸识别
	 */
	private class FaceTask extends AsyncTask<String, Void, String> {
		private byte[] mData;

		// 构造获值
		FaceTask(byte[] data) {
			this.mData = data;
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Size size = camera.getParameters().getPreviewSize(); // 获取预览大小
			screenWidth = size.width; // 宽度
			screenHeight = size.height;
			// 调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
			final YuvImage image = new YuvImage(mData, ImageFormat.NV21, screenWidth, screenHeight, null);
			ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
			if (!image.compressToJpeg(new Rect(0, 0, screenWidth, screenHeight), 100, os)) {
				return null;
			}
			byte[] tmp = os.toByteArray();
			Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
			// doSomethingNeeded(bmp); //自己定义的实时分析预览帧视频的算法
			return null;
		}

		// 02
		@Override
		protected void onPostExecute(String result) {
			if (result == null || result.equals("")) {
				Toast.makeText(LoginByFaceActivity.this, "人脸识别失败！", Toast.LENGTH_SHORT).show();
				// 失败处理
			} else {
				// 对获取数据处理
				try {
					JSONObject j = new JSONObject(result);
					JSONArray js = new JSONArray(j.getString("groupObjs"));
					if (js.length() > 0) {
						JSONObject jj = js.getJSONObject(0);
						// 将获取的数值显示
						// strGroupID = jj.getString("groupID");
						// Toast.makeText(LoginByFaceActivity.this,"已连接数据库:"+
						// jj.getString("groupID"), Toast.LENGTH_SHORT).show();
					} else {
						// Toast.makeText(LoginByFaceActivity.this,
						// "服务器出现问题，请检查服务器设置", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Callback接口实现方法(1)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	/**
	 * Callback接口实现方法(2)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// 设置显示
		try {
			camera.setPreviewDisplay(holder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 根据获得的degree设置旋转角度
		camera.setDisplayOrientation(90);
		// 画面预览
		camera.startPreview();
		// 通过该方法不断调用onPreviewFrame方法
		camera.setPreviewCallback((PreviewCallback) this);
	}

	/**
	 * Callback接口实现方法(3)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 打开前置相机 参数为1/后置默认为0
		camera = Camera.open(1);
		// 画面预览
		camera.startPreview();
		// 通过该方法不断调用onPreviewFrame方法
		camera.setPreviewCallback(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (camera != null) {
			releaseCamera();
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
