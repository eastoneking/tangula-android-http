package com.tangula.android.http;

import java.util.ArrayList;
import java.util.List;

public class PagingBody<T> {

    private int pageIndex=1;
    private int pageSize=10;
    private int total=0;
    private List<T> items = new ArrayList();

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
