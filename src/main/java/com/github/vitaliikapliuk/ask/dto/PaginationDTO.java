package com.github.vitaliikapliuk.ask.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class PaginationDTO<T> {

    private int total;
    private int size;
    private int from;
    private Map<String, T> data;

    public PaginationDTO() {
    }

    public PaginationDTO(int total, int size, int from, LinkedHashMap<String, T> data) {
        this.total = total;
        this.size = size;
        this.from = from;
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public Map<String, T> getData() {
        return data;
    }

    public void setData(Map<String, T> data) {
        this.data = data;
    }
}
