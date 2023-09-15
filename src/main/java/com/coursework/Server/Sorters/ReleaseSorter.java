package com.coursework.Server.Sorters;

import com.coursework.Server.Model.Release;

import java.util.Comparator;
import java.util.List;

public class ReleaseSorter implements Sorter<Release>{
    private ReleaseSorter() {}
    private static ReleaseSorter instance;
    public static ReleaseSorter getInstance(){
        if(instance==null)
            instance = new ReleaseSorter();
        return instance;
    }
    @Override
    public List<Release> sortByDateAsc(List<Release> list) {
        list.sort(new Comparator<Release>() {
            @Override
            public int compare(Release o1, Release o2) {
                if (o1.getOperation().getDate().isAfter(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return 1;
                else if(o1.getOperation().getDate().isBefore(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return -1;
                else return 0;
            }
        });
        return list;
    }

    @Override
    public List<Release> sortByDateDesc(List<Release> list) {
        list.sort(new Comparator<Release>() {
            @Override
            public int compare(Release o1, Release o2) {
                if (o1.getOperation().getDate().isAfter(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return -1;
                else if(o1.getOperation().getDate().isBefore(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return 1;
                else return 0;
            }
        });
        return list;
    }
}
