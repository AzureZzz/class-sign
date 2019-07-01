package com.example.classhelp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewClassActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private SuperTextView stvClassName;
    private SuperTextView stvClassNo;
    private RoundButton rbCreateClass;
    private MiniLoadingDialog miniLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);

        bindView();

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rbCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miniLoadingDialog.show();
                Map<String,String> info = new HashMap<>();
                info.put(Contract.Class.NAME,stvClassName.getCenterEditValue());
                info.put(Contract.Class.NO,stvClassNo.getCenterEditValue());
                info.put(Contract.Token.TOKEN, SPUtils.getToken(NewClassActivity.this));
                new NewClassTask().execute(info);
            }
        });
    }

    private void bindView(){
        titleBar = findViewById(R.id.tb_title_bar);
        stvClassName = findViewById(R.id.stv_class_name);
        stvClassNo = findViewById(R.id.stv_class_no);
        rbCreateClass = findViewById(R.id.rb_create_class);
        miniLoadingDialog = WidgetUtils.getMiniLoadingDialog(NewClassActivity.this);
        miniLoadingDialog.updateMessage("加载中");
    }

    class NewClassTask extends AsyncTask<Map<String, String>, Void, String> {
        @Override
        protected String doInBackground(Map<String, String>... maps) {
            //todo request add class
            String url = getString(R.string.root_url)+"/ljg/class/establish";
            try {
                return OkHttpUtils.post(url,OkHttpUtils.GSON.toJson(maps[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            miniLoadingDialog.dismiss();
            //跳回主页面
            Intent intent = new Intent(NewClassActivity.this,MainActivity.class);
            startActivity(intent);
            super.onPostExecute(s);
        }
    }
}
