package net.yxcoding.httpdemo.retrofithttp;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * User: yxfang
 * Date: 2016-06-27
 * Time: 11:15
 * ------------- Description -------------
 * Retrofit 网络请求库
 * ---------------------------------------
 */
public class RetrofitUtil
{
    private static RetrofitUtil instance;

    private Retrofit retrofit;

    public static RetrofitUtil getInstance()
    {
        if (instance == null)
        {
            synchronized (RetrofitUtil.class)
            {
                instance = new RetrofitUtil();
            }
        }
        return instance;
    }

    private RetrofitUtil()
    {
        retrofit = new Retrofit.Builder()
                // 接口基地址
                .baseUrl("http://op.juhe.cn/")
                // 添加格式转换器
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 创建请求服务接口
     *
     * @param claz
     * @param <T>
     * @return
     */
    public <T> T createService(Class<T> claz)
    {
        return retrofit.create(claz);
    }

    /**
     * 发起请求
     *
     * @param call
     * @param callback
     */
    public void sendRequest(Call call, retrofit2.Callback callback)
    {
        call.enqueue(callback);
    }

    /**
     * 传入请求时的call 对象
     * 取消请求
     *
     * @param call
     */
    public void cancelRequest(Call call)
    {
        call.cancel();
    }
}
