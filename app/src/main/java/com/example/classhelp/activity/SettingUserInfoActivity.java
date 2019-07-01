package com.example.classhelp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.entity.Token;
import com.example.classhelp.utils.FileUtils;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonObject;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.limuyang2.photolibrary.activity.LPhotoPickerActivity;
import top.limuyang2.photolibrary.util.LPPImageType;

public class SettingUserInfoActivity extends Activity implements View.OnClickListener {

    private TitleBar titleBar;
    private CircleImageView cvHeadImg;
    private EditText etName;
    private EditText etStuNo;
    private EditText etSchool;
    private EditText etMajor;
    private EditText etGrade;
    private EditText etEmail;
    private CheckBox cbMan;
    private CheckBox cbWoman;

    private LinearLayout llSave;
    private ImageView ivSave;

    private RelativeLayout rlStuNo;

    private MiniLoadingDialog miniLoadingDialog;

    private int sex = 0;
    private String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user_info);

        bindView();
        initData();

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cvHeadImg.setOnClickListener(this);
        llSave.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        cbWoman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cbWoman.isChecked()){
                    cbMan.setChecked(false);
                }
            }
        });
        cbMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cbMan.isChecked()){
                    cbWoman.setChecked(false);
                }
            }
        });
    }

    private void bindView() {
        titleBar = findViewById(R.id.tb_title_bar);
        cvHeadImg = findViewById(R.id.cv_head_img);
        etName = findViewById(R.id.et_name);
        etStuNo = findViewById(R.id.et_stu_no);
        etSchool = findViewById(R.id.et_school);
        etMajor = findViewById(R.id.et_major);
        etGrade = findViewById(R.id.et_grade);
        etEmail = findViewById(R.id.et_email);
        cbMan = findViewById(R.id.cb_man);
        cbWoman = findViewById(R.id.cb_woman);
        llSave = findViewById(R.id.ll_save);
        ivSave = findViewById(R.id.iv_save);
        rlStuNo = findViewById(R.id.rl_stu_no);
        miniLoadingDialog = WidgetUtils.getMiniLoadingDialog(SettingUserInfoActivity.this);
        miniLoadingDialog.updateMessage("上传中");
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        String src = bundle.getString(Contract.User.HEADSRC);
        if (src != null) {
            Glide.with(SettingUserInfoActivity.this).load(src).into(cvHeadImg);
        }
        etName.setText(bundle.getString(Contract.User.NAME));
        etSchool.setText(bundle.getString(Contract.User.SCHOOL));
        etGrade.setText(bundle.getString(Contract.User.GRADE));
        etMajor.setText(bundle.getString(Contract.User.MAJOR));
        etEmail.setText(bundle.getString(Contract.User.EMAIL));
        int type = bundle.getInt(Contract.User.TYPE);
        if (type == 1) {
            etStuNo.setText(bundle.getString(Contract.User.STUNO));
        } else {  //老师身份隐藏学号
            rlStuNo.setVisibility(View.GONE);
        }
        String sex = bundle.getString(Contract.User.SEX);
        if (sex != null) {
            if (sex.equals("1")) {
                cbMan.setChecked(true);
                this.sex = 1;
            } else if (sex.equals("2")) {
                cbWoman.setChecked(true);
                this.sex = 2;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_head_img:
                selectImage();
                break;
            case R.id.ll_save:
                showConfirmSave();
                break;
            case R.id.iv_save:
                showConfirmSave();
                break;
        }
    }

    private void selectImage() {
        Intent intent =
                new LPhotoPickerActivity.IntentBuilder(SettingUserInfoActivity
                        .this)
                        .columnsNumber(3) //以几列显示图片
                        .imageType(LPPImageType.ofAll()) //需要显示的图片类型
                        // (webp/PNG/GIF/JPG)
                        .pauseOnScroll(false) //滑动时，是否需要暂停图片加载
                        .isSingleChoose(true) //单选模式
                        .build();
        startActivityForResult(intent, 1);
    }

    private void showConfirmChangeHeadImg() {
        new MaterialDialog.Builder(this)
                .content(R.string.tip_change_head_confirm)
                .positiveText(R.string.lab_yes)
                .negativeText(R.string.lab_no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        miniLoadingDialog.show();
                        new UploadTask().execute(new String[]{
                                SPUtils.getToken(SettingUserInfoActivity.this)});
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                filepath = LPhotoPickerActivity.getSelectedPhotos(data).get(0);
                Glide.with(SettingUserInfoActivity.this).load(filepath).into(cvHeadImg);
                showConfirmChangeHeadImg();
            }
        }
    }

    private void showConfirmSave() {
        new MaterialDialog.Builder(this)
                .content(R.string.tip_save_user_info_confirm)
                .positiveText(R.string.lab_yes)
                .negativeText(R.string.lab_no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        miniLoadingDialog.show();
                        //todo: save info
                        saveUserInfo();
                    }
                })
                .show();
    }

    private void saveUserInfo() {
        Map<String, String> map = new HashMap<>();
        if (cbMan.isChecked()) {
            map.put(Contract.User.SEX, "1");
            sex = 1;
        } else if (cbWoman.isChecked()) {
            map.put(Contract.User.SEX, "2");
            sex = 2;
        }
        map.put(Contract.User.NAME, etName.getText().toString());
        map.put(Contract.User.STUNO, etStuNo.getText().toString());
        map.put(Contract.User.SCHOOL, etSchool.getText().toString());
        map.put(Contract.User.MAJOR, etMajor.getText().toString());
        map.put(Contract.User.GRADE, etGrade.getText().toString());
        map.put(Contract.User.EMAIL, etEmail.getText().toString());
        map.put(Contract.Token.TOKEN, SPUtils.getToken(SettingUserInfoActivity.this));
        new SaveInfoTask().execute(map);
    }

    class SaveInfoTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/userinfo/replace";
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
                        SPUtils.saveToken(SettingUserInfoActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());

                        //更新本地用户信息
                        SharedPreferences spUser = getSharedPreferences(Contract.SP.USER_FILE_NAME,
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = spUser.edit();
                        editor.putString(Contract.User.MAJOR, etMajor.getText().toString());
                        editor.putString(Contract.User.GRADE, etGrade.getText().toString());
                        editor.putString(Contract.User.EMAIL, etEmail.getText().toString());
                        editor.putString(Contract.User.SCHOOL, etSchool.getText().toString());
                        editor.putString(Contract.User.STUNO, etStuNo.getText().toString());
                        editor.putString(Contract.User.NAME, etName.getText().toString());
                        editor.putInt(Contract.User.SEX, sex);
                        editor.apply();
                        XToast.success(SettingUserInfoActivity.this,
                                "保存成功！").show();
                    } else {
                        XToast.error(SettingUserInfoActivity.this,
                                "保存失败！").show();
                    }
                } else {
                    XToast.error(SettingUserInfoActivity.this,
                            "no response").show();
                }
                miniLoadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                miniLoadingDialog.dismiss();
                XToast.error(SettingUserInfoActivity.this, "发生未知错误！").show();
            }
        }
    }

    class UploadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            //strings[0]:token
            String url = getString(R.string.root_url) + "/ljg/userInfo/upLoadHeadPic";
            String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
            File file = new File(filepath);
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream")
                    , file);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", filename, fileBody)
                    .addFormDataPart(Contract.Token.TOKEN, strings[0])
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = OkHttpUtils.getInstance();
            Call call = okHttpClient.newCall(request);
            try {
                return call.execute().body().string();
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
                        SPUtils.saveToken(SettingUserInfoActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());
                        //更新本地头像链接
                        SharedPreferences spUser = getSharedPreferences(Contract.SP.USER_FILE_NAME,
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = spUser.edit();
                        editor.putString(Contract.User.HEADSRC,
                                data.get(Contract.JSONKEY.DATA).getAsString());
                        editor.apply();

                        XToast.success(SettingUserInfoActivity.this, "头像上传成功").show();
                    } else {
                        XToast.error(SettingUserInfoActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                } else {
                    XToast.error(SettingUserInfoActivity.this,
                            "no response").show();
                }
                miniLoadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(SettingUserInfoActivity.this, "出现未知错误！").show();
                miniLoadingDialog.dismiss();
            }
        }
    }

}
