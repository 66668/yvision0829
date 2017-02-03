package com.yvision.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.model.OldEmployeeModel;


/**
 * 老员工姓名列表适配
 *
 * @author
 */

public class EmployeeNameListAdapter extends SimpleListAdapter {

    public class WidgetHolder {
        public TextView tvName;
        public TextView tvWorkID;
    }

    public EmployeeNameListAdapter(Context context) {
        super(context);
    }

    @Override
    protected View inflateConvertView() {
        //一条记录的布局
        View view = inflater.inflate(R.layout.item_registermain_list, null);
        //该布局上的控件
        WidgetHolder holder = new WidgetHolder();
        holder.tvWorkID = (TextView) view.findViewById(R.id.tv_workId);
        holder.tvName = (TextView) view.findViewById(R.id.tv_name);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void initViewData(final int position, View convertView) {
        WidgetHolder holder = (WidgetHolder) convertView.getTag();//获取控件管理实例
        //获取一条信息
        //?java.lang.ClassCastException: java.util.ArrayList cannot be cast to com.yvision.model.VisitorBModel
        OldEmployeeModel model = (OldEmployeeModel) entityList.get(position);
        //赋值姓名
        holder.tvName.setText(model.getEmployeeName());
        //赋值workID
        holder.tvWorkID.setText(model.getWrokId());
    }

}
