package net.yxcoding.httpdemo.volleyhttp;

import com.android.volley.Response;

/**
 * User: yxfang
 * Date: 2016-06-24
 * Time: 20:44
 * ------------- Description -------------
 * 重新封装 VolleyCallback 接口回调
 * 通过继承Response.Listener<T>, Response.ErrorListener整合onResponse和onErrorResponse
 * ---------------------------------------
 */
public interface VolleyCallback<T> extends Response.Listener<T>, Response.ErrorListener
{

}
