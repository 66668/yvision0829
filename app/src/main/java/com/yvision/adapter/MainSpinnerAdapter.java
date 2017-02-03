package com.yvision.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yvision.R;

/**
 * 为主界面的spinner自定义样式，关联数据
 * 
 * @author JackSong
 *
 */
public class MainSpinnerAdapter extends ArrayAdapter<String>{
	private Context mContext;
    private String [] mStringArray;
  public MainSpinnerAdapter(Context context, String[] stringArray) {
    super(context, android.R.layout.simple_spinner_item, stringArray);
    mContext = context;
    mStringArray=stringArray;
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    //修改Spinner展开后的字体颜色
    if (convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(mContext);
      convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,false);
    }

    //此处text1是Spinner默认的用来显示下拉菜单TextView
    TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
    tv.setText(mStringArray[position]);
    tv.setTextSize(22f);
    tv.setBackgroundColor(mContext.getResources().getColor(R.color.topbar_bg));
    tv.setTextColor(Color.WHITE);

    return convertView;

  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // 修改Spinner选择后结果的字体颜色
    if (convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(mContext);
      convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
    }

    //此处text1是Spinner默认的用来显示文字的TextView
    TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
    tv.setText(mStringArray[position]);
    tv.setTextSize(24f);
    tv.setTextColor(Color.WHITE);
    return convertView;
  }


}
