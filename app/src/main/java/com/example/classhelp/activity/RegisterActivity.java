package com.example.classhelp.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.classhelp.R;
import com.example.classhelp.enums.RegisterContentPage;
import com.example.classhelp.fragment.RegisterPageFragment;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.tabbar.EasyIndicator;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity{

    private EasyIndicator mEasyIndicator;
    private ViewPager mViewPager;
    private TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindView();

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initFragment();
    }

    private void bindView(){
        titleBar = findViewById(R.id.tb_top_bar);
        mEasyIndicator = findViewById(R.id.ei_register_type);
        mViewPager = findViewById(R.id.vp_register);
    }

    private void initFragment() {
        List<RegisterPageFragment> list = new ArrayList<>();
        String[] pages = RegisterContentPage.getPageNames();
        for (String page : pages) {
            list.add(RegisterPageFragment.newInstance(page, RegisterActivity.this));
        }
        mEasyIndicator.setTabTitles(RegisterContentPage.getPageNames());
        mEasyIndicator.setViewPager(mViewPager, new FragmentAdapter<>(getSupportFragmentManager(), list));
        mViewPager.setOffscreenPageLimit(RegisterContentPage.size() - 1);
    }
}
