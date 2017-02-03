package com.yvision;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.yvision.inject.ViewInject;

/**
 * 地图考勤
 * Created by JackSong on 2016/9/9.
 */
public class MapAttendedActivity extends BaseActivity {
    //back
    @ViewInject(id = R.id.img_back,click = "forback")
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_umain_map_attendance);
    }
    private void forback(View view){
        this.finish();
    }
}
