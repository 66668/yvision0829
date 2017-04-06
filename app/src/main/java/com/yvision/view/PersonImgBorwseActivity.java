package com.yvision.view;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.adapter.GridViewAdapter;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.OldEmployeeImgModel;
import com.yvision.model.OldEmployeeModel;
import com.yvision.utils.PageUtil;

import java.util.List;

/**
 * 老员工注册图像浏览
 * Created by sjy on 2017/3/31.
 */

public class PersonImgBorwseActivity extends BaseActivity {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right, click = "addDoor")
    TextView tv_right;

    //变量

    private OldEmployeeModel OldEmployeeModel;
    private List<OldEmployeeImgModel> listDate;
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    //常量
    private static final int GET_SUCCESS = 11;
    private static final int GET_FAILED = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_person_imgbrowse);
        initMyView();
        getImgListDate();
    }


    private void initMyView() {
        tv_title.setText("员工图片详情");
        tv_right.setText("添加门禁");
        Bundle bundle = getIntent().getExtras();
        OldEmployeeModel = (OldEmployeeModel) bundle.getSerializable("OldEmployeeModel");
        gridView = (GridView) findViewById(R.id.gridview);
    }

    private void getImgListDate() {
        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("SJY", "OldEmployeeModel.getEmployeeId()=" + OldEmployeeModel.getEmployeeId());
                    List<OldEmployeeImgModel> listDate = UserHelper.getOldEmployeeImgDetails(PersonImgBorwseActivity.this, OldEmployeeModel.getEmployeeId());
                    sendMessage(GET_SUCCESS, listDate);
                } catch (MyException e) {
                    e.printStackTrace();
                    Log.d("SJY", "获取图片集合异常=" + e.getMessage());
                    sendMessage(GET_FAILED, e.getMessage());
                }
            }
        });

    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case GET_SUCCESS:
                listDate = (List<OldEmployeeImgModel>) msg.obj;
                gridViewAdapter = new GridViewAdapter(this, listDate);
                gridView.setAdapter(gridViewAdapter);
                break;
            case GET_FAILED:
                PageUtil.DisplayToast((String) msg.obj);
                break;
        }
    }

    public void addDoor(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("OldEmployeeModel", OldEmployeeModel);
        startActivity(AddOldDoorActivity.class, bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    public void forBack(View view){
        this.finish();

    }
}
