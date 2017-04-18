package com.yvision.view;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.adapter.AttendGridViewAdapter;
import com.yvision.adapter.DoorGridViewAdapter;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.OldEmployeeImgModel;
import com.yvision.model.OldEmployeeModel;
import com.yvision.utils.PageUtil;

import java.util.ArrayList;
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
    @ViewInject(id = R.id.tv_right)
    TextView tv_right;
    //
    @ViewInject(id = R.id.add_attend)
    ImageView add_attend;
    //
    @ViewInject(id = R.id.add_door, click = "addDoor")
    ImageView add_door;

    //变量
    int colnum;
    private OldEmployeeModel OldEmployeeModel;
    private ArrayList<OldEmployeeImgModel> listDate;
    private ArrayList<OldEmployeeImgModel> listAttend;
    private ArrayList<OldEmployeeImgModel> listDoor;
    private GridView gridView;
    private GridView door_gridview;
    private AttendGridViewAdapter gridViewAdapter;
    private DoorGridViewAdapter doorAdapter;
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
        tv_right.setText("");
        Bundle bundle = getIntent().getExtras();
        OldEmployeeModel = (OldEmployeeModel) bundle.getSerializable("OldEmployeeModel");
        gridView = (GridView) findViewById(R.id.gridview);
        door_gridview = (GridView) findViewById(R.id.door_gridview);
        //根据200像素的图片，计算列数
        colnum = (int) (((getResources().getDisplayMetrics().widthPixels)) / 200);
        gridView.setNumColumns(colnum);
        door_gridview.setNumColumns(colnum);

        //将img设置成和获取的图片一样大小
        int itemWidth = (int) (getResources().getDisplayMetrics().widthPixels - colnum * 10) / colnum;
        // Calculate the height by your scale rate, I just use itemWidth here
        // 下面根据比例计算您的item的高度，此处只是使用itemWidth
        int itemHeight = itemWidth;

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                itemWidth,
                itemHeight);

        add_attend.setLayoutParams(param);
        add_door.setLayoutParams(param);
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
                listDate = (ArrayList<OldEmployeeImgModel>) msg.obj;
                splitByGroupType(listDate);
                setShow();
                break;
            case GET_FAILED:
                PageUtil.DisplayToast((String) msg.obj);
                break;
        }
    }

    private void setShow() {
        //显示考勤
        if (listAttend == null || listAttend.size() <= 0) {
            add_attend.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        } else {
            add_attend.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            gridViewAdapter = new AttendGridViewAdapter(this, listAttend, colnum);
            gridView.setAdapter(gridViewAdapter);

        }
        //显示门禁
        if (listDoor == null || listDoor.size() <= 0) {
            add_door.setVisibility(View.VISIBLE);
            door_gridview.setVisibility(View.GONE);
        } else {
            //显示门禁
            doorAdapter = new DoorGridViewAdapter(this, listDoor, colnum);
            door_gridview.setAdapter(doorAdapter);
        }
    }

    //1：考勤，2：访客，3：VIP，4：门禁
    private void splitByGroupType(ArrayList<OldEmployeeImgModel> listDate) {
        listAttend = new ArrayList<>();
        listDoor = new ArrayList<>();
        for (int i = 0; i < listDate.size(); i++) {
            if (listDate.get(i).getGroupType().equals("1")) {
                Log.d("SJY", "splitByGroupType 1: " + listDate.get(i).getGroupType());
                listAttend.add(listDate.get(i));
            } else if (listDate.get(i).getGroupType().equals("4")) {
                Log.d("SJY", "splitByGroupType 4: " + listDate.get(i).getGroupType());
                listDoor.add(listDate.get(i));
            }
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

    public void forBack(View view) {
        this.finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getImgListDate();
    }
}
