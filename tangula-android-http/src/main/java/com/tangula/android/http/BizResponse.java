package com.tangula.android.http;

/**
 * 应答数据结构.
 */
@SuppressWarnings("unused")
public class BizResponse<T> {
    private int status;
    private String statusText;
    private String message;
    private T body;

    public BizResponse() {
    }

    public BizResponse(int status, String statusText, String message, T body) {
        this();
        this.status = status;
        this.statusText = statusText;
        this.message = message;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}