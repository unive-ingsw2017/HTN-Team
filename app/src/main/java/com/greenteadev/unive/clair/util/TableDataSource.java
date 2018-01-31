package com.greenteadev.unive.clair.util;

/**
 * Created by Hitech95 on 25/01/2018.
 */


public interface TableDataSource<TFirstHeaderDataType, TRowHeaderDataType, TColumnHeaderDataType, TItemDataType> {

    int getRowsCount();

    int getColumnsCount();

    TFirstHeaderDataType getFirstHeaderData();

    TRowHeaderDataType getRowHeaderData(int index);

    TColumnHeaderDataType getColumnHeaderData(int index);

    TItemDataType getItemData(int rowIndex, int columnIndex);

}
