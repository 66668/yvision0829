package com.yvision.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yvision.R;
import com.yvision.adapter.EmployeeListSpinnerAdapter;
import com.yvision.common.ImageLoadingConfig;
import com.yvision.common.MyException;
import com.yvision.db.entity.UserEntity;
import com.yvision.dialog.DatePickerDialog;
import com.yvision.dialog.Loading;
import com.yvision.dialog.TimePickerDialog;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.EmployeeListModel;
import com.yvision.model.VisitorBModel;
import com.yvision.utils.CameraGalleryUtils;
import com.yvision.utils.PageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VisitorInfoNotReceiveActivity extends BaseActivity implements CameraGalleryUtils.ChoosePicCallBack {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right, click = "toSave")
    TextView tv_right;

    // imgPhoto
    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;

    // 添加照片
    @ViewInject(id = R.id.getPic, click = "getPciture")
    LinearLayout getPic;

    // 访客姓名
    @ViewInject(id = R.id.VisitorName)
    EditText et_VisitorName;

    // vip(1是 0否)
    @ViewInject(id = R.id.isVip)
    CheckBox checkBoxVip;
    // 受访人ID
    @ViewInject(id = R.id.RespondentID)
    Spinner et_RespondentSpinner;

    // 访问目的
    @ViewInject(id = R.id.Aim)
    EditText et_Aim;

    // 预约到访日期
    @ViewInject(id = R.id.ArrivalTimePlan_date, click = "selectStartDate")
    TextView ArrivalTimePlan_date;

    // 预约到访时间
    @ViewInject(id = R.id.ArrivalTimePlan_time, click = "selectStartTime")
    TextView ArrivalTimePlan_time;

    // 预约离开日期
    @ViewInject(id = R.id.LeaveTimePlan_date, click = "selectEndDate")
    TextView LeaveTimePlan_date;

    // 预约离开时间
    @ViewInject(id = R.id.LeaveTimePlan_time, click = "selectEndTime")
    TextView LeaveTimePlan_time;

    // 所属单位
    @ViewInject(id = R.id.Affilication)
    EditText et_Affilication;

    //	 联系方式
    @ViewInject(id = R.id.PhoneNumber)
    EditText PhoneNumber;

    // 欢迎语
    @ViewInject(id = R.id.tv_welcomeword)
    EditText et_WelcomeWord;

    // 备注
    @ViewInject(id = R.id.Remark)
    EditText et_Remark;

    // 常量
    //常量
    public static final int REQUEST_CODE_FOR_EDIT_USER = 2003;//回调函数调用

    private final int GET_EMPLOYEELIST_SUCCESS = 501;// 获取受访者
    private final int ADDVISITOR_SUCESS = 502;//
    // 变量
    private VisitorBModel visitorModel;
    private UserEntity userEntity;
    // public String strGender;// 性别
    private File visitorPicPath;//拍照的图片文件（路径）
    public String respondentID = "";// 受访者
    public boolean isVip;
    public String ArrivalTimePlan_date_str;
    public String ArrivalTimePlan_time_str;
    public String LeaveTimePlan_date_str;
    public String LeaveTimePlan_time_str;
    ArrayList<EmployeeListModel> employeeList;//受访人列表
    EmployeeListSpinnerAdapter employeeListSpinnerAdapter;//自定义spinneradapter
    public String RecordID = "";// 登记的ID

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
        setContentView(R.layout.act_visitormain_detail_info);

        tv_title.setText("访客详情");
        tv_right.setText("保存");

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
        imgOption = ImageLoadingConfig.generateDisplayImageOptionsNoCatchDisc(R.mipmap.ic_launcher);

        //线程获取受访人
        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                try {
                    // 获取受访者信息
                    JSONArray jsonArray = UserHelper.getEmployeeListByStoreID(VisitorInfoNotReceiveActivity.this,
                            UserHelper.getCurrentUser().getStoreID(), // storeID（公司编号）
                            "1");// typeN(写死了)
                    sendMessage(GET_EMPLOYEELIST_SUCCESS, jsonArray);
                    // Log.d("SJY", "jsonArray="+jsonArray.toString());//
                } catch (MyException e) {
                    e.printStackTrace();
                    // 异常抛出
                    sendToastMessage(e.getMessage());
                }
            }
        });
        // spinner监听
        et_RespondentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (employeeList == null) {
                        return;
                    }
                    // 获取 EmployeeID
                    respondentID = ((EmployeeListModel) employeeListSpinnerAdapter.getItem(position)).getEmployeeId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
        String arrivalDate = arrivalTimeStr.substring(0, arrivalTimeStr.indexOf(' '));
        String arrivalTime = arrivalTimeStr.substring(arrivalTimeStr.indexOf(' ') + 1, arrivalTimeStr.length());
        ArrivalTimePlan_date.setText(arrivalDate);//预约日期
        ArrivalTimePlan_time.setText(arrivalTime);//预约时间

        String leaveTimeStr = visitorModel.getLeaveTimePlan();
        String leaveDate = leaveTimeStr.substring(0, leaveTimeStr.indexOf(' '));
        String leaveTime = leaveTimeStr.substring(leaveTimeStr.indexOf(' ') + 1, leaveTimeStr.length());
        LeaveTimePlan_date.setText(leaveDate);//离开日期
        LeaveTimePlan_time.setText(leaveTime);//离开时间

        et_Affilication.setText(visitorModel.getAffilication());//单位
        PhoneNumber.setText(visitorModel.getPhoneNumber());//手机号
        et_WelcomeWord.setText(visitorModel.getWelcomeWord());//欢迎语
        et_Remark.setText(visitorModel.getRemark());//备注
        boolean isVip = visitorModel.isVip();
        //vip
        if (isVip) {
            checkBoxVip.setChecked(true);
        } else {
            checkBoxVip.setChecked(false);
        }

    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case GET_EMPLOYEELIST_SUCCESS:
                JSONArray jsonArray = (JSONArray) msg.obj;
                bindEmployeeList(jsonArray);
                break;
            case ADDVISITOR_SUCESS:
                sendToastMessage("修改成功!");
                break;
            default:
                break;
        }
    }

    /**
     * 将数据绑定到spinner中
     *
     * @param jsonArray
     */
    private void bindEmployeeList(JSONArray jsonArray) {
        employeeList = new ArrayList<EmployeeListModel>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                String EmployeeId = jsonArray.getJSONObject(i).getString("EmployeeId");
                String WrokId = jsonArray.getJSONObject(i).getString("WrokId");
                String EmployeeName = jsonArray.getJSONObject(i).getString("EmployeeName");

                EmployeeListModel employeeListModel = new EmployeeListModel();
                employeeListModel.setEmployeeId(EmployeeId);
                employeeListModel.setEmployeeName(EmployeeName);
                employeeListModel.setWrokId(WrokId);
                employeeList.add(employeeListModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            sendToastMessage(e.getMessage());
            return;
        }
        employeeListSpinnerAdapter = new EmployeeListSpinnerAdapter(this, employeeList);
        et_RespondentSpinner.setAdapter(employeeListSpinnerAdapter);

        // 将当前登返回值的EmployeeID显示到spinner上
        try {
            userEntity = UserHelper.getCurrentUser();
            // 将信息显示到spinner上
            et_RespondentSpinner.setSelection(getEmployeeListIndex(visitorModel.getRespondentID()));//显示访客要访问的受访人
        } catch (Exception e) {
            e.printStackTrace();
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

    // toSave
    public void toSave(View view) {

        // 线程处理保存信息
        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                String RecordID = visitorModel.getRecordID();//
                String VisitorID = visitorModel.getVisitorID();//
                String VisitorName = et_VisitorName.getText().toString();// 访客姓名
                String RespondentID = VisitorInfoNotReceiveActivity.this.respondentID;// 受访者ID
                String Aim = et_Aim.getText().toString();// 目的
                String employeeID = UserHelper.getCurrentUser().getEmployeeId();
                String Affilication = et_Affilication.getText().toString();// 单位
                String ArrivalTimePlan = ArrivalTimePlan_date.getText().toString() + " " + LeaveTimePlan_time.getText().toString();// 来访时间
                String LeaveTimePlan = LeaveTimePlan_date.getText().toString() + " " + LeaveTimePlan_time.getText().toString();// 离开时间
                String WelcomeWord = et_WelcomeWord.getText().toString();// 欢迎语
                String PhoneNumber = VisitorInfoNotReceiveActivity.this.PhoneNumber.getText().toString();//手机号
                String Remark = et_Remark.getText().toString();// 备注
                String StoreID = UserHelper.getCurrentUser().getStoreID();// 到访公司
                try {
                    // JSONObject
                    JSONObject js = new JSONObject();
                    js.put("RecordID", RecordID);
                    js.put("employeeID", employeeID);
                    js.put("VisitorName", VisitorName);
                    js.put("VisitorID", VisitorID);
                    js.put("RespondentID", RespondentID);
                    js.put("Aim", Aim);
                    js.put("Affilication", Affilication);
                    js.put("ArrivalTimePlan", ArrivalTimePlan);
                    js.put("LeaveTimePlan", LeaveTimePlan);
                    js.put("WelcomeWord", WelcomeWord);
                    js.put("Remark", Remark);
                    js.put("PhoneNumber", PhoneNumber);
                    js.put("StoreID", StoreID);
                    js.put("isVip", isVip);
                    js.put("GroupId", "");

                    String msg = "";
                    if (visitorPicPath == null) {
                        msg = UserHelper.updateOneVisitorRecord(VisitorInfoNotReceiveActivity.this, js.toString(), "", null);
                    } else {
                        msg = UserHelper.updateOneVisitorRecord(VisitorInfoNotReceiveActivity.this, js.toString(), "true", visitorPicPath);
                    }
                    sendMessage(ADDVISITOR_SUCESS, msg);

                } catch (MyException e) {
                    e.printStackTrace();
                    sendToastMessage(e.getMessage());
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendToastMessage(e.getMessage());
                    return;
                }
            }
        });
    }

    // 图片展示
    // 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（1）
    @Override
    public void updateAvatarSuccess(int updateType, String picPath, String avatarBase64) {
        imgPhoto.setImageResource(R.mipmap.ic_launcher);// imageView添加图片
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

    // 设置日期
    private final int DATE_START_DATA = 0;
    private final int DATE_END_DATA = 1;

    public void selectStartDate(View view) {
        showDataPickerDialog(DATE_START_DATA, ArrivalTimePlan_date_str);
    }

    public void selectEndDate(View view) {
        showDataPickerDialog(DATE_END_DATA, LeaveTimePlan_date_str);
    }

    private void showDataPickerDialog(final int whichTime, String currentDate) {
        DatePickerDialog dialog = new DatePickerDialog(this,
                currentDate,
                new DatePickerDialog.DatePickerDialogCallBack() {
                    @Override
                    public void confirm(String date) {
                        PageUtil.DisplayToast(date);
                        if (whichTime == DATE_START_DATA) {
                            ArrivalTimePlan_date.setText(date);
                            ArrivalTimePlan_date_str = date;
                        } else {
                            LeaveTimePlan_date.setText(date);
                            LeaveTimePlan_date_str = date;
                        }

                    }
                });
        dialog.show();
    }

    // 设置时间
    private final int TIME_START_DATA = 2;
    private final int TIME_END_DATA = 3;

    public void selectStartTime(View view) {
        showTimePickerDialog(TIME_START_DATA, ArrivalTimePlan_time_str);
    }

    public void selectEndTime(View view) {
        showTimePickerDialog(TIME_END_DATA, LeaveTimePlan_time_str);
    }

    private void showTimePickerDialog(final int whichTime, String currentTime) {
        TimePickerDialog dialog = new TimePickerDialog(VisitorInfoNotReceiveActivity.this,
                LeaveTimePlan_time.getText().toString(), new TimePickerDialog.TimePickerDialogCallBack() {
            @Override
            public void confirm(String date) {
                if (whichTime == TIME_START_DATA) {
                    ArrivalTimePlan_time.setText(date);
                    ArrivalTimePlan_time_str = date;
                } else {
                    LeaveTimePlan_time.setText(date);
                    LeaveTimePlan_time_str = date;//LeaveTimePlan_time.getText().toString()
                }
            }
        });
        dialog.show();
    }
}
