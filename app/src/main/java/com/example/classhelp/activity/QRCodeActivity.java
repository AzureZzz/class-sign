package com.example.classhelp.activity;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xui.widget.toast.XToast;

public class QRCodeActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private SuperTextView stvClassInfo;
    private RadiusImageView rivQrcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        bindView();
        loadInfo();

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void bindView() {
        titleBar = findViewById(R.id.tb_title_bar);
        stvClassInfo = findViewById(R.id.stv_class_info);
        rivQrcode = findViewById(R.id.riv_qrcode);
    }

    private void loadInfo() {
        int type = getIntent().getIntExtra("type", 0);
        if (type == 0) {  //课堂二维码
            stvClassInfo.setCenterTopString(getIntent().getStringExtra(Contract.Class.NAME));
            stvClassInfo.setCenterBottomString(getIntent().getStringExtra(Contract.Class.CODE));
            String url = getIntent().getStringExtra(Contract.Class.QRCODESRC);
            Glide.with(QRCodeActivity.this)
                    .load(url)
                    .into(rivQrcode);
        } else if (type == 1) { //数字签到
            if (getIntent().getStringExtra(Contract.Class.NAME) != null) {
                titleBar.setTitle(getIntent().getStringExtra(Contract.Class.NAME));
            }
            stvClassInfo.setCenterTopString(getIntent().getStringExtra(Contract.SignTask.CODE));
            stvClassInfo.setCenterBottomString(getIntent().getStringExtra(Contract.SignTask.NAME));
            rivQrcode.setImageDrawable(null);
        } else {  //扫码签到
            if (getIntent().getStringExtra(Contract.Class.NAME) != null) {
                titleBar.setTitle(getIntent().getStringExtra(Contract.Class.NAME));
            }
            stvClassInfo.setCenterTopString(getIntent().getStringExtra(Contract.SignTask.NAME));
            stvClassInfo.setCenterBottomString(null);
            Glide.with(QRCodeActivity.this)
                    .load(getIntent().getStringExtra(Contract.SignTask.QRCODESRC))
                    .into(rivQrcode);
        }
    }
}
