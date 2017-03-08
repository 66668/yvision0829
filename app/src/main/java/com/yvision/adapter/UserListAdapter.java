package com.yvision.adapter;

import android.content.Context;
import android.util.Log;
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
public class UserListAdapter extends BaseListAdapter{
    // 图片缓存
    private DisplayImageOptions imgOption;
    private ImageLoader imgLoader;

    public UserListAdapter(Context context) {
        super(context);
        // 图片缓存实例化
        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(context));
        imgOption = ImageLoadingConfig.generateDisplayImageOptionsNoCatchDisc(R.mipmap.ic_launcher);
    }

    public class WidgetHolder {
        public CircleImageView circleImg;//自定义图片
        public TextView tvTime;
        public TextView tvStyle;
    }

    @Override
    protected View inflateConvertView() {
        View view = inflater.inflate(R.layout.item_usermain_list, null);
        //该布局上的控件
        WidgetHolder holder = new WidgetHolder();
        holder.circleImg = (CircleImageView) view.findViewById(R.id.imageView1);
        holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
        holder.tvStyle = (TextView)view.findViewById(R.id.tv_place);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void initViewData(int position, View convertView) {
        WidgetHolder holder = (WidgetHolder) convertView.getTag();//获取控件管理实例
        AttendModel model = (AttendModel)entityList.get(position);
        Log.d("SJY","UserListAdapter--postion="+position);
        if(model.getAttendType().equals("1")){//local
                imgLoader.displayImage(model.getImagePath(), // 已处理图片地址
                        holder.circleImg, // imageView
                        imgOption);// jar包类
        }else if(model.getAttendType().equals("2")){//map
            holder.circleImg.setImageResource(R.mipmap.map);
        }else if(model.getAttendType().equals("3")){//wifi
            holder.circleImg.setImageResource(R.mipmap.wifi);
        }else if(model.getAttendType().equals("4")){
            holder.circleImg.setImageResource(R.mipmap.ic_launcher);
        }
        holder.tvTime.setText(model.getCapTime());

        if(model.getAttendType().equals("2")){
            holder.tvStyle.setText("地图签到");
        }else if(model.getAttendType().equals("3")){
            holder.tvStyle.setText("wifi签到");
        }else{
            holder.tvStyle.setText("本地");
        }


    }
    public void destroy() {
    }
}
