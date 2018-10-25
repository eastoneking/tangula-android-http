package com.tangula.android.http;

import android.util.Log;

import com.tangula.utils.function.Consumer;

public class DefaultConsoleMessageLog implements Consumer<String> {
    @Override
    public void accept(String s) {
        Log.e("http", s);
    }
}