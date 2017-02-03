package com.yvision;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.yvision.adapter.EmployeeNameListAdapter;
import com.yvision.adapter.MainSpinnerAdapter;
import com.yvision.common.MyApplication;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.OldEmployeeModel;
import com.yvision.utils.PageUtil;

import java.util.ArrayList;

/**
 * 员工注册系统详细界面
 */
public class MainRegisterActivity extends BaseActivity {

    // 返回
    @ViewInject(id = R.id.layout_Back, click = "forBack")
    RelativeLayout forBack;

    // 添加访客
    @ViewInject(id = R.id.btn_addCustomerFace, click = "addNewEmployee")
    Button btn_addface;

    // xml:添加访客
    @ViewInject(id = R.id.layout_addCustomerFace)
    LinearLayout layout_addCustomerFace;

    //搜索
    @ViewInject(id = R.id.forSearch)
    SearchView tv_forSearch;

    //listVew
    @ViewInject(id = R.id.OldEmployeeNameList)
    ListView listView;

    //listVew
    @ViewInject(id = R.id.tv_right, click = "refresh")
    TextView tv_right;

    // 常量
    private static final int GET_DATA_SUCCESS = -39;// 获取所有数据列表 标志
    private static final int GET_NONE_NEWDATA = -35;//没有新数据

    // 变量
    private Context context;
    private ArrayAdapter<String> mSpinnerAdapter;
    private String[] mSpinnerArray;

    private EmployeeNameListAdapter employeeNameListAdapter;//记录适配
    private boolean ifLoading = false;//标记
    private int pageSize = 20;
    private ArrayList list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_registermain);
        this.context = this;

        initMainView();// spinner监听/search监听
        employeeNameListAdapter = new EmployeeNameListAdapter(context);// null--没有上拉加载功能
        listView.setAdapter(employeeNameListAdapter);

        //		 点击一条记录后，跳转到登记时详细的信息
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = listView.getHeaderViewsCount();//得到header的总数量
                int newPosition = position - headerViewsCount;//得到新的修正后的position
                final String employeeId = ((OldEmployeeModel) employeeNameListAdapter.getItem(newPosition)).getEmployeeId();//recordID
                Log.d("SJY", "详细--employeeId=" + employeeId);
                Loading.run(MainRegisterActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OldEmployeeModel model = UserHelper.getOldEmployeeDetails(MainRegisterActivity.this, employeeId);
                            Intent intent = new Intent(MainRegisterActivity.this, AddOldEmployeeActivity.class);
                            intent.putExtra("OldEmployeeModel", model);
                            startActivity(intent);
                        } catch (MyException e) {
                            sendToastMessage(e.getMessage());
                        }
                    }
                });

            }
        });
        //加载全部数据
        getData();
    }

    // 加载spinner,加载SearchView格式
    public void initMainView() {
        tv_right.setText("刷新");
        // searchView
        tv_forSearch.setIconifiedByDefault(true);
        tv_forSearch.setSubmitButtonEnabled(true);
        // tv_forSearch.onActionViewExpanded();//进入界面就获取焦点
        tv_forSearch.setIconifiedByDefault(true);
        // spinner
        mSpinnerArray = getResources().getStringArray(R.array.user_date);
        // 使用自定义的ArrayAdapter
        mSpinnerAdapter = new MainSpinnerAdapter(this, mSpinnerArray);
        // 设置下拉列表风格()
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
    }

    // 获取全部记录
    private void getData() {
        if (ifLoading) {
            return;
        }

        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                ifLoading = true;
                //清除数据处理
                MyApplication.getInstance().setiMaxTime("");
                MyApplication.getInstance().setiMinTime("");
                try {
                    employeeNameListAdapter.IsEnd = false;

                    ArrayList<OldEmployeeModel> visitorModelList = UserHelper.getOldEmployList(
                            MainRegisterActivity.this);

                    if (visitorModelList == null) {
                        employeeNameListAdapter.IsEnd = true;
                    } else if (visitorModelList.size() < pageSize) {
                        employeeNameListAdapter.IsEnd = true;
                    }

                    sendMessage(GET_DATA_SUCCESS, visitorModelList);

                } catch (MyException e) {
                    sendMessage(GET_NONE_NEWDATA, e.getMessage());
                    ifLoading = false;
                }
            }
        });
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case GET_DATA_SUCCESS://加载全部/今天
                list = (ArrayList) msg.obj;
                employeeNameListAdapter.setEntityList(list);
                //数据处理，只存最小值
                ifLoading = false;
                break;
            case GET_NONE_NEWDATA://没有获取新数据
                PageUtil.DisplayToast((String) msg.obj);
                ifLoading = false;
                break;

            default:
                break;
        }
        super.handleMessage(msg);
    }

    //    /**
    //     * 获取minTime，上拉加载应用
    //     */
    //    private void setMinTime(ArrayList list) {
    //        if (list.size() > 0) {
    //            OldEmployeeModel model = (OldEmployeeModel) list.get(list.size() - 1);//获取最后一条记录
    //            MyApplication.getInstance().setiMinTime(model.getiLastUpdateTime());
    //        }
    //    }
    //
    //    /**
    //     * 获取maxTime,下拉刷新使用
    //     */
    //    private void setMaxTime(ArrayList list) {
    //        if (list.size() > 0) {
    //            VisitorBModel model = (VisitorBModel) list.get(0);//获取第一条记录
    //            MyApplication.getInstance().setiMaxTime(model.getiLastUpdateTime());
    //        }
    //    }


    // 添加新员工
    public void addNewEmployee(View view) {
        startActivity(AddNewEmployeeActivity.class);
    }

    // 搜索
    //    public OnQueryTextListener searchListener = new OnQueryTextListener() {
    //        @Override
    //        public boolean onQueryTextChange(String newText) {// 在输入时触发的方法
    //            return true;
    //        }
    //
    //        @Override
    //        public boolean onQueryTextSubmit(final String query) {//输入完成后，提交时触发的方法
    //            final String storeID = UserHelper.getCurrentUser().getStoreID();
    //            if (tv_forSearch != null) {
    //                // 得到输入管理对象
    //                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    //                if (inputMethodManager != null) {
    //                    // 隐藏键盘
    //                    inputMethodManager.hideSoftInputFromWindow(tv_forSearch.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
    //                }
    //                tv_forSearch.clearFocus(); // 不获取焦点
    //            }
    //
    //            Loading.run(MainRegisterActivity.this, new Runnable() {
    //                @Override
    //                public void run() {
    //                    try {
    //                        employeeNameListAdapter.IsEnd = false;
    //                        List<VisitorBModel> visitorModelList = UserHelper.getVisitorWithSameName(MainRegisterActivity.this, storeID, query);
    //
    //                        if (visitorModelList.size() < pageSize) {// 20
    //                            employeeNameListAdapter.IsEnd = true;// 标识列表获取结束了，没有翻页
    //                        }
    //                        sendMessage(SEARCH_NAME_SUCCESS, visitorModelList);
    //                    } catch (MyException e) {
    //                        e.printStackTrace();
    //                        sendToastMessage(e.getMessage());
    //                    }
    //                }
    //            });
    //            return true;
    //        }
    //
    //    };

    // 后退
    public void forBack(View view) {
        this.finish();
    }

    public void refresh(View view) {
       getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VisitorInfoActivity.REQUEST_CODE_FOR_EDIT_USER) {
            // 修改 信息界面

            // if(resultCode ==
            // EditUserActivity.RESULT_CODE_FOR_EDIT_USER_SUCCESS){
            // getData();
            // }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
