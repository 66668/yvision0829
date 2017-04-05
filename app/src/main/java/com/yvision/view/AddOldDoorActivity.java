package com.yvision.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.yvision.R;
import com.yvision.adapter.GroupSpinnerAdapter;
import com.yvision.common.HttpParameter;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.GroupModel;
import com.yvision.model.OldEmployeeModel;
import com.yvision.utils.ImageUtils;
import com.yvision.utils.PageUtil;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

/**
 * 添加门禁员工图片
 * Created by sjy on 2016/11/11.
 */

public class AddOldDoorActivity extends BaseActivity {

    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right, click = "refresh")
    TextView tv_right;

    // 注册
    @ViewInject(id = R.id.Register_btn_register, click = "btnRegister")
    Button btnRegister;

    // 姓名
    @ViewInject(id = R.id.tvName)
    TextView tvName;

    // 工号
    @ViewInject(id = R.id.tvWrokId)
    TextView tvWrokId;

    // 性别
    @ViewInject(id = R.id.tvGender)
    TextView tvGender;

    // imageView
    @ViewInject(id = R.id.imageView_picture, click = "btnSnapShot")
    ImageView imgView;

    // 人脸库
    @ViewInject(id = R.id.spinnerType)
    Spinner spinnerType;


    //常量
    public static final int FACE_DATABASE_SUCCESS = -29;// 人脸库
    public static final int SUCCESS_REGISTER = -27;// 注册成功
    public static final int FAILED_REGISTER = -28;// 注册失败
    private static final int REQUEST_CAMERA = 0;//自定义相机

    private File picPath;
    private Point mPoint;//获取屏幕像素尺寸
    private String result;
    //人脸库
    ArrayList<GroupModel> groupIDList;// 人脸库列表
    GroupSpinnerAdapter groupSpinnerAdapter;
    //注册参数
    private String groupID = "";//人脸库ID
    private String groupName = "";
    private String IsAttend, IsVip, IsDoorAccess;
    private String EmployeeName;
    private String WrokId;
    private String employeeID;
    private OldEmployeeModel model;
    //    private CameraGalleryUtils cameraGalleryUtils;// 头像上传工具
    //    // 外部jar包：universal-image-loader-1.9.2.jar/异步加载图片
    //    private DisplayImageOptions imgOption;
    //    // 图片缓存
    //    private ImageLoader imgLoader;
    //    private String avatarBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_old_door_register);

        initMyView();
        setShow();
        initListener();
        getData();
    }

    private void setShow() {
        tvName.setText(EmployeeName);
        tvWrokId.setText(WrokId);
        tvGender.setText(model.getGender());
        Log.d("SJY", "创建==employeeID=" + employeeID + "\nEmployeeName=" + EmployeeName + "\nWrokId=" + WrokId +
                "\n部门=" + model.getDeptName() + "\n性别=" + model.getGender());
    }

    private void initMyView() {
        tv_title.setText("注册图片");
        tv_right.setText("");
        //获取跳转值
        Bundle bundle = getIntent().getExtras();
        model = (OldEmployeeModel) bundle.getSerializable("OldEmployeeModel");

        EmployeeName = model.getEmployeeName();
        WrokId = model.getWrokId();

        //获取屏幕像素尺寸
        Display display = getWindowManager().getDefaultDisplay();
        mPoint = new Point();
        display.getSize(mPoint);
    }

    private void initListener() {
        //选择人脸库类型
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (groupIDList == null) {
                        return;
                    }
                    groupID = groupSpinnerAdapter.getItem(position).getGroupID() + "";
                    groupName = groupSpinnerAdapter.getItem(position).getGroupName();
                    setPermission(groupName.trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void getData() {
        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                try {
                    //获取人像库信息
                    JSONArray jsonArrayGroupID = UserHelper.getDoorFaceDatabase(AddOldDoorActivity.this);
                    sendMessage(FACE_DATABASE_SUCCESS, jsonArrayGroupID);
                } catch (MyException e) {
                    Log.d("SJY", "异常=" + e.getMessage());
                }
            }
        });
    }

    /**
     * 根据人像库加载后台权限
     *
     * @param groupName
     */
    private void setPermission(String groupName) {
        if (groupName.equals("考勤")) {
            IsAttend = "1";
            IsDoorAccess = "0";
            IsVip = "0";
            dialog(groupName);
        }
        if (groupName.equals("门禁")) {
            IsAttend = "0";
            IsDoorAccess = "1";
            IsVip = "0";
            dialog(groupName);
        }
        if (groupName.equals("VIP") || groupName.equals("vip")) {
            IsAttend = "0";
            IsDoorAccess = "0";
            IsVip = "1";
            dialog(groupName);
        }
    }

    private void dialog(String groupName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否添加" + groupName + "权限？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(AddOldDoorActivity.this, "请重新选择人像库！", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    /**
     * 注册
     */
    public void btnRegister(View v) {

        if (picPath == null) {
            PageUtil.DisplayToast("无法添加，请选择图片");
            return;
        }
        if (TextUtils.isEmpty(groupName.trim())) {
            PageUtil.DisplayToast("请选择人像库");
            return;
        }

        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                try {
                    String result = UserHelper.registerOld(AddOldDoorActivity.this,
                            HttpParameter.create().
                                    add("employeeID", employeeID).
                                    add("groupID", groupID).
                                    add("storeID", UserHelper.getCurrentUser().getStoreID()).
                                    add("name", EmployeeName).
                                    add("IsAttend", IsAttend).
                                    add("IsVip", IsVip).
                                    add("IsDoorAccess", "1").
                                    add("operatorName", UserHelper.getCurrentUser().getUserName()),
                            picPath);
                    // 消息处理
                    sendMessage(SUCCESS_REGISTER, result);
                } catch (MyException e) {
                    sendMessage(FAILED_REGISTER, e.getMessage());
                }
            }
        });
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case SUCCESS_REGISTER:
                PageUtil.DisplayToast((String) msg.obj);
                break;
            case FAILED_REGISTER:
                PageUtil.DisplayToast((String) msg.obj);
                break;
            case FACE_DATABASE_SUCCESS:
                Log.d("SJY", "已连接人脸库");
                bindFaceData((JSONArray) msg.obj);
                break;

            default:
                break;
        }
    }


    private void bindFaceData(JSONArray jsonArray) {
        groupIDList = new ArrayList<GroupModel>();
        try {
            //需要加一对空值
            GroupModel groupModel1 = new GroupModel();
            groupModel1.setGroupID("");
            groupModel1.setGroupName("");
            groupIDList.add(groupModel1);
            //正常显示
            for (int i = 0; i < jsonArray.length(); i++) {
                String groupID = jsonArray.getJSONObject(i).getString("groupID");
                String groupName = jsonArray.getJSONObject(i).getString("groupName");
                GroupModel groupModel = new GroupModel();
                groupModel.setGroupID(groupID);
                groupModel.setGroupName(groupName);
                Log.d("SJY", "groupID=" + groupID + ",groupName" + groupName);
                groupIDList.add(groupModel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        groupSpinnerAdapter = new GroupSpinnerAdapter(this, groupIDList);
        spinnerType.setAdapter(groupSpinnerAdapter);

        //获取登录人model,并将登录人中的信息获取出来（不适合本app要求）
        //        try {
        //            spinnerType.setSelection(getGroupIDIndex(vfaceMemberModel.getProvinceId()));
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

    }

    /**
     * 打开摄像头
     *
     * @param view
     */
    public void btnSnapShot(View view) {
        //自定义相机1
        Intent mCameraIntent = new Intent(this, MyChangeCameraActivity.class);
        startActivityForResult(mCameraIntent, REQUEST_CAMERA);
        //自定义相机2
        //        cameraGalleryUtils.showChoosePhotoDialog(CameraGalleryUtils.IMG_TYPE_CAMERA);// -99,处理弹窗调用
    }

    //相机或者相册调用后的回调方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //自定义相机1
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_CAMERA) {
            Uri photoUri = data.getData();
            picPath = new File(ImageUtils.getImageAbsolutePath(this, photoUri));
            // Get the bitmap in according to the width of the device
            Bitmap bitmap = ImageUtils.decodeSampledBitmapFromPath(photoUri.getPath(), mPoint.x, mPoint.x);
            imgView.setImageBitmap(bitmap);
        }

        //        cameraGalleryUtils.onActivityResultAction(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }


    /**
     * 返回
     */
    public void forBack(View v) {
        this.finish();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AddOldEmployee Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    //    // 图片展示
    //    // 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（1）
    //    @Override
    //    public void updateAvatarSuccess(int updateType, String picPath, String avatarBase64) {
    //        imgView.setImageResource(R.mipmap.ic_launcher);// imageView添加图片
    //        // 获取图片路径
    //        this.picPath = new File(picPath);
    //        if (!this.picPath.exists()) {
    //            try {
    //                this.picPath.createNewFile();
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //
    //        if (picPath.toLowerCase().startsWith("http")) {// 将字符参数改写成小写的方式
    //            // 外部jar包方法
    //            ImageLoader.getInstance().displayImage(picPath, // 已处理图片地址
    //                    imgView, // imageView
    //                    imgOption);// jar包类
    //        } else {
    //            ImageLoader.getInstance().displayImage("file://" + picPath,
    //                    imgView, // imageView
    //                    imgOption);// jar包类
    //        }
    //        this.avatarBase64 = avatarBase64;
    //    }
    //
    //    @Override
    //    // 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（2）
    //    public void updateAvatarFailed(int updateType) {
    //        Toast.makeText(this, "头像上传失败", Toast.LENGTH_SHORT).show();// 头像上传失败
    //    }
    //
    //    @Override
    //    // 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（3）
    //    public void cancel() {
    //
    //    }
}
