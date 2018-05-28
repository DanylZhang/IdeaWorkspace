package com.danyl.learnjava.mybatis.query;

import java.util.List;

/**
 * Created by Administrator on 2017-6-26.
 */
public class PageResult {
    private int totalCount;
    private int pageSize;
    private int currentPage;
    private List result;

    public PageResult() {
    }

    public PageResult(int totalCount, int pageSize, int currentPage, List<?> list) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.result = list;
    }

    public PageResult empty(int pageSize) {
        return this;
    }

    public void getResult() {
        for (int i = 0; i < pageSize && i < result.size(); i++) {
            int y = currentPage * pageSize + i + 1;
            System.out.println("第" + y + "项" + result.get(i).toString());
        }
    }
}