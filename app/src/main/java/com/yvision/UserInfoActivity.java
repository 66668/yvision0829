package com.yvision;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yvision.common.ImageLoadingConfig;
import com.yvision.inject.ViewInject;
import com.yvision.model.AttendModel;

/**
 * 考勤详细信息
 * <p/>
 * Created by JackSong on 2016/9/9.
 */
public class UserInfoActivity extends BaseActivity {

    //地点
    @ViewInject(id = R.id.tv_place)
    TextView tv_place;

    //时间
    @ViewInject(id = R.id.tv_time)
    TextView tv_time;

    //back
    @ViewInject(id = R.id.img_back, click = "forBack")
    ImageView img_back;

    //时间方式
    @ViewInject(id = R.id.spinner_time)
    Spinner spinner_time;

    //考勤方式
    @ViewInject(id = R.id.spinner_style)
    Spinner spinner_style;

    //本地界面
    @ViewInject(id = R.id.layout_local)
    RelativeLayout layout_local;
    //imageView
    @ViewInject(id = R.id.img_local)
    ImageView img_local;

    //地图界面
    @ViewInject(id = R.id.layout_map)
    RelativeLayout layout_map;
    //imageView
    @ViewInject(id = R.id.img_map)
    ImageView img_map;

    //wifi界面
    @ViewInject(id = R.id.layout_wifi)
    RelativeLayout layout_wifi;
    //imageView
    @ViewInject(id = R.id.img_wifi)
    ImageView img_wifi;
    //tv
    @ViewInject(id = R.id.tv_wifi)
    TextView tv_wifi;
    // 上一个
    @ViewInject(id = R.id.btn_previous, click = "toPreviousAttend")
    Button bt_previous;

    //下一个
    @ViewInject(id = R.id.btn_next, click = "toNextAttend")
    Button btn_next;

    //变量
    private AttendModel model = null;
    private String captime;//考勤时间
    private String attendType;//考勤方式

    private ImageLoader imgLoader;
    private DisplayImageOptions imgOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_usermain_detail_info);
        initVariables();
        initViews();
        initDate();

    }

    private void initVariables() {
        //图片缓存设置
        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(this));
        imgOptions = ImageLoadingConfig.generateDisplayImageOptions(R.mipmap.photo);

        model = (AttendModel) getIntent().getSerializableExtra("AttendModel");
        captime = model.getCapTime();//格式： 2016-09-12 10:07:55
        Log.d("SJY", "captime=" + captime);
        attendType = model.getAttendType();
    }

    private void initViews() {

        if (attendType.equals("")) {//local
            layout_local.setVisibility(View.VISIBLE);//可见
            if (layout_map != null) {
                layout_map.setVisibility(View.GONE);
            }
            if (layout_wifi != null) {
                layout_wifi.setVisibility(View.GONE);
            }
        } else if (attendType.equals("2")) {//map
            if (layout_map != null) {
                layout_local.setVisibility(View.GONE);
            }
            layout_map.setVisibility(View.VISIBLE);//可见
            if (layout_wifi != null) {
                layout_wifi.setVisibility(View.GONE);
            }

        } else if (attendType.equals("3")) {//wifi
            if (layout_local != null) {
                layout_local.setVisibility(View.GONE);
            }
            if (layout_map != null) {
                layout_map.setVisibility(View.GONE);
            }
            layout_wifi.setVisibility(View.VISIBLE);//可见


        }
    }

    private void initDate() {
        tv_time.setText(captime);
        if (attendType.equals("")) {//local
            imgLoader.displayImage(model.getImagePath(), img_local, imgOptions);//显示图片
            tv_place.setText("本地");
        } else if (attendType.equals("2")) {//map
            Toast.makeText(this, "接口没写好唉！", Toast.LENGTH_SHORT).show();
        } else if (attendType.equals("3")) {//wifi
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            img_wifi.setImageBitmap(bitmap);
//            tv_wifi.setText(model.get);
        }
    }

    /**
     * back
     *
     * @param view
     */
    public void forBack(View view) {
        this.finish();
    }

    /**
     * 下一个
     */
    public void toNextAttend(View view) {

    }

    /**
     * 上一个
     */
    public void toPreviousAttend(View view) {

    }

}
