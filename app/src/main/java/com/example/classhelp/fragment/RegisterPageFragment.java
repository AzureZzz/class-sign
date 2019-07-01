package com.example.classhelp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.activity.LoginActivity;
import com.example.classhelp.utils.OkHttpUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.button.CountDownButton;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterPageFragment extends Fragment {

    private View view;
    private String content;
    private Context context;

    private ButtonView bvRegister;
    private CountDownButton rbGetCode;
    private MaterialEditText metPhone;
    private MaterialEditText metPassword;
    private MaterialEditText metName;
    private MaterialEditText metStuNo;
    private MaterialEditText metSchool;
    private MaterialEditText metCode;
    private LinearLayout llLoad;

    public static RegisterPageFragment newInstance(String content, Context context) {
        return new RegisterPageFragment().setContent(content, context);
    }

    public RegisterPageFragment setContent(String content, Context context) {
        this.context = context;
        this.content = content;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        switch (content) {
            case "学生":
                view = inflater.inflate(R.layout.fragment_register_student, container, false);
                initStudentLayout();
                break;
            case "教师":
                view = inflater.inflate(R.layout.fragment_register_teacher, container, false);
                initTeacherLayout();
                break;
        }
        return view;
    }

    private void initStudentLayout() {
        bvRegister = view.findViewById(R.id.bv_register_student);
        rbGetCode = view.findViewById(R.id.cdb_get_code);
        metPhone = view.findViewById(R.id.met_phone);
        metPassword = view.findViewById(R.id.met_password);
        metName = view.findViewById(R.id.met_name);
        metStuNo = view.findViewById(R.id.met_stu_no);
        metSchool = view.findViewById(R.id.met_school);
        metCode = view.findViewById(R.id.met_code);
        llLoad = view.findViewById(R.id.ll_load);

        rbGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (metPhone.validate()) {
                    rbGetCode.setEnableCountDown(true);
                    String url =
                            getString(R.string.root_url) + "/ljg/verifyCode?phone=" +
                                    metPhone.getText().toString();
                    try {
                        OkHttpUtils.get(url, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final JsonObject res =
                                        OkHttpUtils.GSON.fromJson(response.body().string(),
                                                JsonObject.class);
                                if (!(res.get(Contract.JSONKEY.CODE).getAsInt() == 200)) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            XToast.error(context,
                                                    res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                                        }
                                    });
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    rbGetCode.setEnableCountDown(false);
                }

            }
        });
        bvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (metPhone.validate() && metPassword.validate() && metName.validate()
                        && metStuNo.validate() && metSchool.validate() && metCode.validate()) {
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put(Contract.User.PHONE, metPhone.getText().toString());
                    userInfo.put(Contract.User.PASSWORD, metPassword.getText().toString());
                    userInfo.put(Contract.User.NAME, metName.getText().toString());
                    userInfo.put(Contract.User.STUNO, metStuNo.getText().toString());
                    userInfo.put(Contract.User.SCHOOL, metSchool.getText().toString());
                    userInfo.put(Contract.User.TYPE, "1");
                    userInfo.put(Contract.OTHER.VERIFYCODE, metCode.getText().toString());
                    llLoad.setVisibility(View.VISIBLE);
                    bvRegister.setVisibility(View.GONE);
                    new RegisterTask().execute(userInfo);
                }
            }
        });
    }

    private void initTeacherLayout() {
        bvRegister = view.findViewById(R.id.bv_register_teacher);
        rbGetCode = view.findViewById(R.id.cdb_get_code);
        metPhone = view.findViewById(R.id.met_phone);
        metPassword = view.findViewById(R.id.met_password);
        metName = view.findViewById(R.id.met_name);
        metSchool = view.findViewById(R.id.met_school);
        metCode = view.findViewById(R.id.met_code);
        llLoad = view.findViewById(R.id.ll_load);
        rbGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (metPhone.validate()) {
                    rbGetCode.setEnableCountDown(true);
                    String url =
                            getString(R.string.root_url) + "/ljg/verifyCode?phone=" +
                                    metPhone.getText().toString();
                    try {
                        OkHttpUtils.get(url, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final JsonObject res =
                                        OkHttpUtils.GSON.fromJson(response.body().string(),
                                                JsonObject.class);
                                if (!(res.get(Contract.JSONKEY.CODE).getAsInt() == 200)) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            XToast.error(context,
                                                    res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                                        }
                                    });
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    rbGetCode.setEnableCountDown(false);
                }
            }
        });
        bvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (metPhone.validate() && metPassword.validate() && metName.validate()
                        && metSchool.validate() && metCode.validate()) {
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put(Contract.User.PHONE, metPhone.getText().toString());
                    userInfo.put(Contract.User.PASSWORD, metPassword.getText().toString());
                    userInfo.put(Contract.User.NAME, metName.getText().toString());
                    userInfo.put(Contract.User.SCHOOL, metSchool.getText().toString());
                    userInfo.put(Contract.User.TYPE, "2");
                    userInfo.put(Contract.OTHER.VERIFYCODE, metCode.getText().toString());
                    llLoad.setVisibility(View.VISIBLE);
                    bvRegister.setVisibility(View.GONE);
                    new RegisterTask().execute(userInfo);
                }
            }
        });
    }

    class RegisterTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/user/register";
            try {
                return OkHttpUtils.post(url, OkHttpUtils.GSON.toJson(maps[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            llLoad.setVisibility(View.GONE);
            bvRegister.setVisibility(View.VISIBLE);
            try {
                if (response != null) {
                    JsonObject res = OkHttpUtils.GSON.fromJson(response, JsonObject.class);
                    if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
                        XToast.success(context,
                                res.get(Contract.JSONKEY.DATA).getAsJsonObject()
                                        .get(Contract.JSONKEY.DATA).getAsString()).show();
                        startActivity(new Intent(context, LoginActivity.class));
                    } else if(res.get(Contract.JSONKEY.CODE).getAsInt() == 403) {
                        XToast.error(context,"账号已被注册").show();
                    }else{
                        XToast.error(context,"注册失败").show();
                    }
                }else{
                    XToast.error(context, "no response").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(context, "服务器异常！").show();
            }
        }
    }
}
