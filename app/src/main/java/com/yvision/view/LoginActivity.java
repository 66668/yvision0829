package com.yvision.view;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    // 人脸登录
    @ViewInject(id = R.id.tv_byFace, click = "loginByFace")
    TextView tv_byFace;

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

        // 人脸按钮变色监听
        et_storeId.addTextChangedListener(textCompanyListener);
        et_UserName.addTextChangedListener(textEmployeeListener);
        et_Password.addTextChangedListener(textPasswordListener);

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
                            registRationID);

                    // 访问服务端成功，消息处理
                    sendMessage(LOGIN_SUCESS);
                    //设置自动登录
                    MyApplication.getInstance().setIsLogin(true);
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
                // 文本跳转
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

    /**
     * （1）公司编号监听，当输入顺序：密码，工号--》公司编号 设置双向监听，公司编号和工号输入后，才可实现人脸登录（灰色/蓝色）
     */

    public TextWatcher textCompanyListener = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 人脸
            if (s.toString().length() > 1 && // 公司编号
                    et_UserName.getText().toString().trim().length() > 1) {// 工号
                tv_byFace.setClickable(true);
                tv_byFace.setEnabled(true);
                tv_byFace.setTextColor(Color.WHITE);

                if (et_Password.getText().toString().trim().length() > 0) {
                    btnLogin.setClickable(true);
                    btnLogin.setEnabled(true);
                    btnLogin.setTextColor(getResources().getColor(R.color.common_bg_color));
                } else {
                    btnLogin.setClickable(false);
                    btnLogin.setEnabled(false);
                    btnLogin.setTextColor(Color.GRAY);
                }
            } else {
                tv_byFace.setClickable(false);
                tv_byFace.setEnabled(false);
                tv_byFace.setTextColor(Color.GRAY);
                //
                btnLogin.setClickable(false);
                btnLogin.setEnabled(false);
                btnLogin.setTextColor(Color.GRAY);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    /**
     * (2)工号监听：密码，公司编号-->工号
     */
    public TextWatcher textEmployeeListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (et_storeId.getText().toString().trim().length() > 1 && // 公司编号
                    s.toString().length() > 1) {// 工号
                tv_byFace.setClickable(true);
                tv_byFace.setEnabled(true);
                tv_byFace.setTextColor(Color.WHITE);

                if (et_Password.getText().toString().trim().length() > 0) {
                    btnLogin.setClickable(true);
                    btnLogin.setEnabled(true);
                    btnLogin.setTextColor(getResources().getColor(R.color.common_bg_color));
                } else {
                    btnLogin.setClickable(false);
                    btnLogin.setEnabled(false);
                    btnLogin.setTextColor(Color.GRAY);
                }
            } else {
                tv_byFace.setClickable(false);
                tv_byFace.setEnabled(false);
                tv_byFace.setTextColor(Color.GRAY);
                //
                btnLogin.setClickable(false);
                btnLogin.setEnabled(false);
                btnLogin.setTextColor(Color.GRAY);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    /**
     * （3）密码监听，登录按钮变色
     */
    public TextWatcher textPasswordListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (et_storeId.getText().toString().trim().length() > 1 && // 公司编号
                    et_UserName.getText().toString().trim().length() > 1 && // 工号
                    et_Password.getText().toString().trim().length() > 0) {// 密码
                btnLogin.setClickable(true);
                btnLogin.setEnabled(true);
                btnLogin.setTextColor(getResources().getColor(R.color.common_bg_color));
            } else {
                btnLogin.setClickable(false);
                btnLogin.setEnabled(false);
                btnLogin.setTextColor(Color.GRAY);
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

}
