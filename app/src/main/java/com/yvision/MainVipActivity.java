package com.yvision;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yvision.adapter.VipListAdapter;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.VipModel;
import com.yvision.widget.RefreshAndLoadListView;

import java.util.ArrayList;
import java.util.List;

/**
 * vip界面
 * Created by sjy on 2017/3/8.
 */

public class MainVipActivity extends BaseActivity implements RefreshAndLoadListView.IReflashListener, RefreshAndLoadListView.ILoadMoreListener {

    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right)
    TextView tv_right;

    //listView
    @ViewInject(id = R.id.vip_listView)
    RefreshAndLoadListView listView;


    //常量
    private ArrayList<VipModel> listDate;
    private VipListAdapter adapter;
    private boolean ifLoading = false;
    private String IMaxtime = "";
    private String IMinTime = "";


    //变量
    private static final int POST_SUCCESS = 20;
    private static final int POST_FAILED = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_vipmain);

        initMyView();
        getDate();
    }

    private void initMyView() {
        tv_title.setText(getResources().getString(R.string.title_vip));
        tv_right.setText("");

        listView.setIRefreshListener(this);
        listView.setILoadMoreListener(this);

        adapter = new VipListAdapter(MainVipActivity.this);
        listView.setAdapter(adapter);

    }

    //获取数据
    private void getDate() {
        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                try {
                    List<VipModel> listDate = UserHelper.getViPList(MainVipActivity.this
                            , ""
                            , ""
                            , "4");

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

    }

    @Override
    public void onRefresh() {

    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case POST_SUCCESS:
                listDate = (ArrayList<VipModel>) msg.obj;
                adapter.setEntityList(listDate);
                //数据处理，获取iLastUpdateTime参数方便后续上拉/下拉使用
                setIMinTime(listDate);
                setIMaxTime(listDate);
                listView.loadAndFreshComplete();
                ifLoading = false;
                break;
            case POST_FAILED:

                break;
        }
    }

    public void setIMaxTime(List<VipModel> list) {
        IMaxtime = list.get(0).getCapTime();
    }

    public void setIMinTime(ArrayList<VipModel> list) {
        IMinTime = list.get(list.size() - 1).getCapTime();
    }


}
