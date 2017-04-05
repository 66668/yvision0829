package com.yvision.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
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

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<OldEmployeeImgModel> list;
    private DisplayImageOptions imgOption;
    private ImageLoader imgLoader;

    public GridViewAdapter(Context context, List<OldEmployeeImgModel> list){
        this.mContext = context;
        this.list = list;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        }else{
            imageView=(ImageView) convertView;
        }

        imgLoader.displayImage(list.get(position).getImagePath(), imageView, imgOption);

        return imageView;
    }

    public void destory(){
        if (imgLoader != null) {
            imgLoader.destroy();
        }
    }
}
