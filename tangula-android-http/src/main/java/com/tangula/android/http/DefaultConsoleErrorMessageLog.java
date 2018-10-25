package com.tangula.android.http;

import android.util.Log;

import com.tangula.utils.function.BiConsumer;

public class DefaultConsoleErrorMessageLog implements BiConsumer<String, Throwable> {
    @Override
    public void accept(String s, Throwable e) {
        if (e != null) {
            Log.e("http", e.getLocalizedMessage(), e);
        }
    }
}