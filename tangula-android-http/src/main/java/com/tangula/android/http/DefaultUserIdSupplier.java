package com.tangula.android.http;

import com.tangula.utils.function.Supplier;

public class DefaultUserIdSupplier implements Supplier<String> {
    @Override
    public String get() {
        return "";
    }
}