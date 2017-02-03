package com.yvision;

import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.yvision.common.MyApplication;
import com.yvision.inject.ViewInject;
import com.yvision.utils.ConfigUtil;
import com.yvision.utils.PageUtil;
import com.yvision.widget.WifiTopbar;

/**
 * Created by JackSong on 2016/9/8.
 */
public class WifiSettingActivity extends BaseActivity {

    //无线网名称
    @ViewInject(id = R.id.tv_wifiDetailName)
    TextView tv_wifiDetailName;

    //设备ID
    @ViewInject(id = R.id.tv_ID)
    TextView tv_ID;

    //保存
    @ViewInject(id = R.id.btn_save,click = "toSave")
    Button btnSave;
    //变量
    private String macAdress;//手机本身网卡的mac地址
    private String BSSID;//所连接的WIFI设备的MAC地址
    private String SSID;//wifi名称
    private int ipAddress;//ip地址
    private WifiManager wifiManager;
    private  WifiInfo wifiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_setting_wifi);

        WifiTopbar topbar = (WifiTopbar)findViewById(R.id.wifiTopbar);
        topbar.setOnTopbarClickListener(new WifiTopbar.TopbarClickListener(){
            //back
            @Override
            public void leftClick() {
                WifiSettingActivity.this.finish();
            }
            //刷新
            @Override
            public void rightclick() {
                wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                wifiInfo = wifiManager.getConnectionInfo();
                if(wifiInfo.getRssi() == -200){
                    AlertDialog.Builder builder = new AlertDialog.Builder(WifiSettingActivity.this);
                    builder.setTitle("消息提示：").
                            setMessage("请检查设备，无法连接wifi").
                            setNegativeButton("取消",null).setPositiveButton("确定",null).show();
                }else {
                    initData();
                    initViews();
                    PageUtil.toastInMiddle("已获取wifi信息，请保存");
                }
            }
        });
    }

    private void initData(){
        macAdress = wifiInfo.getMacAddress();
        BSSID = wifiInfo.getBSSID();
        SSID = wifiInfo.getSSID();
        ipAddress = wifiInfo.getIpAddress();
    }

    private void initViews(){
        tv_wifiDetailName.setText(SSID);
        tv_ID.setText(macAdress);
        Log.d("SJY","BSSID="+BSSID+"--ipAddress="+ipAddress+"--Rssi="+wifiInfo.getRssi());

    }
    /**
     * 保存
     */
    public void toSave(View view){
        if(wifiInfo.getRssi() == -200){
            AlertDialog.Builder builder = new AlertDialog.Builder(WifiSettingActivity.this);
            builder.setTitle("消息提示：").
                    setMessage("请点击刷新，或者查看wifi连接").
                    setNegativeButton("取消",null).setPositiveButton("确定",null).show();
        }else{
            //保存
            ConfigUtil configUtil = new ConfigUtil(MyApplication.getInstance());
            configUtil.setWifiName(SSID);
            configUtil.setMacAddress(macAdress);
            PageUtil.toastInMiddle("已保存！");
        }

    }
}
