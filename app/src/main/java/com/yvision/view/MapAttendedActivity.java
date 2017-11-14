package com.yvision.view;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.yvision.R;
import com.yvision.common.MyException;
import com.yvision.dialog.Loading;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.EmployeeInfoModel;
import com.yvision.utils.PageUtil;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 地图考勤
 * Created by JackSong on 2016/9/9.
 */
public class MapAttendedActivity extends BaseActivity implements LocationSource, AMapLocationListener, AMap.OnMapClickListener {
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout forBack;

    @ViewInject(id = R.id.tv_right, click = "forAttend")
    TextView tv_right;

    @ViewInject(id = R.id.tv_title)
    TextView tvTitle;
    @ViewInject(id = R.id.tv_time)
    TextView tv_time;
    @ViewInject(id = R.id.tv_address)
    TextView tv_address;

    //AMap是地图对象
    private AMap aMap;
    private MapView mapView;
    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private OnLocationChangedListener mListener = null;

    private boolean isFirstLoc = true;//标识，一次定位
    private ImageView imgBack = null;
    private StringBuffer buffer = null;
    private double lat;//纬度
    private double lon;//精度
    private String currentTime = null;
    private String address = null;
    private EmployeeInfoModel employeeInfoModel;

    //常量
    private final int POST_SUCESS = 1001; // 登陆成功
    private final int POST_FAILED = 1002; // 失败

    /**
     * 签到
     *
     * @param view
     */
    public void forAttend(View view) {

        if (TextUtils.isEmpty(currentTime)) {
            PageUtil.DisplayToast("没有获取时间");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            PageUtil.DisplayToast("没有获取到地址");
            return;
        }

        Loading.run(this, new Runnable() {

            @Override
            public void run() {
                try {
                    Log.d("SJY", "地图考勤");
                    String msg = UserHelper.mapAttend(MapAttendedActivity.this
                            , currentTime
                            , address
                            , lat
                            , lon
                            , employeeInfoModel.getEmployeeName()
                            , employeeInfoModel.getStoreID()
                            , employeeInfoModel.getEmployeeID()
                            , employeeInfoModel.getDepartmentID());

                    // 访问服务端成功，消息处理
                    sendMessage(POST_SUCESS);
                } catch (MyException e) {
                    sendMessage(POST_FAILED, e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_map);
        tvTitle.setText(getResources().getString(R.string.mapAttendance));
        tv_right.setText(getResources().getString(R.string.forAttend));

        initMyView();
        //实现地图生命周期管理
        mapView.onCreate(savedInstanceState);
        setUpMap();
        //添加marker
        addMarker();

    }

    private void initMyView() {
        //获取跳转值
        Bundle bundle = this.getIntent().getExtras();
        employeeInfoModel = (EmployeeInfoModel) bundle.getSerializable("EmployeeInfoModel");
        mapView = (MapView) findViewById(R.id.map_view);

    }

    /**
     * 添加marker
     */
    private void addMarker() {
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 19));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lon));
        markerOptions.title("当前位置");
        markerOptions.visible(true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.location_marker));
        markerOptions.icon(bitmapDescriptor);
        aMap.addMarker(markerOptions);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            //设置显示定位按钮 并且可以点击
            UiSettings settings = aMap.getUiSettings();
            settings.setMyLocationButtonEnabled(true);

            aMap.setLocationSource(this);//设置了定位的监听
            // 是否显示定位按钮
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            aMap.setOnMapClickListener(this);
        }
    }


    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case POST_FAILED:
                PageUtil.DisplayToast((String) msg.obj);
                break;
            case POST_SUCESS:
                PageUtil.DisplayToast("签到成功！");
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

            //定位成功回调信息，设置相关消息
            aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
            lat = aMapLocation.getLatitude();//获取纬度
            lon = aMapLocation.getLongitude();//获取经度
            aMapLocation.getAccuracy();//获取精度信息

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(aMapLocation.getTime());
            currentTime = df.format(date);//定位时间
            address = aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
            aMapLocation.getCountry();//国家信息
            aMapLocation.getProvince();//省信息
            aMapLocation.getCity();//城市信息
            aMapLocation.getDistrict();//城区信息
            aMapLocation.getStreet();//街道信息
            aMapLocation.getStreetNum();//街道门牌号信息
            aMapLocation.getCityCode();//城市编码
            aMapLocation.getAdCode();//地区编码

            //显示定位结果
            tv_time.setText(currentTime);
            tv_address.setText(address);

            // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
            if (isFirstLoc) {
                //设置缩放级别
                aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                //将地图移动到定位点
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                //点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(aMapLocation);
                //添加图钉
                //  aMap.addMarker(getMarkerOptions(amapLocation));
                //获取定位信息
                buffer = new StringBuffer();
                buffer.append(aMapLocation.getCountry() + ""
                        + aMapLocation.getProvince() + ""
                        + aMapLocation.getCity() + ""
                        + aMapLocation.getProvince() + ""
                        + aMapLocation.getDistrict() + ""
                        + aMapLocation.getStreet() + ""
                        + aMapLocation.getStreetNum());
                Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                isFirstLoc = false;
            }

        } else {
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "location Error, ErrCode:"
                    + aMapLocation.getErrorCode() + ", errInfo:"
                    + aMapLocation.getErrorInfo());
            Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * LocationSource激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 只是为了获取当前位置，所以设置为单次定位
            mLocationOption.setOnceLocation(true);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(3000);
            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    //

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位
            mLocationClient.onDestroy();//销毁定位客户端。
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    public void forBack(View view) {
        this.finish();
    }

    /**
     * AMapLocationListener
     *
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {

    }
}
