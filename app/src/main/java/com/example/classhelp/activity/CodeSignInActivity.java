package com.example.classhelp.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.edittext.verify.VerifyCodeEditText;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CodeSignInActivity extends Activity implements View.OnClickListener {

    private VerifyCodeEditText vcetSignCode;
    private RoundButton rbSignIn;
    private TitleBar titleBar;
    private BaiduMap baiduMap;
    private MapView mapView;
    private LocationClient client;
    private boolean isFirstLocate = true;
    private MiniLoadingDialog miniLoadingDialog;

    private LatLng desLatLng;

    private int locationType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        client = new LocationClient(getApplicationContext());
        client.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_code_sign_in);

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

        rbSignIn.setOnClickListener(this);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);//显示我的位置
        initLcation();
        client.start();
    }

    private void bindView() {
        titleBar = findViewById(R.id.tb_title_bar);
        rbSignIn = findViewById(R.id.rb_sign_in);
        vcetSignCode = findViewById(R.id.vcet_sign_code);
        mapView = findViewById(R.id.bmapView);
        miniLoadingDialog = WidgetUtils.getMiniLoadingDialog(CodeSignInActivity.this);
        miniLoadingDialog.updateMessage("加载中");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_sign_in:
                initLcation();
                client.start();
                if (vcetSignCode.getInputValue().length() < 4) {
                    XToast.warning(this, "请输入完整的考勤码").show();
                } else {
                    miniLoadingDialog.show();
                    startSignIn();
                }
                break;
        }
    }

    private void startSignIn() {
        Map<String, String> map = new HashMap<>();
        map.put(Contract.Token.TOKEN, SPUtils.getToken(CodeSignInActivity.this));
        map.put(Contract.SignIn.LOCATIONTYPE, locationType + "");
        map.put(Contract.SignIn.CODE, vcetSignCode.getInputValue());
        map.put(Contract.SignTask.ID, getIntent().getStringExtra(Contract.SignTask.ID));
        new SignInTask().execute(map);

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
                    .radius(ScanSignInActivity.RADIUS);
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
                    //GPS
                    if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                        navigateTo(bdLocation, ScanSignInActivity.RADIUS, desLatLng);
                        //网络
                    } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                        navigateTo(bdLocation, ScanSignInActivity.RADIUS, desLatLng);
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
                        SPUtils.saveToken(CodeSignInActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());
                        XToast.success(CodeSignInActivity.this, "签到完成").show();
                    } else {
                        XToast.error(CodeSignInActivity.this,
                                "签到失败，确认考勤码正确！").show();
                    }
                } else {
                    XToast.error(CodeSignInActivity.this, "no response").show();
                }
                miniLoadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(CodeSignInActivity.this, "发生未知错误！").show();
            }
        }
    }
}
