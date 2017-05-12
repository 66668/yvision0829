package com.yvision.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.adapter.MainSpinnerAdapter;
import com.yvision.adapter.VisitorListAdapter;
import com.yvision.common.MyApplication;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.VisitorBModel;
import com.yvision.widget.NiceSpinner;
import com.yvision.widget.RefreshAndLoadListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.yvision.dialog.Loading.noDialogRun;
import static com.yvision.dialog.Loading.run;

/**
 * 访客管理系统详细界面
 */
public class MainVisitorActivity extends BaseActivity implements RefreshAndLoadListView.ILoadMoreListener, RefreshAndLoadListView.IReflashListener {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    // spinner日期
    @ViewInject(id = R.id.tv_title)
    NiceSpinner spinner;

    // 编辑
    @ViewInject(id = R.id.tv_right)
    TextView tv_edit;

    // 添加访客
    @ViewInject(id = R.id.btn_addCustomerFace, click = "addCustomerFace")
    Button btn_addface;


    //listView
    @ViewInject(id = R.id.visitorList)
    RefreshAndLoadListView listView;

    // 常量
    private static final int GET_NEWDATA_SUCCESS = -40;// 刷新新数据 标志
    private static final int GET_DATA_SUCCESS = -39;// 获取所有数据列表 标志
    private static final int LOAD_MORE_SUCCESS = -38;
    private static final int SEARCH_NAME_SUCCESS = -37;//搜索
    private static final int DELETE_SUCCESS = -36;//删除成功
    private static final int GET_NONE_NEWDATA = -35;//没有新数据

    // 变量
    private Context context;
    private ArrayAdapter<String> mSpinnerAdapter;
    private String[] mSpinnerArray;
    private boolean editFlag = false;// 编辑按钮标记
    private String IminTime = "";
    private String ImaxTime = "";

    private VisitorListAdapter vAdapter;//记录适配
    private boolean ifLoading = false;//标记
    private int pageSize = 20;
    private ArrayList list = null;
    private List<String> spinnerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_visitormain);

        initMainView();// spinner监听/search监听
        vAdapter = new VisitorListAdapter(this);// 加载全部数据
        listView.setAdapter(vAdapter);
        initListener();

        //加载全部数据
        getData();

    }

    // 加载spinner,加载SearchView格式
    public void initMainView() {
        context = this;
        // spinner
        mSpinnerArray = getResources().getStringArray(R.array.user_date);
        // 使用自定义的ArrayAdapter
        mSpinnerAdapter = new MainSpinnerAdapter(this, mSpinnerArray);
        // 设置下拉列表风格()
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        listView.setIRefreshListener(this);
        listView.setILoadMoreListener(this);
        //spinner绑定数据
        spinnerData = new LinkedList<>(Arrays.asList("全部访客", "今日访客"));
        spinner.attachDataSource(spinnerData);//绑定数据

    }

    private void initListener() {
        //spinner监听，筛选数据
        spinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSelectTimeData(spinnerData.get(position).trim());//参数2必填GET_NEW_DATA

            }
        });


        //		 点击一条记录后，跳转到详细的信息
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //方法1：获取一条详细记录

                int headerViewsCount = listView.getHeaderViewsCount();//得到header的总数量
                int newPosition = position - headerViewsCount;//得到新的修正后的position
                VisitorBModel model = (VisitorBModel) vAdapter.getItem(newPosition);//recordID



                if (model.isReceived() == true) {//已签到
                    Intent intent = new Intent(MainVisitorActivity.this, VisitorInfoReceiveActivity.class);
                    intent.putExtra("VisitorBModel", model);
                    startActivity(intent);
                } else {//未签到
                    Intent intent = new Intent(MainVisitorActivity.this, VisitorInfoNotReceiveActivity.class);
                    intent.putExtra("VisitorBModel", model);
                    startActivityForResult(intent, VisitorInfoNotReceiveActivity.REQUEST_CODE_FOR_EDIT_USER);
                }
            }
        });
    }

    private void getSelectTimeData(String date) {
        switch (date) {
            case "全部访客":
                vAdapter = new VisitorListAdapter(MainVisitorActivity.this);// 加载全部数据
                listView.setAdapter(vAdapter);
                MyApplication.getInstance().setTimespan(0);
                getData();
                break;
            case "今日访客":
                vAdapter = new VisitorListAdapter(MainVisitorActivity.this);// 加载今天数据
                listView.setAdapter(vAdapter);
                MyApplication.getInstance().setTimespan(1);
                getData();
                break;
        }
    }

    // 获取全部记录
    private void getData() {
        if (ifLoading) {
            return;
        }

        run(this, new Runnable() {
            @Override
            public void run() {
                ifLoading = true;
                String storeID = UserHelper.getCurrentUser().getStoreID();
                try {
                    vAdapter.IsEnd = false;

                    ArrayList<VisitorBModel> visitorModelList = UserHelper.getVisitorRecordsByPageA(
                            MainVisitorActivity.this,
                            "",//iMaxTime
                            "",//iMinTime
                            pageSize,
                            MyApplication.getInstance().timespan,
                            storeID);

                    //                  Log.d("SJY", "MainVisitorActivity--getData=" + visitorModelList.toString());//
                    if (visitorModelList == null) {
                        vAdapter.IsEnd = true;
                    } else if (visitorModelList.size() < pageSize) {
                        vAdapter.IsEnd = true;
                    }

                    if (visitorModelList != null) {
                        sendMessage(GET_DATA_SUCCESS, visitorModelList);
                    } else {
                        sendMessage(GET_NONE_NEWDATA, null);
                    }

                } catch (MyException e) {
                    sendToastMessage(e.getMessage());
                    ifLoading = false;
                }
            }
        });
    }

    //加载
    @Override
    public void onLoadMore() {

        if (ifLoading) {
            return;
        }

        noDialogRun(MainVisitorActivity.this, new Runnable() {

            @Override
            public void run() {
                ifLoading = true;//
                String storeID = UserHelper.getCurrentUser().getStoreID();
                try {
                    List<VisitorBModel> visitorModelList = UserHelper.getVisitorRecordsByPageA(
                            MainVisitorActivity.this,
                            "",//iMaxTime
                            IminTime,//iMinTime /获取前20条数据的最后后一条的iLastUpdateTime参数
                            pageSize,
                            MyApplication.getInstance().timespan,
                            storeID);

                    if (visitorModelList == null || visitorModelList.size() < pageSize) {
                        vAdapter.IsEnd = true;
                    }

                    sendMessage(LOAD_MORE_SUCCESS, visitorModelList);

                } catch (MyException e) {
                    Log.d("SJY", "异常=" + e.getMessage());
                    sendMessage(GET_NONE_NEWDATA, e.getMessage());
                }
            }
        });
    }

    //刷新
    @Override
    public void onRefresh() {
        if (ifLoading) {
            return;
        }
        Loading.noDialogRun(this, new Runnable() {
            @Override
            public void run() {
                ifLoading = true;
                String storeID = UserHelper.getCurrentUser().getStoreID();
                try {
                    vAdapter.IsEnd = false;

                    ArrayList<VisitorBModel> visitorModelList = UserHelper.getVisitorRecordsByPageA(
                            MainVisitorActivity.this,
                            ImaxTime,//iMaxTime
                            "",//iMinTime
                            pageSize,
                            MyApplication.getInstance().timespan,
                            storeID);

                    if (visitorModelList == null || visitorModelList.size() < pageSize) {
                        vAdapter.IsEnd = true;
                    }

                    sendMessage(GET_NEWDATA_SUCCESS, visitorModelList);
                } catch (MyException e) {
                    sendMessage(GET_NONE_NEWDATA, e.getMessage());
                    return;
                }
            }
        });
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case GET_NEWDATA_SUCCESS://刷新数据并拼接
                list = (ArrayList) msg.obj;
                vAdapter.insertEntityList(list);
                //数据处理/只存最大值,做刷新新数据使用
                setMaxTime(list);
                listView.loadAndFreshComplete();
                ifLoading = false;
                break;
            case GET_DATA_SUCCESS://进入页面加载最新
                // 数据显示
                list = (ArrayList) msg.obj;
                vAdapter.setEntityList(list);
                //数据处理，获取iLastUpdateTime参数方便后续上拉/下拉使用
                setMinTime(list);
                setMaxTime(list);
                ifLoading = false;
                listView.loadAndFreshComplete();
                break;
            case LOAD_MORE_SUCCESS://加载全部/今天
                list = (ArrayList) msg.obj;
                vAdapter.addEntityList(list);
                //数据处理，只存最小值
                setMinTime(list);
                ifLoading = false;
                listView.loadAndFreshComplete();
                break;
            case GET_NONE_NEWDATA://没有获取新数据
                sendToastMessage((String) msg.obj);
                ifLoading = false;
                listView.loadAndFreshComplete();
                break;

            default:
                break;
        }
        super.handleMessage(msg);
    }

    /**
     * 获取minTime，上拉加载应用
     */
    private void setMinTime(ArrayList list) {
        if (list.size() > 0) {
            VisitorBModel model = (VisitorBModel) list.get(list.size() - 1);//获取最后一条记录
            IminTime = model.getiLastUpdateTime();
        }
    }

    /**
     * 获取maxTime,下拉刷新使用
     */
    private void setMaxTime(ArrayList list) {
        if (list.size() > 0) {
            VisitorBModel model = (VisitorBModel) list.get(0);//获取第一条记录
            ImaxTime = model.getiLastUpdateTime();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vAdapter != null) {
            vAdapter.destroy();
        }
    }

    // 添加访客
    public void addCustomerFace(View view) {
        startActivity(AddVisitorFaceActivity.class);
    }

    // 后退
    public void forBack(View view) {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VisitorInfoNotReceiveActivity.REQUEST_CODE_FOR_EDIT_USER) {
            // 修改 信息界面

            // if(resultCode ==
            // EditUserActivity.RESULT_CODE_FOR_EDIT_USER_SUCCESS){
            // getData();
            // }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
