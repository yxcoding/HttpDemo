package net.yxcoding.httpdemo.okhttp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * User: yxfang
 * Date: 2016-06-24
 * Time: 16:20
 * ------------- Description -------------
 * OkHttp 网络请求封装工具类
 * ---------------------------------------
 */
public class OkHttpUtil
{
    private static final String TAG = "OkHttpUtil";
    private static OkHttpUtil instance;
    private static OkHttpClient okHttpClient;
    private Handler httpHandler;

    private Gson gson;

    public static synchronized OkHttpUtil getInstance()
    {
        if (instance == null)
        {
            synchronized (OkHttpUtil.class)
            {
                instance = new OkHttpUtil();
            }
        }
        return instance;
    }

    public OkHttpUtil()
    {
        okHttpClient = new OkHttpClient();
        httpHandler = new Handler(Looper.getMainLooper());
        gson = new Gson();
    }

    /**
     * 发送Get 请求
     *
     * @param context  通过context.getClass().getName() 设置请求tag，用来取消请求
     * @param url
     * @param callback 基于okHttp Callback 封装的一层回调
     */
    public void sendGetRequest(Context context, String url, HttpRequestCallback callback)
    {
        Request request = new Request.Builder().tag(getTagByContext(context)).url(url).get().build();
        okHttpClient.newCall(request).enqueue(getCallback(context, callback));
    }

    /**
     * 发送POST请求
     *
     * @param context  通过context.getClass().getName() 设置请求tag，用来取消请求
     * @param url
     * @param params   基于FormBody.Builder 封装的post请求参数对象
     * @param callback 基于okHttp Callback 封装的一层回调
     */
    public void sendPostRequest(Context context, String url, OkRequestParams params, HttpRequestCallback callback)
    {
        Request request = new Request.Builder().tag(getTagByContext(context)).url(url).post(params.toParams()).build();
        okHttpClient.newCall(request).enqueue(getCallback(context, callback));
    }

    /**
     * 通过context 生成http 请求tag
     * tag 用来标识 http 请求，可通过tag 来取消请求
     *
     * @param context
     * @return
     */
    private String getTagByContext(Context context)
    {
        return context != null ? context.getClass().getName() : null;
    }

    /**
     * okHttp 2.x 可利用okHttpClient.cancel(tag)来取消该tag下的所有请求
     * okHttp 3.x 取消该方式，可在回调中 获取call 来取消请求
     * @param context
     */
    public void cancelRequest(Context context)
    {
        okHttpClient.dispatcher().cancelAll();
    }

    /**
     * 重新封装一层callback
     * 添加onStart 和 onFinish
     *
     * @param callback
     * @return
     */
    private Callback getCallback(final Context context, final HttpRequestCallback callback)
    {
        if (callback != null)
        {
            callback.onStart();
        }

        return new Callback()
        {

            @Override
            public void onFailure(Call call, IOException e)
            {
                if (callback != null)
                {
                    HttpResult httpResult = new HttpResult(HttpHandler.HTTP_FAILURE);
                    httpResult.callback = callback;
                    httpResult.exception = new HttpException(e);
                    httpResult.call = call;
                    httpHandler.post(new HttpHandler(httpResult));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                if (callback != null)
                {
                    if (response.code() == 200)
                    {
                        HttpResult httpResult = new HttpResult(HttpHandler.HTTP_SUCCESS);
                        httpResult.callback = callback;
                        httpResult.response = response.body().string();
                        httpResult.call = call;
                        httpHandler.post(new HttpHandler(httpResult));
                    }
                    else
                    {
                        HttpResult httpResult = new HttpResult(HttpHandler.HTTP_FAILURE);
                        httpResult.callback = callback;
                        httpResult.call = call;
                        httpResult.exception = new HttpException(response.code());
                        httpHandler.post(new HttpHandler(httpResult));
                    }
                }
            }
        };
    }

    class HttpHandler implements Runnable
    {
        public static final int HTTP_SUCCESS = 1;
        public static final int HTTP_FAILURE = 2;

        private HttpResult httpResult;

        public HttpHandler(HttpResult httpResult)
        {
            this.httpResult = httpResult;
        }

        @Override
        public void run()
        {
            httpResult.callback.onFinish();

            if (httpResult.what == HTTP_SUCCESS)
            {
                // 当返回的类型是String
                if (httpResult.callback.type == String.class)
                {
                    httpResult.callback.onResponse(httpResult.response);
                }
                else
                {
                    try
                    {
                        Object obj = gson.fromJson(httpResult.response, httpResult.callback.type);
                        httpResult.callback.onResponse(obj);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, httpResult.response);
                        // 解析异常
                        httpResult.callback.onFailure(httpResult.call, new HttpException(HttpException.EXCEPTION_DATA));
                    }
                }
            }
            else
            {
                httpResult.callback.onFailure(httpResult.call, httpResult.exception);
            }
        }
    }

    /**
     * 封住请求回调实体对象
     */
    class HttpResult
    {
        private HttpRequestCallback callback;
        private String response;
        private HttpException exception;
        private Call call;
        private int what;

        private Message msg;

        public HttpResult(int what)
        {
            this.msg = new Message();
            this.what = what;
        }

        public Message getMessage()
        {
            this.msg.what = what;
            msg.obj = this;
            return msg;

        }
    }
}
