package com.yvision.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yvision.R;
import com.yvision.common.ImageLoadingConfig;
import com.yvision.model.AttendModel;
import com.yvision.widget.CircleImageView;

/**
 * Created by JackSong on 2016/9/12.
 */
public class AttendListAdapter extends BaseListAdapter {
    // 图片缓存
    private Context context;
    private DisplayImageOptions imgOption;
    private ImageLoader imgLoader;

    public AttendListAdapter(Context context) {
        super(context);
        this.context = context;
        // 图片缓存实例化
        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(context));
        imgOption = ImageLoadingConfig.generateDisplayImageOptions(R.mipmap.default_photo);//R.mipmap.ic_launcher
    }

    public class WidgetHolder {
        public TextView tv_name;
        public TextView tvTime;
        public CircleImageView imageView;
    }

    @Override
    protected View inflateConvertView() {
        View view = inflater.inflate(R.layout.item_common_list, null);
        //该布局上的控件
        WidgetHolder holder = new WidgetHolder();
        holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
        holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
        holder.imageView = (CircleImageView) view.findViewById(R.id.imageView);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void initViewData(int position, View convertView) {
        WidgetHolder holder = (WidgetHolder) convertView.getTag();//获取控件管理实例
        AttendModel model = (AttendModel) entityList.get(position);

        holder.tv_name.setText(model.getEmployeeName());
        holder.tvTime.setText(model.getCapTime());
        //根据选择图片显示方式
        if (model.getAttendType().contains("1")) {
            holder.imageView.setImageBitmap(null);
            imgLoader.init(ImageLoaderConfiguration.createDefault(context));//异常提示没注册
            imgLoader.displayImage(model.getSmallCapImagePath(), holder.imageView, imgOption);
        } else if (model.getAttendType().contains("2")) {
            holder.imageView.setBackground(convertView.getResources().getDrawable(R.mipmap.item_map_logo));
        }else if (model.getAttendType().contains("3")) {
            holder.imageView.setBackground(convertView.getResources().getDrawable(R.mipmap.default_photo));
        }else{
            holder.imageView.setImageBitmap(null);
            imgLoader.init(ImageLoaderConfiguration.createDefault(context));//异常提示没注册
            imgLoader.displayImage(model.getSmallCapImagePath(), holder.imageView, imgOption);
        }


    }

    public void destroy() {
        if (imgLoader != null) {
            imgLoader.clearMemoryCache();
            imgLoader.destroy();
        }
    }
}
