package com.yvision.view;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.adapter.VipListAdapter;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.VipModel;
import com.yvision.utils.PageUtil;
import com.yvision.widget.NiceSpinner;
import com.yvision.widget.RefreshAndLoadListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.yvision.R.id.tv_title;

/**
 * vip界面
 * Created by sjy on 2017/3/8.
 */

public class MainVipActivity extends BaseActivity implements RefreshAndLoadListView.IReflashListener, RefreshAndLoadListView.ILoadMoreListener {

    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = tv_title)
    NiceSpinner niceSpinner;

    //
    @ViewInject(id = R.id.tv_right)
    TextView tv_right;

    //listView
    @ViewInject(id = R.id.vip_listView)
    RefreshAndLoadListView listView;


    //常量
    private VipListAdapter adapter;
    private boolean ifLoading = false;
    private String IMaxtime = "";
    private String IMinTime = "";
    private String timespan = "4";//1today 2month 3 week 4 all

    private String alldate = "4";
    private String todaydate = "1";
    private String weekdate = "2";
    private String monthdate = "3";

    // spinner
    private List<String> spinnerData;
    private String myLastSelectState;//记录spinner上次选中的值

    private ArrayList<VipModel> listAll = new ArrayList<>();//每次获取的数据段

    private ArrayList<VipModel> listToday = new ArrayList<>();//
    private ArrayList<VipModel> listWeek = new ArrayList<>();//
    private ArrayList<VipModel> listMonth = new ArrayList<>();//


    //变量
    private static final int POST_SUCCESS = 20;
    private static final int REFRESH_SUCCESS = 22;
    private static final int LOADMORE_SUCCESS = 23;
    private static final int POST_FAILED = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_vipmain);

        initMyView();
        initListener();
        timespan = alldate;
        getDate();
    }

    private void initMyView() {
        tv_right.setText("");

        listView.setIRefreshListener(this);
        listView.setILoadMoreListener(this);

        adapter = new VipListAdapter(MainVipActivity.this);
        listView.setAdapter(adapter);

        //spinner数据
        spinnerData = new LinkedList<>(Arrays.asList("所有记录", "本日记录", "本周记录", "本月记录"));
        myLastSelectState = spinnerData.get(0);
        niceSpinner.attachDataSource(spinnerData);//绑定数据
    }

    //监听
    private void initListener() {

        //spinner监听，筛选数据
        niceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SJY", "spinner监听--" + spinnerData.get(position));

                //如果选择状态没变，就不做处理
                if (!spinnerData.get(position).equals(myLastSelectState)) {

                    myLastSelectState = spinnerData.get(position);

                    adapter = new VipListAdapter(MainVipActivity.this);
                    listView.setAdapter(adapter);

                    changeDate(myLastSelectState);
                } else {
                    return;
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                int headerViewsCount = listView.getHeaderViewsCount();//得到header的总数量
                int newPosition = position - headerViewsCount;//得到新的修正后的position

                VipModel model = (VipModel) adapter.getItem(newPosition);

                Bundle bundle = new Bundle();
                bundle.putString("AttendID", model.getVipRecordID());
                startActivity(VipInfoActivity.class, bundle);
            }
        });

    }

    //获取数据
    private void getDate() {
        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                try {
                    List<VipModel> listDate = UserHelper.getViPList(MainVipActivity.this
                            , IMaxtime
                            , IMinTime
                            , timespan);

                    sendMessage(POST_SUCCESS, listDate);

                } catch (MyException e) {
                    Log.d("SJY", "异常=" + e.getMessage());
                    sendMessage(POST_FAILED, e.getMessage());
                }
                //                sendMessage(POST_SUCCESS,null);
            }
        });

    }

    @Override
    public void onLoadMore() {
        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                try {
                    List<VipModel> listDate = UserHelper.getViPList(MainVipActivity.this
                            , IMaxtime
                            , IMinTime
                            , timespan);

                    sendMessage(LOADMORE_SUCCESS, listDate);

                } catch (MyException e) {
                    Log.d("SJY", "异常=" + e.getMessage());
                    sendMessage(POST_FAILED, e.getMessage());
                }
                //                sendMessage(POST_SUCCESS,null);
            }
        });
    }

    @Override
    public void onRefresh() {
        Loading.noDialogRun(this, new Runnable() {
            @Override
            public void run() {
                try {
                    List<VipModel> listDate = UserHelper.getViPList(MainVipActivity.this
                            , IMaxtime
                            , IMinTime
                            , timespan);

                    sendMessage(REFRESH_SUCCESS, listDate);

                } catch (MyException e) {
                    Log.d("SJY", "异常=" + e.getMessage());
                    sendMessage(POST_FAILED, e.getMessage());
                }
                //                sendMessage(POST_SUCCESS,null);
            }
        });
    }

    //spinner更换
    private void changeDate(String spinnerState) {
        switch (spinnerState) {
            case "所有记录":
                //重新设置初始值
                IMaxtime = "";
                IMinTime = "";
                timespan = alldate;//all
                //获取数据
                getDate();
                break;
            case "本日记录":
                //重新设置初始值
                IMaxtime = "";
                IMinTime = "";
                timespan = todaydate;//today
                //获取数据
                getDate();
                break;

            case "本周记录":
                //重新设置初始值
                IMaxtime = "";
                IMinTime = "";
                timespan = weekdate;//week
                //获取数据
                getDate();
                break;

            case "本月记录":
                //重新设置初始值
                IMaxtime = "";
                IMinTime = "";
                timespan = monthdate;//month
                //获取数据
                getDate();
                break;
        }
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case POST_SUCCESS:
                //具体展示数据
                showLiseDate((ArrayList<VipModel>) msg.obj, POST_SUCCESS, null);
                break;

            case REFRESH_SUCCESS:
                //具体展示数据
                showLiseDate((ArrayList<VipModel>) msg.obj, REFRESH_SUCCESS, null);
                break;

            case LOADMORE_SUCCESS:
                //具体展示数据
                showLiseDate((ArrayList<VipModel>) msg.obj, LOADMORE_SUCCESS, null);
                break;
            case POST_FAILED:

                //具体展示数据
                showLiseDate(null, POST_FAILED, (String) msg.obj);

                break;
        }
    }

    public void setIMaxTime(List<VipModel> list) {
        Log.d("SJY", "list size= " + list.size());
        IMaxtime = list.get(0).getCapTime();
    }

    public void setIMinTime(ArrayList<VipModel> list) {
        IMinTime = list.get(list.size() - 1).getCapTime();
    }

    //具体展示处理
    private void showLiseDate(ArrayList<VipModel> listdate, int key, String message) {
        switch (myLastSelectState) {
            case "所有记录":
                if (!timespan.equals(alldate)) {
                    PageUtil.DisplayToast("代码有误，设置timespan错误");
                    return;
                }
                if (key == POST_SUCCESS) {
                    listAll = listdate;
                    adapter.setEntityList(listAll);

                    setIMinTime(listAll);
                    setIMaxTime(listAll);

                    listView.loadAndFreshComplete();
                    ifLoading = false;

                } else if (key == REFRESH_SUCCESS) {
                    listAll = listdate;
                    adapter.insertEntityList(listAll);

                    setIMaxTime(listAll);

                    listView.loadAndFreshComplete();
                    ifLoading = false;

                } else if (key == LOADMORE_SUCCESS) {
                    listAll = listdate;
                    adapter.setEntityList(listAll);

                    setIMinTime(listAll);

                    listView.loadAndFreshComplete();
                    ifLoading = false;
                } else {

                    PageUtil.DisplayToast(message);
                    listView.loadAndFreshComplete();
                    ifLoading = false;
                }
                break;
            case "本日记录":

                if (!timespan.equals(todaydate)) {
                    PageUtil.DisplayToast("代码有误，设置timespan错误");
                    return;
                }

                if (key == POST_SUCCESS) {
                    listToday = listdate;
                    adapter.setEntityList(listToday);

                    setIMinTime(listToday);
                    setIMaxTime(listToday);

                    listView.loadAndFreshComplete();
                    ifLoading = false;

                } else if (key == REFRESH_SUCCESS) {
                    listToday = listdate;
                    adapter.insertEntityList(listToday);

                    setIMaxTime(listToday);

                    listView.loadAndFreshComplete();
                    ifLoading = false;

                } else if (key == LOADMORE_SUCCESS) {
                    listToday = listdate;
                    adapter.setEntityList(listToday);

                    setIMinTime(listToday);

                    listView.loadAndFreshComplete();
                    ifLoading = false;
                } else {

                    PageUtil.DisplayToast(message);
                    listView.loadAndFreshComplete();
                    ifLoading = false;
                }
                break;

            case "本周记录":

                if (!timespan.equals(weekdate)) {
                    PageUtil.DisplayToast("代码有误，设置timespan错误");
                    return;
                }
                if (key == POST_SUCCESS) {
                    listWeek = listdate;
                    adapter.setEntityList(listWeek);

                    setIMinTime(listWeek);
                    setIMaxTime(listWeek);

                    listView.loadAndFreshComplete();
                    ifLoading = false;

                } else if (key == REFRESH_SUCCESS) {
                    listWeek = listdate;
                    adapter.insertEntityList(listWeek);

                    setIMaxTime(listWeek);

                    listView.loadAndFreshComplete();
                    ifLoading = false;

                } else if (key == LOADMORE_SUCCESS) {
                    listWeek = listdate;
                    adapter.setEntityList(listWeek);

                    setIMinTime(listWeek);

                    listView.loadAndFreshComplete();
                    ifLoading = false;
                } else {

                    PageUtil.DisplayToast(message);
                    listView.loadAndFreshComplete();
                    ifLoading = false;
                }
                break;

            case "本月记录":
                if (!timespan.equals(monthdate)) {
                    PageUtil.DisplayToast("代码有误，设置timespan错误");
                    return;
                }
                if (key == POST_SUCCESS) {
                    listMonth = listdate;
                    adapter.setEntityList(listMonth);

                    setIMinTime(listMonth);
                    setIMaxTime(listMonth);

                    listView.loadAndFreshComplete();
                    ifLoading = false;

                } else if (key == REFRESH_SUCCESS) {
                    listMonth = listdate;
                    adapter.insertEntityList(listMonth);

                    setIMaxTime(listMonth);

                    listView.loadAndFreshComplete();
                    ifLoading = false;

                } else if (key == LOADMORE_SUCCESS) {
                    listMonth = listdate;
                    adapter.setEntityList(listMonth);

                    setIMinTime(listMonth);

                    listView.loadAndFreshComplete();
                    ifLoading = false;
                } else {

                    PageUtil.DisplayToast(message);
                    listView.loadAndFreshComplete();
                    ifLoading = false;
                }
                break;
        }
    }

    /**
     * forback
     * @param view
     */

    public void forBack(View view) {
        this.finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.destroy();
        }
    }
}
