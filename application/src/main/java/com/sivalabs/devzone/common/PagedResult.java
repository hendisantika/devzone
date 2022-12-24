package com.sivalabs.devzone.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class PagedResult<T> {
    private Pageable pageable;
    private List<T> data;
    private long totalElements;
    private int pageNumber;
    private int totalPages;

    @JsonProperty("isFirst")
    private boolean isFirst;

    @JsonProperty("isLast")
    private boolean isLast;

    @JsonProperty("hasNext")
    private boolean hasNext;

    @JsonProperty("hasPrevious")
    private boolean hasPrevious;

    public PagedResult(Page<T> page) {
        this.setPageable(page.getPageable());
        this.setData(page.getContent());
        this.setTotalElements(page.getTotalElements());
        this.setPageNumber(page.getNumber() + 1); // 1 - based page numbering
        this.setTotalPages(page.getTotalPages());
        this.setFirst(page.isFirst());
        this.setLast(page.isLast());
        this.setHasNext(page.hasNext());
        this.setHasPrevious(page.hasPrevious());
    }

    public Pageable getPageable() {
        return this.pageable;
    }

    public List<T> getData() {
        return this.data;
    }

    public long getTotalElements() {
        return this.totalElements;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public boolean isFirst() {
        return this.isFirst;
    }

    public boolean isLast() {
        return this.isLast;
    }

    public boolean isHasNext() {
        return this.hasNext;
    }

    public boolean isHasPrevious() {
        return this.hasPrevious;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public void setLast(boolean isLast) {
        this.isLast = isLast;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
