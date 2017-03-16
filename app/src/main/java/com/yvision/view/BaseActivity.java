package com.yvision.view;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.yvision.helper.ActivityInjectHelper;
import com.yvision.helper.InjectHelper;
import com.yvision.receiver.ExitAppReceiver;
import com.yvision.utils.PageUtil;

/**
 * 详细说明： InjectView:
 * 
 * 引包：android-support-v4.jar--FragmentActivity
 * 
 * @author JackSong
 *
 */
public class BaseActivity extends FragmentActivity {
	// 关闭程序的类
	private ExitAppReceiver exitAppReceiver = new ExitAppReceiver();
	// 对应的Action
	protected static final String EXIT_APP_ACTION = Intent.ACTION_CLOSE_SYSTEM_DIALOGS;//某一个包名也可？

	// 状态栏通知的管理类
	protected NotificationManager notificationManager;
	// 常量 消息处理
	public static final int MESSAGE_TOAST = 1001;
	public static final int MESSAGE_CLOSE = 1002;
	/**
	 * SDK服务是否启动
	 */
	private static final int REQUEST_PERMISSION = 0;
	// （1）setContentView方法重载
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		initView();// 获取反射
	}

	// (2)
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		initView();
	}

	// (3)
	public void setContentView(View view) {
		super.setContentView(view);
		initView();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 通过 getSystemService()方法来获取管理类
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		registerExitRecevier();// 先注册广播
	}

	//注册 退出功能 广播
	private void registerExitRecevier() {
		IntentFilter exitFilter = new IntentFilter();
		exitFilter.addAction(EXIT_APP_ACTION);
		this.registerReceiver(exitAppReceiver, exitFilter);
	}
	//onDestroy调用
	private void unRegisterExitReceiver(){
		this.unregisterReceiver(exitAppReceiver);//取消注册
	}
	/**
	 * Activity
	 * 
	 * @param newClass
	 */
	// (1)页面跳转重载
	public void startActivity(Class<?> newClass) {
		Intent intent = new Intent(this, newClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	// (2)
	public void startActivity(Class<?> newClass, Bundle extras) {
		Intent intent = new Intent(this, newClass);
		intent.putExtras(extras);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/**
	 * handler sendMessage的处理
	 */
	@SuppressLint("HandlerLeak") // 确保类内部的handler不含有对外部类的隐式引用
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 调用下边的方法处理信息
			BaseActivity.this.handleMessage(msg);
		}
	};

	protected void handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_TOAST:// 1001
			if (msg.obj != null && msg.obj.toString().trim().length() > 0) {
				PageUtil.DisplayToast(msg.obj.toString());
			}
			break;
		case MESSAGE_CLOSE:// 1002
			this.finish();
			break;
		}
	}

	/**
	 * sendMessage的重载
	 */
	// 01
	protected void sendMessage(Message msg) {
		handler.sendMessage(msg);
	}

	// 02
	protected void sendMessage(int what) {
		handler.sendEmptyMessage(what);
	}

	// 03
	public void sendMessage(int what, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, obj));
	}

	// 04
	public void sendToastMessage(int resId) {
		Message msg = new Message();
		msg.what = MESSAGE_TOAST;// 1001
		msg.obj = getString(resId);
		handler.sendMessage(msg);
	}

	// 05
	public void sendToastMessage(String result) {//result = e.getMessage()
		Message msg = new Message();
		msg.what = MESSAGE_TOAST;//标识
		msg.obj = result;
		handler.sendMessage(msg);
	}

	// 06
	public void sendCloseMessage() {
		handler.sendEmptyMessage(MESSAGE_CLOSE);
	}

	/**
	 * InjectHelper.initView setContentView方法调用
	 */
	void initView() {
		InjectHelper helper = new ActivityInjectHelper(this);
		helper.initView();
	}

	// 枚举
	public enum Method {
		Click, LongClick, ItemClick, ItemLongClick
	}

	// 生命周期函数
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//退出程序的广播
		unRegisterExitReceiver();
	}
}
