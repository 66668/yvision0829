package com.yvision;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.yvision.common.MyApplication;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.EmployeeInfoModel;
import com.yvision.utils.PageUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 界面：
 * 访客管理系统/用户管理系统
 */
public class MainActivity extends BaseActivity {

    // 访客系统
    @ViewInject(id = R.id.visitorManager, click = "toVisitor")
    RelativeLayout visitorManger;

    //考勤管理
    @ViewInject(id = R.id.attendanceManager, click = "toUser")
    RelativeLayout attendanceManager;

    //人员注册
    @ViewInject(id = R.id.employeeRegisterManager, click = "toRegister")
    RelativeLayout employeeRegisterManager;

    // 设置按钮
    @ViewInject(id = R.id.tv_settings, click = "toSetting")
    RelativeLayout tv_settings;

    //变量
    private SimpleDateFormat formatter;
    private Date curDate;

    //常量
    private static final int TO_REGISTER = -96;
    private static final int TO_VISITOR = -92;
    private static final int TO_SETTINGS = -93;
    private static final int TO_ATTENDANCE = -94;
    private static final int TO_ATTENDANCE_FAILED = -97;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_main);
        //判断自动登录
        if (!MyApplication.getInstance().isLogin() && (MyApplication.getInstance().getClientID() == null)) {
            startActivity(LoginActivity.class);
            this.finish();
        }
        Log.d("SJY", "clientID=" + MyApplication.getInstance().getClientID());
    }

    /**
     * 访客系统
     */
    public void toVisitor(View view) {
        sendMessage(TO_VISITOR);
    }

    /**
     * 员工考勤注册
     */
    public void toRegister(View view) {
        sendMessage(TO_REGISTER);
    }

    @Override
    protected void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case TO_VISITOR://访客
                startActivity(MainVisitorActivity.class);
                break;
            case TO_SETTINGS://设置
                startActivity(SettingActivity.class);
                break;
            case TO_REGISTER://注册
                startActivity(MainRegisterActivity.class);
                break;
            case TO_ATTENDANCE://考勤
                Intent intent2 = new Intent(MainActivity.this, MainUserActivity.class);
                intent2.putExtra("EmployeeInfoModel", (EmployeeInfoModel) msg.obj);
                startActivity(intent2);
                break;
            case TO_ATTENDANCE_FAILED://考勤返回数据失败
                PageUtil.DisplayToast((String)msg.obj);
                break;
            default:
                break;
        }
    }

    /**
     * 考勤系统
     */
    public void toUser(View view) {
        final String employeID = UserHelper.getCurrentUser().getEmployeeID();
        Loading.run(MainActivity.this, new Runnable() {
            @Override
            public void run() {
                try {
                    EmployeeInfoModel employeeInfoModel = UserHelper.GetEmployeeInfoByID(MainActivity.this, employeID);

                    sendMessage(TO_ATTENDANCE, employeeInfoModel);
                } catch (MyException e) {
                    sendToastMessage(e.getMessage());
                    Log.d("SJY", "空异常：" + e.getMessage());
                    sendMessage(TO_ATTENDANCE_FAILED, e.getMessage());
                }
            }
        });
    }

    /**
     * 设置
     */
    public void toSetting(View view) {
        sendMessage(TO_SETTINGS);
    }

    //	// 双击退出
    //	private static Boolean isExit = false;
    //
    //	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    //		if (keyCode == KeyEvent.KEYCODE_BACK) {
    //			Timer tExit = null;
    //			if (isExit == false) {
    //				isExit = true; // 准备退出
    //				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
    //				tExit = new Timer();
    //				tExit.schedule(new TimerTask() {
    //					@Override
    //					public void run() {
    //						isExit = false; // 取消退出
    //					}
    //				}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
    //
    //			} else {
    //				finish();
    //				System.exit(0);
    //			}
    //		}
    //		return false;
    //	}
}
