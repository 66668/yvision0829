package com.yvision.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yvision.R;
import com.yvision.common.ImageLoadingConfig;
import com.yvision.model.OldEmployeeImgModel;

import java.util.List;

/**
 * Created by sjy on 2017/3/31.
 */

public class AttendGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<OldEmployeeImgModel> list;
    private DisplayImageOptions imgOption;
    private ImageLoader imgLoader;
    int colnum;

    public AttendGridViewAdapter(Context context, List<OldEmployeeImgModel> list, int colnum) {
        this.mContext = context;
        this.list = list;
        this.colnum = colnum;
        // 图片缓存实例化
        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(context));
        imgOption = ImageLoadingConfig.generateDisplayImageOptions(R.mipmap.default_photo);//R.mipmap.ic_launcher
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    //
    //    @Override
    //    //创建View方法
    //    public View getView(int position, View convertView, ViewGroup parent) {
    //        ImageView imageView;
    //        if (convertView == null) {
    //            imageView = new ImageView(mContext);
    //            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));//设置ImageView对象布局(100,100)
    //            imageView.setAdjustViewBounds(false);//设置边界对齐
    ////            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型
    ////            imageView.setPadding(8, 8, 8, 8);//设置间距
    //        }
    //        else {
    //            imageView = (ImageView) convertView;
    //        }
    //        imgLoader.displayImage(list.get(position).getImagePath(), imageView, imgOption);
    //        return imageView;
    //    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the note_item layout manually, and treat it as the item view
        // 重新填充note_item部局，并把它作为项的view返回

        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_item);

        imgLoader.displayImage(list.get(position).getImagePath(), imageView, imgOption);

        // Calculate the item width by the column number to let total width fill the screen width
        // 根据列数计算项目宽度，以使总宽度尽量填充屏幕
        int itemWidth = (int) (mContext.getResources().getDisplayMetrics().widthPixels - colnum * 10) / colnum;
        // Calculate the height by your scale rate, I just use itemWidth here
        // 下面根据比例计算您的item的高度，此处只是使用itemWidth
        int itemHeight = itemWidth;

        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                itemWidth,
                itemHeight);
        convertView.setLayoutParams(param);

        return convertView;
    }

    public void destory() {
        if (imgLoader != null) {
            imgLoader.destroy();
        }
    }
}
