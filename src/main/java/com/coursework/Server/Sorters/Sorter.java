package com.coursework.Server.Sorters;

import java.util.List;

public interface Sorter<T> {
    public List<T> sortByDateAsc(List<T> list);
    public List<T> sortByDateDesc(List<T> list);
}
