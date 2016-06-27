package net.yxcoding.httpdemo.asynhttp;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * User: yxfang
 * Date: 2016-06-24
 * Time: 19:09
 * ------------- Description -------------
 * Android Asynchronous Http Client 网络请求封装工具类
 * ---------------------------------------
 */
public class AsynHttpUtil
{
    private static AsynHttpUtil instance;

    private AsyncHttpClient asyncHttpClient;

    public static AsynHttpUtil getInstance()
    {
        if (instance == null)
        {
            synchronized (AsynHttpUtil.class)
            {
                instance = new AsynHttpUtil();
            }
        }
        return instance;
    }

    private AsynHttpUtil()
    {
        asyncHttpClient = new AsyncHttpClient();
    }

    /**
     * 发送Get 请求
     *
     * @param context  用于取消http请求的凭证
     * @param url
     * @param callback 异步请求回调接口
     */
    public void sendGetRequest(Context context, String url, AsyncHttpResponseHandler callback)
    {
        asyncHttpClient.get(context, url, callback);
    }

    /**
     * 发送POST 请求
     * @param context   用于取消http请求的凭证
     * @param url
     * @param params    请求参数回对象
     * @param callback  异步请求回调接口
     */
    public void sendPostRequest(Context context, String url, RequestParams params, AsyncHttpResponseHandler callback)
    {
        asyncHttpClient.post(context, url, params, callback);
    }

    /**
     * 取消http 请求
     *
     * @param context 取消该上下文中的所有请求
     */
    public void cancelRequest(Context context)
    {
        asyncHttpClient.cancelRequests(context, true);
    }
}
