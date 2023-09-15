package com.coursework.Server.Factories;

import com.coursework.Server.Sorters.ReleaseSorter;
import com.coursework.Server.Sorters.Sorter;

public class ReleaseService extends AbstractFactory{
    @Override
    public Sorter createSorter() {
        return ReleaseSorter.getInstance();
    }
}
