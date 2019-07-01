package com.example.classhelp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.classhelp.Contract;
import com.example.classhelp.enums.SignInfoContentPage;
import com.example.classhelp.R;
import com.example.classhelp.entity.SignInfo;
import com.example.classhelp.fragment.RegisterPageFragment;
import com.example.classhelp.fragment.SignInfoPageFragment;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.tabbar.EasyIndicator;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private PieChart mPieChart;
    private EasyIndicator mEasyIndicator;
    private ViewPager mViewPager;
    private ImageView ivBack;
    private ImageView ivCode;

    private List<SignInfo> signInfos = new ArrayList<>();
    private List<SignInfo> arriveSignInfos = new ArrayList<>();
    private List<SignInfo> truantSignInfos = new ArrayList<>();
    private List<SignInfo> errorSignInfos = new ArrayList<>();

    private List<SignInfoPageFragment> pageFragmentList;

    private SmartRefreshLayout mRefreshLayout;
    private MaterialHeader mMaterialHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_info);

        bindView();
        mRefreshLayout.autoRefresh();
        mMaterialHeader.setShowBezierWave(false);

        ivCode.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadSignInfo();
            }
        });
    }

    private void bindView() {
        mPieChart = findViewById(R.id.pc_sign_statistics);
        mEasyIndicator = findViewById(R.id.ei_sign_type);
        mViewPager = findViewById(R.id.vp_sign_list);
        ivBack = findViewById(R.id.iv_back);
        ivCode = findViewById(R.id.iv_code);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mMaterialHeader = (MaterialHeader) mRefreshLayout.getRefreshHeader();
    }

    private void initFragment() {
        pageFragmentList = new ArrayList<>();
        String[] pages = SignInfoContentPage.getPageNames();
        pageFragmentList.add(SignInfoPageFragment.newInstance(pages[0], SignInfoActivity.this,
                arriveSignInfos));
        pageFragmentList.add(SignInfoPageFragment.newInstance(pages[1], SignInfoActivity.this,
                truantSignInfos));
        pageFragmentList.add(SignInfoPageFragment.newInstance(pages[2], SignInfoActivity.this,
                errorSignInfos));
        mEasyIndicator.setTabTitles(SignInfoContentPage.getPageNames());
        mEasyIndicator.setViewPager(mViewPager, new FragmentAdapter<>(getSupportFragmentManager()
                , pageFragmentList));
        mViewPager.setOffscreenPageLimit(SignInfoContentPage.size() - 1);
    }

    private void loadSignInfo() {
        Map<String, String> map = new HashMap<>();
        map.put(Contract.Token.TOKEN, SPUtils.getToken(SignInfoActivity.this));
        map.put(Contract.SignTask.ID, getIntent().getStringExtra(Contract.SignTask.ID));
        new LoadSignInfoTask().execute(map);
    }

    private void initPieChart(int[] nums) {
        mPieChart.setUsePercentValues(false);//是否使用百分比显示
        Description description = mPieChart.getDescription();
        description.setEnabled(false);
        description.setText("Assets View"); //设置描述的文字
        mPieChart.setHighlightPerTapEnabled(true); //设置piecahrt图表点击Item高亮是否可用
        mPieChart.animateX(1000);
        initPieChartData(nums);

//        mPieChart.setDrawEntryLabels(false); // 设置entry中的描述label是否画进饼状图中
//        mPieChart.setEntryLabelColor(Color.GRAY);//设置该文字是的颜色
//        mPieChart.setEntryLabelTextSize(10f);//设置该文字的字体大小

        mPieChart.setDrawHoleEnabled(true);//设置圆孔的显隐，也就是内圆
        mPieChart.setHoleRadius(28f);//设置内圆的半径。外圆的半径好像是不能设置的，改变控件的宽度和高度，半径会自适应。
        mPieChart.setHoleColor(Color.WHITE);//设置内圆的颜色
        mPieChart.setDrawCenterText(true);//设置是否显示文字
        mPieChart.setCenterText("签到统计");//设置饼状图中心的文字
        mPieChart.setCenterTextSize(10f);//设置文字的消息
        mPieChart.setCenterTextColor(Color.parseColor("#00CED1"));//设置文字的颜色
        mPieChart.setTransparentCircleRadius(31f);//设置内圆和外圆的一个交叉园的半径，这样会凸显内外部的空间
        mPieChart.setTransparentCircleColor(Color.BLACK);//设置透明圆的颜色
        mPieChart.setTransparentCircleAlpha(50);//设置透明圆你的透明度
        mPieChart.setDrawEntryLabels(false);//设置mPieChart是否只显示饼图上百分比不显示文字

        Legend legend = mPieChart.getLegend();//图例
        legend.setEnabled(true);//是否显示
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);//对齐
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//对齐
        legend.setForm(Legend.LegendForm.LINE);//设置图例的图形样式,默认为圆形
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);//设置图例的排列走向:vertacal相当于分行
        legend.setFormSize(8f);//设置图例的大小
        legend.setTextSize(8f);//设置图注的字体大小
        legend.setFormToTextSpace(8f);//设置图例到图注的距离

        legend.setDrawInside(true);//不是很清楚
        legend.setWordWrapEnabled(false);//设置图列换行(注意使用影响性能,仅适用legend位于图表下面)，我也不知道怎么用的
        legend.setTextColor(Color.BLACK);
    }

    private void initPieChartData(int[] nums) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(nums[0], "出勤" + nums[0] + "人"));
        pieEntries.add(new PieEntry(nums[1], "旷课" + nums[1] + "人"));
        pieEntries.add(new PieEntry(nums[2], "异常" + nums[2] + "人"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, null);
        pieDataSet.setColors(Color.parseColor("#00CED1"),
                Color.parseColor("#FF4500"), Color.parseColor("#C0C0C0"));
        pieDataSet.setSliceSpace(2f);//设置每块饼之间的空隙
        pieDataSet.setSelectionShift(10f);//点击某个饼时拉长的宽度

        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);
        pieData.setValueTextSize(12f);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Integer.valueOf((int) value).toString();
            }
        });
        pieData.setValueTextColor(Color.WHITE);

        mPieChart.setData(pieData);
        mPieChart.invalidate();
    }

    private void refreshData() {
        truantSignInfos.clear();
        arriveSignInfos.clear();
        errorSignInfos.clear();
        for (SignInfo info : signInfos) {
            switch (info.getSignState()) {
                case 0:
                    truantSignInfos.add(info);
                    break;
                case 1:
                    arriveSignInfos.add(info);
                    break;
                case 2:
                    errorSignInfos.add(info);
                    break;
            }
        }
        int[] nums = {
                arriveSignInfos.size(),
                truantSignInfos.size(),
                errorSignInfos.size()
        };;
        initFragment();
        initPieChart(nums);
        for (SignInfoPageFragment fragment : pageFragmentList) {
            fragment.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_code:
                Intent intent = new Intent(SignInfoActivity.this, QRCodeActivity.class);
                intent.putExtra(Contract.SignTask.NAME,
                        getIntent().getStringExtra(Contract.SignTask.NAME));
                String type = getIntent().getStringExtra(Contract.SignTask.TYPE);
                if (type.equals("1")) {
                    intent.putExtra("type", 1);
                    intent.putExtra(Contract.SignTask.CODE,
                            getIntent().getStringExtra(Contract.SignTask.TASKCODE));
                } else {
                    intent.putExtra("type", 2);
                    intent.putExtra(Contract.SignTask.QRCODESRC,
                            getIntent().getStringExtra(Contract.SignTask.QRCODESRC));
                }
                startActivity(intent);
                break;
        }
    }

    class LoadSignInfoTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/sign/getSignRecByTec";
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
                        SPUtils.saveToken(SignInfoActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());

                        Type type = new TypeToken<ArrayList<SignInfo>>() {
                        }.getType();
                        JsonArray signInfoArray = data.getAsJsonArray(Contract.JSONKEY.DATA);
                        List<SignInfo> signInfoList =
                                OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(signInfoArray),
                                        type);
                        signInfos.clear();
                        signInfos.addAll(signInfoList);
                        refreshData();
                        mRefreshLayout.finishRefresh();
                    } else {
                        XToast.error(SignInfoActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(SignInfoActivity.this, "发生未知错误！").show();
            }
        }
    }

}
