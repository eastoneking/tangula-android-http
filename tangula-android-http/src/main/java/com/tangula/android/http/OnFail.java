package com.tangula.android.http;

public interface OnFail {

    //返回失败的处理函数
    void onFail(int code,String msg,Throwable th);
}
