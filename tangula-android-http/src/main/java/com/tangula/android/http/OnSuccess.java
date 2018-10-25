package com.tangula.android.http;

@SuppressWarnings("unused")
public interface OnSuccess<T> {

    //返回成功的处理函数
    void onSuccess(T result);
}
