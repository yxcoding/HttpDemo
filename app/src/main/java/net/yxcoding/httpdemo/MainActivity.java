package net.yxcoding.httpdemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.yxcoding.httpdemo.asynhttp.AsynHttpUtil;
import net.yxcoding.httpdemo.okhttp.HttpException;
import net.yxcoding.httpdemo.okhttp.HttpRequestCallback;
import net.yxcoding.httpdemo.okhttp.OkHttpUtil;
import net.yxcoding.httpdemo.okhttp.OkRequestParams;
import net.yxcoding.httpdemo.retrofithttp.RetrofitService;
import net.yxcoding.httpdemo.retrofithttp.RetrofitUtil;
import net.yxcoding.httpdemo.volleyhttp.VolleyCallback;
import net.yxcoding.httpdemo.volleyhttp.VolleyRequestParams;
import net.yxcoding.httpdemo.volleyhttp.VolleyUtil;

import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener
{
    private RadioGroup rgHttp;
    private TextView tvResult;

    private ProgressDialog loadingDialog;

    /**
     * q	string	是	需要检索的关键字,请UTF8 URLENCODE
     * key	string	是	应用APPKEY(应用详细页查询)
     * dtype	string	否	返回数据的格式,xml或json，默认json
     */
    private static final String API = "http://op.juhe.cn/onebox/news/query";
    private static final String API_GET = "http://op.juhe.cn/onebox/news/query?key=5173fa20d74cf85747dcf6f4636856af&q=\"\"";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rgHttp = (RadioGroup) findViewById(R.id.rg_http);
        tvResult = (TextView) findViewById(R.id.tv_result);

        rgHttp.setOnCheckedChangeListener(this);
    }

    /**
     * 获取新闻列表
     *
     * @param get
     */
    private void getNewsList(boolean get)
    {
        tvResult.setText("");
        int httpType = rgHttp.getCheckedRadioButtonId();
        switch (httpType)
        {
            // OKHttp
            case 1:
                if (get)
                {
                    OkHttpUtil.getInstance().sendGetRequest(this, API_GET, okHttpCallback());
                }
                else
                {
                    OkRequestParams params = new OkRequestParams();
                    params.put("key", "5173fa20d74cf85747dcf6f4636856af");
                    params.put("q", "\"\"");
                    OkHttpUtil.getInstance().sendPostRequest(this, API, params, okHttpCallback());
                }
                break;
            // Volley
            case 2:
                showLoadingDialog();
                if (get)
                {
                    VolleyUtil.getInstance(this).sendGetRequest(this, API_GET, volleyCallback());
                }
                else
                {
                    VolleyRequestParams params = new VolleyRequestParams();
                    params.put("key", "5173fa20d74cf85747dcf6f4636856af");
                    params.put("q", "\"\"");
                    VolleyUtil.getInstance(this).sendPostRequest(this, API, params, volleyCallback());
                }
                break;
            // Retrofit
            case 3:
                showLoadingDialog();
                // 通过call 发起请求和取消请求
                retrofit2.Call<Result<List<News>>> call;
                if (get)
                {
                    // 通过call 发起请求和取消请求
                    call = RetrofitUtil.getInstance().createService(RetrofitService.class).getNewsList("5173fa20d74cf85747dcf6f4636856af", "\"\"");
                }
                else
                {
                    // 通过call 发起请求和取消请求
                    call = RetrofitUtil.getInstance().createService(RetrofitService.class).getNewsListByPost("5173fa20d74cf85747dcf6f4636856af", "\"\"");
                }
                RetrofitUtil.getInstance().sendRequest(
                        call, retrofitCallback());
                break;
            // Android Asynchronous Http Client
            case 4:
                if (get)
                {
                    AsynHttpUtil.getInstance().sendGetRequest(this, API_GET, asynCallback());
                }
                else
                {
                    RequestParams params = new RequestParams();
                    params.put("key", "5173fa20d74cf85747dcf6f4636856af");
                    params.put("q", "\"\"");
                    AsynHttpUtil.getInstance().sendPostRequest(this, API, params, asynCallback());
                }
                break;
        }

    }

    /**
     * 获取Retrofit 异步回掉接口
     *
     * @return
     */
    private retrofit2.Callback<Result<List<News>>> retrofitCallback()
    {
        return new Callback<Result<List<News>>>()
        {
            @Override
            public void onResponse(retrofit2.Call<Result<List<News>>> call, Response<Result<List<News>>> response)
            {
                closeLoadingDialog();

                if (response.isSuccessful())
                {
                    //注意这里用第一个Response参数的
                    Result<List<News>> result = response.body();
                    if (result.getError_code() == 0)
                    {
                        List<News> list = result.getResult();
                        if (list != null && !list.isEmpty())
                        {
                            StringBuffer sb = new StringBuffer();
                            for (News news : list)
                            {
                                sb.append(news.getFull_title() + "\n");
                            }
                            setResult(true, sb.toString());
                        }
                    }
                    else
                    {
                        setResult(true, result.getReason());
                    }
                }
                else
                {
                    try
                    {
                        setResult(false, response.errorBody().string());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Result<List<News>>> call, Throwable t)
            {
                closeLoadingDialog();
                setResult(false, t.getMessage());
            }
        };
    }

    /**
     * 获取Volley异步请求接口回调
     *
     * @return
     */
    private VolleyCallback<String> volleyCallback()
    {
        return new VolleyCallback()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                closeLoadingDialog();
                setResult(false, error.getMessage());
            }

            @Override
            public void onResponse(Object response)
            {
                closeLoadingDialog();
                Log.d("", response.toString());
                setResult(true, response.toString());
            }
        };
    }

    /**
     * 获取OKHttp 异步请求回调接口
     *
     * @return
     */
    private HttpRequestCallback<String> okHttpCallback()
    {
        return new HttpRequestCallback<String>()
        {
            @Override
            public void onStart()
            {
                showLoadingDialog();
            }

            @Override
            public void onFinish()
            {
                closeLoadingDialog();
            }

            @Override
            public void onResponse(String s)
            {
                setResult(true, s);
            }

            @Override
            public void onFailure(Call call, HttpException e)
            {
                setResult(false, e.getMessage());
            }
        };
    }

    /**
     * 获取Android Asynchronous Http Client 异步请求回调接口
     * <p/>
     * 也可返回 JsonHttpResponseHandler 接口回调 自动将响应结果解析为json格式
     *
     * @return
     */
    private AsyncHttpResponseHandler asynCallback()
    {
        return new AsyncHttpResponseHandler()
        {
            @Override
            public void onStart()
            {
                showLoadingDialog();
            }

            @Override
            public void onFinish()
            {
                closeLoadingDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                setResult(true, new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                setResult(false, error.getMessage());
            }
        };
    }

    private void setResult(boolean success, String result)
    {
        if (success)
        {
            tvResult.setText("请求结果\n-------------------------------------------\n" + result);
        }
        else
        {
            tvResult.setText("请求异常\n-----------------------\n" + result);
        }
    }


    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_get:
                getNewsList(true);
                break;
            case R.id.btn_post:
                getNewsList(false);
                break;
        }
    }

    private void showLoadingDialog()
    {
        if (loadingDialog == null)
        {
            loadingDialog = new ProgressDialog(this);
            loadingDialog.setTitle("loading...");
        }
        loadingDialog.show();
    }

    private void closeLoadingDialog()
    {
        if (loadingDialog != null && loadingDialog.isShowing())
        {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {

    }
}
