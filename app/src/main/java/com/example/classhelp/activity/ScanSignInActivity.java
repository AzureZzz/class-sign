package com.example.classhelp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonObject;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.toast.XToast;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScanSignInActivity extends AppCompatActivity implements View.OnClickListener {

    public final static int RADIUS = 500;

    private LinearLayout llScan;
    private ImageView ivScan;
    private TitleBar titleBar;
    private BaiduMap baiduMap;
    private MapView mapView;
    private LocationClient client;
    private MiniLoadingDialog miniLoadingDialog;
    private boolean isFirstLocate = true;

    private LatLng desLatLng;

    private int locationType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        client = new LocationClient(getApplicationContext());
        client.registerLocationListener(new ScanSignInActivity.MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_scan_sign_in);

        bindView();

        double latitude = Double.valueOf(getIntent().getStringExtra(Contract.SignTask.LATITUDE));
        double longitude = Double.valueOf(getIntent().getStringExtra(Contract.SignTask.LONGITUDE));
        desLatLng = new LatLng(latitude, longitude);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llScan.setOnClickListener(this);
        ivScan.setOnClickListener(this);

        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);//显示我的位置
        initLcation();
        client.start();
    }

    private void bindView() {
        llScan = findViewById(R.id.ll_scan);
        ivScan = findViewById(R.id.iv_scan);
        titleBar = findViewById(R.id.tb_title_bar);
        mapView = findViewById(R.id.bmapView);
        miniLoadingDialog = WidgetUtils.getMiniLoadingDialog(ScanSignInActivity.this);
        miniLoadingDialog.updateMessage("加载中");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_scan:
                scan();
                initLcation();
                client.start();
                break;
            case R.id.ll_scan:
                scan();
                initLcation();
                client.start();
                break;
        }
    }

    private void scan() {
        Intent openCameraIntent = new Intent(getApplication(), CaptureActivity.class);
        startActivityForResult(openCameraIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Map<String, String> map = new HashMap<>();
            map.put(Contract.Token.TOKEN, SPUtils.getToken(ScanSignInActivity.this));
            map.put(Contract.SignIn.LOCATIONTYPE, locationType + "");
            map.put(Contract.SignIn.CODE, scanResult);
            map.put(Contract.SignTask.ID, getIntent().getStringExtra(Contract.SignTask.ID));
            new SignInTask().execute(map);
        }
    }

    private void initLcation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(3000);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setIsNeedAddress(true);
        client.setLocOption(option);
    }

    private void navigateTo(BDLocation location, int radius, LatLng desLatLng) {
        if (isFirstLocate) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.animateMapStatus(statusUpdate);
            statusUpdate = MapStatusUpdateFactory.zoomTo(16f); //地图缩放
            baiduMap.animateMapStatus(statusUpdate);
            //画圆
            OverlayOptions ooCircle = new CircleOptions().fillColor(0x384d73b3)
                    .center(latLng).stroke(new Stroke(3, 0x784d73b3))
                    .radius(500);
            baiduMap.addOverlay(ooCircle);
            isFirstLocate = false;

            //判断是否在在打卡圈中
            boolean flag = isRange(latLng, radius, desLatLng);
            if (flag) {
                locationType = 1;
            } else {
                locationType = 2;
            }

        }
        //显示我的位置
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData locationData = builder.build();
        baiduMap.setMyLocationData(locationData);

    }

    /**
     * 返回是否在打卡范围内
     *
     * @return 返回值
     * var0表示圆心的坐标，var1代表圆心的半径，var2代表要判断的点是否在圆内
     * isCircleContainsPoint(LatLng var0, int var1, LatLng var2);
     */
    private boolean isRange(LatLng latLng, int radius, LatLng desLatLng) {
        return SpatialRelationUtil.isCircleContainsPoint(latLng, radius, desLatLng);
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                        navigateTo(bdLocation, RADIUS, desLatLng);
                    } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                        navigateTo(bdLocation, RADIUS, desLatLng);
                    }
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    class SignInTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/sign/stuSign";
            try {
                return OkHttpUtils.post(url, OkHttpUtils.GSON.toJson(maps[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s != null) {
                    JsonObject res = OkHttpUtils.GSON.fromJson(s, JsonObject.class);
                    if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
                        JsonObject data = res.getAsJsonObject(Contract.JSONKEY.DATA);
                        JsonObject newToken = data.getAsJsonObject(Contract.JSONKEY.NEWTOKEN);
                        SPUtils.saveToken(ScanSignInActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());
                        XToast.success(ScanSignInActivity.this, "签到完成").show();
                    } else {
                        XToast.error(ScanSignInActivity.this,
                                "签到失败，确认二维码正确！").show();
                    }
                } else {
                    XToast.error(ScanSignInActivity.this, "no response").show();
                }
                miniLoadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(ScanSignInActivity.this, "发生未知错误！").show();
            }
        }
    }
}
