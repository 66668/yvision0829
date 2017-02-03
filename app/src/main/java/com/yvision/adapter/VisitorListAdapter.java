package com.yvision.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yvision.R;
import com.yvision.common.ImageLoadingConfig;
import com.yvision.common.MyApplication;
import com.yvision.model.VisitorBModel;

import java.util.ArrayList;

/**
 * 访客列表适配
 *
 * @author
 */

public class VisitorListAdapter extends CommonListAdapter {
    private ImageLoader imgLoader;
    private DisplayImageOptions imgOptions;


    public class WidgetHolder {
        public CheckBox checkBox;//
        public ImageView imageHeader;//自定义图片
        public TextView tvName;
        public TextView tvTime;
        public TextView tvStatus;//接待状态
    }

    public VisitorListAdapter(Context context, AdapterCallBack callBack) {
        super(context, callBack);
        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(context));
        imgOptions = ImageLoadingConfig.generateDisplayImageOptions(R.mipmap.photo);
    }

    @Override
    protected View inflateConvertView() {
        initData();
        //一条记录的布局
        View view = inflater.inflate(R.layout.item_visitormain_list, null);
        //该布局上的控件
        WidgetHolder holder = new WidgetHolder();
        holder.imageHeader = (ImageView) view.findViewById(R.id.imageView1);
        holder.tvName = (TextView) view.findViewById(R.id.tv_name);
        holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
        holder.tvStatus = (TextView) view.findViewById(R.id.tv_Status);
        holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void initViewData(final int position, View convertView) {
        WidgetHolder holder = (WidgetHolder) convertView.getTag();//获取控件管理实例
        //获取一条信息
        //?java.lang.ClassCastException: java.util.ArrayList cannot be cast to com.yvision.model.VisitorBModel
        VisitorBModel model = (VisitorBModel) entityList.get(position);
        //赋值
        imgLoader.displayImage(model.getImagePath(), holder.imageHeader, imgOptions);//显示图片
        holder.tvName.setText(model.getVisitorName());
        //
        String showTime = model.getArrivalTimePlan();
        if (showTime.length() > 0) {
            holder.tvTime.setText(showTime);//预约时间显示
        } else {
            holder.tvTime.setText("注册时未添加！");
        }

        if (model.isReceived()) {
            holder.tvStatus.setText("已接待");
            //处理不可点击
        } else {
            holder.tvStatus.setText("未接待");
        }

        if (MyApplication.getInstance().checkBoxStatus == true) {
            holder.checkBox.setVisibility(View.VISIBLE);//checkBox不可见

            //关联全选按钮
            if (isCheckedList.size() > 0) {
                Log.d("List", "VisitorAdapter--initViewData--isCheckedList长度=" + isCheckedList.size());
                holder.checkBox.setChecked(isCheckedList.get(position));//下标越界？
            }

            //checkBox多选
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        isCheckedList.set(position, true);
                        Log.d("List","visitorAdapter--position ="+position+"赋值"+true);
                    } else {
                        isCheckedList.set(position, false);
                        Log.d("List","visitorAdapter--position ="+position+"赋值"+false);
                    }
                }
            });

        } else {
            //checkBox不可见
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

    }

    public void initData() {
        Log.d("SJY", "VisitorAdapter--创建加载--entitylist长度=" + getEntityList().size());
        if (isCheckedList == null) {
            isCheckedList = new ArrayList<Boolean>();
            Log.d("SJY", "VisitorListAdapter--List长度=" + isCheckedList.size());
        }
        //getView回调，list长度不断增加，做不增加处理
        if (getEntityList().size() == isCheckedList.size()) {
            for (int i = 0; i < getEntityList().size(); i++) {
                isCheckedList.set(i, false);//赋值(避免长度增长)

            }
        } else {
            isCheckedList.clear();
            for (int i = 0; i < getEntityList().size(); i++) {
                isCheckedList.add(i, false);

            }
        }
        setisCheckedList(isCheckedList);
    }

    public void destroy() {
        imgLoader.clearMemoryCache();
        imgLoader.destroy();
    }

    //checkBox相关
    public static void setisCheckedList(ArrayList<Boolean> isCheckedList) {
        VisitorListAdapter.isCheckedList = isCheckedList;
    }
}
