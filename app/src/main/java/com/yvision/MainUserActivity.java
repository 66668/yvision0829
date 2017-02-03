package com.yvision;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yvision.adapter.CommonListAdapter;
import com.yvision.adapter.MainSpinnerAdapter;
import com.yvision.adapter.UserListAdapter;
import com.yvision.common.MyApplication;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.AttendModel;
import com.yvision.model.AttendParModel3;
import com.yvision.model.EmployeeInfoModel;
import com.yvision.utils.ConfigUtil;
import com.yvision.utils.PageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 考勤系统主界面
 */
public class MainUserActivity extends BaseActivity{
    //back
    @ViewInject(id = R.id.img_back, click = "forBack")
    RelativeLayout img_back;

    //添加地图签到
    @ViewInject(id = R.id.btn_addMapAttendance, click = "addAtendanceByMap")
    Button btn_addMapAttendance;

    //添加wifi 签到
    @ViewInject(id = R.id.tv_addWifiAttend, click = "addAttendByWifi")
    TextView tv_addWifiAttend;

    //时间
    @ViewInject(id = R.id.spinner_time)
    Spinner spinner_time;

    //方式
    @ViewInject(id = R.id.spinner_style)
    Spinner spinner_style;
    //变量
    private ListView listView;
    private UserListAdapter uAdapter;
    private ArrayAdapter<String> spinnerTimeAdapter;//time spinner
    private String[] spinnerArrayTime;
    private ArrayAdapter<String> spinnerStyleAdapter;//style spinner
    private String[] spinnerArrayStyle;
    private AttendParModel3 attendParModel3;

    private boolean ifLoading = false;//标记
    private static int pageSize = 20;//返回数据个数
    private static String employeeID;//员工ID
    private static int attendType = 4;//考勤方式--1:local 2:map3:wifi 4:all
    private static int timespan = 4;//时间筛选方式--1:today 3:this month 2:this week 4: all
    private ArrayList list = null;
    private EmployeeInfoModel employeeInfoModel = null;
    private static String storeID;
    private static String minTime = "";
    private static String maxTime = "";
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private String currentSSID;

    //常量
    private static final int ADDONEWIFI_SUCCESS = 30;
    private static final int ADDONEWIFI_FAILED = 31;
    private static final int GET_ATTEND_SUCCESS = -41;// 获取所有数据列表 标志
    private static final int LOAD_MORE_ATTEND_SUCCESS = -42;//
    private static final int GET_NONE_ATTENDDATA = -43;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_usermain);

        employeeInfoModel = (EmployeeInfoModel) getIntent().getSerializableExtra("EmployeeInfoModel");//获取跳转
        employeeID = employeeInfoModel.getEmployeeID();//获取员工ID
        storeID = UserHelper.getCurrentUser().getStoreID();

        initVariaies();//控件实例化
        uAdapter = new UserListAdapter(this, adapterCallBack);
        listView.setAdapter(uAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("SJY", "MainUserActivity--item--position="+position);
                int headerViewsCount = listView.getHeaderViewsCount();//得到header的总数量
                int newPosition = position - headerViewsCount;//得到新的修正后的position
                AttendModel model = (AttendModel) uAdapter.getItem(newPosition);
                Intent intent = new Intent(MainUserActivity.this, UserInfoActivity.class);
                intent.putExtra("AttendModel", model);
                startActivity(intent);
            }
        });

        getData();
    }

    public void initVariaies() {
        listView = (ListView) findViewById(R.id.UserList);

        //time spinner
        spinnerArrayTime = getResources().getStringArray(R.array.forTime);
        spinnerTimeAdapter = new MainSpinnerAdapter(MainUserActivity.this, spinnerArrayTime);// 使用自定义的ArrayAdapter
        spinnerTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);// 设置下拉列表风格()
        spinner_time.setAdapter(spinnerTimeAdapter);
        spinner_time.setOnItemSelectedListener(new SpinnerTimeItemSelectedListener());// 监听Item选中事件

        // style spinner
        spinnerArrayStyle = getResources().getStringArray(R.array.forstyle);
        spinnerStyleAdapter = new MainSpinnerAdapter(MainUserActivity.this, spinnerArrayStyle);// 使用自定义的ArrayAdapter
        spinnerStyleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);// 设置下拉列表风格()
        spinner_style.setAdapter(spinnerStyleAdapter);
        spinner_style.setOnItemSelectedListener(new SpinnerStyleItemSelectedListener());
    }

    /**
     * time spinner
     */
    private class SpinnerTimeItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
            switch (position) {
                case 0://全部
                    uAdapter = new UserListAdapter(MainUserActivity.this, adapterCallBack);// 加载全部数据
                    listView.setAdapter(uAdapter);
                    timeForAll();
                    getData();
                    break;
                case 1://今天
                    uAdapter = new UserListAdapter(MainUserActivity.this, adapterCallBack);// 加载今天数据
                    listView.setAdapter(uAdapter);
                    timeForToday();
                    getData();
                    break;
                case 2://本周
                    uAdapter = new UserListAdapter(MainUserActivity.this, adapterCallBack);// 加载今天数据
                    listView.setAdapter(uAdapter);
                    timeForWekend();
                    getData();
                    break;
                case 3://本月
                    uAdapter = new UserListAdapter(MainUserActivity.this, adapterCallBack);// 加载今天数据
                    listView.setAdapter(uAdapter);
                    timeForMonth();
                    getData();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    /**
     * style spinner
     */
    private class SpinnerStyleItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
            switch (position) {
                case 0://全部
                    uAdapter = new UserListAdapter(MainUserActivity.this, adapterCallBack);// 加载全部数据
                    listView.setAdapter(uAdapter);
                    styleForAll();
                    getData();
                    break;
                case 1://local
                    uAdapter = new UserListAdapter(MainUserActivity.this, adapterCallBack);// 加载今天数据
                    listView.setAdapter(uAdapter);
                    styleForLocal();
                    getData();
                    break;
                case 2://地图
                    uAdapter = new UserListAdapter(MainUserActivity.this, adapterCallBack);// 加载今天数据
                    listView.setAdapter(uAdapter);
                    styleForMap();
                    getData();
                    break;
                case 3://wifi
                    uAdapter = new UserListAdapter(MainUserActivity.this, adapterCallBack);// 加载今天数据
                    listView.setAdapter(uAdapter);
                    styleForWifi();
                    getData();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    // 上拉拼接旧记录
    CommonListAdapter.AdapterCallBack adapterCallBack = new CommonListAdapter.AdapterCallBack() {
        @Override
        public void loadMore() {

            if (ifLoading) {
                return;
            }

            Loading.run(MainUserActivity.this, new Runnable() {
                @Override
                public void run() {
                    ifLoading = true;//
                    try {
                        Log.d("SJY", "上拉拼接minTime=" + minTime);
                        List<AttendModel> userModelList = UserHelper.GetAttendanceRecordByPage(
                                MainUserActivity.this,
                                "",
                                minTime,
                                pageSize,
                                timespan,
                                storeID,
                                employeeID,
                                attendType);

                        if (userModelList == null) {
                            uAdapter.IsEnd = true;
                        } else if (userModelList.size() < pageSize) {
                            uAdapter.IsEnd = true;
                        }
                        sendMessage(LOAD_MORE_ATTEND_SUCCESS, userModelList);
                        if (userModelList.size() > 0) {

                        } else {

                        }
                    } catch (MyException e) {
                        sendMessage(GET_NONE_ATTENDDATA, e.getMessage());
                        ifLoading = false;
                    }
                }
            });

        }
    };

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case GET_ATTEND_SUCCESS://进入页面加载最新
                // 数据显示
                list = (ArrayList) msg.obj;
                uAdapter.setEntityList(list);
                //数据处理，获取iLastUpdateTime参数方便后续上拉/下拉使用
                setMinTime(list);
                setMaxTime(list);
                ifLoading = false;
                break;
            case LOAD_MORE_ATTEND_SUCCESS://加载全部/今天
                list = (ArrayList) msg.obj;
                uAdapter.addEntityList(list);
                //数据处理，只存最小值
                setMinTime(list);
                ifLoading = false;
                break;
            case GET_NONE_ATTENDDATA://没有获取新数据
               PageUtil.DisplayToast((String)msg.obj);
                ifLoading = false;
                break;
            case ADDONEWIFI_SUCCESS://添加wifi签到
                sendToastMessage("添加成功");
                break;
            case ADDONEWIFI_FAILED://添加wifi签到
                PageUtil.DisplayToast((String)msg.obj);
                break;
            default:
                break;
        }
        super.handleMessage(msg);
    }

    // 获取minTime，上拉加载使用
    private void setMinTime(ArrayList list) {
        if (list.size() > 0) {
            AttendModel model = (AttendModel) list.get(list.size() - 1);//获取最后一条记录
            setMinTime(model.getCapTime());
        }
    }

    // 获取全部记录
    private void getData() {
        if (ifLoading) {
            return;
        }
        setMaxTime("");
        setMinTime("");
        Loading.run(MainUserActivity.this, new Runnable() {
            @Override
            public void run() {
                ifLoading = true;
                //清除数据处理

                try {
                    uAdapter.IsEnd = false;
                    List<AttendModel> userModelList = UserHelper.GetAttendanceRecordByPage(
                            MainUserActivity.this,
                            maxTime,
                            minTime,
                            pageSize,
                            timespan,
                            storeID,
                            employeeID,
                            attendType);
                    Log.d("SJY", "max=" + MyApplication.getInstance().iMaxTime + "/n" + ",min=" + MyApplication.getInstance().iMinTime);
                    if (userModelList == null) {
                        uAdapter.IsEnd = true;
                    } else if (userModelList.size() < pageSize) {
                        uAdapter.IsEnd = true;
                    }
                    sendMessage(GET_ATTEND_SUCCESS, userModelList);

                } catch (MyException e) {
                    sendMessage(GET_NONE_ATTENDDATA, e.getMessage());
                }
            }
        });
    }

    //获取maxTime,下拉刷新使用
    private void setMaxTime(ArrayList list) {
        if (list.size() > 0) {
            AttendModel model = (AttendModel) list.get(0);//获取第一条记录
            setMaxTime(model.getCapTime());
        }
    }

    /**
     * back
     */
    public void forBack(View view) {
        this.finish();
    }

    /**
     * 添加地图考勤
     */
    public void addAtendanceByMap(View view) {
        startActivity(MapAttendedActivity.class);
    }

    /**
     * 添加wifi考勤
     */
    public void addAttendByWifi(View view) {
        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        ConfigUtil configUtil = new ConfigUtil(MyApplication.getInstance());
        if(wifiInfo.getRssi() == -200){//没联网
            AlertDialog.Builder builder = new AlertDialog.Builder(MainUserActivity.this);
            builder.setTitle("消息提示：").
                    setMessage("请检查设备，无法连接wifi").
                    setNegativeButton("取消",null).setPositiveButton("确定",null).show();
            return;
        }else {
            currentSSID=wifiInfo.getSSID();//
        }

        if(configUtil.getWifiName().equals(currentSSID)){
            Loading.run(this, new Runnable() {
                @TargetApi(Build.VERSION_CODES.N)
                @Override
                public void run() {
                    attendParModel3 = AttendParModel3.getInstance();//单例模式
                    //capTime
                    Time time = new Time();//"GMT+8"
                    time.setToNow();
                    int year = time.year;
                    int month = time.month + 1;
                    int day = time.monthDay;
                    int minute = time.minute;
                    int hour = time.hour;
                    int sec = time.second;
                    String capTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec;

                    //Builder模式
                    attendParModel3.buildCapTime(capTime).
                            buildCompanyID(employeeInfoModel.getStoreID()).//??
                            buildDepartmentID(employeeInfoModel.getDepartmentID()).
                            buildEmployeeName(employeeInfoModel.getEmployeeName()).
                            buildEmployeeID(employeeID).
                            buildWrokId(employeeInfoModel.getWrokId()).
                            create();
                    final String jsonStr = new Gson().toJson(attendParModel3);
                    try {
                        String msg = UserHelper.addOneWIFIAttendanceRecord(MainUserActivity.this, jsonStr);
                        sendMessage(ADDONEWIFI_SUCCESS, msg);
                        Log.d("SJY", "wifi签到msg=" + msg);
                    } catch (MyException e) {
                        sendMessage(ADDONEWIFI_FAILED, e.getMessage());
                    }

                }
            });
        }else {
            PageUtil.toastInMiddle("请到主界面设置中配置-wifi信息");
            return;
        }

    }

    public void styleForAll() {
        setAttendType(4);
    }

    public void styleForLocal() {
        setAttendType(1);
    }

    public void styleForMap() {
        setAttendType(2);
    }

    public void styleForWifi() {
        setAttendType(3);
    }

    public void timeForAll() {
        setTimespan(4);
    }

    public void timeForToday() {
        setTimespan(1);
    }

    public void timeForWekend() {
        setTimespan(2);
    }

    public void timeForMonth() {
        setTimespan(3);
    }

    public void setTimespan(int timespan) {
        this.timespan = timespan;
    }

    public void setAttendType(int attendType) {
        this.attendType = attendType;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (uAdapter != null) {
            uAdapter.destroy();
        }
    }

}
