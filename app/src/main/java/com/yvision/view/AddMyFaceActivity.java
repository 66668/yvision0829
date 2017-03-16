package com.yvision.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yvision.R;
import com.yvision.inject.ViewInject;
import com.yvision.common.ImageLoadingConfig;
import com.yvision.utils.CameraGalleryUtils;
import com.yvision.utils.CameraGalleryUtils.ChoosePicCallBack;

import java.io.File;
import java.io.IOException;

/**
 * 人脸登记
 * 
 * @author JackSong
 *
 */

public class AddMyFaceActivity extends BaseActivity implements ChoosePicCallBack {
	// back
	@ViewInject(id = R.id.imgBack, click = "forBack")
	ImageView imgBack;

	// 标题
	@ViewInject(id = R.id.txtTitle)
	TextView textTitle;

	// 大图
	@ViewInject(id = R.id.imgSwitcher, longClick = "addImageView")
	ImageView imgSwitcher;

	// 小图
	@ViewInject(id = R.id.gallery)
	Gallery gallery;

	//
	@ViewInject(id = R.id.btn_addMyFace, click = "addMyFace")
	Button btn_addMyFace;

	Button btn;
	// 添加照片

	private CameraGalleryUtils cameraGalleryUtils;// 头像上传工具
	// 外部jar包：universal-image-loader-1.9.2.jar/异步加载图片
	private DisplayImageOptions imgOption;
	private ImageLoader imgLoader;
	private String avatarBase64 = "";
	private File myPicPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_setting_adduserface);

		// 图片处理
		cameraGalleryUtils = new CameraGalleryUtils(this, this);// 实例化
		// 全局初始化外部包：个推推送消息使用
		imgLoader = ImageLoader.getInstance();// 实例化
		imgLoader.init(ImageLoaderConfiguration.createDefault(this));
		// 异步加载图片
		imgOption = ImageLoadingConfig.generateDisplayImageOptionsNoCatchDisc(R.mipmap.ic_launcher);
	}

	// back
	public void forBack(View view) {
		this.finish();
	}

	public void addMyFace(View view) {
		cameraGalleryUtils.showChoosePhotoDialog(CameraGalleryUtils.IMG_TYPE_CAMERA);// -99,处理弹窗调用
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		cameraGalleryUtils.onActivityResultAction(requestCode, resultCode, data);
	}

	// 图片展示
	// 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（1）
	@Override
	public void updateAvatarSuccess(int updateType, String picPath, String avatarBase64) {
		imgSwitcher.setImageResource(R.mipmap.ic_launcher);// imageView添加图片
		// 获取图片路径
		this.myPicPath = new File(picPath);
		if (!this.myPicPath.exists()) {
			try {
				this.myPicPath.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (picPath.toLowerCase().startsWith("http")) {// 将字符参数改写成小写的方式
			// 外部jar包方法
			ImageLoader.getInstance().displayImage(picPath, // 已处理图片地址
					imgSwitcher, // imageView
					imgOption);// jar包类
		} else {
			ImageLoader.getInstance().displayImage("file://" + picPath, imgSwitcher, // imageView
					imgOption);// jar包类
		}
		this.avatarBase64 = avatarBase64;
	}

	@Override
	// 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（2）
	public void updateAvatarFailed(int updateType) {
		Toast.makeText(this, "头像上传失败", Toast.LENGTH_SHORT).show();// 头像上传失败
	}

	@Override
	// 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（3）
	public void cancel() {

	}
}
