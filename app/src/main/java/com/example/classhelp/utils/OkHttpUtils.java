package com.example.classhelp.utils;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

    public static Gson GSON = new Gson();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client;

    public OkHttpUtils() { }

    public static OkHttpClient getInstance() {
        if (client == null) {
            synchronized (OkHttpUtils.class) {
                if (client == null) {
                    //配置了网络请求的超时时间
                    client = new OkHttpClient().newBuilder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .build();

                }
            }
        }
        return client;
    }

    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = getInstance().newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static void post(String url,String json,Callback callback) throws IOException{
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        getInstance().newCall(request).enqueue(callback);
    }

    public static void get(String url,Callback callback)throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();
        getInstance().newCall(request).enqueue(callback);
    }
}
