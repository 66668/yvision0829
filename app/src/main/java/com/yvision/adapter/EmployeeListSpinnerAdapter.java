package com.yvision.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.model.EmployeeListModel;

import java.util.ArrayList;
/**
 * 添加访客-->受访者列表处理
 * 
 * @author JackSong
 *
 */
public class EmployeeListSpinnerAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<EmployeeListModel> employeeList = new ArrayList<EmployeeListModel>();
	public EmployeeListSpinnerAdapter(Context context, ArrayList<EmployeeListModel> employeeList){
		this.context = context;
		this.employeeList = employeeList;
	}
	@Override
	public int getCount() {
		return employeeList.size();
	}

	@Override
	public Object getItem(int position) {
		return employeeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_employee_spinner, null);
		}
		TextView textView = (TextView)convertView.findViewById(R.id.item_txt);
		textView.setText(employeeList.get(position).getEmployeeName());
		return convertView;
	}

}
