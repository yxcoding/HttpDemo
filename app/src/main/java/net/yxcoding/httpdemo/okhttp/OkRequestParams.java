package net.yxcoding.httpdemo.okhttp;

import okhttp3.FormBody;

/**
 * User: yxfang
 * Date: 2016-06-24
 * Time: 16:59
 * ------------- Description -------------
 * OKHttp 自封装请求参数对象
 * ---------------------------------------
 */
public class OkRequestParams
{
    private FormBody.Builder builder;

    public OkRequestParams()
    {
        builder = new FormBody.Builder();
    }

    public void put(String key, String value)
    {
        builder.add(key, value);
    }

    public FormBody toParams()
    {
        return builder.build();
    }
}
