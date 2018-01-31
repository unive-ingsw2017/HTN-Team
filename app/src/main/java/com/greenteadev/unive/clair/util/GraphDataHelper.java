package com.greenteadev.unive.clair.util;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.components.LimitLine;
import com.google.common.collect.TreeMultimap;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.MeasurePlotData;

/**
 * Created by Hitech95 on 16/01/2018.
 */

public class GraphDataHelper {

    public enum BestOrder {MIN, MAX}

    TreeMultimap<LocalDate, MeasurePlotData> treeMapFiltered;

    public GraphDataHelper(@NonNull List<MeasurePlotData> dataList,
                           @NonNull LocalDate startDate, @NonNull LocalDate endDate) {
        treeMapFiltered = TreeMultimap.create();

        for (MeasurePlotData data : dataList) {

            if (data.date().isBefore(startDate) || data.date().isAfter(endDate)) {
                continue;
            }
            treeMapFiltered.put(data.date(), data);
        }
    }

    public List<MeasureData.MeasureType> getSets() {
        List<MeasureData.MeasureType> setList = new ArrayList<>();

        for (MeasurePlotData data : treeMapFiltered.values()) {
            if (!setList.contains(data.type()))
                setList.add(data.type());
        }
        return setList;
    }

    public List<MeasureData.MeasureType> getSets(LocalDate date) {
        List<MeasureData.MeasureType> setList = new ArrayList<>();

        for (MeasurePlotData data : treeMapFiltered.get(date)) {
            if (!setList.contains(data.type()))
                setList.add(data.type());
        }
        return setList;
    }

    public TreeMultimap<MeasureData.MeasureType, MeasurePlotData> getDataSet(LocalDate date) {
        TreeMultimap<MeasureData.MeasureType, MeasurePlotData> map = TreeMultimap.create();

        for (MeasurePlotData measure : treeMapFiltered.get(date)) {
            map.put(measure.type(), measure);
        }

        return map;
    }

    public List<MeasurePlotData> getRawData(LocalDate date) {
        return new ArrayList<>(treeMapFiltered.get(date));
    }

    public MeasurePlotData getRawData(LocalDate date, MeasureData.MeasureType type) {
        ArrayList<MeasurePlotData> measureList = new ArrayList<>(treeMapFiltered.get(date));
        for (MeasurePlotData data : measureList) {
            if (data.type() == type) return data;
        }

        return null;
    }

    public List<LocalDate> getXAxeValues() {
        return new ArrayList<>(treeMapFiltered.keySet());
    }

    public LocalDate bestDay(MeasureData.MeasureType type) {
        return bestDay(type, BestOrder.MIN);
    }

    public LocalDate bestDay(MeasureData.MeasureType type, BestOrder order) {
        MeasurePlotData measureBest = null;
        for (MeasurePlotData measure : treeMapFiltered.values()) {
            if (measureBest == null) measureBest = measure;
            boolean condition = (order == BestOrder.MIN) ?
                    measureBest.min() < measure.min() : measure.max() > measureBest.max();
            if (condition) {
                measureBest = measure;
            }
        }
        return measureBest.date();
    }
}
