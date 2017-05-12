package com.yvision.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yvision.R;
import com.yvision.common.ImageLoadingConfig;
import com.yvision.inject.ViewInject;
import com.yvision.model.EmployeeListModel;
import com.yvision.model.VisitorBModel;
import com.yvision.utils.CameraGalleryUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VisitorInfoReceiveActivity extends BaseActivity implements CameraGalleryUtils.ChoosePicCallBack {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right)
    TextView tv_right;

    // imgPhoto
    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;


    // 添加照片
    @ViewInject(id = R.id.getPic, click = "getPciture")
    LinearLayout getPic;

    // 访客姓名
    @ViewInject(id = R.id.VisitorName)
    TextView et_VisitorName;

    // vip(1是 0否)
    @ViewInject(id = R.id.isVip)
    CheckBox checkBoxVip;
    // 受访人ID
    @ViewInject(id = R.id.RespondentID)
    TextView et_RespondentSpinner;

    // 访问目的
    @ViewInject(id = R.id.Aim)
    TextView et_Aim;

    // 预约到访日期
    @ViewInject(id = R.id.ArrivalTimePlan_date, click = "selectStartDate")
    TextView ArrivalTimePlan_date;

    // 预约离开日期
    @ViewInject(id = R.id.LeaveTimePlan_date, click = "selectEndDate")
    TextView LeaveTimePlan_date;


    // 所属单位
    @ViewInject(id = R.id.Affilication)
    TextView et_Affilication;

    //	 联系方式
    @ViewInject(id = R.id.PhoneNumber)
    TextView PhoneNumber;

    // 欢迎语
    @ViewInject(id = R.id.tv_welcomeword)
    TextView et_WelcomeWord;

    // 备注
    @ViewInject(id = R.id.Remark)
    TextView et_Remark;

    // 变量
    private VisitorBModel visitorModel;
    private File visitorPicPath;//拍照的图片文件（路径）
    public boolean isVip;
    ArrayList<EmployeeListModel> employeeList;//受访人列表

    public String imagePath;//获取服务端图片路径

    private CameraGalleryUtils cameraGalleryUtils;// 头像上传工具
    // 外部jar包：universal-image-loader-1.9.2.jar/异步加载图片
    private DisplayImageOptions imgOption;
    // 图片缓存
    private ImageLoader imgLoader;
    private String avatarBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_visitormain_detail_receive);
        initMyView();
        initListener();
    }

    private void initMyView() {
        tv_title.setText("访客详情");
        tv_right.setText("");

        //获取跳转信息(来自MainVisitorActivity)
        visitorModel = (VisitorBModel) getIntent().getSerializableExtra("VisitorBModel");
        if (visitorModel != null) {
            imagePath = visitorModel.getImagePath();
            initModel();
        }
        // 拍照相册处理类
        cameraGalleryUtils = new CameraGalleryUtils(this, this);// 实例化

        // 图片缓存初始化
        imgLoader = ImageLoader.getInstance();// 实例化
        imgLoader.init(ImageLoaderConfiguration.createDefault(this));
        imgOption = ImageLoadingConfig.generateDisplayImageOptionsNoCatchDisc(R.mipmap.default_photo);


    }

    private void initListener() {

        // vip监听
        checkBoxVip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isVip = true;
                } else {
                    isVip = false;

                }
            }
        });

    }

    /**
     * 填写已保存的用户信息，方便修改
     */
    void initModel() {
        Log.d("SJY", "imagPath = " + imagePath);
        //图片缓存的 图片显示
        if (imagePath.toLowerCase().startsWith("http")) {// 将字符参数改写成小写的方式
            ImageLoader.getInstance().displayImage(imagePath, imgPhoto, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    super.onLoadingStarted(imageUri, view);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    sendToastMessage("获取网络图片失败");
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    super.onLoadingCancelled(imageUri, view);
                }
            });
        } else {
            ImageLoader.getInstance().displayImage("file://" + imagePath, imgPhoto, imgOption);
        }


        //获取已保存信息
        et_VisitorName.setText(visitorModel.getVisitorName());//访客姓名
        et_Aim.setText(visitorModel.getAim());//目的

        String arrivalTimeStr = visitorModel.getArrivalTimePlan();
        ArrivalTimePlan_date.setText(arrivalTimeStr);//预约日期

        String leaveTimeStr = visitorModel.getLeaveTimePlan();
        LeaveTimePlan_date.setText(leaveTimeStr);//离开日期

        et_Affilication.setText(visitorModel.getAffilication());//单位
        PhoneNumber.setText(visitorModel.getPhoneNumber());//手机号
        et_WelcomeWord.setText(visitorModel.getWelcomeWord());//欢迎语
        et_Remark.setText(visitorModel.getRemark());//备注
        et_RespondentSpinner.setText(visitorModel.getRespondentName());
        boolean isVip = visitorModel.isVip();
        //vip
        if (isVip) {
            checkBoxVip.setChecked(true);
        } else {
            checkBoxVip.setChecked(false);
        }

    }

    //显示访客要访问的受访人
    private int getEmployeeListIndex(String respondentID) {
        for (int i = 0; i < employeeList.size(); i++) {
            if (respondentID.compareTo(employeeList.get(i).getEmployeeId()) == 0) {//相同就执行
                return i;
            }
        }
        return 0;
    }

    // back
    public void forBack(View view) {
        this.finish();
    }

    // 获取人脸图片
    public void getPciture(View view) {
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
        imgPhoto.setImageResource(R.mipmap.default_photo);// imageView添加图片
        // 获取图片路径
        this.visitorPicPath = new File(picPath);
        if (!this.visitorPicPath.exists()) {
            try {
                this.visitorPicPath.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //图片缓存的 图片显示
        if (picPath.toLowerCase().startsWith("http")) {// 将字符参数改写成小写的方式
            // 外部jar包方法
            ImageLoader.getInstance().displayImage(picPath, // 已处理图片地址
                    imgPhoto, // imageView
                    imgOption);// jar包类
        } else {
            ImageLoader.getInstance().displayImage("file://" + picPath,//
                    imgPhoto, // imageView
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
