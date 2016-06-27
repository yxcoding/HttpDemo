package net.yxcoding.httpdemo.retrofithttp;


import net.yxcoding.httpdemo.News;
import net.yxcoding.httpdemo.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * User: yxfang
 * Date: 2016-06-27
 * Time: 11:17
 * ------------- Description -------------
 * <p/>
 * ---------------------------------------
 */
public interface RetrofitService
{
    @GET("onebox/news/query")
    Call<Result<List<News>>> getNewsList(@Query("key") String key, @Query("q") String q);

    @POST("onebox/news/query")
    Call<Result<List<News>>> getNewsListByPost(@Query("key") String key, @Query("q") String q);
}
