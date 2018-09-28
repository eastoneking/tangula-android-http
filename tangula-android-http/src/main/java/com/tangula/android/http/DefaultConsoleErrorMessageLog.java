package com.tangula.android.http;

import com.tangula.utils.function.BiConsumer;

public class DefaultConsoleErrorMessageLog implements BiConsumer<String, Throwable> {
    @Override
    public void accept(String s, Throwable e) {
        System.out.println("[tag:http]" + s);
        if (e != null) {
            e.printStackTrace();
        }
    }
}