package com.coursework.Server.Factories;

import com.coursework.Server.Sorters.PlacementSorter;
import com.coursework.Server.Sorters.Sorter;

public class PlacementService extends AbstractFactory{
    @Override
    public Sorter createSorter() {
        return PlacementSorter.getInstance();
    }
}
