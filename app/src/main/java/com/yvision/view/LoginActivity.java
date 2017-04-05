package com.yvision.view;


import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.yvision.R;
import com.yvision.common.MyApplication;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.utils.ConfigUtil;
import com.yvision.utils.PageUtil;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity {
    // 公司
    @ViewInject(id = R.id.et_storeId)
    EditText et_storeId;

    // 工号
    @ViewInject(id = R.id.et_UserNmae)
    EditText et_UserName;

    // 密码
    @ViewInject(id = R.id.et_Password)
    EditText et_Password;


    // 高级设置
    @ViewInject(id = R.id.tv_settings, click = "higherSettings")
    EditText tv_settings;

    // 登录按钮
    @ViewInject(id = R.id.btnLogin, click = "login")
    Button btnLogin;
    //常量
    private final int LOGIN_SUCESS = 2001; // 登陆成功
    private final int LOGIN_FAILED = 2002; // 失败

    //变量
    boolean isLogin = false;// 是否登录
    private String storeId, workId, password;
    private String registRationID;//极光注册成功 会返回这个id
    /**
     * SDK服务是否启动
     */
    private static final int REQUEST_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_login_password);

        //判断自动登录
        if (MyApplication.getInstance().isLogin()) {
            startActivity(MainActivity.class);
            this.finish();
        }

        //中断保存
        ConfigUtil configUtil = new ConfigUtil(this);
        et_storeId.setText(configUtil.getAdminUserName());
        et_UserName.setText(configUtil.getUserName());
        et_Password.setText(configUtil.getPassword());

    }



    // 人脸登录监听
    public void loginByFace(View view) {
        // 传值
        storeId = et_storeId.getText().toString().trim();
        workId = et_UserName.getText().toString().trim();
        Bundle bundle = new Bundle();
        bundle.putString("storeId", storeId);
        bundle.putString("workId", workId);

        startActivity(LoginByFaceActivity.class, bundle);
    }

    // 登录
    public void login(View view) {
        // //非空验证
        if (!checkInput()) {
            return;
        }
        storeId = et_storeId.getText().toString().trim();
        workId = et_UserName.getText().toString().trim();
        password = et_Password.getText().toString().trim();
        registRationID = JPushInterface.getRegistrationID(getApplicationContext());
        // 线程处理登录
        Loading.run(this, new Runnable() {

            @Override
            public void run() {
                try {
                    UserHelper.loginByPs(LoginActivity.this,
                            storeId, // 公司编号
                            workId, // 工号
                            password,// 密码
                            "0");//是否接受推送0/1

                    // 访问服务端成功，消息处理
                    sendMessage(LOGIN_SUCESS);
                } catch (MyException e) {
                    sendMessage(LOGIN_FAILED, e.getMessage());
                }
            }
        });

    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case LOGIN_SUCESS: // 1001
                this.finish();// 页面注销
                //设置自动登录
                MyApplication.getInstance().setIsLogin(true);
                startActivity(MainActivity.class);
                break;
            case LOGIN_FAILED: // 1001
                Log.d("SJY", (String) msg.obj);
                PageUtil.DisplayToast((String)msg.obj);
                break;
            default:
                break;
        }
    }

    /*
     * 非空验证
     */
    private boolean checkInput() {
        if (TextUtils.isEmpty(et_storeId.getText().toString().trim())) {
            PageUtil.DisplayToast(R.string.LoginActivity_companyNum);// 公司编号
            return false;
        }
        if (TextUtils.isEmpty(et_UserName.getText().toString().trim())) {
            PageUtil.DisplayToast(R.string.LoginActivity_employeeNum);// 工号
            return false;
        }
        if (TextUtils.isEmpty(et_Password.getText().toString().trim())) {
            PageUtil.DisplayToast(R.string.LoginActivity_psd);
        }
        return true;
    }


}
