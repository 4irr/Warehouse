package com.coursework.Server.Sorters;

import com.coursework.Server.Model.Acceptance;
import com.coursework.Server.Model.Placement;

import java.util.Comparator;
import java.util.List;

public class PlacementSorter implements Sorter<Placement>{
    private PlacementSorter() {}
    private static PlacementSorter instance;
    public static PlacementSorter getInstance(){
        if(instance==null)
            instance = new PlacementSorter();
        return instance;
    }
    @Override
    public List<Placement> sortByDateAsc(List<Placement> list) {
        list.sort(new Comparator<Placement>() {
            @Override
            public int compare(Placement o1, Placement o2) {
                if (o1.getOperation().getDate().isAfter(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return 1;
                else if(o1.getOperation().getDate().isBefore(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return -1;
                else return 0;
            }
        });
        return list;
    }

    @Override
    public List<Placement> sortByDateDesc(List<Placement> list) {
        list.sort(new Comparator<Placement>() {
            @Override
            public int compare(Placement o1, Placement o2) {
                if (o1.getOperation().getDate().isAfter(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return -1;
                else if(o1.getOperation().getDate().isBefore(o2.getOperation().getDate()) || o1.getOperation().getDate().equals(o2.getOperation().getDate())) return 1;
                else return 0;
            }
        });
        return list;
    }
}
