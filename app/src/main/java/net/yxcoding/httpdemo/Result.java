package net.yxcoding.httpdemo;

/**
 * User: yxfang
 * Date: 2016-06-27
 * Time: 14:26
 * ------------- Description -------------
 * <p>
 * ---------------------------------------
 */
public class Result<T>
{
    public static final int OK_CODE = 0;
    // token 过期
    public static final int TOKEN_CODE = -1;

    private String reason;

    private T result;

    private int error_code;

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public T getResult()
    {
        return result;
    }

    public void setResult(T result)
    {
        this.result = result;
    }

    public int getError_code()
    {
        return error_code;
    }

    public void setError_code(int error_code)
    {
        this.error_code = error_code;
    }
}
