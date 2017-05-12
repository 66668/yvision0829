package com.yvision.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yvision.R;
import com.yvision.common.ImageLoadingConfig;
import com.yvision.model.VisitorBModel;
import com.yvision.widget.CircleImageView;

import java.util.ArrayList;

/**
 * 访客列表适配
 *
 * @author
 */

public class VisitorListAdapter extends BaseListAdapter {
    private ImageLoader imgLoader;
    private DisplayImageOptions imgOptions;


    public class WidgetHolder {
        public TextView tv_name;
        public TextView tvTime;
        public TextView tvIsArrival;
        public CircleImageView imageView;
    }

    public VisitorListAdapter(Context context) {
        super(context);
        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(context));
        imgOptions = ImageLoadingConfig.generateDisplayImageOptions(R.mipmap.photo);
    }

    @Override
    protected View inflateConvertView() {
        //一条记录的布局
        View view = inflater.inflate(R.layout.item_common_list, null);
        //该布局上的控件
        WidgetHolder holder = new WidgetHolder();
        holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
        holder.tvIsArrival = (TextView) view.findViewById(R.id.tv_isArrival);
        holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
        holder.imageView = (CircleImageView) view.findViewById(R.id.imageView);
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
        imgLoader.displayImage(model.getImagePath(), holder.imageView, imgOptions);//显示图片
        holder.tv_name.setText(model.getVisitorName());
        holder.tvTime.setText(model.getiLastUpdateTime());
        if (model.isReceived() == true) {
            holder.tvIsArrival.setText("已接待");
        } else {
            holder.tvIsArrival.setText("未接待");

        }


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
