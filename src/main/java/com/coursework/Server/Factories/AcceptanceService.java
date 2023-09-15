package com.coursework.Server.Factories;

import com.coursework.Server.Sorters.AcceptanceSorter;
import com.coursework.Server.Sorters.Sorter;

public class AcceptanceService extends AbstractFactory{
    @Override
    public Sorter createSorter() {
        return AcceptanceSorter.getInstance();
    }
}
