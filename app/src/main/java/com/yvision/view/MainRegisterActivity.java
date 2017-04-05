package com.yvision.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.adapter.MainSpinnerAdapter;
import com.yvision.adapter.RegisterListAdapter;
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
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right, click = "refresh")
    TextView tv_right;

    // 添加访客
    @ViewInject(id = R.id.btn_addCustomerFace, click = "addNewEmployee")
    Button btn_addface;

    // xml:添加访客
    @ViewInject(id = R.id.layout_addCustomerFace)
    LinearLayout layout_addCustomerFace;

    //    //搜索
    //    @ViewInject(id = R.id.forSearch)
    //    SearchView tv_forSearch;

    //listVew
    @ViewInject(id = R.id.OldEmployeeNameList)
    ListView listView;


    // 常量
    private static final int GET_DATA_SUCCESS = -39;// 获取所有数据列表 标志
    private static final int GET_NONE_NEWDATA = -35;//没有新数据

    // 变量
    private Context context;
    private ArrayAdapter<String> mSpinnerAdapter;
    private String[] mSpinnerArray;
    private String minTime = "";
    private String maxTime = "";

    private RegisterListAdapter registerListAdapter;//记录适配
    private boolean ifLoading = false;//标记
    private int pageSize = 20;
    private ArrayList list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_registermain);

        initMyView();
        initListener();
        getData();
    }

    private void initListener() {
        //		 点击一条记录后，跳转到登记时详细的信息
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = listView.getHeaderViewsCount();//得到header的总数量
                int newPosition = position - headerViewsCount;//得到新的修正后的position

                OldEmployeeModel employeeId = ((OldEmployeeModel) registerListAdapter.getItem(newPosition));//recordID
                Bundle bundle = new Bundle();
                bundle.putSerializable("OldEmployeeModel", employeeId);
                startActivity(AllPersonDetailActivity.class, bundle);
            }
        });
    }

    public void initMyView() {
        this.context = this;
        tv_title.setText("员工注册");
        tv_right.setText("刷新");

        //        // 搜索
        //        tv_forSearch.setIconifiedByDefault(true);
        //        tv_forSearch.setSubmitButtonEnabled(true);
        //        tv_forSearch.setIconifiedByDefault(true);
        // spinner
        mSpinnerArray = getResources().getStringArray(R.array.user_date);
        // 使用自定义的ArrayAdapter
        mSpinnerAdapter = new MainSpinnerAdapter(this, mSpinnerArray);
        // 设置下拉列表风格()
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        registerListAdapter = new RegisterListAdapter(context);// null--没有上拉加载功能
        listView.setAdapter(registerListAdapter);
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
                try {
                    registerListAdapter.IsEnd = false;

                    ArrayList<OldEmployeeModel> visitorModelList = UserHelper.getOldEmployList(
                            MainRegisterActivity.this);

                    if (visitorModelList == null) {
                        registerListAdapter.IsEnd = true;
                    } else if (visitorModelList.size() < pageSize) {
                        registerListAdapter.IsEnd = true;
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

                registerListAdapter.setEntityList(list);
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


    // 添加新员工
    public void addNewEmployee(View view) {
        new AlertDialog.Builder(MainRegisterActivity.this)
                .setCancelable(false)    //不响应back按钮
                .setTitle("选择注册类型：")
                .setItems(new String[]{"考勤", "VIP"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(AddNewAttenderActivity.class);
                                dialog.dismiss();
                                break;
                            case 1:
                                startActivity(AddNewViperActivity.class);
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
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

    /**
     * 刷新功能
     *
     * @param view
     */
    public void refresh(View view) {
        registerListAdapter = new RegisterListAdapter(context);// null--没有上拉加载功能
        listView.setAdapter(registerListAdapter);
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerListAdapter = new RegisterListAdapter(context);// null--没有上拉加载功能
        listView.setAdapter(registerListAdapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registerListAdapter != null) {
            registerListAdapter.destroy();
        }
    }
}
