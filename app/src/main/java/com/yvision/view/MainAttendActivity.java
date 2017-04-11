package com.yvision.view;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.adapter.AttendListAdapter;
import com.yvision.common.MyApplication;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.AttendModel;
import com.yvision.model.EmployeeInfoModel;
import com.yvision.utils.PageUtil;
import com.yvision.widget.RefreshAndLoadListView;
import com.yvision.widget.SelectSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 考勤主界面
 */
public class MainAttendActivity extends BaseActivity implements RefreshAndLoadListView.IReflashListener, RefreshAndLoadListView.ILoadMoreListener {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right, click = "addAtendanceByMap")
    TextView tv_right;

    //listView
    @ViewInject(id = R.id.UserList)
    RefreshAndLoadListView listView;

    //时间
    @ViewInject(id = R.id.spinner_time)
    SelectSpinner spinnerTime;

    //方式
    @ViewInject(id = R.id.spinner_style)
    SelectSpinner spinnerType;

    //时间
    @ViewInject(id = R.id.spinner_time)
    Spinner spinner_time;

    //方式
    @ViewInject(id = R.id.spinner_style)
    Spinner spinner_style;

    //变量
    private AttendListAdapter uAdapter;
    //spinner
    private List<String> spinnerTimeData;
    private List<String> spinnerTypeData;

    private boolean ifLoading = false;//标记
    private static String pageSize = "20";//返回数据个数
    private static String attendType = "4";//考勤方式--1:local 2:map3:wifi 4:all
    private static String timespan = "4";//时间筛选方式--1:today 3:this month 2:this week 4: all
    private List<AttendModel> list = null;

    //地图考勤使用
    private EmployeeInfoModel employeeInfoModel = null;

    private String minTime = "";
    private String maxTime = "";

    //常量
    private static final int ADDONEWIFI_SUCCESS = 30;
    private static final int ADDONEWIFI_FAILED = 31;

    private static final int GET_NEW_DATA = -41;// 获取所有数据列表 标志
    private static final int LOAD_MORE_SUCCESS = -42;//
    private static final int REFRESH_SUCCESS = -22;//
    private static final int GET_NONE_ATTENDDATA = -43;//

    private static final int GET_EMPLOYEE_DATA = -44;//
    private static final int GET_NONE_EMPLOYEE_DATA = -45;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_usermain);

        initMyView();//控件实例化
        getEmployeeDate();//获取跳转

        initListener();
        getData();
    }

    public void initMyView() {
        tv_title.setText(getResources().getString(R.string.attendTile));
        //        tv_right.setText(getResources().getString(R.string.wifiAttend));
        tv_right.setText("地图签到");
        //spinner绑定数据
        spinnerTimeData = new LinkedList<>(Arrays.asList("全部时间", "今日", "本周", "本月"));
        spinnerTime.attachDataSource(spinnerTimeData);//绑定数据

        spinnerTypeData = new LinkedList<>(Arrays.asList("全部方式", "普通", "地图", "wifi"));
        spinnerType.attachDataSource(spinnerTypeData);//绑定数据

        //自定义listVIew监听
        listView.setILoadMoreListener(this);
        listView.setIRefreshListener(this);
        uAdapter = new AttendListAdapter(this);
        listView.setAdapter(uAdapter);
    }

    private void initListener() {

        //spinner监听，筛选数据
        spinnerTime.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SJY", "spinner监听--" + spinnerTimeData.get(position));
                getSelectTimeData(spinnerTimeData.get(position).trim());//参数2必填GET_NEW_DATA

            }
        });

        spinnerType.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SJY", "spinner监听--" + spinnerTimeData.get(position));
                getSelectTypeData(spinnerTypeData.get(position).trim());//参数2必填GET_NEW_DATA

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int headerViewsCount = listView.getHeaderViewsCount();//得到header的总数量
                int newPosition = position - headerViewsCount;//得到新的修正后的position

                AttendModel model = (AttendModel) uAdapter.getItem(newPosition);

                Bundle bundle = new Bundle();
                bundle.putString("AttendID", model.getAttendID());
                startActivity(AttendInfoActivity.class, bundle);
            }
        });
    }

    private void getSelectTimeData(String timeSpinner) {
        switch (timeSpinner) {
            case "全部时间":
                //                uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载全部数据
                //                listView.setAdapter(uAdapter);
                timeForAll();
                getData();
                break;
            case "今日":
                uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载今天数据
                listView.setAdapter(uAdapter);
                timeForToday();
                getData();
                break;
            case "本周":
                uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载今天数据
                listView.setAdapter(uAdapter);
                timeForWekend();
                getData();
                break;
            case "本月":
                uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载今天数据
                listView.setAdapter(uAdapter);
                timeForMonth();
                getData();
                break;
        }

    }

    private void getSelectTypeData(String timeSpinner) {
        switch (timeSpinner) {
            case "全部方式":
                uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载全部数据
                listView.setAdapter(uAdapter);
                styleForAll();
                getData();
                break;
            case "普通":
                uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载今天数据
                listView.setAdapter(uAdapter);
                styleForLocal();
                getData();
                break;
            case "地图":
                uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载今天数据
                listView.setAdapter(uAdapter);
                styleForMap();
                getData();
                break;
            case "wifi":
                uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载今天数据
                listView.setAdapter(uAdapter);
                styleForWifi();
                getData();
                break;
        }
    }

    /**
     * 获取全部记录
     */
    private void getData() {
        if (ifLoading) {
            return;
        }
        maxTime = "";
        minTime = "";

        Loading.run(MainAttendActivity.this, new Runnable() {
            @Override
            public void run() {
                ifLoading = true;
                //清除数据处理
                try {
                    uAdapter.IsEnd = false;
                    List<AttendModel> userModelList = UserHelper.GetAttendanceRecordByPage(
                            MainAttendActivity.this,
                            maxTime,
                            minTime,
                            pageSize,
                            timespan,
                            attendType);
                    Log.d("SJY", "max=" + MyApplication.getInstance().iMaxTime + "/n" + ",min=" + MyApplication.getInstance().iMinTime);
                    if (userModelList == null || userModelList.size() < Integer.parseInt(pageSize)) {

                        uAdapter.IsEnd = true;
                    }
                    sendMessage(GET_NEW_DATA, userModelList);

                } catch (MyException e) {
                    Log.d("SJY", "getdata异常=" + e.getMessage());
                    sendMessage(GET_NONE_ATTENDDATA, e.getMessage());
                }
            }
        });
    }


    /**
     * 加载
     */
    @Override
    public void onLoadMore() {
        if (ifLoading) {
            return;
        }

        Loading.noDialogRun(MainAttendActivity.this, new Runnable() {
            @Override
            public void run() {
                ifLoading = true;//
                try {
                    Log.d("SJY", "上拉拼接minTime=" + minTime);
                    List<AttendModel> userModelList = UserHelper.GetAttendanceRecordByPage(
                            MainAttendActivity.this,
                            "",
                            minTime,
                            pageSize,
                            timespan,
                            attendType);

                    if (userModelList == null || userModelList.size() < Integer.parseInt(pageSize)) {
                        uAdapter.IsEnd = true;
                    }
                    sendMessage(LOAD_MORE_SUCCESS, userModelList);

                } catch (MyException e) {
                    Log.d("SJY", "getdata异常=" + e.getMessage());
                    sendMessage(GET_NONE_ATTENDDATA, e.getMessage());
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (ifLoading) {
            return;
        }

        Loading.noDialogRun(MainAttendActivity.this, new Runnable() {
            @Override
            public void run() {
                ifLoading = true;//
                try {
                    Log.d("SJY", "上拉拼接minTime=" + minTime);
                    List<AttendModel> userModelList = UserHelper.GetAttendanceRecordByPage(
                            MainAttendActivity.this,
                            maxTime,
                            "",
                            pageSize,
                            timespan,
                            attendType);

                    if (userModelList == null || (userModelList.size() < Integer.parseInt(pageSize))) {
                        uAdapter.IsEnd = true;
                    }
                    sendMessage(REFRESH_SUCCESS, userModelList);
                } catch (MyException e) {
                    Log.d("SJY", "getdata异常=" + e.getMessage());
                    sendMessage(GET_NONE_ATTENDDATA, e.getMessage());
                }
            }
        });
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case GET_NEW_DATA://
                // 数据显示
                list = (List<AttendModel>) msg.obj;
                uAdapter.setEntityList(list);
                //数据处理，获取iLastUpdateTime参数方便后续上拉/下拉使用
                setMinTime(list);
                setMaxTime(list);
                ifLoading = false;
                listView.loadAndFreshComplete();
                break;

            case LOAD_MORE_SUCCESS://加载
                list = (ArrayList<AttendModel>) msg.obj;
                uAdapter.addEntityList(list);
                //数据处理，只存最小值
                setMinTime(list);
                ifLoading = false;
                listView.loadAndFreshComplete();
                break;

            case REFRESH_SUCCESS://刷新
                list = (ArrayList<AttendModel>) msg.obj;
                uAdapter.insertEntityList(list);
                //数据处理，只存最小值
                setMaxTime(list);
                ifLoading = false;
                listView.loadAndFreshComplete();
                break;

            case GET_NONE_ATTENDDATA://没有获取新数据
                PageUtil.DisplayToast((String) msg.obj);
                ifLoading = false;
                listView.loadAndFreshComplete();
                break;

            case GET_EMPLOYEE_DATA://获取employee信息
                employeeInfoModel = (EmployeeInfoModel) msg.obj;
                ifLoading = false;
                break;

            case GET_NONE_EMPLOYEE_DATA://获取employee为空，设置界面不显示
                sendToastMessage((String) msg.obj);
                ifLoading = false;
                break;
            case ADDONEWIFI_SUCCESS://添加wifi签到
                sendToastMessage("添加成功");
                break;
            case ADDONEWIFI_FAILED://添加wifi签到
                PageUtil.DisplayToast((String) msg.obj);
                break;
            default:
                break;
        }
        super.handleMessage(msg);
    }

    private void getEmployeeDate() {
        if (TextUtils.isEmpty(UserHelper.getCurrentUser().getEmployeeId())) {
            Log.d("SJY", "获取employeeid = null");
            return;
        }
        Log.d("SJY", "获取employeeid != null");

        Loading.run(MainAttendActivity.this, new Runnable() {
            @Override
            public void run() {
                try {
                    EmployeeInfoModel employeeInfoModel = UserHelper.GetEmployeeInfoByID(MainAttendActivity.this);

                    sendMessage(GET_EMPLOYEE_DATA, employeeInfoModel);
                } catch (MyException e) {
                    Log.d("SJY", "EmployeeInfoModel异常：" + e.getMessage());
                    sendMessage(GET_NONE_EMPLOYEE_DATA, e.getMessage());
                }
            }
        });
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
        if (TextUtils.isEmpty(UserHelper.getCurrentUser().getEmployeeId())) {
            Log.d("SJY", "employeeid=" + UserHelper.getCurrentUser().getEmployeeId());
            PageUtil.DisplayToast("非员工不可地图考勤");
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("EmployeeInfoModel", employeeInfoModel);
            startActivity(MapAttendedActivity.class, bundle);
        }
    }

    //设置attendType
    public void styleForAll() {
        setAttendType("4");
    }

    public void styleForLocal() {
        setAttendType("1");
    }

    public void styleForMap() {
        setAttendType("2");
    }

    public void styleForWifi() {
        setAttendType("3");
    }

    //设置 timespan
    public void timeForAll() {
        setTimespan("4");
    }

    public void timeForToday() {
        setTimespan("1");
    }

    public void timeForWekend() {
        setTimespan("2");
    }

    public void timeForMonth() {
        setTimespan("3");
    }

    public void setTimespan(String timespan) {
        this.timespan = timespan;
    }

    public void setAttendType(String attendType) {
        this.attendType = attendType;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    private void setMinTime(List<AttendModel> list) {
        if (list.size() > 0) {
            AttendModel model = list.get(list.size() - 1);//获取最后一条记录
            setMinTime(model.getCapTime());
        }
    }

    private void setMaxTime(List<AttendModel> list) {
        if (list.size() > 0) {
            AttendModel model = list.get(0);//获取第一条记录
            setMaxTime(model.getCapTime());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        if (uAdapter != null) {
        //            uAdapter.destroy();
        //        }
    }

}
