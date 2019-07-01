package com.example.classhelp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.entity.Student;
import com.example.classhelp.utils.DateUtils;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.layout.XUILinearLayout;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.configure.TimePickerType;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectChangeListener;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewSignActivity extends AppCompatActivity implements View.OnClickListener {

    private XUILinearLayout xllScan;
    private XUILinearLayout xllNumber;
    private RoundButton rbStart;
    private SuperTextView stvName;
    private SuperTextView stvEndTime;
    private TitleBar titleBar;
    private MiniLoadingDialog miniLoadingDialog;


    private LocationClient client;

    private double latitude;
    private double longitude;

    private String type = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        client = new LocationClient(getApplicationContext());
        client.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_new_sign);

        bindView();

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        stvName.setCenterEditString(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        rbStart.setTextColor(getColor(R.color.xui_config_color_gray_9));

        xllNumber.setOnClickListener(this);
        xllScan.setOnClickListener(this);
        rbStart.setOnClickListener(this);
        stvEndTime.setOnClickListener(this);
        client.start();
    }

    private void bindView() {
        xllNumber = findViewById(R.id.xll_new_num_sign);
        xllScan = findViewById(R.id.xll_new_scan_sign);
        rbStart = findViewById(R.id.rb_start_sign);
        stvName = findViewById(R.id.stv_name);
        stvEndTime = findViewById(R.id.stv_end);
        titleBar = findViewById(R.id.tb_title_bar);
        miniLoadingDialog = WidgetUtils.getMiniLoadingDialog(NewSignActivity.this);
        miniLoadingDialog.updateMessage("加载中");
    }

    private void startNewSign() {
        Map<String, String> signInfo = new HashMap<>();
        signInfo.put(Contract.SignTask.TYPE, type);
        String name = stvName.getCenterEditText().toString();
        if (!"".equals(name)) {
            name = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm");
        }
        signInfo.put(Contract.SignTask.NAME, name);
        signInfo.put(Contract.SignTask.END,
                String.valueOf(DateUtils.parse(stvEndTime.getCenterString()).getTime()));
        signInfo.put(Contract.SignTask.LONGITUDE, String.valueOf(longitude));
        signInfo.put(Contract.SignTask.LATITUDE, String.valueOf(latitude));
        signInfo.put(Contract.Class.ID, getIntent().getStringExtra(Contract.Class.ID));
        signInfo.put(Contract.Token.TOKEN, SPUtils.getToken(NewSignActivity.this));
        new NewSignTask().execute(signInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xll_new_num_sign:
                xllNumber.setBorderColor(getColor(R.color.sign_type_select_border));
                xllNumber.setBackgroundColor(getColor(R.color.sign_type_select_bg));
                xllScan.setBorderColor(getColor(R.color.sign_type_normal_border));
                xllScan.setBackgroundColor(getColor(R.color.sign_type_normal_bg));
                type = "1";
                break;
            case R.id.xll_new_scan_sign:
                xllScan.setBorderColor(getColor(R.color.sign_type_select_border));
                xllScan.setBackgroundColor(getColor(R.color.sign_type_select_bg));
                xllNumber.setBorderColor(getColor(R.color.sign_type_normal_border));
                xllNumber.setBackgroundColor(getColor(R.color.sign_type_normal_bg));
                type = "2";
                break;
            case R.id.rb_start_sign:
                miniLoadingDialog.show();
                startNewSign();
                break;
            case R.id.stv_end:
                TimePickerView mTimePicker = new TimePickerBuilder(this,
                        new OnTimeSelectListener() {
                            @Override
                            public void onTimeSelected(Date date, View v) {
                                if (date.getTime() > new Date().getTime()) {
                                    stvEndTime.setCenterTextColor(Color.BLACK);
                                    stvEndTime.setCenterString(DateUtils.format(date,
                                            DateUtils.FORMAT_LONG));
                                    rbStart.setEnabled(true);
                                    rbStart.setTextColor(getColor(R.color.xui_btn_blue_normal_color));
                                } else {
                                    stvEndTime.setCenterTextColor(getColor(R.color.xui_config_color_gray_9));
                                    stvEndTime.setCenterString("请选择当前之后的时间");
                                    rbStart.setEnabled(false);
                                    rbStart.setTextColor(getColor(R.color.xui_config_color_gray_9));
                                }
                            }
                        })
                        .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                            @Override
                            public void onTimeSelectChanged(Date date) {
                            }
                        })
                        .setType(TimePickerType.ALL)
                        .setTitleBgColor(getResources().getColor(R.color.title_text))
                        .setTitleColor(Color.WHITE)
                        .setTitleText("时间选择")
                        .isDialog(true)
                        .setOutSideCancelable(false)
                        .build();
                mTimePicker.show();
                break;
        }
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            //获取当前位置经纬度
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
        }
    }

    class NewSignTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/task/release";
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
                        SPUtils.saveToken(NewSignActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());

                        JsonObject signInfo = data.getAsJsonObject(Contract.JSONKEY.DATA);

                        //todo: to a show page
                        Intent intent = new Intent(NewSignActivity.this, QRCodeActivity.class);
                        intent.putExtra(Contract.Class.NAME,
                                getIntent().getStringExtra(Contract.Class.NAME));
                        intent.putExtra(Contract.SignTask.NAME, stvName.getCenterEditValue());
                        if (type.equals("1")) {
                            intent.putExtra("type", 1);
                            intent.putExtra(Contract.SignTask.CODE,
                                    signInfo.get(Contract.SignTask.CODE).getAsString());
                        } else {
                            intent.putExtra("type", 2);
                            intent.putExtra(Contract.SignTask.QRCODESRC,
                                    signInfo.get(Contract.SignTask.QRCODESRC).getAsString());
                        }
                        startActivity(intent);
                    } else {
                        XToast.error(NewSignActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                } else {
                    XToast.error(NewSignActivity.this,
                            "no response").show();
                }
                miniLoadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(NewSignActivity.this, "出现未知错误！").show();
                miniLoadingDialog.dismiss();
            }
        }
    }

}
