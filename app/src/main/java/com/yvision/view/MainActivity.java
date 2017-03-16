package com.yvision.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.yvision.R;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.utils.JPushUtil;
import com.yvision.utils.PageUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 界面：
 * 访客管理系统/用户管理系统
 */
public class MainActivity extends BaseActivity {

    // 01访客系统
    @ViewInject(id = R.id.visitorManager, click = "toVisitor")
    RelativeLayout visitorManger;

    //02考勤管理
    @ViewInject(id = R.id.attendanceManager, click = "toUser")
    RelativeLayout attendanceManager;

    //03人员注册
    @ViewInject(id = R.id.employeeRegisterManager, click = "toRegister")
    RelativeLayout employeeRegisterManager;

    //04vip
    @ViewInject(id = R.id.layout_vip, click = "toVip")
    RelativeLayout layout_vip;

    //05门禁
    @ViewInject(id = R.id.layout_door, click = "toDoor")
    RelativeLayout layout_door;

    // 设置按钮
    @ViewInject(id = R.id.tv_settings, click = "toSetting")
    RelativeLayout tv_settings;

    //变量
    private SimpleDateFormat formatter;
    private Date curDate;
    public static boolean isForeground = false;//推送 判断
    //常量
    private static final int TO_REGISTER = -97;//注册
    private static final int TO_ATTENDANCE_FAILED = -96;
    private static final int TO_ATTENDANCE = -94;//考勤
    private static final int TO_SETTINGS = -93;//设置
    private static final int TO_VISITOR = -92;//访客
    private static final int TO_VIP = -91;//VIP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_main);

        initJpush();
    }

    private void initJpush() {
        JPushInterface.init(getApplicationContext());
        registerMessageReceiver();  // used for receive msg
        //推送设置别名
        setAlias(UserHelper.getCurrentUser().getUserName());
    }

    /**
     * 01访客系统
     */
    public void toVisitor(View view) {
        sendMessage(TO_VISITOR);
    }


    /**
     * 02考勤系统
     */
    public void toUser(View view) {
        startActivity(MainAttendActivity.class);

//
    }

    /**
     * 03员工考勤注册
     */
    public void toRegister(View view) {
        sendMessage(TO_REGISTER);
    }

    /**
     * 04vip系统
     */
    public void toVip(View view) {
        startActivity(MainVipActivity.class);
    }

    /**
     * 05门禁系统
     */
    public void toDoor(View view) {
        startActivity(MainDoorAccessActivity.class);
    }

    @Override
    protected void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case TO_VISITOR://01访客
                startActivity(MainVisitorActivity.class);
                break;
            case TO_SETTINGS://设置
                startActivity(SettingActivity.class);
                break;
            case TO_REGISTER://03注册
                startActivity(MainRegisterActivity.class);
                break;

            case TO_VIP://04vip
                startActivity(MainRegisterActivity.class);
                break;
            case TO_ATTENDANCE_FAILED://考勤返回数据失败
                PageUtil.DisplayToast((String) msg.obj);
                break;
            default:
                break;
        }
    }

    /**
     * 设置
     */
    public void toSetting(View view) {
        sendMessage(TO_SETTINGS);
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMessageReceiver != null) {
            unregisterReceiver(mMessageReceiver);
        }
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


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!JPushUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                Log.d("JPush", "rid=" + JPushInterface.getRegistrationID(MainActivity.this) + "\n--showMsg" + showMsg);
            }
        }
    }

    /**
     * jpush 绑定别名
     */
    private void setAlias(String workid) {
        JPushInterface.setAliasAndTags(getApplicationContext(), workid, null, new TagAliasCallback() {

            @Override
            public void gotResult(int code, String s, Set<String> set) {
                String logs;
                switch (code) {
                    case 0:
                        Log.i("JPush", "Set tag and alias success极光推送别名设置成功");
                        break;
                    case 6002:
                        Log.i("JPush", "极光推送别名设置失败，Code = 6002");
                        break;
                    default:
                        Log.e("JPush", "极光推送设置失败，Code = " + code);
                        break;
                }
            }
        });
    }
}
