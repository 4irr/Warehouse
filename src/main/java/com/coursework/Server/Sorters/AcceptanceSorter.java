package com.coursework.Server.Sorters;

import com.coursework.Server.Model.Acceptance;

import java.util.Comparator;
import java.util.List;

public class AcceptanceSorter implements Sorter<Acceptance> {
    private AcceptanceSorter() {}
    private static AcceptanceSorter instance;
    public static AcceptanceSorter getInstance(){
        if(instance==null)
            instance = new AcceptanceSorter();
        return instance;
    }
    @Override
    public List<Acceptance> sortByDateAsc(List<Acceptance> list) {
        list.sort(new Comparator<Acceptance>() {
            @Override
            public int compare(Acceptance o1, Acceptance o2) {
                if (o1.getOperation().getDate().isAfter(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return 1;
                else if(o1.getOperation().getDate().isBefore(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return -1;
                else return 0;
            }
        });
        return list;
    }

    @Override
    public List<Acceptance> sortByDateDesc(List<Acceptance> list) {
        list.sort(new Comparator<Acceptance>() {
            @Override
            public int compare(Acceptance o1, Acceptance o2) {
                if (o1.getOperation().getDate().isAfter(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return -1;
                else if(o1.getOperation().getDate().isBefore(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return 1;
                else return 0;
            }
        });
        return list;
    }
}
