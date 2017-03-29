package com.yvision.view;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.dialog.UpdateAppDialog;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.UpgradeModel;
import com.yvision.utils.IntentUtil;
import com.yvision.utils.PageUtil;

/**
 * 设置界面
 */
public class SettingActivity extends BaseActivity {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right, click = "forCommit")
    TextView tv_right;

     //退出
    @ViewInject(id = R.id.btn_quit, click = "quit")
     Button btn_quit;


    //密码修改
    @ViewInject(id = R.id.btn_changePsd, click = "changePassword")
    LinearLayout btn_changePsd;

    //人脸登记
    @ViewInject(id = R.id.btn_changeFace, click = "changeFace")
    LinearLayout btn_changeFace;


    //点击升级版本
    @ViewInject(id = R.id.layout_version, click = "getVersion")
    LinearLayout layout_Version;
    //版本
    @ViewInject(id = R.id.tv_Version)
    TextView tv_Version;

    private static final int SUCCESS = -40;// 获取所有数据列表 标志
    private static final int QUIT_SCCESS = -39;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_main_settings);
        tv_title.setText("设置");
        tv_right.setText("");
    }

    /**
     * 修改密码跳转
     */
    public void changePassword(View view) {
        startActivity(ChangePassWordActivity.class);
    }

    /**
     * 人脸登记跳转
     *
     * @param view
     */
    public void changeFace(View view) {
//        startActivity(AddMyFaceActivity.class);
        PageUtil.DisplayToast("该功能模块不可用");
    }

    /**
     * 后退
     */
    public void forBack(View vie) {
        this.finish();
    }



    /**
     * 程序退出
     */

    public void quit(View view) {
        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                try {
                    UserHelper.logout(getApplicationContext());
                    sendMessage(QUIT_SCCESS);
                } catch (MyException e) {
                    Log.d("SJY", "异常=" + e.getMessage());
                }
            }
        });

    }

    /**
     * 版本升级
     *
     * @param view
     */
    public void getVersion(View view) {
        PageUtil.DisplayToast("该功能模块不可用");
//        Loading.run(this, new Runnable() {
//            @Override
//            public void run() {
//                String type = "1001";
//                try {
//                    UpgradeModel upgradeModel = UserHelper.CheckVersion(SettingActivity.this, type);
//                    if (upgradeModel.isIsexistsnewversion()) {
//                        sendMessage(SUCCESS, upgradeModel);
//                    } else {
//                        sendToastMessage(R.string.last_version_already);// 已是最新版本
//                    }
//                } catch (MyException e) {
//                    sendToastMessage(e.getMessage());
//                }
//            }
//        });
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case SUCCESS:
                final UpgradeModel upgradeModel = (UpgradeModel) msg.obj;
                new UpdateAppDialog(this, upgradeModel.getMessage(), false, new UpdateAppDialog.UpdateAppDialogCallBack() {

                    @Override
                    public void confirm() {
                        Intent intent = IntentUtil.getBrowserIntent(upgradeModel.getPackageUrl());
                        startActivity(intent);
                    }

                    @Override
                    public void cancel() {

                    }
                }).show();
                break;

            case QUIT_SCCESS:
                Intent intent = new Intent();
                intent.setAction(EXIT_APP_ACTION);
                sendBroadcast(intent);//发送退出的广播
                break;
        }
    }
}
