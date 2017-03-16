package com.yvision.view;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yvision.R;
import com.yvision.common.ImageLoadingConfig;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.VipModel;

/**
 * vip详细信息
 * <p/>
 * Created by JackSong on 2016/9/9.
 */
public class VipInfoActivity extends BaseActivity {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right)
    TextView tv_right;

    //姓名
    @ViewInject(id = R.id.tv_name)
    TextView tv_name;

    //vip号
    @ViewInject(id = R.id.tv_workid)
    TextView tv_workid;

    //性别
    @ViewInject(id = R.id.tv_gender)
    TextView tv_gender;

    //时间
    @ViewInject(id = R.id.tv_time)
    TextView tv_time;

    //分数
    @ViewInject(id = R.id.tv_score)
    TextView tv_score;

    //注册图
    @ViewInject(id = R.id.img_org)
    ImageView img_org;

    //抓拍图
    @ViewInject(id = R.id.img_cap)
    ImageView img_cap;

    //变量
    private VipModel model = null;
    private String attendId;

    private ImageLoader imgLoader;
    private DisplayImageOptions imgOptions;

    //变量
    private static final int GET_DATA_SUCCESS = 22;
    private static final int GET_DATA_FAILED = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_vip_detail_info);

        initMyView();
        getDate();

    }

    private void initMyView() {

        tv_title.setText(getResources().getString(R.string.text_vip_title));
        tv_right.setText("");

        //图片缓存设置
        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(this));
        imgOptions = ImageLoadingConfig.generateDisplayImageOptions(R.mipmap.photo);

        Bundle bundle = this.getIntent().getExtras();
        attendId = (String) bundle.get("AttendID");
    }

    private void getDate() {

        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                try {
                    VipModel model = UserHelper.getVIPDetail(VipInfoActivity.this, attendId);
                    sendMessage(GET_DATA_SUCCESS, model);
                } catch (MyException e) {
                    sendMessage(GET_DATA_FAILED, e.getMessage());
                }
            }
        });

    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case GET_DATA_SUCCESS:
                model = (VipModel) msg.obj;
                setShow(model);

                break;
        }
    }

    private void setShow(VipModel model) {

        tv_name.setText(model.getEmployeeName());
        tv_workid.setText(model.getEmployeeCardNo());
        tv_gender.setText(model.getEmployeeGender());
        tv_time.setText(model.getCapTime());
        tv_score.setText(model.getScore());

        imgLoader.displayImage(model.getImagePath(), img_org, imgOptions);
        imgLoader.displayImage(model.getSmallCapImagePath(), img_cap, imgOptions);

    }

    /**
     * back
     *
     * @param view
     */
    public void forBack(View view) {
        this.finish();
    }
}
