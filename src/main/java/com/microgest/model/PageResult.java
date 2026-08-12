package com.microgest.model;

import java.util.List;

public class PageResult<T> {

    private final List<T> items;
    private final long totalItems;
    private final int pageNumber;
    private final int pageSize;

    public PageResult(List<T> items, long totalItems, int pageNumber, int pageSize) {
        this.items = items;
        this.totalItems = totalItems;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        if (pageSize <= 0) {
            return 1;
        }
        return Math.max(1, (int) Math.ceil((double) totalItems / pageSize));
    }

    public boolean isFirstPage() {
        return pageNumber <= 0;
    }

    public boolean isLastPage() {
        return pageNumber >= getTotalPages() - 1;
    }
}
