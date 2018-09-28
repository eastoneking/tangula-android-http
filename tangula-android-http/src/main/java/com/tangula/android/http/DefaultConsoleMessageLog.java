package com.tangula.android.http;

import com.tangula.utils.function.Consumer;

public class DefaultConsoleMessageLog implements Consumer<String> {
    @Override
    public void accept(String s) {
        System.out.println("[tag:http]" + s);
    }
}