package com.example.classhelp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.classhelp.R;
import com.example.classhelp.fragment.ClassListFragment;
import com.example.classhelp.fragment.MyFragment;
import com.example.classhelp.utils.SPUtils;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {


    private ImageView ivMenu;
    private LinearLayout llClass;
    private LinearLayout llMe;
    private ImageView ivClass;
    private ImageView ivMe;
    private ClassListFragment classListFragment;
    private MyFragment myFragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSelect(0);

        llClass = findViewById(R.id.ll_main_class);
        llMe = findViewById(R.id.ll_main_my);
        ivClass = findViewById(R.id.iv_main_class);
        ivMe = findViewById(R.id.iv_main_my);
        ivClass.setImageResource(R.drawable.class_blue);
        llClass.setOnClickListener(this);
        llMe.setOnClickListener(this);
        ivClass.setOnClickListener(this);
        ivMe.setOnClickListener(this);

        ivMenu = findViewById(R.id.iv_main_fragment_menu);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterItem[] menuItems;
                if (SPUtils.getUserType(MainActivity.this) == 1) {
                    menuItems = new AdapterItem[]{
                            new AdapterItem("加入课堂", R.drawable.ic_create_class)
                    };
                } else {
                    menuItems = new AdapterItem[]{
                            new AdapterItem("加入课堂", R.drawable.ic_create_class),
                            new AdapterItem("新建课堂", R.drawable.ic_pre_icon_class)
                    };
                }
                XUISimplePopup mMenuPopup = new XUISimplePopup(MainActivity.this, menuItems)
                        .create(new XUISimplePopup.OnPopupItemClickListener() {
                            @Override
                            public void onItemClick(XUISimpleAdapter adapter, AdapterItem item,
                                                    int position) {
                                Intent intent;
                                switch (position) {
                                    case 0:
                                        intent = new Intent(MainActivity.this,
                                                JoinClassActivity.class);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        intent = new Intent(MainActivity.this,
                                                NewClassActivity.class);
                                        startActivity(intent);
                                        break;
                                }
                            }
                        });
                mMenuPopup.show(v);
//                mMenuPopup.showDown(v);

            }
        });

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_main_class:
                setSelect(0);
                ivClass.setImageResource(R.drawable.class_blue);
                ivMe.setImageResource(R.drawable.my_gray);
                break;
            case R.id.iv_main_class:
                setSelect(0);
                ivClass.setImageResource(R.drawable.class_blue);
                ivMe.setImageResource(R.drawable.my_gray);
                break;
            case R.id.iv_main_my:
                setSelect(1);
                ivClass.setImageResource(R.drawable.class_gray);
                ivMe.setImageResource(R.drawable.my_blue);
                break;
            case R.id.ll_main_my:
                setSelect(1);
                ivClass.setImageResource(R.drawable.class_gray);
                ivMe.setImageResource(R.drawable.my_blue);
                break;
            default:
                break;

        }

    }

    //提供相应的fragment显示
    private void setSelect(int i) {

        //获取fragment事务管理
        FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = supportFragmentManager.beginTransaction();
        //隐藏所有fragment的显示
        hideFragment();
        switch (i) {
            case 0:
                if (classListFragment == null) {
                    classListFragment = new ClassListFragment(); // 创建对象以后，并不会调用声明周期方法，而上在commit后调用
                    fragmentTransaction.add(R.id.fl_main, classListFragment);
                }
                //显示当前的fragment
                fragmentTransaction.show(classListFragment);
                break;

            case 1:
                if (myFragment == null) {
                    myFragment = new MyFragment(); // 创建对象以后，并不会调用声明周期方法，而上在commit后调用
                    fragmentTransaction.add(R.id.fl_main, myFragment);
                }
                //显示当前的fragment
                fragmentTransaction.show(myFragment);
                break;
        }

        fragmentTransaction.commit(); //提交事务
    }

    private void hideFragment() {
        if (classListFragment != null) {
            fragmentTransaction.hide(classListFragment);
        }
        if (myFragment != null) {
            fragmentTransaction.hide(myFragment);
        }

    }


}
