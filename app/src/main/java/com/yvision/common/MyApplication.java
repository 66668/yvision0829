package com.yvision.common;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.yvision.model.VisitorBModel;
import com.yvision.utils.Utils;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

/**
 * 定义MyApplication/<application>需要修改权限:
 * android:name="com.yvision.tools.MyApplication"
 * 
 * @author JackSong
 *
 */
public class MyApplication extends Application {
	private static MyApplication instance;
	private Context currentContext;
	boolean isLogin = false;
	public Boolean checkBoxStatus = false;//记录checkBox
	private final static String sdcardDirName = "YUEVISION";
	public VisitorBModel visitorBModel = null;
	public int timespan = 0;//默认获取访客记录是全部数据
	public String iMinTime = "";
	public String iMaxTime = "";

	public MyApplication() {
		super();
	}
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		currentContext = this.getApplicationContext();

		// 极光推送 SDK初始化
		JPushInterface.setDebugMode(true);//设置打印日志，测试用
		JPushInterface.init(this);
		Log.d("JPush", "rid=" + JPushInterface.getRegistrationID(getApplicationContext()));

		//图片缓存初始化设置
		initImageLoader(this);
	}

	private void initImageLoader(Context context) {

		File cacheDir = new File(getPicCachePath(context));

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCache(new UnlimitedDiscCache(cacheDir))
				.build();
		ImageLoader.getInstance().init(config);
	}
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static String getAppFilesPath() {

		return instance.getFilesDir().getAbsolutePath();
	}

	// get方法
	public static MyApplication getInstance() {
		return instance;
	}

	public Context getCurrentContext() {
		return currentContext;
	}

	// 处理图片路径
	public static String getHandledUserPhotoPath(Context context) {
		//图片路径：1）YUEVISION/tempPics/uploadTemp/handled.jpg
		return getUploadPicPath(context) + File.separator + "handled.jpg";
	}

	// 未处理图片路径
	// CreateUserActivity--UpdateAvatar--MyApplication该方法：拍照图片选择
	public static String getUnhandledUserPhotoPath(Context context) {
		// （1）YUEVISION/tempPics/uploadTemp/unhandled.jpg
		String path = getUploadPicPath(context) + File.separator + "unhandled.jpg";
		return path;
	}

	// 图片上传目录
	public static String getUploadPicPath(Context context) {
		// (2)YUEVISION/tempPics/uploadTemp
		String uploadPath = getPicCachePath(context) + File.separator + "uploadTemp";
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		return uploadPath;
	}

	// 获取图片缓存目录
	// LoginActivity--MyApplication该方法
	public static String getPicCachePath(Context context) {
		// (3)YUEVISION/tempPics
		String cachePicPath = getBaseDir(context) + File.separator + "tempPics";
		File cachePath = new File(cachePicPath);
		if (!cachePath.exists()) {
			cachePath.mkdir();
		}
		return cachePicPath;
	}

	// 图片上传目录
	public static String getBaseDir(Context context) {
		// (4)获取sd卡根路径
		String sdcard_base_path = null;
		long availableSDCardSpace = Utils.getExternalStorageSpace();// 获取SD卡可用空间
		if (availableSDCardSpace != -1L) {// 如果存在SD卡/-1L:没有SD卡
			// sd/YUEVISION
			sdcard_base_path = Environment.getExternalStorageDirectory() + File.separator + sdcardDirName;//YUEVISION
		} else if (Utils.getInternalStorageSpace() != -1L) {//有内存空间
			// YUEVISION
			sdcard_base_path = context.getFilesDir().getPath() + File.separator + sdcardDirName;
		} else {// sd卡不存在
			// 没有可写入位置
		}
		if (sdcard_base_path != null) {
			// 初始化根目录
			File basePath = new File(sdcard_base_path);
			if (!basePath.exists()) {
				basePath.mkdir();
			}
		}
		return sdcard_base_path;
	}

	// MainVisitorActivity--MyApplication该方法，判断是否登录
	public boolean isLogin() {
		return isLogin;
	}

	// 登录成功赋值 true LoginActivity---MyApplication该方法
	public void setIsLogin(boolean b) {
		isLogin = b;
	}

	public void setCheckBoxStatus(Boolean status){
		this.checkBoxStatus = status;
	}

	//访客数据的（全部/今天）标记
	public void setTimespan(int timespan) {
		this.timespan = timespan;
	}

	//加载数据的标记
	public void setiMaxTime(String iMaxTime) {
		this.iMaxTime = iMaxTime;
	}
	public void setiMinTime(String iMinTime) {
		this.iMinTime = iMinTime;
	}
}
