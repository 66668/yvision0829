package com.yvision.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yvision.R;
import com.yvision.common.ImageLoadingConfig;

/**
 * Created by sjy on 2017/4/14.
 */

public class DetailImageDialog extends Dialog {

    private final String TAG = "SJY";
    //变量

    private Context context;
    private String url;

    private ImageLoader imgLoader;
    private DisplayImageOptions imgOptions;

//    private ClickListenerInterface clickListenerInterface;
    //控件
    private ImageView img;

//    private Button btn_sure;
//    private Button btn_cancel;
//
//    public interface ClickListenerInterface {
//        public void forSure();
//
//        public void forCancel();
//    }

    public DetailImageDialog(Context context, String url) {
        super(context);
        this.context = context;
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setValue();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_img_detail, null);
        setContentView(view);

        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(context));
        imgOptions = ImageLoadingConfig.generateDisplayImageOptions(R.mipmap.default_photo);

        img = (ImageView) view.findViewById(R.id.img);
//        btn_sure = (Button) view.findViewById(R.id.btn_sure);
//        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        //
    }

    public void setValue() {

        imgLoader.displayImage(url, img, imgOptions);
//
//        btn_cancel.setOnClickListener(new ClickListener());
//        btn_sure.setOnClickListener(new ClickListener());

        //弹窗大小
        //        Window dialogWindow = getWindow();
        //        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        //        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        //        dialogWindow.setAttributes(lp);

    }

//    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
//        this.clickListenerInterface = clickListenerInterface;
//    }

//    private class ClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            int id = v.getId();
//            switch (id) {
//                case btn_sure:
//                    clickListenerInterface.forSure();
//                    break;
//                case btn_cancel:
//                    clickListenerInterface.forCancel();
//                    break;
//            }
//        }
//
//    }

}
