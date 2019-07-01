package com.example.classhelp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonObject;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.toast.XToast;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JoinClassActivity extends Activity implements View.OnClickListener,
        View.OnFocusChangeListener {

    private ImageView ivScan, ivJoinClassBack;
    private RoundButton rbJoin;
    private EditText first, second, third, fourth, five, six;
    private List<EditText> mEdits = new ArrayList<>();
    private OnInputFinishListener mInputListener;
    private MiniLoadingDialog miniLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);

        bindView();

        ivScan.setOnClickListener(this);
        ivJoinClassBack.setOnClickListener(this);
        rbJoin.setOnClickListener(this);
        mEdits.add(first);
        mEdits.add(second);
        mEdits.add(third);
        mEdits.add(fourth);
        mEdits.add(five);
        mEdits.add(six);

        first.setFocusable(true);
        first.addTextChangedListener(new MyTextWatcher());
        second.addTextChangedListener(new MyTextWatcher());
        third.addTextChangedListener(new MyTextWatcher());
        fourth.addTextChangedListener(new MyTextWatcher());
        five.addTextChangedListener(new MyTextWatcher());
        six.addTextChangedListener(new MyTextWatcher());
    }

    private void bindView() {
        ivScan = findViewById(R.id.iv_scan);
        rbJoin = findViewById(R.id.rb_join_class);
        ivJoinClassBack = findViewById(R.id.iv_join_class_back);
        first = findViewById(R.id.edit_first);
        second = findViewById(R.id.edit_second);
        third = findViewById(R.id.edit_third);
        fourth = findViewById(R.id.edit_fourth);
        five = findViewById(R.id.edit_five);
        six = findViewById(R.id.edit_six);
        miniLoadingDialog = WidgetUtils.getMiniLoadingDialog(JoinClassActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_scan:
                scan();
                break;
            case R.id.iv_join_class_back:
                finish();
                break;
            case R.id.rb_join_class:
                if (getInputString().length() == 6) {
                    miniLoadingDialog.show();
                    Map<String, String> info = new HashMap<>();
                    info.put(Contract.Token.TOKEN, SPUtils.getToken(JoinClassActivity.this));
                    info.put(Contract.Class.CODE, getInputString());
                    new JoinClassTask().execute(info);
                } else {
                    XToast.warning(JoinClassActivity.this, "请输入完整的课程码!").show();
                }
                break;
        }
    }

    private void scan() {
        // 打开扫描界面扫描条形码或二维码
        Intent openCameraIntent = new Intent(getApplication(), CaptureActivity.class);
        startActivityForResult(openCameraIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Map<String, String> info = new HashMap<>();
            info.put(Contract.Token.TOKEN, SPUtils.getToken(JoinClassActivity.this));
            info.put(Contract.Class.CODE, scanResult);
            new JoinClassTask().execute(info);
        }
    }


    private String getInputString() {
        StringBuffer sb = new StringBuffer();
        for (EditText e : mEdits) {
            sb.append(e.getText());
        }
        return sb.toString();
    }

    //监听控制输入框
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }

    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() != 0) {
                focus();
            }
        }

        private void focus() {
            EditText editText;
            //利用for循环找出前面还没被输入字符的EditText
            for (int i = 0; i < mEdits.size(); i++) {
                editText = mEdits.get(i);
                if (editText.getText().length() < 1) {
                    editText.requestFocus();
                    return;
                } else {
                    editText.setCursorVisible(false);
                }
            }
            EditText lastEditText = mEdits.get(mEdits.size() - 1);
            if (lastEditText.getText().length() > 0) {
                //收起软键盘 并不允许编辑 同时将输入的文本提交
                getResponse();
                lastEditText.setCursorVisible(false);
                InputMethodManager imm = (InputMethodManager) getApplication()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }


        public void getResponse() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mEdits.size(); i++) {
                sb.append(mEdits.get(i).getText().toString());
            }
            if (mInputListener != null) {
                mInputListener.onFinish(sb.toString());
            }
        }

        //对外封装一个重置或直接填写验证码的方法
        public void setText(String text) {
            if (text.length() == mEdits.size()) {
                StringBuilder sb = new StringBuilder(text);
                first.setText(sb.substring(0, 1));
                second.setText(sb.substring(1, 2));
                third.setText(sb.substring(2, 3));
                fourth.setText(sb.substring(3, 4));
                five.setText(sb.substring(4, 5));
                six.setText(sb.substring(5, 6));
            } else {
                first.setText("");
                second.setText("");
                third.setText("");
                fourth.setText("");
                five.setText("");
                six.setText("");
//            first.setCursorVisible(true);
                first.requestFocus();
            }
        }
    }

    //一个监听输入结束的接口，以便外部回调结束后执行的方法
    public interface OnInputFinishListener {
        void onFinish(String code);
    }

    class JoinClassTask extends AsyncTask<Map<String, String>, Void, String> {
        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/myClass/join";
            try {
                return OkHttpUtils.post(url, OkHttpUtils.GSON.toJson(maps[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            miniLoadingDialog.dismiss();
            super.onPostExecute(s);
            try {
                if (s != null) {
                    JsonObject res = OkHttpUtils.GSON.fromJson(s, JsonObject.class);
                    if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
                        JsonObject data = res.getAsJsonObject(Contract.JSONKEY.DATA);
                        JsonObject newToken = data.getAsJsonObject(Contract.JSONKEY.NEWTOKEN);
                        SPUtils.saveToken(JoinClassActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());
                        //todo: to a show page
                        startActivity(new Intent(JoinClassActivity.this, MainActivity.class));
                    } else if (res.get(Contract.JSONKEY.CODE).getAsInt() == 403){
                        XToast.warning(JoinClassActivity.this,
                                "您已加入该课堂！").show();
                    }else{
                        XToast.error(JoinClassActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                } else {
                    XToast.error(JoinClassActivity.this,
                            "no response").show();
                }
                miniLoadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(JoinClassActivity.this, "出现未知错误！").show();
                miniLoadingDialog.dismiss();
            }
        }
    }
}
