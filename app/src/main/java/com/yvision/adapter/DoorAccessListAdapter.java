package com.yvision.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yvision.R;
import com.yvision.common.ImageLoadingConfig;
import com.yvision.model.DoorAccessModel;


/**
 * 应用-采购适配
 */
public class DoorAccessListAdapter extends BaseListAdapter {
    private ImageLoader imgLoader;
    private DisplayImageOptions imgOptions;
    private Context context;

    public class WidgetHolder {
        public TextView tv_Title;
        public TextView tvTime;
        public TextView tvState;
        public ImageView imageView;
    }

    public DoorAccessListAdapter(Context context) {
        super(context);
        this.context = context;
        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(context));
        imgOptions = ImageLoadingConfig.generateDisplayImageOptions(R.mipmap.ic_launcher);
    }

    @Override
    protected View inflateConvertView() {
        //一条记录的布局
        View view = inflater.inflate(R.layout.item_viplist, null);
        //该布局上的控件
        WidgetHolder holder = new WidgetHolder();
        holder.tv_Title = (TextView) view.findViewById(R.id.tv_procurementTitle);
        holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
        holder.tvState = (TextView) view.findViewById(R.id.tv_state);
        holder.imageView = (ImageView) view.findViewById(R.id.imageView);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void initViewData(final int position, View convertView) {
        WidgetHolder holder = (WidgetHolder) convertView.getTag();//获取控件管理实例

        DoorAccessModel model = (DoorAccessModel) entityList.get(position);
        //获取一条信息
        holder.tvTime.setText(model.getSmallCapImagePath());
        holder.tv_Title.setText(model.getCapTime());
        imgLoader.displayImage(model.getSmallCapImagePath(), holder.imageView, imgOptions);
    }


    public void destroy() {
        imgLoader.clearMemoryCache();
        imgLoader.destroy();
    }

}
