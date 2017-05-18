package com.yvision.view;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
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
import com.yvision.model.StoreEmployeeModel;
import com.yvision.utils.PageUtil;
import com.yvision.widget.RefreshAndLoadListView;
import com.yvision.widget.SelectSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.yvision.R.id.spinner_name;

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

    //人员1
    @ViewInject(id = spinner_name)
    SelectSpinner spinnerName;

    //人员2
    @ViewInject(id = R.id.tv_name)
    TextView tv_name;

    //变量
    private AttendListAdapter uAdapter;
    //spinner
    private List<String> spinnerTimeData;
    private List<String> spinnerTypeData;
    private ArrayList<String> spinnerNameData;

    private boolean ifLoading = false;//标记
    private static String pageSize = "20";//返回数据个数
    private static String attendType = "4";//考勤方式--1:local 2:map3:wifi 4:all
    private static String timespan = "4";//时间筛选方式--1:today 3:this month 2:this week 4: all
    private static String employeeId = "";//默认为空，获取的数据为全部员工记录
    private List<AttendModel> list = null;
    ArrayList<StoreEmployeeModel> listName;//人员（管理权限）

    //地图考勤使用
    private EmployeeInfoModel employeeInfoModel = null;

    private String minTime = "";
    private String maxTime = "";

    //常量
    private static final int ADDONEWIFI_SUCCESS = 30;
    private static final String TAG = "MainAttendActivity";
    private static final int ADDONEWIFI_FAILED = 31;

    private static final int GET_NEW_DATA = -41;// 获取所有数据列表 标志
    private static final int LOAD_MORE_SUCCESS = -42;//
    private static final int REFRESH_SUCCESS = -40;//
    private static final int GET_NONE_ATTENDDATA = -43;//

    private static final int GET_EMPLOYEE_DATA = -44;//
    private static final int GET_NONE_EMPLOYEE_DATA = -45;//
    private static final int GET_EMPLOYEE_NAME_LIST = -46;//
    private static final int GET_EMPLOYEE_NAME_LIST_FAILED = -47;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_usermain);

        initMyView();//控件实例化
        isEmployeeNameNULL();//登录返回是否有employeeName,有--》,布局更换为人名，无--》布局更换为spinner

        getEmployeeDate();//获取跳转

        initListener();
        getData();

    }


    public void initMyView() {
        tv_title.setText(getResources().getString(R.string.attendTile));
        //        tv_right.setText(getResources().getString(R.string.wifiAttend));
        tv_right.setText("地图考勤");

        //spinner绑定数据
        spinnerTimeData = new LinkedList<>(Arrays.asList("全部时间", "今日", "本周", "本月"));
        spinnerTime.attachDataSource(spinnerTimeData);//绑定数据

        spinnerTypeData = new LinkedList<>(Arrays.asList("全部方式", "普通", "地图"));
        spinnerType.attachDataSource(spinnerTypeData);//绑定数据

        spinnerNameData = new ArrayList<>();
        //自定义listVIew监听
        listView.setILoadMoreListener(this);
        listView.setIRefreshListener(this);
        uAdapter = new AttendListAdapter(this);
        listView.setAdapter(uAdapter);
    }

    private void isEmployeeNameNULL() {
        employeeId = UserHelper.getCurrentUser().getEmployeeId();

        Log.d(TAG, "isEmployeeNameNULL: 判断登录返回EmployeeName" + TextUtils.isEmpty(employeeId));
        //空
        if (TextUtils.isEmpty(UserHelper.getCurrentUser().getEmployeeId())) {
            //显示spinner
            tv_name.setVisibility(View.GONE);
            spinnerName.setVisibility(View.VISIBLE);

            //获取数据
            getEmployeeNameListDate();
        } else {
            //显示人名
            tv_name.setVisibility(View.VISIBLE);
            spinnerName.setVisibility(View.GONE);
            tv_name.setText(TextUtils.isEmpty(UserHelper.getCurrentUser().getEmployeeName()) ? "返回值为空" : UserHelper.getCurrentUser().getEmployeeName());
        }
    }

    private void initListener() {

        //spinner监听，筛选数据
        spinnerTime.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "spinner监听--" + spinnerTimeData.get(position));
                getSelectTimeData(spinnerTimeData.get(position).trim());//参数2必填GET_NEW_DATA

            }
        });

        spinnerType.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "spinner监听--" + spinnerTypeData.get(position));
                getSelectTypeData(spinnerTypeData.get(position).trim());//参数2必填GET_NEW_DATA

            }
        });

        spinnerName.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "spinner监听--" + spinnerNameData.get(position));
                getSelectNameData(spinnerNameData.get(position));
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

    //获取人员（管理员权限）
    private void getEmployeeNameListDate() {
        Loading.noDialogRun(this, new Runnable() {
            @Override
            public void run() {
                try {

                    ArrayList<StoreEmployeeModel> list = UserHelper.getEmployeeListNameByStoreID(MainAttendActivity.this
                            , UserHelper.getCurrentUser().getStoreID()
                            , "1");
                    Log.d(TAG, "run: " + list.size());
                    sendMessage(GET_EMPLOYEE_NAME_LIST, list);
                } catch (MyException e) {
                    e.printStackTrace();
                    sendMessage(GET_EMPLOYEE_NAME_LIST_FAILED, e.getMessage());
                }

            }
        });
    }

    //根据时间筛选数据
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

    //根据类型筛选数据
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
            //            case "wifi":
            //                uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载今天数据
            //                listView.setAdapter(uAdapter);
            //                styleForWifi();
            //                getData();
            //                break;
        }
    }

    //根据名字筛选数据
    private void getSelectNameData(final String nameSpinner) {
        Log.d(TAG, "getSelectNameData: 1");
        if (listName == null || listName.size() <= 0) {
            Log.d(TAG, "getSelectNameData:listName被回收 ");
        }
        if (nameSpinner.equals("全部")) {
            Log.d(TAG, "getSelectNameData: 选择员工=" + nameSpinner);
            employeeId = "";
            uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载全部数据
            listView.setAdapter(uAdapter);
            getData();
        } else {
            Log.d(TAG, "getSelectNameData: 2");
            //遍历的耗时操作
            //            for (StoreEmployeeModel a : listName) {
            //                if (a.getEmployeeName().trim().contains(nameSpinner)) {
            //                    employeeId = a.getEmployeeId();
            //                    //                            sendMessage(SEARCH_EMPLOYEEID,employeeId);
            //                    uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载全部数据
            //                    listView.setAdapter(uAdapter);
            //                    getData();
            //                }
            //            }
            for (int i = 0; i < listName.size(); i++) {

                if (listName.get(i).getEmployeeName().trim().contains(nameSpinner)) {
                    Log.d(TAG, "getSelectNameData: 选择员工=" + nameSpinner + "--对应id=" + list.get(i).getEmployeeID());
                    employeeId = list.get(i).getEmployeeID();
                    uAdapter = new AttendListAdapter(MainAttendActivity.this);// 加载全部数据
                    listView.setAdapter(uAdapter);
                    getData();
                }
            }


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
        Log.d(TAG, "getData: 获取数据employeeid状态=" + employeeId);
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
                            employeeId,
                            attendType);
                    Log.d(TAG, "max=" + MyApplication.getInstance().iMaxTime + "/n" + ",min=" + MyApplication.getInstance().iMinTime);
                    if (userModelList == null || userModelList.size() < Integer.parseInt(pageSize)) {

                        uAdapter.IsEnd = true;
                    }
                    sendMessage(GET_NEW_DATA, userModelList);

                } catch (MyException e) {
                    Log.d(TAG, "getdata异常=" + e.getMessage());
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
                    Log.d(TAG, "上拉拼接minTime=" + minTime);
                    List<AttendModel> userModelList = UserHelper.GetAttendanceRecordByPage(
                            MainAttendActivity.this,
                            "",
                            minTime,
                            pageSize,
                            timespan,
                            employeeId,
                            attendType);

                    if (userModelList == null || userModelList.size() < Integer.parseInt(pageSize)) {
                        uAdapter.IsEnd = true;
                    }
                    sendMessage(LOAD_MORE_SUCCESS, userModelList);

                } catch (MyException e) {
                    Log.d(TAG, "getdata异常=" + e.getMessage());
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
                    Log.d(TAG, "上拉拼接minTime=" + minTime);
                    List<AttendModel> userModelList = UserHelper.GetAttendanceRecordByPage(
                            MainAttendActivity.this,
                            maxTime,
                            "",
                            pageSize,
                            timespan,
                            employeeId,
                            attendType);

                    if (userModelList == null || (userModelList.size() < Integer.parseInt(pageSize))) {
                        uAdapter.IsEnd = true;
                    }
                    sendMessage(REFRESH_SUCCESS, userModelList);
                } catch (MyException e) {
                    Log.d(TAG, "getdata异常=" + e.getMessage());
                    sendMessage(GET_NONE_ATTENDDATA, e.getMessage());
                }
            }
        });
    }

    private void getEmployeeDate() {
        if (TextUtils.isEmpty(UserHelper.getCurrentUser().getEmployeeId())) {
            Log.d(TAG, "获取employeeid = null");
            return;
        }
        Log.d(TAG, "获取employeeid != null");

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

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case GET_EMPLOYEE_NAME_LIST:
                //将list转换成数组，显示到自定义spinner中供选择
                listName = (ArrayList<StoreEmployeeModel>) msg.obj;
                spinnerName.attachDataSource(getByteArray(listName));//绑定数据
                break;
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
            case GET_EMPLOYEE_NAME_LIST_FAILED:
                PageUtil.DisplayToast((String) msg.obj + "获取管理权限下的人员数据出错");
            default:
                break;
        }
        super.handleMessage(msg);
    }

    //获取人员（管理者权限）后，取出名称当做数组
    private ArrayList<String> getByteArray(ArrayList<StoreEmployeeModel> list) {
        for (int i = 0; i < list.size(); i++) {
            spinnerNameData.add(list.get(i).getEmployeeName());
        }
        spinnerNameData.add(0, "全部");
        Log.d(TAG, "getByteArray: 转换成数组spinnerNameData=" + spinnerNameData.size());
        return spinnerNameData;
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

    //    public void styleForWifi() {
    //        setAttendType("3");
    //    }

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
