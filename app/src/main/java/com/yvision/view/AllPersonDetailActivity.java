package com.yvision.view;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.inject.ViewInject;
import com.yvision.model.OldEmployeeModel;

/**
 * Created by sjy on 2017/3/31.
 */

public class AllPersonDetailActivity extends BaseActivity {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right)
    TextView tv_right;

    //姓名
    @ViewInject(id = R.id.tv_name)
    TextView tv_name;
    //性别
    @ViewInject(id = R.id.tv_gender)
    TextView tv_gender;
    //编号
    @ViewInject(id = R.id.tv_workid)
    TextView tv_workid;
    //部门
    @ViewInject(id = R.id.tv_dept)
    TextView tv_dept;
    //权限
    @ViewInject(id = R.id.tv_groupid)
    TextView tv_groupid;
    //图像
    @ViewInject(id = R.id.tv_img, click = "toImg")
    TextView tv_img;

    private OldEmployeeModel OldEmployeeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_person_detail);

        initMyView();
    }

    private void initMyView() {
        tv_title.setText("员工详情");
        tv_right.setText("");
        Bundle bundle = getIntent().getExtras();
        OldEmployeeModel = (OldEmployeeModel) bundle.getSerializable("OldEmployeeModel");
        setShow(OldEmployeeModel);
    }

    private void setShow(OldEmployeeModel model) {
        tv_name.setText(model.getEmployeeName()!=null?model.getEmployeeName():"无");
        tv_gender.setText(model.getGender() != null?model.getGender():"无");
        tv_workid.setText(model.getWrokId() != null?model.getWrokId():"无");
        tv_dept.setText(model.getDeptName() != null? model.getDeptName():"无");

        StringBuilder builder = new StringBuilder();
        if (model.getIsAttend().contains("1")) {
            builder.append(" 考勤 ");
            tv_groupid.setText(builder.toString());
        } else if (model.getIsDoorAccess().contains("1")) {
            builder.append(" 门禁 ");
            tv_groupid.setText(builder.toString());

        } else if (model.getIsVip().contains("1")) {
            builder.append(" VIP ");
            tv_groupid.setText(builder.toString());

        } else if (model.getIsVisitor().contains("1")) {
            builder.append(" 访客 ");
            tv_groupid.setText(builder.toString());

        }
    }

    public void toImg(View view){
        Bundle bundle = new Bundle();
        bundle.putSerializable("OldEmployeeModel",OldEmployeeModel);
        startActivity(PersonImgBorwseActivity.class,bundle);
    }
    // 后退
    public void forBack(View view) {
        this.finish();
    }
}
